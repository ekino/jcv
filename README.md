# {#JCV#}

JSON Content Validator (JCV) allows you to compare JSON contents with embedded validation.

[![Build Status](https://travis-ci.org/ekino/jcv.svg?branch=master)](https://travis-ci.org/ekino/jcv)
[![GitHub (pre-)release](https://img.shields.io/github/release/ekino/jcv/all.svg)](https://github.com/ekino/jcv/releases)
[![GitHub license](https://img.shields.io/github/license/ekino/jcv.svg)](https://github.com/ekino/jcv/blob/master/LICENSE.md)

## Table of contents

* [Summary](#summary)
* [Examples](#examples)
* [Quick start](#quick-start)
    * [Core module](#core-module)
    * [AssertJ module](#assertj-module)
    * [Hamcrest module](#hamcrest-module)
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
    <version>1.1.0-SNAPSHOT</version>
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
  testImplementation 'com.ekino.oss.jcv:jcv-core:1.1.0-SNAPSHOT'
  ...
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
    <version>1.1.0-SNAPSHOT</version>
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
  testImplementation 'com.ekino.oss.jcv:jcv-assertj:1.1.0-SNAPSHOT'
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
    <version>1.1.0-SNAPSHOT</version>
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
  testImplementation 'com.ekino.oss.jcv:jcv-hamcrest:1.1.0-SNAPSHOT'
  ...
}
```

## Validators

JCV comes with a library of pre-defined validators :

- [contains](#contains)
- [starts_with](#starts_with)
- [ends_with](#ends_with)
- [regex](#regex)
- [uuid](#uuid)
- [not_null](#not_null)
- [not_empty](#not_empty)
- [boolean_type](#boolean_type)
- [string_type](#string_type)
- [number_type](#number_type)
- [array_type](#array_type)
- [object_type](#object_type)
- [url](#url)
- [url_ending](#url_ending)
- [url_regex](#url_regex)
- [templated_url](#templated_url)
- [templated_url_ending](#templated_url_ending)
- [templated_url_regex](#templated_url_regex)
- [date_time_format](#date_time_format)

### contains

#### Parameters

1. (required) the text to search for

#### Examples

Actual JSON :
```json
{  
  "field_name": "Hello world!"
}
```

Expected JSON :
```json
{  
  "field_name": "{#contains:llo wor#}"  
}
```

### starts_with

#### Parameters

1. (required) the text to search for

#### Examples

Actual JSON :
```json
{  
  "field_name": "Hello world!"
}
```

Expected JSON :
```json
{
  "field_name": "{#starts_with:Hello#}"
}
```

### ends_with

#### Parameters

1. (required) the text to search for

#### Examples

Actual JSON :
```json
{  
  "field_name": "Hello world!"
}
```

Expected JSON :
```json
{
  "field_name": "{#ends_with:world!#}"
}
```

### regex

#### Parameters

1. (required) the regex pattern

#### Examples

Actual JSON :
```json
{  
  "field_name": "Hellowurld !!!"
}
```

Expected JSON :
```json
{
  "field_name": "{#regex:.*llo ?w.r.*#}"
}
```

### uuid

#### Examples

Actual JSON :
```json
{  
  "field_name": "8525aa57-d491-41e2-b065-013aaacb24f7"
}
```

Expected JSON :
```json
{
  "field_name": "{#uuid#}"
}
```

### not_null

#### Examples

Actual JSON :
```json
{  
  "field_name": ""
}
```

Expected JSON :
```json
{
  "field_name": "{#not_null#}"
}
```

### not_empty

#### Examples

Actual JSON :
```json
{  
  "field_name": " "
}
```

Expected JSON :
```json
{
  "field_name": "{#not_empty#}"
}
```

### boolean_type

#### Examples

Actual JSON :
```json
{  
  "field_name": true
}
```

Expected JSON :
```json
{
  "field_name": "{#boolean_type#}"
}
```

### string_type

#### Examples

Actual JSON :
```json
{  
  "field_name": "some text"
}
```

Expected JSON :
```json
{
  "field_name": "{#string_type#}"
}
```

### number_type

#### Examples

Actual JSON :
```json
{  
  "field_name": 123.45
}
```

Expected JSON :
```json
{
  "field_name": "{#number_type#}"
}
```

### array_type

#### Examples

Actual JSON :
```json
{  
  "field_name": ["Value 1", "Value 2"]
}
```

Expected JSON :
```json
{
  "field_name": "{#array_type#}"
}
```

### object_type

#### Examples

Actual JSON :
```json
{  
  "field_name": { "some_sub_field": "some value" }
}
```

Expected JSON :
```json
{
  "field_name": "{#object_type#}"
}
```

### url

#### Examples

Actual JSON :
```json
{  
  "field_name": "http://some.url:9999/path?param"
}
```

Expected JSON :
```json
{
  "field_name": "{#url#}"
}
```

### url_ending

#### Parameters

1. (required) url ending

#### Examples

Actual JSON :
```json
{  
  "field_name": "http://some.url:9999/path?param"
}
```

Expected JSON :
```json
{
  "field_name": "{#url_ending:/path?param#}"
}
```

### url_regex

#### Parameters

1. (required) regex pattern

#### Examples

Actual JSON :
```json
{  
  "field_name": "http://some.url:9999/path?param"
}
```

Expected JSON :
```json
{
  "field_name": "{#url_regex:^.+some\\.url.+/path\\?param$#}"
}
```

### templated_url

#### Examples

Actual JSON :
```json
{  
  "field_name": "http://some.url:9999/path{?param}"
}
```

Expected JSON :
```json
{
  "field_name": "{#templated_url#}"
}
```

### templated_url_ending

#### Parameters

1. (required) templated url ending

#### Examples

Actual JSON :
```json
{  
  "field_name": "http://some.url:9999/path{?param}"
}
```

Expected JSON :
```json
{
  "field_name": "{#templated_url_ending:/path{?param}#}"
}
```

### templated_url_regex

#### Parameters

1. (required) regex pattern

#### Examples

Actual JSON :
```json
{  
  "field_name": "http://some.url:9999/path{?param}"
}
```

Expected JSON :
```json
{
  "field_name": "{#templated_url_regex:^.+some\\.url.+/path\\{\\?param\\}$#}"
}
```

### date_time_format

#### Parameters

1. (required) the date time pattern (a [predefined patterns](#predefined-patterns) or a [custom pattern](#custom-pattern-symbols))
2. (optional) the language tag ([IETF BCP 47](https://tools.ietf.org/rfc/bcp/bcp47.txt)) for the custom pattern

#### Examples

Actual JSON :
```json
{
  "date_time_predefined_format": "10:15:30+01:00",
  "date_time_format": "3 Feb 2011",
  "date_time_format_with_locale": "3 févr. 2011"
}
```

Expected JSON :
```json
{
  "date_time_predefined_format": "{#date_time_format:iso_time#}",
  "date_time_format": "{#date_time_format:d MMM uuu#}",
  "date_time_format_with_locale": "{#date_time_format:d MMM uuu;fr-FR#}"
}
```

#### Predefined patterns

<table>
<thead>
<tr>
    <th>Pattern name</th>
    <th>Description</th>
    <th>Example</th>
</tr>
</thead>
<tbody>
<tr>
    <td><code>basic_iso_date</code></td>
    <td>Basic ISO date</td>
    <td><code>20111203</code></td>
</tr>
<tr>
    <td><code>iso_local_date</code></td>
    <td>ISO Local Date</td>
    <td><code>2011-12-03</code></td>
</tr>
<tr>
    <td><code>iso_offset_date</code></td>
    <td>ISO Date with offset</td>
    <td><code>2011-12-03+01:00</code></td>
</tr>
<tr>
    <td><code>iso_date</code></td>
    <td>ISO Date with or without offset '2011-12-03+01:00';</td>
    <td><code>2011-12-03</code></td>
</tr>
<tr>
    <td><code>iso_local_time</code></td>
    <td>Time without offset</td>
    <td><code>10:15:30</code></td>
</tr>
<tr>
    <td><code>iso_offset_time</code></td>
    <td>Time with offset</td>
    <td><code>10:15:30+01:00</code></td>
</tr>
<tr>
    <td><code>iso_time</code></td>
    <td>Time with or without offset '10:15:30+01:00';</td>
    <td><code>10:15:30</code></td>
</tr>
<tr>
    <td><code>iso_local_date_time</code></td>
    <td>ISO Local Date and Time</td>
    <td><code>2011-12-03T10:15:30</code></td>
</tr>
<tr>
    <td><code>iso_offset_date_time</code></td>
    <td>Date Time with Offset</td>
    <td><code>2011-12-03T10:15:30+01:00</code></td>
</tr>
<tr>
    <td><code>iso_zoned_date_time</code></td>
    <td>Zoned Date Time</td>
    <td><code>2011-12-03T10:15:30+01:00[Europe/Paris]</code></td>
</tr>
<tr>
    <td><code>iso_date_time</code></td>
    <td>Date and time with ZoneId</td>
    <td><code>2011-12-03T10:15:30+01:00[Europe/Paris]</code></td>
</tr>
<tr>
    <td><code>iso_ordinal_date</code></td>
    <td>Year and day of year</td>
    <td><code>2012-337</code></td>
</tr>
<tr>
    <td><code>iso_week_date</code></td>
    <td>Year and Week</td>
    <td><code>2012-W48-6</code></td>
</tr>
<tr>
    <td><code>iso_instant</code></td>
    <td>Date and Time of an Instant</td>
    <td><code>2011-12-03T10:15:30Z</code></td>
</tr>
<tr>
    <td><code>rfc_1123_date_time</code></td>
    <td>RFC 1123 / RFC 822</td>
    <td><code>Tue, 3 Jun 2008 11:05:30 GMT</code></td>
</tr>
</tbody>
</table>

#### Custom pattern symbols

<table>
<thead>
<tr>
    <th>Symbol</th>
    <th>Meaning</th>
    <th>Presentation</th>
    <th>Examples</th>
</tr>
</thead>
<tbody>
<tr>
    <td><code>G</code></td>
    <td>era</td>
    <td>text</td>
    <td><code>AD; Anno Domini; A</code></td>
</tr>
<tr>
    <td><code>u</code></td>
    <td>year</td>
    <td>year</td>
    <td><code>2004; 04</code></td>
</tr>
<tr>
    <td><code>y</code></td>
    <td>year-of-era</td>
    <td>year</td>
    <td><code>2004; 04</code></td>
</tr>
<tr>
    <td><code>D</code></td>
    <td>day-of-year</td>
    <td>number</td>
    <td><code>189</code></td>
</tr>
<tr>
    <td><code>M/L</code></td>
    <td>month-of-year</td>
    <td>number/text</td>
    <td><code>7; 07; Jul; July; J</code></td>
</tr>
<tr>
    <td><code>d</code></td>
    <td>day-of-month</td>
    <td>number</td>
    <td><code>10</code></td>
</tr>
<tr>
    <td><code>Q/q</code></td>
    <td>quarter-of-year</td>
    <td>number/text</td>
    <td><code>3; 03; Q3; 3rd quarter</code></td>
</tr>
<tr>
    <td><code>Y</code></td>
    <td>week-based-year</td>
    <td>year</td>
    <td><code>1996; 96</code></td>
</tr>
<tr>
    <td><code>w</code></td>
    <td>week-of-week-based-year</td>
    <td>number</td>
    <td><code>27</code></td>
</tr>
<tr>
    <td><code>W</code></td>
    <td>week-of-month</td>
    <td>number</td>
    <td><code>4</code></td>
</tr>
<tr>
    <td><code>E</code></td>
    <td>day-of-week</td>
    <td>text</td>
    <td><code>Tue; Tuesday; T</code></td>
</tr>
<tr>
    <td><code>e/c</code></td>
    <td>localized day-of-week</td>
    <td>number/text</td>
    <td><code>2; 02; Tue; Tuesday; T</code></td>
</tr>
<tr>
    <td><code>F</code></td>
    <td>week-of-month</td>
    <td>number</td>
    <td><code>3</code></td>
</tr>
<tr>
    <td><code>a</code></td>
    <td>am-pm-of-day</td>
    <td>text</td>
    <td><code>PM</code></td>
</tr>
<tr>
    <td><code>h</code></td>
    <td>clock-hour-of-am-pm (1-12)</td>
    <td>number</td>
    <td><code>12</code></td>
</tr>
<tr>
    <td><code>K</code></td>
    <td>hour-of-am-pm (0-11)</td>
    <td>number</td>
    <td><code>0</code></td>
</tr>
<tr>
    <td><code>k</code></td>
    <td>clock-hour-of-am-pm (1-24)</td>
    <td>number</td>
    <td><code>0</code></td>
</tr>
<tr>
    <td><code>H</code></td>
    <td>hour-of-day (0-23)</td>
    <td>number</td>
    <td><code>0</code></td>
</tr>
<tr>
    <td><code>m</code></td>
    <td>minute-of-hour</td>
    <td>number</td>
    <td><code>30</code></td>
</tr>
<tr>
    <td><code>s</code></td>
    <td>second-of-minute</td>
    <td>number</td>
    <td><code>55</code></td>
</tr>
<tr>
    <td><code>S</code></td>
    <td>fraction-of-second</td>
    <td>fraction</td>
    <td><code>978</code></td>
</tr>
<tr>
    <td><code>A</code></td>
    <td>milli-of-day</td>
    <td>number</td>
    <td><code>1234</code></td>
</tr>
<tr>
    <td><code>n</code></td>
    <td>nano-of-second</td>
    <td>number</td>
    <td><code>987654321</code></td>
</tr>
<tr>
    <td><code>N</code></td>
    <td>nano-of-day</td>
    <td>number</td>
    <td><code>1234000000</code></td>
</tr>
<tr>
    <td><code>V</code></td>
    <td>time-zone ID</td>
    <td>zone-id</td>
    <td><code>America/Los_Angeles; Z; -08:30</code></td>
</tr>
<tr>
    <td><code>z</code></td>
    <td>time-zone name</td>
    <td>zone-name</td>
    <td><code>Pacific Standard Time; PST</code></td>
</tr>
<tr>
    <td><code>O</code></td>
    <td>localized zone-offset</td>
    <td>offset-O</td>
    <td><code>GMT+8; GMT+08:00; UTC-08:00;</code></td>
</tr>
<tr>
    <td><code>X</code></td>
    <td>zone-offset 'Z' for zero</td>
    <td>offset-X</td>
    <td><code>Z; -08; -0830; -08:30; -083015; -08:30:15;</code></td>
</tr>
<tr>
    <td><code>x</code></td>
    <td>zone-offset</td>
    <td>offset-x</td>
    <td><code>+0000; -08; -0830; -08:30; -083015; -08:30:15;</code></td>
</tr>
<tr>
    <td><code>Z</code></td>
    <td>zone-offset</td>
    <td>offset-Z</td>
    <td><code>+0000; -0800; -08:00;</code></td>
</tr>
<tr>
    <td><code>p</code></td>
    <td>pad next</td>
    <td>pad modifier</td>
    <td><code>1</code></td>
</tr>
<tr>
    <td><code>'</code></td>
    <td>escape for text</td>
    <td>delimiter</td>
    <td><code></code></td>
</tr>
<tr>
    <td><code>''</code></td>
    <td>single quote</td>
    <td>literal</td>
    <td><code>'</code></td>
</tr>
<tr>
    <td><code>[</code></td>
    <td>optional section start</td>
    <td></td>
    <td></td>
</tr>
<tr>
    <td><code>]</code></td>
    <td>optional section end</td>
    <td></td>
    <td></td>
</tr>
<tr>
    <td><code>#</code></td>
    <td>reserved for future use</td>
    <td></td>
    <td></td>
</tr>
<tr>
    <td><code>{</code></td>
    <td>reserved for future use</td>
    <td></td>
    <td></td>
</tr>
<tr>
    <td><code>}</code></td>
    <td>reserved for future use</td>
    <td></td>
    <td></td>
</tr>
</tbody>
</table>
