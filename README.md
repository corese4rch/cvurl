![Logo](https://i.ibb.co/L66Ywrn/c-Vurl-sticker-600x300.png)

[![Coverage Status](https://coveralls.io/repos/github/corese4rch/cvurl/badge.svg?branch=master&kill_cache=1)](https://coveralls.io/github/corese4rch/cvurl?branch=master)
[![Build Status](https://travis-ci.com/corese4rch/cvurl.svg?branch=dev)](https://travis-ci.com/corese4rch/cvurl)
[![GNU License](https://img.shields.io/badge/license-GNU%20GPL%20v3-green.svg)](https://github.com/corese4rch/cvurl/blob/master/LICENSE)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.github.corese4rch/cvurl-io/badge.svg?kill_cache=1)](https://search.maven.org/search?q=g:com.github.corese4rch%20AND%20a:cvurl-io)

## cVurl is an open-source wrapper for the [Java HTTP client](https://openjdk.java.net/groups/net/httpclient/intro.html). 

## Requirements 
 **cVurl** is written in Java 11 and can be used with any JDK 11.0.2 or newer
 (mainly because of this [issue](https://bugs.openjdk.java.net/browse/JDK-8211437))
 
## Dependencies
 **cVurl** is built purely with Java 11, and one of our goals was to bring as few dependencies to your project as possible. The only dependencies that will be added to your project are:
 
 ```xml
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
If you choose to use something different from [Jackson](https://github.com/FasterXML/jackson), you shouldn't be worried about the first dependency.
On the other hand, if you do use Jackson  - leave it as is. The library will create a default ObjectMapper for you under the hood.
And there is a high chance you will use some logging library, so [Slf4j](https://github.com/qos-ch/slf4j) shouldn't be an unexpected resident in your build file. (For comprehensive examples, please, navigate to [Examples](#examples) section)
 
## Concept
The idea behind this project is simple - to make life easier for developers.
We, as developers, have used numerous HTTP clients (such as RestTemplate, Feign, plain OkHttp3 / OkHttp4, Apache HttpClient, and others) in our work. With the transition to Java 11, we started working with the new Java HTTP client. While all of the above tools are good, each of them has something we are not willing to tolerate.

Let's take a look at the following example:

_What if we want to get some memes from the Internet?_

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

**Plain Java 11 Example**

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

We, at [Avenga](https://www.avenga.com/), don't like checked exceptions at all. Null checks are something we detest even more. Therefore, we came up with the following:

```java
public void cVurl() {
    CVurl cVurl = new CVurl();

    //GET
    Response<String> response = cVurl.get("https://api.imgflip.com/get_memes")
        .asString()
        .orElseThrow(RuntimeException::new);

    System.out.println("CVurl GET: " + response.getBody());
}
```    
Looks much better, right? ;)

_What about something a little more complex, like a POST request?_

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

**Plain Java 11 Example**
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

And again, we see the same "troubles" - checked exceptions, checks for null, verbosity...

**cVurl Example**
```java
public void cVurl() {
    CVurl cVurl = new CVurl();

    //POST
    Result result = cVurl.post("https://api.imgflip.com/caption_image")
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
And one more example - thanks to the basic mapper (which is Jackson by default), you can send Java objects in a request body without any problems.
```java
public Optional<UserCreatedResponseDto> createUser(UserDto userDto) {
    return cVurl.post(HOST + USERS)
        .body(userDto)
        .asObject(UserCreatedResponseDto.class, HttpStatus.CREATED);
}
``` 

As easy as pie.

Interested? We encourage you to try cVurl!

## Java 11 HTTP Client missing features

#### Form data
There is no built-in support to send a POST request with x-www-form-urlencoded,
and to implement it with plain Java 11 HTTP client, you have to add several lines of code to create a custom BodyPublisher implementation.
```java
public static HttpRequest.BodyPublisher ofFormData(Map<Object, Object> data) {
    var builder = new StringBuilder();
    for (Map.Entry<Object, Object> entry : data.entrySet()) {
        if (builder.length() > 0) {
            builder.append("&");
        }
        builder.append(URLEncoder.encode(entry.getKey().toString(), StandardCharsets.UTF_8));
        builder.append("=");
        builder.append(URLEncoder.encode(entry.getValue().toString(), StandardCharsets.UTF_8));
    }
    return HttpRequest.BodyPublishers.ofString(builder.toString());
}
```
Well, we bet you will try not to write something like that because that's not all.
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
We don't like such verbosity. That's why we do everything for you under the hood. You will only be using this simple and beautiful (fingers crossed) API.
###### cVurl form data example
```java
public User createUserFromFormUrlencoded(Map<String, String> userMap) {
    return cVurl.post("https://...")
        .formData(userMap) // will automatically add application/x-www-form-urlencoded header
        .asObject(User.class, HttpStatus.CREATED) // we assume that API returns JSON that represents the User class
        .orElseThrow(() -> new RuntimeException("Some comprehensive explanation of what went wrong"));
}
```

#### Compression
The Java 11 HTTP Client does not process compressed responses and does not send an Accept-Encoding request header to request compressed responses by default. If we know the server can send back compressed data, we can request it by adding the Accept-Encoding header. In this example, we only want a compressed response if it's in the gzip format.

```java
var client = HttpClient.newHttpClient();
var request = HttpRequest.newBuilder()
        .GET()
        .header("Accept-Encoding", "gzip")
        .uri(URI.create("https://localhost:8443/indexWithoutPush"))
        .build();
```

The server can ignore this header and send the response uncompressed, or it accepts it and sends back gzipped data. In our application, we have to handle both cases unless you are sure that a server always sends back compressed data. To check if the data is compressed, the application reads the Content-Encoding response header. If the value is gzip, the application uses the built-in GZIPInputStream to decompress the response body. Otherwise, the data is not compressed, and no special processing is required.
```java
try {
    HttpResponse<InputStream> response = client.send(request, HttpResponse.BodyHandlers.ofInputStream());

    String encoding = response.headers().firstValue("Content-Encoding").orElse("");
    if (encoding.equals("gzip")) {
        System.out.println("gzip compressed");
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try (InputStream is = new GZIPInputStream(response.body()); var autoCloseOs = os) {
            is.transferTo(autoCloseOs);
        }
        System.out.println(new String(os.toByteArray(), StandardCharsets.UTF_8));
    } else {
        System.out.println("not compressed");
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try (var is = response.body(); var autoCloseOs = os) {
            is.transferTo(autoCloseOs);
        }
        System.out.println(new String(os.toByteArray(), StandardCharsets.UTF_8));
    }
} catch (IOException io) {
    io.printStackTrace();
}
```

Wow, this is too much. That is why we have provided this API for you to achieve the same at a lower development cost.
###### cVurl compression example

```java
public String singleUserAsStringCompressed(String userId) {
    Response<String> response = cVurl.get("https://..." + userId)
        .acceptCompressed() //we mark that we expect gzip response from server
        .asString()
        .orElseThrow(() -> new RuntimeException("An error occurred while executing the request"));

    if (response.status() == HttpStatus.OK) {
        return response.getBody();
    } else {
        return "Not what you expect to receive";
    }
}
```

#### Multipart
If the server endpoint expects binary data in the body, the application can send a POST request using the BodyPublishers.ofFile publisher. This publisher reads a file from the filesystem and sends the bytes in the body to the server. But in this case, we need to send the body in a specific format with the multipart/form-data value in the Content-Type header.  The request body is specially formatted as a series of parts, separated by boundaries. Unfortunately, the Java 11 HTTP client does not provide any convenient support for this type of request body, so we have to build it from scratch. The following method takes a map of key/value pairs and a boundary and then creates the multipart body.
```java
public static HttpRequest.BodyPublisher ofMimeMultipartData(Map<Object, Object> data,
        String boundary) throws IOException {
    var byteArrays = new ArrayList<byte[]>();
    byte[] separator = ("--" + boundary + "\r\nContent-Disposition: form-data; name=").getBytes(StandardCharsets.UTF_8);
    for (Map.Entry<Object, Object> entry : data.entrySet()) {
        byteArrays.add(separator);

        if (entry.getValue() instanceof Path) {
            var path = (Path) entry.getValue();
            String mimeType = Files.probeContentType(path);
            byteArrays.add(("\"" + entry.getKey() + "\"; filename=\"" + path.getFileName()
                + "\"\r\nContent-Type: " + mimeType + "\r\n\r\n").getBytes(StandardCharsets.UTF_8));
            byteArrays.add(Files.readAllBytes(path));
            byteArrays.add("\r\n".getBytes(StandardCharsets.UTF_8));
        } else {
            byteArrays.add(("\"" + entry.getKey() + "\"\r\n\r\n" + entry.getValue() + "\r\n").getBytes(StandardCharsets.UTF_8));
        }
    }
    byteArrays.add(("--" + boundary + "--").getBytes(StandardCharsets.UTF_8));
    return HttpRequest.BodyPublishers.ofByteArrays(byteArrays);
}
```

```java
try {
    Map<Object, Object> data = new LinkedHashMap<>();
    data.put("file", localFile);
    String boundary = new BigInteger(256, new Random()).toString();

    request = HttpRequest.newBuilder()
        .header("Content-Type", "multipart/form-data;boundary=" + boundary)
        .POST(ofMimeMultipartData(data, boundary))
        .uri(URI.create("https://..."))
        .build();

    HttpResponse<String> vtResponse = client.send(request, HttpResponse.BodyHandlers.ofString());

    try (JsonReader jsonReader = Json.createReader(new StringReader(vtResponse.body()))) {
        JsonObject jobj = jsonReader.readObject();
        String resource = jobj.getString("resource");
        URI uri = UrlBuilder.fromString("https://www.virustotal.com/vtapi/v2/file/report")
        .addParameter("apikey", virusTotalApiKey).addParameter("resource", resource)
        .toUri();

        HttpResponse<String> status = client.send(HttpRequest.newBuilder(uri).build(), HttpResponse.BodyHandlers.ofString());
    }
} catch (IOException io) {
    io.printStackTrace();
}
```

We've tried our best to overcome these dozens of lines of code, and here's what we've created for you.
###### cVurl multipart example 
```java
PostMapping("/photos") // we are using Spring to represent the multipart example
public ResponseEntity uploadPhoto(@RequestParam MultipartFile photo, @RequestParam String title) throws IOException {
    Response<String> response = cVurl.post(HOST + PHOTOS)
        .body(MultipartBody.create()
                .formPart("title", Part.of(title))
                .formPart("photo", Part.of(photo.getName(), photo.getContentType(), photo.getBytes())))
        .asString()
        .orElseThrow(() -> new RuntimeException("An error occurred while executing the request"));

    return ResponseEntity.status(response.status()).build();
}
```

## How to get cVurl
Well, as simple as everything else about cVurl.
 
 **Maven**
 ```xml
<dependecies>
    <dependency>
        <groupId>com.github.corese4rch</groupId>
        <artifactId>cvurl-io</artifactId>
        <version>1.5</version>
    </dependency>
</dependencies>
```
 **Gradle**
```groovy
compile group: 'com.github.corese4rch', name: 'cvurl-io', version: '1.5'
```
   
## Examples
Examples can be found on our [Wiki page](https://github.com/corese4rch/cvurl/wiki).
 
## Changelog
Please see the [changelog](https://github.com/corese4rch/cvurl/wiki/Changelog) page to see what's recently changed.
   
## License
 >  Copyright 2019-2021 Avenga. Licensed under GNU GPLv3 
  
