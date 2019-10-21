# Client Demo

This project is a demo application that does the following:

- Create a TSS
- Create a client
- Issue a transaction using the AEAO-scheme


In case of the exit code being 0, the client successfully issued all requests.

## Setup
The demo application depends on the client being available as a fat JAR in the `libs` directory. 


Build instructions for the client can be found in the [readme.md](https://github.com/fiskaly/fiskaly-kassensichv-client-java/blob/master/readme.md) in the project root.

## Build instructions
Use the following command to create a self-contained ("fat") JAR.


```
$ ./gradlew shadowJar
```


The resulting fat JAR will reside at `./build/libs/client-demo-1.0-SNAPSHOT-all.jar`.

## Running the demo application
Firstly, values for the following environment variables have to be set: 
- `API_KEY`
- `API_SECRET`

```
$ export API_KEY=<your-api-key>
$ export API_SECRET=<your-api-secret>
```

After these have been set, you can run the application, use the following command.

```
$ java -jar client-demo-1.0-SNAPSHOT-all.jar.jar
```
