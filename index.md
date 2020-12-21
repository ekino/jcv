[![Build Status](https://github.com/ekino/jcv/workflows/Build%20branch/badge.svg?branch=master)](https://github.com/ekino/jcv/actions?query=workflow%3A%22Build+branch%22+branch%3Amaster)
[![GitHub (pre-)release](https://img.shields.io/github/release/ekino/jcv/all.svg)](https://github.com/ekino/jcv/releases)
[![Maven Central](https://img.shields.io/maven-central/v/com.ekino.oss.jcv/jcv-core)](https://search.maven.org/search?q=g:com.ekino.oss.jcv)
[![GitHub license](https://img.shields.io/github/license/ekino/jcv.svg)](https://github.com/ekino/jcv/blob/master/LICENSE.md)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=ekino_jcv&metric=alert_status)](https://sonarcloud.io/dashboard?id=ekino_jcv)

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

## Try it online!

You can try it online here : <a href="{{ '/try-jcv-online.html' | relative_url }}">Try JCV online</a>

## Best experience on IntelliJ IDEA®

You can get the JCV Assistant plugin available on IntelliJ Platforms:

<div>
    <iframe frameborder="none" width="245px" height="48px" src="https://plugins.jetbrains.com/embeddable/install/13916"></iframe>
</div>

<div>
    <a href="https://plugins.jetbrains.com/plugin/13916-jcv">
        <iframe frameborder="none" width="384px" height="319px" src="https://plugins.jetbrains.com/embeddable/card/13916"></iframe>
    </a>
</div>

<script defer src="https://plugins.jetbrains.com/assets/scripts/mp-widget.js"></script>
