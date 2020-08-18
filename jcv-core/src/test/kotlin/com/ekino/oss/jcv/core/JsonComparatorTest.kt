/*
 * Copyright (c) 2019 ekino (https://www.ekino.com/)
 */
package com.ekino.oss.jcv.core

import assertk.all
import assertk.assertAll
import assertk.assertThat
import assertk.assertions.hasMessage
import assertk.assertions.isEqualTo
import assertk.assertions.isFailure
import assertk.assertions.isFalse
import assertk.assertions.isInstanceOf
import assertk.assertions.isNotNull
import assertk.assertions.isNullOrEmpty
import assertk.assertions.isTrue
import assertk.assertions.message
import assertk.assertions.startsWith
import assertk.tableOf
import com.ekino.oss.jcv.core.validator.Validators
import com.ekino.oss.jcv.core.validator.Validators.defaultValidators
import com.ekino.oss.jcv.core.validator.comparator
import com.ekino.oss.jcv.core.validator.forPathPrefix
import com.ekino.oss.jcv.core.validator.validator
import com.ekino.oss.jcv.core.validator.validators
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.skyscreamer.jsonassert.JSONCompare
import org.skyscreamer.jsonassert.JSONCompareMode
import org.skyscreamer.jsonassert.JSONCompareResult
import org.skyscreamer.jsonassert.ValueMatcherException
import java.util.Objects

class JsonComparatorTest {

  companion object {
    private val comparator = comparator()

    private fun comparator(
      vararg validators: JsonValidator<out Any?> = Validators.defaultValidators().toTypedArray(),
      mode: JSONCompareMode = JSONCompareMode.NON_EXTENSIBLE
    ) = JsonComparator(mode, validators.toList())

    private fun comparator(
      validators: List<JsonValidator<out Any?>>,
      mode: JSONCompareMode = JSONCompareMode.NON_EXTENSIBLE
    ) = JsonComparator(mode, validators)

    private fun compare(
      actualJson: String,
      expectedJson: String,
      comparator: JsonComparator = Companion.comparator,
      body: (JSONCompareResult) -> Unit = {}
    ) {
      body.invoke(JSONCompare.compareJSON(expectedJson, actualJson, comparator))
    }

    private fun loadJson(fileName: String): String {
      return this::class.java.getResource("/$fileName").readText()
    }
  }

  @Test
  fun `sample JSON validation`() {

    compare(
      loadJson("test_sample_json_actual.json"),
      loadJson("test_sample_json_expected.json")
    ) {
      assertAll {
        assertThat(it.passed()).isTrue()
        assertThat(it.message).isNullOrEmpty()
      }
    }
  }

  @Test
  fun `default validators`() {

    compare(
      loadJson("test_default_validators_actual.json"),
      loadJson("test_default_validators_expected.json")
    ) {
      assertAll {
        assertThat(it.passed()).isTrue()
        assertThat(it.message).isNullOrEmpty()
      }
    }
  }

  @Test
  fun `prefix matcher`() {

    compare(
      loadJson("test_prefix_matcher_actual.json"),
      loadJson("test_prefix_matcher_expected.json"),
      comparator(
        validators {
          +defaultValidators()
          +forPathPrefix<Any>("child.child.level", comparator { actual, _ -> actual == 9999 })
        }
      )
    ) {
      assertAll {
        assertThat(it.passed()).isTrue()
        assertThat(it.message).isNullOrEmpty()
      }
    }
  }

  @Test
  fun `custom validator id in value matcher`() {

    compare(
      loadJson("test_validator_id_in_value_matcher_actual_invalid.json"),
      loadJson("test_validator_id_in_value_matcher_expected.json"),
      comparator(
        validator {
          templatedValidator<String>(
            "someSpecificValue",
            comparator { actual, _ ->
              val specificValue = "THE_VALUE"
              if (specificValue == actual) {
                return@comparator true
              }
              throw ValueMatcherException(
                "Value should be '$specificValue'",
                specificValue,
                Objects.toString(actual)
              )
            }
          )
        }
      )
    ) {
      assertAll {
        assertThat(it.passed()).isFalse()
        assertThat(it.message).isEqualTo(
          """
          field_2: Value should be 'THE_VALUE'
          Expected: THE_VALUE
               got: {#someSpecificValue#}

          """.trimIndent()
        )
      }
    }
  }

  @Test
  fun `unknown date time format language tag`() {

    assertThat {
      compare(
        // language=json
        """{"field_name": "3 Feb 2011"}""",
        // language=json
        """{"field_name": "{#date_time_format:d MMM uuu;some_TAG#}"}"""
      )
    }.isFailure().all {
      isInstanceOf(IllegalArgumentException::class.java)
      hasMessage("Invalid language tag some_TAG")
    }
  }

  @Test
  fun `unknown date time format pattern`() {

    assertThat {
      compare(
        // language=json
        """{"field_name": "2011-12-03T10:15:30Z"}""",
        // language=json
        """{"field_name": "{#date_time_format:some_unknown_pattern#}"}"""
      )
    }.isFailure().all {
      isInstanceOf(IllegalArgumentException::class.java)
      message().isNotNull().startsWith("Unknown pattern")
    }
  }

