---
title: Installation
---
# {{ page.title }}

## Core module

JCV add a new JSONComparator implementation to make the possibility to use validators inside the expected JSON,
so it requires the [JSONassert](https://github.com/skyscreamer/JSONassert) dependency to work.

### Dependencies

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
    <version>1.4.1</version>
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
  testImplementation 'com.ekino.oss.jcv:jcv-core:1.4.1'
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

## AssertJ module

A JCV module that supports [AssertJ](https://github.com/joel-costigliola/assertj-core).

### Example

```java
import static com.ekino.oss.jcv.assertion.assertj.JsonCompareAssert.*;

@Test
void testContainsValidator() throws JSONException {
    assertThatJson("{\"field_name\": \"hello world!\"}")
        .isValidAgainst("{\"field_name\": \"{#contains:llo wor#}\"}");
}
```

### Dependencies

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
    <version>1.4.1</version>
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
  testImplementation 'com.ekino.oss.jcv:jcv-assertj:1.4.1'
  ...
}
```

## Hamcrest module

A JCV module that supports [Hamcrest](https://github.com/hamcrest/JavaHamcrest).

### Example

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

### Dependencies

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
    <version>1.4.1</version>
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
  testImplementation 'com.ekino.oss.jcv:jcv-hamcrest:1.4.1'
  ...
}
```
