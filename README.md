# securekg_examples

This is an example project that demonstrates how to use cothority-java to
communicate with securekg test servers.

## Setting up the project
1. Clone cothority-java
```
git clone https://github.com/dedis/cothority.git
```

2. Install it to your local maven repository 
```
cd cothority/external/java
mvn -Dmaven.test.skip=true install
```
We skip the tests because they depend on the existance of our docker images.

3. Clone this project
```
git clone https://github.com/dedis/securekg_examples.git
```

4. Try to build and run it
```
cd securekg_examples
mvn exec:java -Dexec.mainClass="ch.epfl.dedis.securekg.Main"
```

## Classes
The `ServerConfig` class contains the configuration details. You should copy it
into your project and use that to access our test servers. The `Main` class is
an example of how one might use `ServerConfig`.
