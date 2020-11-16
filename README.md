# {#JCV#}

JSON Content Validator (JCV) allows you to compare JSON contents with embedded validation.

[![Build Status](https://github.com/ekino/jcv/workflows/Build%20branch/badge.svg?branch=master)](https://github.com/ekino/jcv/actions?query=workflow%3A%22Build+branch%22+branch%3Amaster)
[![GitHub (pre-)release](https://img.shields.io/github/release/ekino/jcv/all.svg)](https://github.com/ekino/jcv/releases)
[![Maven Central](https://img.shields.io/maven-central/v/com.ekino.oss.jcv/jcv-core)](https://search.maven.org/search?q=g:com.ekino.oss.jcv)
[![GitHub license](https://img.shields.io/github/license/ekino/jcv.svg)](https://github.com/ekino/jcv/blob/master/LICENSE.md)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=ekino_jcv&metric=alert_status)](https://sonarcloud.io/dashboard?id=ekino_jcv)

## Table of contents

* [Summary](#summary)
* [Examples](#examples)
* [Quick start](#quick-start)
    * [Core module](#core-module)
    * [AssertJ module](#assertj-module)
    * [Hamcrest module](#hamcrest-module)
    * [WireMock module](#wiremock-module)
* [Validators](#validators)


## Summary

Make a full json content assertion using the minimum amount of code using an expected JSON content with the possibility to add custom field validation.

Make your tests light, easy to write, readable and exhaustive.

This tool is based on the very useful [JSONassert](https://github.com/skyscreamer/JSONassert) one and add the embedded validation system to assert unpredicatable values.

## Examples

The actual JSON you want to validate :
```json
{
    "field_1": "some value",
    "field_2": "3716a0cf-850e-46c3-bd97-ac1f34437c43",
    "date": "2011-12-03T10:15:30Z",
    "other_fields": [{
        "id": "2",
        "link": "https://another.url.com/my-base-path/query?param1=true"
    }, {
        "id": "1",
        "link": "https://some.url.com"
    }]
}
```

The expected JSON with embedded validation :
```json
{
   "field_1": "some value",
   "field_2": "{#uuid#}",
   "date": "{#date_time_format:iso_instant#}",
   "other_fields": [{
       "id": "1",
       "link": "{#url#}"
   }, {
       "id": "2",
       "link": "{#url_ending:query?param1=true#}"
   }]
}
```

More examples available here : [ekino/jcv-examples](https://github.com/ekino/jcv-examples)

## Quick start

### Core module

JCV add a new JSONComparator implementation to make the possibility to use validators inside the expected JSON,
so it requires the [JSONassert](https://github.com/skyscreamer/JSONassert) dependency to work.

#### Dependencies

Maven
```xml
<dependencies>
  ...
  <dependency>
    <groupId>org.skyscreamer</groupId>
    <artifactId>jsonassert</artifactId>
    <version>1.5.0</version>
    <scope>test</scope>
  </dependency>
  <dependency>
    <groupId>com.ekino.oss.jcv</groupId>
    <artifactId>jcv-core</artifactId>
    <version>1.5.0</version>
    <scope>test</scope>
  </dependency>
  ...
</dependencies>
```

Gradle
```groovy
dependencies {
  ...
  testImplementation 'org.skyscreamer:jsonassert:1.5.0'
  testImplementation 'com.ekino.oss.jcv:jcv-core:1.5.0'
  ...
}
```

Note:

Do not forget to add the maven snapshots repository for SNAPSHOT versions :

Maven
```xml
<repositories>
  ...
  <repository>
    <id>maven-snapshots</id>
    <url>http://oss.sonatype.org/content/repositories/snapshots</url>
    <layout>default</layout>
    <releases>
      <enabled>false</enabled>
    </releases>
    <snapshots>
      <enabled>true</enabled>
    </snapshots>
  </repository>
  ...
</repositories>
```

Gradle
```groovy
repositories {
    maven {
        url = uri("https://oss.sonatype.org/content/repositories/snapshots/")
    }
}
```

### AssertJ module

A JCV module that supports [AssertJ](https://github.com/joel-costigliola/assertj-core).

#### Example

```java
import static com.ekino.oss.jcv.assertion.assertj.JsonCompareAssert.*;

@Test
void testContainsValidator() throws JSONException {
    assertThatJson("{\"field_name\": \"hello world!\"}")
        .isValidAgainst("{\"field_name\": \"{#contains:llo wor#}\"}");
}
```

#### Dependencies

Maven
```xml
<dependencies>
  ...
  <dependency>
    <groupId>org.skyscreamer</groupId>
    <artifactId>jsonassert</artifactId>
    <version>1.5.0</version>
    <scope>test</scope>
  </dependency>
  <dependency>
    <groupId>org.assertj</groupId>
    <artifactId>assertj-core</artifactId>
    <version>3.9.1</version>
    <scope>test</scope>
  </dependency>
  <dependency>
    <groupId>com.ekino.oss.jcv</groupId>
    <artifactId>jcv-assertj</artifactId>
    <version>1.5.0</version>
    <scope>test</scope>
  </dependency>
  ...
</dependencies>
```

Gradle
```groovy
dependencies {
  ...
  testImplementation 'org.skyscreamer:jsonassert:1.5.0'
  testImplementation 'org.assertj:assertj-core:3.9.1'
  testImplementation 'com.ekino.oss.jcv:jcv-assertj:1.5.0'
  ...
}
```

### Hamcrest module

A JCV module that supports [Hamcrest](https://github.com/hamcrest/JavaHamcrest).

#### Example

```java
import static com.ekino.oss.jcv.assertion.hamcrest.JsonMatchers.*;
import static org.hamcrest.MatcherAssert.*;

@Test
void testContainsValidator() throws JSONException {
    assertThat(
        "{\"field_name\": \"hello world!\"}",
        jsonMatcher("{\"field_name\": \"{#contains:llo wor#}\"}")
    );
}
```

#### Dependencies

Maven
```xml
<dependencies>
  ...
  <dependency>
    <groupId>org.skyscreamer</groupId>
    <artifactId>jsonassert</artifactId>
    <version>1.5.0</version>
    <scope>test</scope>
  </dependency>
  <dependency>
    <groupId>org.hamcrest</groupId>
    <artifactId>hamcrest</artifactId>
    <version>2.1</version>
    <scope>test</scope>
  </dependency>
  <dependency>
    <groupId>com.ekino.oss.jcv</groupId>
    <artifactId>jcv-hamcrest</artifactId>
    <version>1.5.0</version>
    <scope>test</scope>
  </dependency>
  ...
</dependencies>
```

Gradle
```groovy
dependencies {
  ...
  testImplementation 'org.skyscreamer:jsonassert:1.5.0'
  testImplementation 'org.hamcrest:hamcrest:2.1'
  testImplementation 'com.ekino.oss.jcv:jcv-hamcrest:1.5.0'
  ...
}
```

### WireMock module

A JCV module extension for [WireMock](http://wiremock.org/).

#### Example

Register the extension on your WireMock server:
```kotlin
import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.core.WireMockConfiguration
import com.ekino.oss.jcv.assertion.wiremock.JcvRequestMatcherExtension

private val wireMockServer by lazy {
  WireMockServer(
    WireMockConfiguration()
      .dynamicPort()
      .extensions(JcvRequestMatcherExtension())
  )
}
```

Then you can stub via code:
```kotlin
import com.github.tomakehurst.wiremock.client.WireMock.aResponse
import com.github.tomakehurst.wiremock.client.WireMock.post
import com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo
import com.ekino.oss.jcv.assertion.wiremock.JcvRequestMatcherExtension

wireMockServer.stubFor(
  post(urlEqualTo("/some_url"))
    .andMatching(
      JcvRequestMatcherExtension.toRequestMatcher(
        json =
        """
        {
          "some_field": "{#not_empty#}"
        }
        """
      )
    )
    .willReturn(
      aResponse()
        .withHeader("Content-Type", "text/plain")
        .withStatus(200)
        .withBody("OK")
    )
)
```

Or you can stub via mapping file:
```json
{
  "request": {
    "method": "POST",
    "url": "/some_url",
    "customMatcher": {
      "name": "equalToJcv",
      "parameters": {
        "json": {
          "some_field": "{#not_empty#}"
        },
        "ignoreArrayOrder": true,
        "ignoreExtraElements": false
      }
    }
  },
  "response": {
    "status": 200,
    "body": "OK"
  }
}
```

#### Dependencies

Maven
```xml
<dependencies>
  ...
  <dependency>
    <groupId>org.skyscreamer</groupId>
    <artifactId>jsonassert</artifactId>
    <version>1.5.0</version>
    <scope>test</scope>
  </dependency>
  <dependency>
    <groupId>com.github.tomakehurst</groupId>
    <artifactId>wiremock-jre8</artifactId>
    <version>2.27.2</version>
    <scope>test</scope>
  </dependency>
  <dependency>
    <groupId>com.ekino.oss.jcv</groupId>
    <artifactId>jcv-wiremock</artifactId>
    <version>1.5.0</version>
    <scope>test</scope>
  </dependency>
  ...
</dependencies>
```

Gradle
```groovy
dependencies {
  ...
  testImplementation 'org.skyscreamer:jsonassert:1.5.0'
  testImplementation 'com.github.tomakehurst:wiremock-jre8:2.27.2'
  testImplementation 'com.ekino.oss.jcv:jcv-wiremock:1.5.0'
  ...
}
```

## Learn more

You will find more information (validators, guided tours...) in the [Wiki](https://github.com/ekino/jcv/wiki).