  @Test
  fun `validator errors`() {

    tableOf("actual", "expected", "error")
      .row(
        // language=json
        """{"field_name": "hello_world!"}""",
        // language=json
        """{"field_name": "{#contains:llo wor#}"}""",
        """
        field_name: Value should contain 'llo wor'
        Expected: {#contains:llo wor#}
             got: hello_world!

        """.trimIndent()
      )
      .row(
        // language=json
        """{"field_name": "hello_world!"}""",
        // language=json
        """{"field_name": "{#starts_with:llo_wor#}"}""",
        """
        field_name: Value should start with 'llo_wor'
        Expected: {#starts_with:llo_wor#}
             got: hello_world!

        """.trimIndent()
      )
      .row(
        // language=json
        """{"field_name": "hello_world!"}""",
        // language=json
        """{"field_name": "{#ends_with:llo_wor#}"}""",
        """
        field_name: Value should end with 'llo_wor'
        Expected: {#ends_with:llo_wor#}
             got: hello_world!

        """.trimIndent()
      )
      .row(
        // language=json
        """{"field_name": "hello_world!"}""",
        // language=json
        """{"field_name": "{#regex:.*llo ?w.r.*#}"}""",
        """
        field_name: Value does not match pattern /.*llo ?w.r.*/
        Expected: {#regex:.*llo ?w.r.*#}
             got: hello_world!

        """.trimIndent()
      )
      .row(
        // language=json
        """{"field_name": "some value"}""",
        // language=json
        """{"field_name": "{#uuid#}"}""",
        """
        field_name: Value is not a valid UUID
        Expected: {#uuid#}
             got: some value

        """.trimIndent()
      )
      .row(
        // language=json
        """{"field_name": null}""",
        // language=json
        """{"field_name": "{#not_null#}"}""",
        """
        field_name: Value should not be null
        Expected: {#not_null#}
             got: null

        """.trimIndent()
      )
      .row(
        // language=json
        """{"field_name": ""}""",
        // language=json
        """{"field_name": "{#not_empty#}"}""",
        """
        field_name: Value should not be empty
        Expected: {#not_empty#}
             got: 

        """.trimIndent()
      )
      .row(
        // language=json
        """{"field_name": "some value"}""",
        // language=json
        """{"field_name": "{#url#}"}""",
        """
        field_name: Value is not a valid URL
        Expected: {#url#}
             got: some value

        """.trimIndent()
      )
      .row(
        // language=json
        """{"field_name": "some value/?param"}""",
        // language=json
        """{"field_name": "{#url_ending:?param#}"}""",
        """
        field_name: Value is not a valid URL
        Expected: {#url_ending:?param#}
             got: some value/?param

        """.trimIndent()
      )
      .row(
        // language=json
        """{"field_name": "http://some.url:9999/path?param"}""",
        // language=json
        """{"field_name": "{#url_ending:/path?param2#}"}""",
        """
        field_name: Value should end with '/path?param2'
        Expected: {#url_ending:/path?param2#}
             got: http://some.url:9999/path?param

        """.trimIndent()
      )
      .row(
        // language=json
        """{"field_name": "some value/?param"}""",
        // language=json
        """{"field_name": "{#url_regex:^.+some\\.url.+/path\\?param$#}"}""",
        """
        field_name: Value is not a valid URL
        Expected: {#url_regex:^.+some\.url.+/path\?param$#}
             got: some value/?param

        """.trimIndent()
      )
      .row(
        // language=json
        """{"field_name": "http://some_url:9999/path?param"}""",
        // language=json
        """{"field_name": "{#url_regex:^.+some\\.url.+/path\\?param$#}"}""",
        """
        field_name: Value does not match pattern /^.+some\.url.+/path\?param$/
        Expected: {#url_regex:^.+some\.url.+/path\?param$#}
             got: http://some_url:9999/path?param

        """.trimIndent()
      )
      .row(
        // language=json
        """{"field_name": "some value"}""",
        // language=json
        """{"field_name": "{#templated_url#}"}""",
        """
        field_name: Value is not a valid templated URL
        Expected: {#templated_url#}
             got: some value

        """.trimIndent()
      )
      .row(
        // language=json
        """{"field_name": "some value {?param}"}""",
        // language=json
        """{"field_name": "{#templated_url_ending:{?param}#}"}""",
        """
        field_name: Value is not a valid templated URL
        Expected: {#templated_url_ending:{?param}#}
             got: some value {?param}

        """.trimIndent()
      )
      .row(
        // language=json
        """{"field_name": "http://some.url:9999/path{?param}"}""",
        // language=json
        """{"field_name": "{#templated_url_ending:/path{?param2}#}"}""",
        """
        field_name: Value should end with '/path{?param2}'
        Expected: {#templated_url_ending:/path{?param2}#}
             got: http://some.url:9999/path{?param}

        """.trimIndent()
      )
      .row(
        // language=json
        """{"field_name": "some value/{?param}"}""",
        // language=json
        """{"field_name": "{#templated_url_regex:^.+some\\.url.+\/path\\{\\?param\\}$#}"}""",
        """
        field_name: Value is not a valid templated URL
        Expected: {#templated_url_regex:^.+some\.url.+/path\{\?param\}$#}
             got: some value/{?param}

        """.trimIndent()
      )
      .row(
        // language=json
        """{"field_name": "http://some_url:9999/path{?param}"}""",
        // language=json
        """{"field_name": "{#templated_url_regex:^.+some\\.url.+\/path\\{\\?param\\}$#}"}""",
        """
        field_name: Value does not match pattern /^.+some\.url.+/path\{\?param\}$/
        Expected: {#templated_url_regex:^.+some\.url.+/path\{\?param\}$#}
             got: http://some_url:9999/path{?param}

        """.trimIndent()
      )
      .row(
        // language=json
        """{"field_name": "some value"}""",
        // language=json
        """{"field_name": "{#boolean_type#}"}""",
        """
        field_name: Invalid value type
        Expected: {#boolean_type#}
             got: some value

        """.trimIndent()
      )
      .row(
        // language=json
        """{"field_name": true}""",
        // language=json
        """{"field_name": "{#string_type#}"}""",
        """
        field_name: Invalid value type
        Expected: {#string_type#}
             got: true

        """.trimIndent()
      )
      .row(
        // language=json
        """{"field_name": "some value"}""",
        // language=json
        """{"field_name": "{#number_type#}"}""",
        """
        field_name: Invalid value type
        Expected: {#number_type#}
             got: some value

        """.trimIndent()
      )
      .row(
        // language=json
        """{"field_name": "some value"}""",
        // language=json
        """{"field_name": "{#array_type#}"}""",
        """
        field_name: Invalid value type
        Expected: {#array_type#}
             got: some value

        """.trimIndent()
      )
      .row(
        // language=json
        """{"field_name": "some value"}""",
        // language=json
        """{"field_name": "{#object_type#}"}""",
        """
        field_name: Invalid value type
        Expected: {#object_type#}
             got: some value

        """.trimIndent()
      )
      .row(
        // language=json
        """{"field_name": "some value"}""",
        // language=json
        """{"field_name": "{#date_time_format:iso_instant#}"}""",
        """
        field_name: Invalid date time format
        Expected: {#date_time_format:iso_instant#}
             got: some value

        """.trimIndent()
      )
      .forAll { actual, expected, error ->
        compare(
          actualJson = actual,
          expectedJson = expected
        ) {
          assertAll {
            assertThat(it.passed()).isFalse()
            assertThat(it.message).isEqualTo(error)
          }
        }
      }
  }

