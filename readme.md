# fiskaly KassenSichV client for Java

The fiskaly KassenSichV client is an HTTP client that is needed<sup>[1](#fn1)</sup> for accessing the [kassensichv.io](https://kassensichv.io) API that implements a cloud-based, virtual **CTSS** (~Certified~ Technical Security System) / **TSE** (Technische Sicherheitseinrichtung) as defined by the German **KassenSichV** ([Kassen­sich­er­ungsver­ord­nung](https://www.bundesfinanzministerium.de/Content/DE/Downloads/Gesetze/2017-10-06-KassenSichV.pdf)).

Conceptually this client is a thin (convenience) wrapper around the [OkHttp](https://square.github.io/okhttp)
library for Java. This means you will have to look up the
[API documentation](https://square.github.io/okhttp/) of OkHttp to learn 
how this client is used. From a developer's point of view, the only difference
is that you have to instantiate your OkHttpClient through the ```ClientFactory``` provided by the SDK.

Be sure to have a look at our [integration guide / tutorial](https://github.com/fiskaly/fiskaly-kassensichv-client-java/blob/master/tutorial.md).

## Features

- [X] Automatic authentication handling (fetch/refresh JWT and re-authenticate upon 401 errors).
- [X] Automatic retries on failures (server errors or network timeouts/issues).
- [ ] Automatic JSON parsing and serialization of request and response bodies.
- [X] Future: [<a name="fn1">1</a>] compliance regarding [BSI CC-PP-0105-2019](https://www.bsi.bund.de/SharedDocs/Downloads/DE/BSI/Zertifizierung/Reporte/ReportePP/pp0105b_pdf.pdf?__blob=publicationFile&v=7) which mandates a locally executed SMA component for creating signed log messages. 
- [ ] Future: Automatic offline-handling (collection and documentation according to [Anwendungserlass zu § 146a AO](https://www.bundesfinanzministerium.de/Content/DE/Downloads/BMF_Schreiben/Weitere_Steuerthemen/Abgabenordnung/AO-Anwendungserlass/2019-06-17-einfuehrung-paragraf-146a-AO-anwendungserlass-zu-paragraf-146a-AO.pdf?__blob=publicationFile&v=1))

## Build project

First of all, you have to initialize the required git submodule(s) using:

```
$ git submodule update --init
```

The project uses [Gradle](https://gradle.org/) as a build and dependency management tool.
In order to build the project, use the following command:

```
$ ./gradlew jar
```

This will result in multiple JAR-files for each sub-project that can be used in your project.

Each built library will be available at ```<sub-project-name>/build/libs/<sub-project-name>-<version>.jar```.


## Integration

To integrate the client into your project, use the appropriate client build for your target platform.

* `client/build/libs/com.fiskaly.kassensichv.client.general-<version>.jar` for standard Java platforms
* `client/build/libs/com.fiskaly.kassensichv.client.android-<version>.jar` for Android

## Usage example

The following example demonstrates how to instantiate an OkHttpClient using the SDK
as well as how to issue requests against the KassenSichV-API.

```java
public class Main {
  // com.fasterxml.jackson.databind.ObjectMapper
  private static final ObjectMapper mapper = new ObjectMapper(); 

  public static void Main(String[] args) {
    // Instantiate the appropriate SMA implementation for your target platform
    GeneralSMA sma = new GeneralSMA();

    // Instantiate the OkHttpClient using the provided Factory class
    OkHttpClient client = ClientFactory.getClient(apiKey, apiSecret, sma);

    // Create a basic TSS

    Map<String, String> tssMap = new HashMap<>();

    tssMap.put("state", "INITIALIZED");
    tssMap.put("description", "An example TSS");

    String jsonBody = mapper.writeValueAsString(tssMap);

    RequestBody body = RequestBody.create(jsonBody, MediaType.parse("application/json"));
    UUID uuid = UUID.randomUUID();

    Request request = new Request
        .Builder()
        .url("https://kassensichv.io/api/v0/tss/" + uuid)
        .put(body)
        .build();

    Response response = client
        .newCall(request)
        .execute();
  }
}
```

## Related

- [fiskaly.com](https://fiskaly.com)
- [dashboard.fiskaly.com](https://dashboard.fiskaly.com)
- [kassensichv.io](https://kassensichv.io)
- [kassensichv.net](https://kassensichv.net)
