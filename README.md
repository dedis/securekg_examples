# securekg_examples

This is an example project that demonstrates how to use the cothority-java
client API to communicate with securekg test servers via both Java and Scala.

## Setting up the project
1. Install docker containers for the tests
```
git clone https://github.com/dedis/cothority
cd cothority
make docker
```

2. Clone this project
```
git clone https://github.com/dedis/securekg_examples.git
```

3. Try to build and run it
```
cd securekg_examples
sbt test
```
`sbt run` does not work yet. When the conodes from DEDIS and SDSC are configured we will fix the main function.


## Using multiple eventlogs in a single ByzCoin

In a realistic scenario, there will be multiple deployments of Renku where each of them will use a different eventlog instance.
This section describes how to configure such a scenario.

### Overview

This scenario will have two types of players, the super-admin and the deployment-admin.
The super-admin controls (holds the private key) the genesis DARC and is primarily responsible for adding and removing deployment-admins.
The deployment-admin has their own private keys that the super-admin does not need to know about, they can spawn eventlogs and then log events.
However, deployment-admins should not be allowed to do anything else other than interacting with eventlog instances.

If the super-admin added the deployment-admin to ByzCoin correctly, then the deployment-admins are able to start logging events without knowing anything about the environment (e.g., the identity of the super-admin or other deployment-admins).
We do not allow deployment-admins to use the eventlogs controlled by other deployment-admins.

For clarity, we assume the players are individuals, that is, they hold only one key-pair.
But we can easily generalize the players into a t-out-of-n scenario where there are a total of n keys and t of them is needed to create a signature.

### Creating the DARCs

The two types of players are generalized into DARCs: super-darc and deployment-darc.
Super-darc should be created using [`ByzCoinRPC.makeGenesisDarc`](https://static.javadoc.io/ch.epfl.dedis/cothority/3.1.3/ch/epfl/dedis/byzcoin/ByzCoinRPC.html#makeGenesisDarc-ch.epfl.dedis.lib.darc.Signer-ch.epfl.dedis.lib.network.Roster-). 
The function needs a `SignerEd25519` and a `Roster` which are loaded from the super-admin configuration file (more on this later).
This DARC will have the `spawn:darc` rule which allows it to create new DARCs, effectively delegating power to others.

Deployment-darcs need the help of the super-darc to be created.
First, the devops person who is responsible for a particular deployment needs to create the DARC using the `Darc` constructor.
Here is an example.

```scala
val deploymentSigner = new SignerEd25519(deploymentPrivateKey)
val deploymentDarc = new Darc(List(deploymentSigner.getIdentity).asJava, 
                              List(deploymentSigner.getIdentity).asJava, 
                              "darc for my deployment".getBytes)
deploymentDarc.addIdentity("_name:eventlog", deploymentSigner.getIdentity, Rules.OR)
deploymentDarc.addIdentity("spawn:eventlog", deploymentSigner.getIdentity, Rules.OR)
deploymentDarc.addIdentity("invoke:" + EventLogInstance.contractID + "." + EventLogInstance.logCmd, 
                           deploymentSigner.getIdentity, Rules.OR)
```

Then, the devops person needs to send `deploymentDarc` to the super-admin because only the super-admin has the permission to create new DARCs, that is, storing it on ByzCoin.
The devops person should create a config file that contains `deploymentDarc`, or one that contains enough information that the super-admin can use to re-create `deploymentDarc`.
The super-admin checks that the `deploymentDarc` only has the rules related to eventlogs and then uses [`SecureDarcInstance`](https://static.javadoc.io/ch.epfl.dedis/cothority/3.1.3/ch/epfl/dedis/byzcoin/contracts/SecureDarcInstance.html#SecureDarcInstance-ch.epfl.dedis.byzcoin.ByzCoinRPC-ch.epfl.dedis.lib.darc.DarcId-ch.epfl.dedis.lib.darc.Signer-java.lang.Long-ch.epfl.dedis.lib.darc.Darc-) to create it on ByzCoin.

```scala
new SecureDarcInstance(bc, superDarcID, superSigner, superSignerCtr, deploymentDarc)
```

This concludes the DARC creation phase and new deployments are ready to start using eventlogs.

### Recommended configuration files

The super-admin and deployment-admin the same configuration file format.
We assume there are two configuration files for every player, but they can be combined into one.

Both players need the (same) roster information, which is typically found in a file called `public.toml`, this is our first configuration file.
The roster file is used for connecting to ByzCoin.
The ByzCoin service needs to be created if it does not exist.
To detect whether the service exists, use [`SkipchainRPC.getAllSkipChainIDs`](https://static.javadoc.io/ch.epfl.dedis/cothority/3.1.3/ch/epfl/dedis/skipchain/SkipchainRPC.html#getAllSkipChainIDs-ch.epfl.dedis.lib.network.Roster-) to find a list of existing ByzCoin IDs and check the length.
Thus it is not necessary to store the ByzCoin ID in the configuration.

The second configuration file contains all information other than the roster.
The fields are shown below.

- The *private key* is essential because the player needs it to sign transactions. The public key can be derived from the private key so it does not need to be stored.
- The *DARC ID* is recommended because it is needed to look up (using [NamingInstance](https://static.javadoc.io/ch.epfl.dedis/cothority/3.1.3/ch/epfl/dedis/byzcoin/contracts/NamingInstance.html)) the eventlog instance ID if one already exists.
  It is not essential because one can create the DARC deterministically just from the private key.
  But it is a good idea to pre-create the DARC and then store the ID just in case the DARC-creation function changes in the future.
- The *eventlog name* is recommended because it is used for looking up the instance ID of the eventlog. 
  Unless the name never changes. This field is not needed if this configuration file is for the admin.

Note that if the deployment-admin sends the configuration to the super-admin, then it is generally not a good idea to include the private key.
Otherwise the super-admin will have the ability to impersonate deployment-admins.
The super-admin only needs the public key to spawn the deployment-darc.

