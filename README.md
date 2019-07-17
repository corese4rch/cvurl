[![GNU License](https://img.shields.io/badge/license-GNU%20GPL%20v3-green.svg)](https://github.com/corese4rch/cvurl/blob/master/LICENSE)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.github.corese4rch/cvurl-io/badge.svg)](https://search.maven.org/search?q=g:com.github.corese4rch%20AND%20a:cvurl-io)

## cVurl is an open-source wrapper for an [Java HTTP/2 Client](https://openjdk.java.net/groups/net/httpclient/intro.html). 
<pre>

          oooooo     oooo                      oooo  
           `888.     .8'                       `888  
 .ooooo.    `888.   .8'   oooo  oooo  oooo d8b  888  
d88' `"Y8    `888. .8'    `888  `888  `888""8P  888  
888           `888.8'      888   888   888      888  
888   .o8      `888'       888   888   888      888  
`Y8bod8P'       `8'        `V88V"V8P' d888b    o888o    
                                                  
</pre>

## Requirements 
 **cVurl** is written in java11 and can be used with any jdk11.0.2 and higher
 (mostly because of this [issue](https://bugs.openjdk.java.net/browse/JDK-8211437))
 
## Dependencies
 **cVurl** is made purely with Java 11 and one of our goals was to bring as less dependencies to your project as possible.
 The only dependencies that will be added to your project are:
 
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
 So, there are nothing hidden from you or transitive. If You decide to use smth different from [Jackson](https://github.com/FasterXML/jackson),
 You shouldn't be worried about first dependency. 
 On the other hand, if You do use Jackson us should just leave it as a dependency, we will crete default ObjectMapper for You under the hood.
 And, with high chance, You, most likely, will use some logging library so [Slf4j](https://github.com/qos-ch/slf4j) shouldn't be an unexpected resident in your build file.   
 
## Concept
 The idea behind this project is very simple - Make a life of developers a little bit easier.
 During our work we heavily use different HTTP clients: 
 Spring's RestTemplate, Feign, plain OkHttp3/OkHttp4, then (with switching to Java 11+) 
 we start using Java native HTTP/2 Client.
 Even though all of listed above are a great tools, every time we weren't totally happy :(.
 
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

We, at [CoreValue](https://corevalue.com.ua/) didn't like checked Exceptions at all. What we didn't like even more, that's the null checks.
So We come up with something like this:

```java
public void cvurl() {
        CVurl cvurl = new CVurl();

        //GET
        Response<String> response = cvurl.GET("https://api.imgflip.com/get_memes")
                        .build()
                        .asString()
                        .orElseThrow(RuntimeException::new);

        System.out.println("CVurl GET: " + response.getBody());
    }
```    
Looks much nicer, isn't it? :)

_What about something a little bit complicated, like a POST request?_

For a simplicity of examples below we will use this data structure:
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
        Result result = cvurl.POST("https://api.imgflip.com/caption_image")
                .queryParams(Map.of(
                        "template_id", "112126428",
                        "username", "test-user",
                        "password", "123test321",
                        "text0", "text0",
                        "text1", "text1"
                ))
                .build()
                .asObject(Result.class, HttpStatus.OK);

        System.out.println("CVurl POST: " + result);
    }
```
Simple as a pie.
Interested? We encourage You to try cVurl!

## How to get cVurl
 Well, simple as everything else about cVurl.
 
 **Maven**
 ```xml
<dependecies>
    <dependency>
        <groupId>com.github.corese4rch</groupId>
        <artifactId>cvurl-io</artifactId>
        <version>0.9</version>
    </dependency>
</dependencies>
```
 **Gradle**
```groovy
compile group: 'com.github.corese4rch', name: 'cvurl-io', version: '0.9'
```
   
## Examples
 More can be found at [example repository](https://github.com/corese4rch/cvurl-examples)
 
 Usage examples:
 * [spring-boot example](https://github.com/corese4rch/cvurl-examples/tree/master/cvurl-usage-spring-boot)
 * [plain java 11 example](https://github.com/corese4rch/cvurl-examples/tree/master/cvurl-usage-plain-java)
 * [micronaut example](https://github.com/corese4rch/cvurl-examples/tree/master/cvurl-usage-micronaut)
 * [quarkus example](https://github.com/corese4rch/cvurl-examples/tree/master/cvurl-usage-quarkus) 
 
   
## License
 >  Copyright 2019 Core Value, Inc. Licensed under GNU GPLv3 
  
