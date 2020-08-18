---
title: Try JCV online
---

# {{ page.title }}

{% capture example_1 %}
import com.ekino.oss.jcv.assertion.assertj.JsonCompareAssert.Companion.assertThatJson
import org.junit.Test

class Example1Test {

    @Test
    fun `should validate actual JSON against the expected one`() {

//sampleStart
        val actualJson = &quot;&quot;&quot;
            {
              &quot;field_1&quot;: &quot;some value&quot;,
              &quot;field_2&quot;: &quot;3716a0cf-850e-46c3-bd97-ac1f34437c43&quot;,
              &quot;date&quot;: &quot;2011-12-03T10:15:30Z&quot;,
              &quot;other_fields&quot;: [{
                &quot;id&quot;: &quot;2&quot;,
                &quot;link&quot;: &quot;https://another.url.com/my-base-path/query?param1=true&quot;
              }, {
                &quot;id&quot;: &quot;1&quot;,
                &quot;link&quot;: &quot;https://some.url.com&quot;
              }]
            }
            &quot;&quot;&quot;.trimIndent()

        val expectedJson = &quot;&quot;&quot;
                {
                  &quot;field_1&quot;: &quot;some value&quot;,
                  &quot;field_2&quot;: &quot;{#uuid#}&quot;,
                  &quot;date&quot;: &quot;{#date_time_format:iso_instant#}&quot;,
                  &quot;other_fields&quot;: [{
                    &quot;id&quot;: &quot;1&quot;,
                    &quot;link&quot;: &quot;{#url#}&quot;
                  }, {
                    &quot;id&quot;: &quot;2&quot;,
                    &quot;link&quot;: &quot;{#url_ending:query?param1=true#}&quot;
                  }]
                }
                &quot;&quot;&quot;.trimIndent()

        assertThatJson(actualJson).isValidAgainst(expectedJson)
//sampleEnd
    }
}
{% endcapture %}

A simple assertion with default validators:

{% include try-jcv.html content=example_1 %}

{% capture example_2 %}
import com.ekino.oss.jcv.assertion.assertj.JsonCompareAssert.Companion.assertThatJson
import com.ekino.oss.jcv.core.JsonValueComparator
import com.ekino.oss.jcv.core.validator.Validators.defaultValidators
import com.ekino.oss.jcv.core.validator.validators
import org.junit.Test
import org.skyscreamer.jsonassert.ValueMatcherException

class Example2Test {

//sampleStart
    @Test
    fun `should validate json content with custom validator`() {

        assertThatJson(
            &quot;&quot;&quot;
            {
              &quot;id&quot;: &quot;fda7a233-99b9-4756-8ecc-826a1c5a9bf5&quot;,
              &quot;reference&quot;: &quot;REF_0123456789&quot;
            }
            &quot;&quot;&quot;.trimIndent()
        )
            .using(validators {
                +defaultValidators()
                +templatedValidator(&quot;my_ref&quot;, MyRefComparator())
            })
            .isValidAgainst(
                &quot;&quot;&quot;
                {
                  &quot;id&quot;: &quot;{#uuid#}&quot;,
                  &quot;reference&quot;: &quot;{#my_ref#}&quot;
                }
                &quot;&quot;&quot;.trimIndent()
            )
    }

    private class MyRefComparator : JsonValueComparator&lt;String&gt; {

        override fun hasCorrectValue(actual: String?, expected: String?): Boolean {
            if (actual != null &amp;&amp; actual.startsWith(&quot;REF_&quot;) &amp;&amp; actual.length == 14) {
                return true
            }
            throw ValueMatcherException(&quot;Invalid reference format&quot;, expected, actual)
        }
    }
//sampleEnd
}
{% endcapture %}

Try with your custom validator:

{% include try-jcv.html content=example_2 %}

<script src="https://unpkg.com/kotlin-playground@1"
        data-selector=".kotlin-code"
        data-server="https://kotlin-jcv-compiler-server.herokuapp.com"
        data-version="1.4.0">
</script>
