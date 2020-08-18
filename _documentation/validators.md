---
title: Predefined validators
---

# {{ page.title }}

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

## contains

### Parameters

1. (required) the text to search for

### Examples

{% capture example_contains %}
import com.ekino.oss.jcv.assertion.assertj.JsonCompareAssert.Companion.assertThatJson
import org.junit.Test

class ValidatorsExampleTest {

@Test
fun `validator contains`() {

//sampleStart
val actualJson = &quot;&quot;&quot;
{
  &quot;field_name&quot;: &quot;Hello world!&quot;
}
&quot;&quot;&quot;
val expectedJson = &quot;&quot;&quot;
{
  &quot;field_name&quot;: &quot;{#contains:llo wor#}&quot;  
}
&quot;&quot;&quot;
//sampleEnd

assertThatJson(actualJson.trimIndent()).isValidAgainst(expectedJson.trimIndent())
}
}
{% endcapture %}

{% include try-jcv.html content=example_contains %}


[back to top ▲](#top)

## starts_with

### Parameters

1. (required) the text to search for

### Examples

{% capture example_starts_with %}
import com.ekino.oss.jcv.assertion.assertj.JsonCompareAssert.Companion.assertThatJson
import org.junit.Test

class ValidatorsExampleTest {

@Test
fun `validator starts_with`() {

//sampleStart
val actualJson = &quot;&quot;&quot;
{
  &quot;field_name&quot;: &quot;Hello world!&quot;
}
&quot;&quot;&quot;
val expectedJson = &quot;&quot;&quot;
{
  &quot;field_name&quot;: &quot;{#starts_with:Hello#}&quot;
}
&quot;&quot;&quot;
//sampleEnd

assertThatJson(actualJson.trimIndent()).isValidAgainst(expectedJson.trimIndent())
}
}
{% endcapture %}

{% include try-jcv.html content=example_starts_with %}

[back to top ▲](#top)

## ends_with

### Parameters

1. (required) the text to search for

### Examples

{% capture example_ends_with %}
import com.ekino.oss.jcv.assertion.assertj.JsonCompareAssert.Companion.assertThatJson
import org.junit.Test

class ValidatorsExampleTest {

@Test
fun `validator ends_with`() {

//sampleStart
val actualJson = &quot;&quot;&quot;
{
  &quot;field_name&quot;: &quot;Hello world!&quot;
}
&quot;&quot;&quot;
val expectedJson = &quot;&quot;&quot;
{
  &quot;field_name&quot;: &quot;{#ends_with:world!#}&quot;
}
&quot;&quot;&quot;
//sampleEnd

assertThatJson(actualJson.trimIndent()).isValidAgainst(expectedJson.trimIndent())
}
}
{% endcapture %}

{% include try-jcv.html content=example_ends_with %}

[back to top ▲](#top)

## regex

### Parameters

1. (required) the regex pattern

### Examples

{% capture example_regex %}
import com.ekino.oss.jcv.assertion.assertj.JsonCompareAssert.Companion.assertThatJson
import org.junit.Test

class ValidatorsExampleTest {

@Test
fun `validator regex`() {

//sampleStart
val actualJson = &quot;&quot;&quot;
{
  &quot;field_name&quot;: &quot;Hellowurld !!!&quot;
}
&quot;&quot;&quot;
val expectedJson = &quot;&quot;&quot;
{
  &quot;field_name&quot;: &quot;{#regex:.*llo ?w.r.*#}&quot;
}
&quot;&quot;&quot;
//sampleEnd

assertThatJson(actualJson.trimIndent()).isValidAgainst(expectedJson.trimIndent())
}
}
{% endcapture %}

{% include try-jcv.html content=example_regex %}

[back to top ▲](#top)

## uuid

### Examples

{% capture example_uuid %}
import com.ekino.oss.jcv.assertion.assertj.JsonCompareAssert.Companion.assertThatJson
import org.junit.Test

class ValidatorsExampleTest {

@Test
fun `validator uuid`() {

//sampleStart
val actualJson = &quot;&quot;&quot;
{
  &quot;field_name&quot;: &quot;8525aa57-d491-41e2-b065-013aaacb24f7&quot;
}
&quot;&quot;&quot;
val expectedJson = &quot;&quot;&quot;
{
  &quot;field_name&quot;: &quot;{#uuid#}&quot;
}
&quot;&quot;&quot;
//sampleEnd

assertThatJson(actualJson.trimIndent()).isValidAgainst(expectedJson.trimIndent())
}
}
{% endcapture %}

{% include try-jcv.html content=example_uuid %}

[back to top ▲](#top)

## not_null

### Examples

{% capture example_not_null %}
import com.ekino.oss.jcv.assertion.assertj.JsonCompareAssert.Companion.assertThatJson
import org.junit.Test

class ValidatorsExampleTest {

@Test
fun `validator not_null`() {

//sampleStart
val actualJson = &quot;&quot;&quot;
{
  &quot;field_name&quot;: true
}
&quot;&quot;&quot;
val expectedJson = &quot;&quot;&quot;
{
  &quot;field_name&quot;: &quot;{#not_null#}&quot;
}
&quot;&quot;&quot;
//sampleEnd

assertThatJson(actualJson.trimIndent()).isValidAgainst(expectedJson.trimIndent())
}
}
{% endcapture %}

{% include try-jcv.html content=example_not_null %}

[back to top ▲](#top)

## not_empty

### Examples

{% capture example_not_empty %}
import com.ekino.oss.jcv.assertion.assertj.JsonCompareAssert.Companion.assertThatJson
import org.junit.Test

class ValidatorsExampleTest {

@Test
fun `validator not_empty`() {

//sampleStart
val actualJson = &quot;&quot;&quot;
{
  &quot;field_name&quot;: &quot; &quot;
}
&quot;&quot;&quot;
val expectedJson = &quot;&quot;&quot;
{
  &quot;field_name&quot;: &quot;{#not_empty#}&quot;
}
&quot;&quot;&quot;
//sampleEnd

assertThatJson(actualJson.trimIndent()).isValidAgainst(expectedJson.trimIndent())
}
}
{% endcapture %}

{% include try-jcv.html content=example_not_empty %}

[back to top ▲](#top)

## boolean_type

### Examples

{% capture example_boolean_type %}
import com.ekino.oss.jcv.assertion.assertj.JsonCompareAssert.Companion.assertThatJson
import org.junit.Test

class ValidatorsExampleTest {

@Test
fun `validator boolean_type`() {

//sampleStart
val actualJson = &quot;&quot;&quot;
{  
  &quot;field_name&quot;: true
}
&quot;&quot;&quot;
val expectedJson = &quot;&quot;&quot;
{
  &quot;field_name&quot;: &quot;{#boolean_type#}&quot;
}
&quot;&quot;&quot;
//sampleEnd

assertThatJson(actualJson.trimIndent()).isValidAgainst(expectedJson.trimIndent())
}
}
{% endcapture %}

{% include try-jcv.html content=example_boolean_type %}

[back to top ▲](#top)

## string_type

### Examples

{% capture example_string_type %}
import com.ekino.oss.jcv.assertion.assertj.JsonCompareAssert.Companion.assertThatJson
import org.junit.Test

class ValidatorsExampleTest {

@Test
fun `validator string_type`() {

//sampleStart
val actualJson = &quot;&quot;&quot;
{
  &quot;field_name&quot;: &quot;some text&quot;
}
&quot;&quot;&quot;
val expectedJson = &quot;&quot;&quot;
{
  &quot;field_name&quot;: &quot;{#string_type#}&quot;
}
&quot;&quot;&quot;
//sampleEnd

assertThatJson(actualJson.trimIndent()).isValidAgainst(expectedJson.trimIndent())
}
}
{% endcapture %}

{% include try-jcv.html content=example_string_type %}

[back to top ▲](#top)

## number_type

### Examples

{% capture example_number_type %}
import com.ekino.oss.jcv.assertion.assertj.JsonCompareAssert.Companion.assertThatJson
import org.junit.Test

class ValidatorsExampleTest {

@Test
fun `validator number_type`() {

//sampleStart
val actualJson = &quot;&quot;&quot;
{  
  &quot;field_name&quot;: 123.45
}
&quot;&quot;&quot;
val expectedJson = &quot;&quot;&quot;
{
  &quot;field_name&quot;: &quot;{#number_type#}&quot;
}
&quot;&quot;&quot;
//sampleEnd

assertThatJson(actualJson.trimIndent()).isValidAgainst(expectedJson.trimIndent())
}
}
{% endcapture %}

{% include try-jcv.html content=example_number_type %}

[back to top ▲](#top)

## array_type

### Examples

{% capture example_array_type %}
import com.ekino.oss.jcv.assertion.assertj.JsonCompareAssert.Companion.assertThatJson
import org.junit.Test

class ValidatorsExampleTest {

@Test
fun `validator array_type`() {

//sampleStart
val actualJson = &quot;&quot;&quot;
{
  &quot;field_name&quot;: [&quot;Value 1&quot;, &quot;Value 2&quot;]
}
&quot;&quot;&quot;
val expectedJson = &quot;&quot;&quot;
{
  &quot;field_name&quot;: &quot;{#array_type#}&quot;
}
&quot;&quot;&quot;
//sampleEnd

assertThatJson(actualJson.trimIndent()).isValidAgainst(expectedJson.trimIndent())
}
}
{% endcapture %}

{% include try-jcv.html content=example_array_type %}

[back to top ▲](#top)

## object_type

### Examples

{% capture example_object_type %}
import com.ekino.oss.jcv.assertion.assertj.JsonCompareAssert.Companion.assertThatJson
import org.junit.Test

class ValidatorsExampleTest {

@Test
fun `validator object_type`() {

//sampleStart
val actualJson = &quot;&quot;&quot;
{
  &quot;field_name&quot;: { &quot;some_sub_field&quot;: &quot;some value&quot; }
}
&quot;&quot;&quot;
val expectedJson = &quot;&quot;&quot;
{
  &quot;field_name&quot;: &quot;{#object_type#}&quot;
}
&quot;&quot;&quot;
//sampleEnd

assertThatJson(actualJson.trimIndent()).isValidAgainst(expectedJson.trimIndent())
}
}
{% endcapture %}

{% include try-jcv.html content=example_object_type %}

[back to top ▲](#top)

## url

### Examples

{% capture example_url %}
import com.ekino.oss.jcv.assertion.assertj.JsonCompareAssert.Companion.assertThatJson
import org.junit.Test

class ValidatorsExampleTest {

@Test
fun `validator url`() {

//sampleStart
val actualJson = &quot;&quot;&quot;
{
  &quot;field_name&quot;: &quot;http://some.url:9999/path?param&quot;
}
&quot;&quot;&quot;
val expectedJson = &quot;&quot;&quot;
{
  &quot;field_name&quot;: &quot;{#url#}&quot;
}
&quot;&quot;&quot;
//sampleEnd

assertThatJson(actualJson.trimIndent()).isValidAgainst(expectedJson.trimIndent())
}
}
{% endcapture %}

{% include try-jcv.html content=example_url %}

[back to top ▲](#top)

## url_ending

### Parameters

1. (required) url ending

### Examples

{% capture example_url_ending %}
import com.ekino.oss.jcv.assertion.assertj.JsonCompareAssert.Companion.assertThatJson
import org.junit.Test

class ValidatorsExampleTest {

@Test
fun `validator url_ending`() {

//sampleStart
val actualJson = &quot;&quot;&quot;
{
  &quot;field_name&quot;: &quot;http://some.url:9999/path?param&quot;
}
&quot;&quot;&quot;
val expectedJson = &quot;&quot;&quot;
{
  &quot;field_name&quot;: &quot;{#url_ending:/path?param#}&quot;
}
&quot;&quot;&quot;
//sampleEnd

assertThatJson(actualJson.trimIndent()).isValidAgainst(expectedJson.trimIndent())
}
}
{% endcapture %}

{% include try-jcv.html content=example_url_ending %}

[back to top ▲](#top)

## url_regex

### Parameters

1. (required) regex pattern

### Examples

{% capture example_url_regex %}
import com.ekino.oss.jcv.assertion.assertj.JsonCompareAssert.Companion.assertThatJson
import org.junit.Test

class ValidatorsExampleTest {

@Test
fun `validator url_regex`() {

//sampleStart
val actualJson = &quot;&quot;&quot;
{
  &quot;field_name&quot;: &quot;http://some.url:9999/path?param&quot;
}
&quot;&quot;&quot;
val expectedJson = &quot;&quot;&quot;
{
  &quot;field_name&quot;: &quot;{#url_regex:^.+some\\.url.+/path\\?param$#}&quot;
}
&quot;&quot;&quot;
//sampleEnd

assertThatJson(actualJson.trimIndent()).isValidAgainst(expectedJson.trimIndent())
}
}
{% endcapture %}

{% include try-jcv.html content=example_url_regex %}

[back to top ▲](#top)

## templated_url

### Examples

{% capture example_templated_url %}
import com.ekino.oss.jcv.assertion.assertj.JsonCompareAssert.Companion.assertThatJson
import org.junit.Test

class ValidatorsExampleTest {

@Test
fun `validator templated_url`() {

//sampleStart
val actualJson = &quot;&quot;&quot;
{
  &quot;field_name&quot;: &quot;http://some.url:9999/path{?param}&quot;
}
&quot;&quot;&quot;
val expectedJson = &quot;&quot;&quot;
{
  &quot;field_name&quot;: &quot;{#templated_url#}&quot;
}
&quot;&quot;&quot;
//sampleEnd

assertThatJson(actualJson.trimIndent()).isValidAgainst(expectedJson.trimIndent())
}
}
{% endcapture %}

{% include try-jcv.html content=example_templated_url %}

[back to top ▲](#top)

## templated_url_ending

### Parameters

1. (required) templated url ending

### Examples

{% capture example_templated_url_ending %}
import com.ekino.oss.jcv.assertion.assertj.JsonCompareAssert.Companion.assertThatJson
import org.junit.Test

class ValidatorsExampleTest {

@Test
fun `validator templated_url_ending`() {

//sampleStart
val actualJson = &quot;&quot;&quot;
{
  &quot;field_name&quot;: &quot;http://some.url:9999/path{?param}&quot;
}
&quot;&quot;&quot;
val expectedJson = &quot;&quot;&quot;
{
  &quot;field_name&quot;: &quot;{#templated_url_ending:/path{?param}#}&quot;
}
&quot;&quot;&quot;
//sampleEnd

assertThatJson(actualJson.trimIndent()).isValidAgainst(expectedJson.trimIndent())
}
}
{% endcapture %}

{% include try-jcv.html content=example_templated_url_ending %}

[back to top ▲](#top)

## templated_url_regex

### Parameters

1. (required) regex pattern

### Examples

{% capture example_templated_url_regex %}
import com.ekino.oss.jcv.assertion.assertj.JsonCompareAssert.Companion.assertThatJson
import org.junit.Test

class ValidatorsExampleTest {

@Test
fun `validator templated_url_regex`() {

//sampleStart
val actualJson = &quot;&quot;&quot;
{
  &quot;field_name&quot;: &quot;http://some.url:9999/path{?param}&quot;
}
&quot;&quot;&quot;
val expectedJson = &quot;&quot;&quot;
{
  &quot;field_name&quot;: &quot;{#templated_url_regex:^.+some\\.url.+/path\\{\\?param\\}$#}&quot;
}
&quot;&quot;&quot;
//sampleEnd

assertThatJson(actualJson.trimIndent()).isValidAgainst(expectedJson.trimIndent())
}
}
{% endcapture %}

{% include try-jcv.html content=example_templated_url_regex %}

[back to top ▲](#top)

## date_time_format

### Parameters

1. (required) the date time pattern (a [predefined patterns](#predefined-patterns) or a [custom pattern](#custom-pattern-symbols))
2. (optional) the language tag ([IETF BCP 47](https://tools.ietf.org/rfc/bcp/bcp47.txt)) for the custom pattern

### Examples

{% capture example_date_time_format %}
import com.ekino.oss.jcv.assertion.assertj.JsonCompareAssert.Companion.assertThatJson
import org.junit.Test

class ValidatorsExampleTest {

@Test
fun `validator date_time_format`() {

//sampleStart
val actualJson = &quot;&quot;&quot;
{
  &quot;date_time_predefined_format&quot;: &quot;10:15:30+01:00&quot;,
  &quot;date_time_format&quot;: &quot;3 Feb 2011&quot;,
  &quot;date_time_format_with_locale&quot;: &quot;3 f&eacute;vr. 2011&quot;
}
&quot;&quot;&quot;
val expectedJson = &quot;&quot;&quot;
{
  &quot;date_time_predefined_format&quot;: &quot;{#date_time_format:iso_time#}&quot;,
  &quot;date_time_format&quot;: &quot;{#date_time_format:d MMM uuu#}&quot;,
  &quot;date_time_format_with_locale&quot;: &quot;{#date_time_format:d MMM uuu;fr-FR#}&quot;
}
&quot;&quot;&quot;
//sampleEnd

assertThatJson(actualJson.trimIndent()).isValidAgainst(expectedJson.trimIndent())
}
}
{% endcapture %}

{% include try-jcv.html content=example_date_time_format %}

[back to top ▲](#top)

### Predefined patterns

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

### Custom pattern symbols

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

[back to top ▲](#top)

<script src="https://unpkg.com/kotlin-playground@1"
        data-selector=".kotlin-code"
        data-server="https://kotlin-jcv-compiler-server.herokuapp.com"
        data-version="1.4.0">
</script>
