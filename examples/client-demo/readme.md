# Client Demo

This project is a demo application that does the following:

- Create a TSS
- Create a client
- Issue a transaction using the AEAO-scheme


In case of the exit code being 0, the client successfully issued all requests.

## Build instructions
Use the following command to create a self-contained ("fat JAR") JAR.


```
$ ./gradlew shadowJar
```


The resulting fat JAR will reside at `./build/libs/client-demo-1.0-SNAPSHOT-all.jar`.

## Running the demo application
In order to run the application, use the following command.


```
$ java -jar client-demo-1.0-SNAPSHOT-all.jar.jar
```
