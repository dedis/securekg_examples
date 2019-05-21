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
