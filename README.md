![Logo](https://i.ibb.co/L66Ywrn/c-Vurl-sticker-600x300.png)

[![Coverage Status](https://coveralls.io/repos/github/corese4rch/cvurl/badge.svg?branch=master&kill_cache=1)](https://coveralls.io/github/corese4rch/cvurl?branch=master)
[![Build Status](https://travis-ci.com/corese4rch/cvurl.svg?branch=dev)](https://travis-ci.com/corese4rch/cvurl)
[![GNU License](https://img.shields.io/badge/license-GNU%20GPL%20v3-green.svg)](https://github.com/corese4rch/cvurl/blob/master/LICENSE)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.github.corese4rch/cvurl-io/badge.svg?kill_cache=1)](https://search.maven.org/search?q=g:com.github.corese4rch%20AND%20a:cvurl-io)

## cVurl is an open-source wrapper for [Java HTTP/2 Client](https://openjdk.java.net/groups/net/httpclient/intro.html). 

## Requirements 
 **cVurl** is written in java11 and can be used with any jdk11.0.2 and higher
 (mostly because of this [issue](https://bugs.openjdk.java.net/browse/JDK-8211437))
 
## Dependencies
 **cVurl** is made purely with Java 11 and one of our goals was to bring as fewer dependencies to your project as possible. The only dependencies that will be added to your project are:
 
 ```java
        //(1)
        <dependency>
            <groupId>com.fasterxml.jackson.core</groupId>
            <artifactId>jackson-databind</artifactId>
            <version>${jackson.core.version}</version>
            <optional>true</optional>
        </dependency>
        //(2)
        <dependency>
            <groupId>org.slf4j</groupId>
            <artifactId>slf4j-api</artifactId>
            <version>${slf4j-api.version}</version>
            <scope>provided</scope>
        </dependency>
```
 So, there is nothing hidden from you or transitive. If you decide to use smth different from [Jackson](https://github.com/FasterXML/jackson), you shouldn't be worried about the first dependency. 
 On the other hand, if you do use Jackson  - just leave it as is, we will create default ObjectMapper for you under the hood.
And there is a high chance you will use some logging library so [Slf4j](https://github.com/qos-ch/slf4j) shouldn't be an unexpected resident in your build file. (For comprehensive examples, please, navigate to [Examples](#examples) section)   
 
## Concept
The idea behind this project is very simple - Make developers’ life a little bit easier. During our work we heavily use different HTTP clients: Spring's RestTemplate, Feign, plain OkHttp3/OkHttp4, then (with switching to Java 11+) we start using Java native HTTP/2 Client. Even though all the tools listed above are great, each time there was something we weren't totally happy with:(.
 
 Let's take a look at this simple example:
 
 _What if we want to get some memes from the internet?_ 
 
 **OkHttp4 Example**
 ```java
public void okHttp() {
        OkHttpClient okHttpClient = new OkHttpClient();
        //GET
        try {
            ResponseBody body = okHttpClient.newCall(new Request.Builder()
                    .url("https://api.imgflip.com/get_memes")
                    .build())
                    .execute()
                    .body();

            if (body != null) {
                System.out.println("OkHttp GET: " + body.string());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
```

**Plain Java11 Example**

```java
public void javaHttp() {
        HttpClient client = HttpClient.newHttpClient();

        //GET
        try {
            HttpResponse<String> response = client.send(HttpRequest.newBuilder()
                    .uri(URI.create("https://api.imgflip.com/get_memes"))
                    .build(), HttpResponse.BodyHandlers.ofString());

            System.out.println("Java HttpClient GET: " + response.body());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
``` 

We, at [CoreValue](https://corevalue.com.ua/) didn't like checked Exceptions at all. What we didn't like even more, that's the null checks. So we have come up with something like this:

```java
public void cvurl() {
        CVurl cvurl = new CVurl();

        //GET
        Response<String> response = cvurl.get("https://api.imgflip.com/get_memes")
                        .asString()
                        .orElseThrow(RuntimeException::new);

        System.out.println("CVurl GET: " + response.getBody());
    }
```    
Looks much better, doesn't it? ;)

_What about something a little bit more complicated, like a POST request?_

To simplify the examples below, we will use the following data structure:
```java
    @Data //Lombok annotation
    public class Result {
        private boolean success;
        private Data data;

        @lombok.Data
        private static class Data {
            private String url;
            @JsonAlias("page_url")
            private String pageUrl;
        }
    }
```

**OkHttp4 Example**
```java
public void okHttp() {
        OkHttpClient okHttpClient = new OkHttpClient();
        
        //POST
        HttpUrl url = HttpUrl.parse("https://api.imgflip.com/caption_image").newBuilder()
                .addQueryParameter("template_id", "112126428")
                .addQueryParameter("username", "test-user")
                .addQueryParameter("password", "123test321")
                .addQueryParameter("text0", "text0")
                .addQueryParameter("text1", "text1")
                .build();

        try {
            ResponseBody body = okHttpClient.newCall(new Request.Builder()
                    .url(url)
                    .post(RequestBody.create(new byte[]{})).build())
                    .execute()
                    .body();

            if (body != null) {
                Result result = new ObjectMapper().readValue(body.string(), Result.class);
                System.out.println("OkHttp POST: " + result);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
```

**Plain Java11 Example**
```java
public void javaHttp() {
        HttpClient client = HttpClient.newHttpClient();

        //POST
        URI uri = HttpUrl.parse("https://api.imgflip.com/caption_image").newBuilder()
                .addQueryParameter("template_id", "112126428")
                .addQueryParameter("username", "test-user")
                .addQueryParameter("password", "123test321")
                .addQueryParameter("text0", "text0")
                .addQueryParameter("text1", "text1")
                .build()
                .uri();

        try {
            HttpResponse<String> response = client.send(HttpRequest.newBuilder()
                            .uri(uri)
                            .method("POST", HttpRequest.BodyPublishers.noBody())
                            .build(),
                    HttpResponse.BodyHandlers.ofString());

            Result result = new ObjectMapper().readValue(response.body(), Result.class);
            System.out.println("Java HttpClient POST: " + result);

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
```

And again, we see the same 'troubles' - checked exceptions, null-checking, verbosity...

**cVurl Example**
```java
public void cvurl() {
        CVurl cvurl = new CVurl();

        //POST
        Result result = cvurl.post("https://api.imgflip.com/caption_image")
                .queryParams(Map.of(
                        "template_id", "112126428",
                        "username", "test-user",
                        "password", "123test321",
                        "text0", "text0",
                        "text1", "text1"
                ))
                .asObject(Result.class);

        System.out.println("CVurl POST: " + result);
    }
```
And another example - due to underlying Mapper (by default it's Jackson) You can send use Java objects as a Request body without a hassle
```java
 public Optional<UserCreatedResponseDto> createUser(UserDto userDto) {
        return cVurl.POST(HOST + USERS)
                .body(userDto)
                .header(HttpHeader.CONTENT_TYPE, MIMEType.APPLICATION_JSON)
                .asObject(UserCreatedResponseDto.class, HttpStatus.CREATED);
    }
``` 

Easy  as pie. 
Interested? We encourage you to try cVurl!

## Java 11 Http/2 Client missing parts

#### Formdata
There is no built-in support to send a POST request with x-www-form-urlencoded, 
and to implement it with plain Java 11 Http/2 client you have to add several lines of code to create BodyPublisher
```java
public static BodyPublisher ofFormData(Map<Object, Object> data) {
    var builder = new StringBuilder();
    for (Map.Entry<Object, Object> entry : data.entrySet()) {
      if (builder.length() > 0) {
        builder.append("&");
      }
      builder
          .append(URLEncoder.encode(entry.getKey().toString(), StandardCharsets.UTF_8));
      builder.append("=");
      builder
          .append(URLEncoder.encode(entry.getValue().toString(), StandardCharsets.UTF_8));
    }
    return BodyPublishers.ofString(builder.toString());
  }
```
Well, we bet you would try to avoid writing something like this. Because, that's not all.
```java
    var client = HttpClient.newHttpClient();

    Map<Object, Object> data = new HashMap<>();
    data.put("id", 1);
    data.put("name", "a name");
    data.put("ts", System.currentTimeMillis());

    var request = HttpRequest.newBuilder()
                    .POST(ofFormData(data))
                    .uri(URI.create("https://localhost:8443/formdata"))
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .build();

    return client.sendAsync(request, BodyHandlers.ofString())
                    .thenApply(HttpResponse::body)
                    .exceptionally(e -> "Error: " + e.getMessage())
                    .thenAccept(System.out::println);
```
We do not fancy such verbosity.
That's why we do all stuff for you under the hood. What You really use is just this simple and nice-looking (fingers crossed) API.

###### cVurl formdata example
```java
 public User createUserFromFormUrlencoded(Map<String, String> userMap) {
        return cVurl.post("https://...")
                .formData(userMap) // will automatically add application/x-www-form-urlencoded header
                .asObject(User.class, HttpStatus.CREATED) // we assume that API returns JSON that represents User.class
                .orElseThrow(() -> new RuntimeException("Some comprehensive explanation what went wrong"));
    }
```

#### Compression
The Java 11 HTTP client does not handle compressed responses nor does it send the Accept-Encoding request header 
to request compressed responses by default.
If we know that the server is able to send back compressed resources, we can request them by adding the Accept-Encoding header. 
In this example we only want a compressed response if it's in the gzip format.

```java
    var client = HttpClient.newHttpClient();
    var request = HttpRequest.newBuilder()
                    .GET()
                    .header("Accept-Encoding", "gzip")
                    .uri(URI.create("https://localhost:8443/indexWithoutPush"))
                    .build();
```

The server can disregard this header and send back an uncompressed response or he complies and sends back gzip compressed resources. 
In our application we have to handle both cases, unless your are absolutely certain that a server always sends back compressed resources.
To check if a resource is compressed the application reads the Content-Encoding response header. 
If this header is present and contains the value gzip, the application uses the built-in GZIPInputStream to decompress the response body. 
Otherwise, the resource is uncompressed and no special handling is needed.
```java
    HttpResponse<InputStream> response = client.send(request, BodyHandlers.ofInputStream());

    String encoding = response.headers().firstValue("Content-Encoding").orElse("");
    if (encoding.equals("gzip")) {
      System.out.println("gzip compressed");
      ByteArrayOutputStream os = new ByteArrayOutputStream();
      try (InputStream is = new GZIPInputStream(response.body()); var autoCloseOs = os) {
        is.transferTo(autoCloseOs);
      }
      System.out.println(new String(os.toByteArray(), StandardCharsets.UTF_8));
    }
    else {
      System.out.println("not compressed");
      ByteArrayOutputStream os = new ByteArrayOutputStream();
      try (var is = response.body(); var autoCloseOs = os) {
        is.transferTo(autoCloseOs);
      }
      System.out.println(new String(os.toByteArray(), StandardCharsets.UTF_8));
    }
```

Wow, that's just too much. That's why we give You this API to achieve the same with less
###### cVurl compression example

```java
public String singleUserAsStringCompressed(String userId) {
        Response<String> response = cVurl.get("https://..." + userId)
                .acceptCompressed() //we mark that we expect gzip response from server
                .asString()
                .orElseThrow(() -> new RuntimeException("Some error happened during request execution"));

        if (response.status() == HttpStatus.OK) {
            return response.getBody();
        } else {
            return "Not what you expect to get";
        }
    }
```

#### Multipart
If the server endpoint just expects binary data in the request body an application could just send a POST request with BodyPublishers.ofFile. 
This publisher reads a file from the filesystem and sends the bytes in the body to the server.
But in this case we need to send some additional data in the POST request body and use a multipart form post with the Content-Type multipart/form-data. 
The request body is specially formatted as a series of parts, separated with boundaries. 
Unfortunately the Java 11 HTTP client does not provide any convenient support for this kind of body, so we have to build it from scratch.
The following method takes a Map of key/value pairs and a boundary and then builds the multipart body.
```java
public static BodyPublisher ofMimeMultipartData(Map<Object, Object> data,
      String boundary) throws IOException {
    var byteArrays = new ArrayList<byte[]>();
    byte[] separator = ("--" + boundary + "\r\nContent-Disposition: form-data; name=")
        .getBytes(StandardCharsets.UTF_8);
    for (Map.Entry<Object, Object> entry : data.entrySet()) {
      byteArrays.add(separator);

      if (entry.getValue() instanceof Path) {
        var path = (Path) entry.getValue();
        String mimeType = Files.probeContentType(path);
        byteArrays.add(("\"" + entry.getKey() + "\"; filename=\"" + path.getFileName()
            + "\"\r\nContent-Type: " + mimeType + "\r\n\r\n").getBytes(StandardCharsets.UTF_8));
        byteArrays.add(Files.readAllBytes(path));
        byteArrays.add("\r\n".getBytes(StandardCharsets.UTF_8));
      }
      else {
        byteArrays.add(("\"" + entry.getKey() + "\"\r\n\r\n" + entry.getValue() + "\r\n")
            .getBytes(StandardCharsets.UTF_8));
      }
    }
    byteArrays.add(("--" + boundary + "--").getBytes(StandardCharsets.UTF_8));
    return BodyPublishers.ofByteArrays(byteArrays);
```

```java
    Map<Object, Object> data = new LinkedHashMap<>();
    data.put("file", localFile);
    String boundary = new BigInteger(256, new Random()).toString();

    request = HttpRequest.newBuilder()
              .header("Content-Type", "multipart/form-data;boundary=" + boundary)
              .POST(ofMimeMultipartData(data, boundary))
              .uri(URI.create("https://..."))
              .build();

    HttpResponse<String> vtResponse = client.send(request, BodyHandlers.ofString());

    try (JsonReader jsonReader = Json.createReader(new StringReader(vtResponse.body()))) {
      JsonObject jobj = jsonReader.readObject();
      String resource = jobj.getString("resource");
      URI uri = UrlBuilder.fromString("https://www.virustotal.com/vtapi/v2/file/report")
          .addParameter("apikey", virusTotalApiKey).addParameter("resource", resource)
          .toUri();

      HttpResponse<String> status = client.send(HttpRequest.newBuilder(uri).build(),BodyHandlers.ofString());
    }
```

We try our best to overcome this dozens of lines of code, and here is what we have build for You
###### cVurl multipart example 
```java
    PostMapping("/photos") // we use Spring to represent multipart flow
    public ResponseEntity uploadPhoto(@RequestParam MultipartFile photo, @RequestParam String title) throws IOException {
        Response<String> response = cVurl.post(HOST + PHOTOS)
                .body(MultipartBody.create()
                        .formPart("title", Part.of(title))
                        .formPart("photo", Part.of(photo.getName(), photo.getContentType(), photo.getBytes())))
                .asString()
                .orElseThrow(() -> new RuntimeException("Some error happened during request execution"));

        return ResponseEntity.status(response.status()).build();
    }
```

## How to get cVurl
 Well, as simply as everything else about cVurl.
 
 **Maven**
 ```xml
<dependecies>
    <dependency>
        <groupId>com.github.corese4rch</groupId>
        <artifactId>cvurl-io</artifactId>
        <version>1.1</version>
    </dependency>
</dependencies>
```
 **Gradle**
```groovy
compile group: 'com.github.corese4rch', name: 'cvurl-io', version: '1.1'
```
   
## Examples
 More examples can be found at [example repository](https://github.com/corese4rch/cvurl-examples)
 
 Usage examples:
 * [spring-boot example](https://github.com/corese4rch/cvurl-examples/tree/master/cvurl-usage-spring-boot)
 * [plain java 11 example](https://github.com/corese4rch/cvurl-examples/tree/master/cvurl-usage-plain-java)
 * [micronaut example](https://github.com/corese4rch/cvurl-examples/tree/master/cvurl-usage-micronaut)
 * [quarkus example](https://github.com/corese4rch/cvurl-examples/tree/master/cvurl-usage-quarkus) 
 
   
## License
 >  Copyright 2019 Core Value, Inc. Licensed under GNU GPLv3 
  
