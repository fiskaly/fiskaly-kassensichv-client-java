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

### Basic Client Test
If you simply want to test the compatibility of the client in regards to your platform, use the following
command:

```
$ java -jar client-demo-1.0-SNAPSHOT-all.jar
```

This will issue a few test requests to verify the client is working on your platform.


### Advanced Client Test
The demo application also offers an advanced test option that allows you to measure response times over a
period of time.

```
$ java -jar client-demo-1.0-SNAPSHOT-all.jar monitor --out-file=<path> --interval=<milliseconds> \
    --error-out-file=<path>
```

This will log requests using the following CSV schema:

```
timestamp;requestId;method;url;clientDelta;serverDelta
```

If no paths are specified the application will default to `System.out` and
`System.err` respectively.