  /**
   * Tests about array with simple values assertions with validators as expected elements in non-strict order.
   */
  @Nested
  inner class ArrayWithSimpleValuesTest {

    @Test
    fun `should handle validators in arrays with simple values`() {

      compare(
        loadJson("array_with_simple_values/test_actual.json"),
        loadJson("array_with_simple_values/test_expected.json")
      ) {
        assertAll {
          assertThat(it.passed()).isTrue()
          assertThat(it.message).isNullOrEmpty()
        }
      }
    }

    @Test
    fun `should throw an error if element count does not match between the two arrays`() {

      compare(
        // language=json
        """
        {
            "some_array": [
                "value_1",
                "value_2",
                "value_3"
            ]
        }
        """.trimIndent(),
        // language=json
        """
        {
            "some_array": [
                "{#contains:value_#}"
            ]
        }
        """.trimIndent()
      ) {
        assertAll {
          assertThat(it.passed()).isFalse()
          assertThat(it.message).isEqualTo("some_array[]: Expected 1 values but got 3")
        }
      }
    }

    @Test
    fun `should throw a detailed error if some elements did not match`() {

      compare(
        // language=json
        """
        {
            "some_array": [
                "cd820a36-aa32-42ea-879d-293ba5f3c1e5",
                "value_1",
                "hello",
                "value_3",
                "839ceac0-2e60-4405-b27c-db2ac753d809"
            ]
        }
        """.trimIndent(),
        // language=json
        """
        {
            "some_array": [
                "value_1",
                "{#uuid#}",
                "{#uuid#}",
                "value_2",
                "{#contains:value_#}"
            ]
        }
        """.trimIndent()
      ) {
        assertAll {
          assertThat(it.passed()).isFalse()
          assertThat(it.message).isEqualTo(
            """
            some_array[2]
            Unexpected: hello
             ; some_array[0] -> value_1 matched with: [[1] -> value_1]
            some_array[1] -> {#uuid#} matched with: [[0] -> cd820a36-aa32-42ea-879d-293ba5f3c1e5,[4] -> 839ceac0-2e60-4405-b27c-db2ac753d809]
            some_array[2] -> {#uuid#} matched with: [[0] -> cd820a36-aa32-42ea-879d-293ba5f3c1e5,[4] -> 839ceac0-2e60-4405-b27c-db2ac753d809]
            some_array[3] -> value_2 matched with: []
            some_array[4] -> {#contains:value_#} matched with: [[3] -> value_3]
            """.trimIndent()
          )
        }
      }
    }
  }
}
