# Integration Guide
This guide describes all necessary steps to integrate the KassenSichV
client in your Java application.

*Notice*: The example guide focuses on demonstrating the client and
therefore disobeys some best practices to simplify 
the resulting source code.

## Setup and integration
The integration process differs a little depending on your target
platform. Because we need to provide you with a platform specific SMA
implementation, there are 2 different implementations of the SMA interface.
One for all standard JVM platforms (e.g. desktop) and another one
for Android devices. 

### Gradle
For integration using `Gradle`, use the following dependencies:

```
implementation group: 'com.fiskaly.kassensichv', name: 'client', version: '0.0.1-alpha'
implementation group: 'com.fiskaly.kassensichv', name: 'platform-<general|android>', version: '0.0.1-alpha'
implementation group: 'com.fiskaly.kassensichv', name: 'platform-common', version: '0.0.1-alpha'
```
### Maven
For integration using `Maven`, use the following dependencies:

```
<dependencies>
    <dependency>
      <groupId>com.fiskaly.kassensichv</groupId>
      <artifactId>platform-<general|android></artifactId>
      <version>0.0.1-alpha</version>
    </dependency>

    <dependency>
      <groupId>com.fiskaly.kassensichv</groupId>
      <artifactId>platform-common</artifactId>
      <version>0.0.1-alpha</version>
    </dependency>

    <dependency>
      <groupId>com.fiskaly.kassensichv</groupId>
      <artifactId>client</artifactId>
      <version>0.0.1-alpha</version>
    </dependency>
</dependencies>
```
### Manual
In case you are not using either `Maven` or `Gradle` you'll have to
embed a Fat-JAR into your project.

To build the Fat-JAR(s), you can issue the following command
within the `fiskaly-kassensichv-client-java` directory:

`$ ./gradlew shadowJar -Dtarget=<general|android>` 

This will create two Fat-JAR files containing everything
you'll need depending on your target platform.

* `./build/libs/com.fiskaly.kassensichv.client.general-<version>-all.jar` for standard Java platforms
* `./build/libs/com.fiskaly.kassensichv.client.android-<version>-all.jar` for Android

## Working with the client
Currently all the client does is adding interceptors to help you being
compliant with the KassenSichV. Therefore, the client works the same
as any other OkHttpClient instance. 
### Creating a client using your API secret and key
The library provides a factory that lets you create an instance
of the OkHttpClient class with additional functionality. The method
also expects your API key & secret as well as a SMA implementation.

For Android that means having to instantiate the AndroidSMA class and 
passing it to the factory method.

```java
OkHttpClient client = ClientFactory.getClient(
        apiKey,
        apiSecret,
        new AndroidSma()
);
```

### Creating a TSS
The following code snippet illustrates how to create a basic
TSS.
```java
ObjectMapper mapper = new ObjectMapper();
Map<String, String> tssMap = new HashMap<>();

tssMap.put("state", "INITIALIZED");
tssMap.put("description", "A very basic TSS");

final String jsonBody = mapper.writeValueAsString(tssMap);
final UUID tssId = UUID.randomUUID();

final Request createTssRequest = new Request.Builder()
        .url("https://kassensichv.io/api/v0/tss" + tssId)
        .put(RequestBody.create(jsonBody, MediaType.parse("application/json")))
        .build();
```
### Create a client
After creating a client you can create a client using the `tssId`
from the previous example.
```java
ObjectMapper mapper = new ObjectMapper();
Map<String, String> clientMap = new HashMap<>();

clientMap.put("serial_number", clientId);
String jsonBody = mapper.writeValueAsString(clientMap);

final UUID clientId = UUID.randomUUID();

final Request createClientRequest = new Request.Builder()
        .url("https://kassensichv.io/api/v0/tss/" + tssId + "/client/" + clientId)
        .post(RequestBody.create(jsonBody, MediaType.parse("application/json")))
        .build();
```
### Issue a transaction
After you've created a TSS and a client you can start issuing
transactions. The following example shows how to create a transaction
using the `aeao` scheme.
```java
Map<String, Object> transactionMap = new HashMap<>();

transactionMap.put("client_id", clientId);
transactionMap.put("type", "RECEIPT");
transactionMap.put("state", "ACTIVE");

Map<String, Object> transactionDataMap = new HashMap<>();

transactionMap.put("data", transactionDataMap);

Map<String, Object> aeaoMap = new HashMap<>();
aeaoMap.put("receipt_type", "RECEIPT");

Map<String, Object> amountsPerVatRateMap = new HashMap<>();
amountsPerVatRateMap.put("vat_rate", "19");
amountsPerVatRateMap.put("amount", "10.0");

aeaoMap.put("amounts_per_vat_rate", new Object[] { amountsPerVatRateMap });

Map<String, Object> amountPerPaymentTypeMap = new HashMap<>();
amountPerPaymentTypeMap.put("payment_type", "CASH");
amountPerPaymentTypeMap.put("amount", "10.0");

aeaoMap.put("amount_per_payment_type", amountPerPaymentTypeMap);

String jsonBody = mapper.writeValueAsString(transactionMap);

final Request createTransaction = Request.Builder()
        .url("https://kassensichv.io/api/v0/tss/" + tssId + "/tx/" + txId)
        .put(RequestBody.create(jsonBody, MediaType.parse("application/json")))
        .build();
```
### A closer look at the transaction properties
Using the request from the previous example we can take a closer look
at the transaction properties from the response.
```java
Response response = client
        .newCall(createTransaction)
        .execute();

String transactionResponse = response
    .body()
    .string();

Map<String, Object> transactionMap = objectMapper
    .readValue(transactionResponse,
        new TypeReference<Map<String,Object>>() {});

int start = (int) transactionMap.get("time_start");
int end = (int) transactionMap.get("time_end");
int signatureCounter = (int) ((Map) transactionMap.get("signature")).get("counter"));
int transactionCounter = (int) transactionMap.get("number");
String certificateSerial = (String) transactionMap.get("certificate_serial");
```

### Trigger an export
Because exports can be huge in size, the process of retrieving an export
is asynchronous.

First, you'll need to trigger an export.
```java
final UUID exportId = UUID.randomUUID();

Request triggerExport = new Request.Builder()
        .url("https://kassensichv.io/api/v0/tss/" + tssId + "/export/" + exportId)
        .put(RequestBody.create("", MediaType.parse("application/json")))
        .build();
```
### Retrieve a previously triggered export
After you've triggered an export, you can retrieve the export when
it's completed.

```java
Request retrieveExport = new Request.Builder()
        .url("https://kassensichv.io/api/v0/tss/" + tssId + "/export/" + exportId)
        .get()
        .build();

Response response = client
        .newCall(retrieveExport)
        .execute();

String exportResponse = response
    .body()
    .string();

Map<String, Object> exportMap = objectMapper
    .readValue(exportResponse,
        new TypeReference<Map<String,Object>>() {});

// Link to the generated TAR file
String exportLink = exportMap.get("href");

Request retrieveTar = new Request.Builder()
        .url(exportLink)
        .get()
        .build();

// Execute request and further process the file
```
