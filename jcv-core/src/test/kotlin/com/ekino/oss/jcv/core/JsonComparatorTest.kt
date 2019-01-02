/*
 * Copyright (c) 2019 ekino (https://www.ekino.com/)
 */
package com.ekino.oss.jcv.core

import assertk.assert
import assertk.assertAll
import assertk.assertions.hasMessage
import assertk.assertions.isEqualTo
import assertk.assertions.isFalse
import assertk.assertions.isInstanceOf
import assertk.assertions.isNotNull
import assertk.assertions.isNullOrEmpty
import assertk.assertions.isTrue
import assertk.assertions.message
import assertk.assertions.startsWith
import assertk.tableOf
import com.ekino.oss.jcv.core.validator.Validators
import org.apache.commons.io.IOUtils
import org.junit.jupiter.api.Test
import org.skyscreamer.jsonassert.JSONCompare
import org.skyscreamer.jsonassert.JSONCompareMode
import org.skyscreamer.jsonassert.JSONCompareResult
import org.skyscreamer.jsonassert.ValueMatcherException
import java.nio.charset.StandardCharsets
import java.util.Objects

class JsonComparatorTest {

    companion object {
        private val comparator = comparator()

        private fun comparator(
            vararg validators: JsonValidator<Any> = Validators.defaultValidators().toTypedArray(),
            mode: JSONCompareMode = JSONCompareMode.NON_EXTENSIBLE
        ) = JsonComparator(mode, validators.toList())

        private fun compare(
            actualJson: String,
            expectedJson: String,
            comparator: JsonComparator = Companion.comparator,
            body: (JSONCompareResult) -> Unit = {}
        ) {
            body.invoke(JSONCompare.compareJSON(expectedJson, actualJson, comparator))
        }

        private fun loadJson(fileName: String): String {
            return IOUtils.resourceToString("/$fileName", StandardCharsets.UTF_8)
        }
    }

    @Test
    fun `sample JSON validation`() {

        compare(
            loadJson("test_sample_json_actual.json"),
            loadJson("test_sample_json_expected.json")
        ) {
            assertAll {
                assert(it.passed()).isTrue()
                assert(it.message).isNullOrEmpty()
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
                assert(it.passed()).isTrue()
                assert(it.message).isNullOrEmpty()
            }
        }
    }

    @Test
    fun `prefix matcher`() {

        compare(
            loadJson("test_prefix_matcher_actual.json"),
            loadJson("test_prefix_matcher_expected.json"),
            comparator(Validators.forPath("child.child.level") { actual, _ -> actual == 9999 })
        ) {
            assertAll {
                assert(it.passed()).isTrue()
                assert(it.message).isNullOrEmpty()
            }
        }
    }

    @Test
    fun `custom validator id in value matcher`() {

        compare(
            loadJson("test_validator_id_in_value_matcher_actual_invalid.json"),
            loadJson("test_validator_id_in_value_matcher_expected.json"),
            comparator(Validators.templatedValidator("someSpecificValue") { actual, _ ->
                val specificValue = "THE_VALUE"
                if (specificValue == actual) {
                    return@templatedValidator true
                }
                throw ValueMatcherException(
                    "Value should be '$specificValue'",
                    specificValue,
                    Objects.toString(actual)
                )
            })
        ) {
            assertAll {
                assert(it.passed()).isFalse()
                assert(it.message).isEqualTo(
                    """field_2: Value should be 'THE_VALUE'
Expected: THE_VALUE
     got: {#someSpecificValue#}
""")
            }
        }
    }

    @Test
    fun `unknown date time format language tag`() {

        assert {
            compare(
                """{"field_name": "3 Feb 2011"}""",
                """{"field_name": "{#date_time_format:d MMM uuu;some_TAG#}"}"""
            )
        }.thrownError {
            isInstanceOf(IllegalArgumentException::class.java)
            hasMessage("Invalid language tag some_TAG")
        }
    }

    @Test
    fun `unknown date time format pattern`() {

        assert {
            compare(
                """{"field_name": "2011-12-03T10:15:30Z"}""",
                """{"field_name": "{#date_time_format:some_unknown_pattern#}"}"""
            )
        }.thrownError {
            isInstanceOf(IllegalArgumentException::class.java)
            message().isNotNull { it.startsWith("Unknown pattern") }
        }
    }

    @Test
    fun `validator errors`() {

        tableOf("actual", "expected", "error")
            .row(
                """{"field_name": "hello_world!"}""",
                """{"field_name": "{#contains:llo wor#}"}""",
                """field_name: Value should contain 'llo wor'
Expected: {#contains:llo wor#}
     got: hello_world!
""")
            .row(
                """{"field_name": "hello_world!"}""",
                """{"field_name": "{#starts_with:llo_wor#}"}""",
                """field_name: Value should start with 'llo_wor'
Expected: {#starts_with:llo_wor#}
     got: hello_world!
""")
            .row(
                """{"field_name": "hello_world!"}""",
                """{"field_name": "{#ends_with:llo_wor#}"}""",
                """field_name: Value should end with 'llo_wor'
Expected: {#ends_with:llo_wor#}
     got: hello_world!
""")
            .row(
                """{"field_name": "hello_world!"}""",
                """{"field_name": "{#regex:.*llo ?w.r.*#}"}""",
                """field_name: Value does not match pattern /.*llo ?w.r.*/
Expected: {#regex:.*llo ?w.r.*#}
     got: hello_world!
""")
            .row(
                """{"field_name": "some value"}""",
                """{"field_name": "{#uuid#}"}""",
                """field_name: Value is not a valid UUID
Expected: {#uuid#}
     got: some value
""")
            .row(
                """{"field_name": null}""",
                """{"field_name": "{#not_null#}"}""",
                """field_name: Value should not be null
Expected: {#not_null#}
     got: null
""")
            .row(
                """{"field_name": ""}""",
                """{"field_name": "{#not_empty#}"}""",
                """field_name: Value should not be empty
Expected: {#not_empty#}""" + "\n     got: \n"
            )
            .row(
                """{"field_name": "some value"}""",
                """{"field_name": "{#url#}"}""",
                """field_name: Value is not a valid URL
Expected: {#url#}
     got: some value
""")
            .row(
                """{"field_name": "some value/?param"}""",
                """{"field_name": "{#url_ending:?param#}"}""",
                """field_name: Value is not a valid URL
Expected: {#url_ending:?param#}
     got: some value/?param
""")
            .row(
                """{"field_name": "http://some.url:9999/path?param"}""",
                """{"field_name": "{#url_ending:/path?param2#}"}""",
                """field_name: Value should end with '/path?param2'
Expected: {#url_ending:/path?param2#}
     got: http://some.url:9999/path?param
""")
            .row(
                """{"field_name": "some value/?param"}""",
                """{"field_name": "{#url_regex:^.+some\\.url.+/path\\?param$#}"}""",
                """field_name: Value is not a valid URL
Expected: {#url_regex:^.+some\.url.+/path\?param$#}
     got: some value/?param
""")
            .row(
                """{"field_name": "http://some_url:9999/path?param"}""",
                """{"field_name": "{#url_regex:^.+some\\.url.+/path\\?param$#}"}""",
                """field_name: Value does not match pattern /^.+some\.url.+/path\?param$/
Expected: {#url_regex:^.+some\.url.+/path\?param$#}
     got: http://some_url:9999/path?param
""")
            .row(
                """{"field_name": "some value"}""",
                """{"field_name": "{#templated_url#}"}""",
                """field_name: Value is not a valid templated URL
Expected: {#templated_url#}
     got: some value
""")
            .row(
                """{"field_name": "some value {?param}"}""",
                """{"field_name": "{#templated_url_ending:{?param}#}"}""",
                """field_name: Value is not a valid templated URL
Expected: {#templated_url_ending:{?param}#}
     got: some value {?param}
""")
            .row(
                """{"field_name": "http://some.url:9999/path{?param}"}""",
                """{"field_name": "{#templated_url_ending:/path{?param2}#}"}""",
                """field_name: Value should end with '/path{?param2}'
Expected: {#templated_url_ending:/path{?param2}#}
     got: http://some.url:9999/path{?param}
""")
            .row(
                """{"field_name": "some value/{?param}"}""",
                """{"field_name": "{#templated_url_regex:^.+some\\.url.+\/path\\{\\?param\\}$#}"}""",
                """field_name: Value is not a valid templated URL
Expected: {#templated_url_regex:^.+some\.url.+/path\{\?param\}$#}
     got: some value/{?param}
""")
            .row(
                """{"field_name": "http://some_url:9999/path{?param}"}""",
                """{"field_name": "{#templated_url_regex:^.+some\\.url.+\/path\\{\\?param\\}$#}"}""",
                """field_name: Value does not match pattern /^.+some\.url.+/path\{\?param\}$/
Expected: {#templated_url_regex:^.+some\.url.+/path\{\?param\}$#}
     got: http://some_url:9999/path{?param}
""")
            .row(
                """{"field_name": "some value"}""",
                """{"field_name": "{#boolean_type#}"}""",
                """field_name: Invalid value type
Expected: {#boolean_type#}
     got: some value
""")
            .row(
                """{"field_name": true}""",
                """{"field_name": "{#string_type#}"}""",
                """field_name: Invalid value type
Expected: {#string_type#}
     got: true
""")
            .row(
                """{"field_name": "some value"}""",
                """{"field_name": "{#number_type#}"}""",
                """field_name: Invalid value type
Expected: {#number_type#}
     got: some value
""")
            .row(
                """{"field_name": "some value"}""",
                """{"field_name": "{#array_type#}"}""",
                """field_name: Invalid value type
Expected: {#array_type#}
     got: some value
""")
            .row(
                """{"field_name": "some value"}""",
                """{"field_name": "{#object_type#}"}""",
                """field_name: Invalid value type
Expected: {#object_type#}
     got: some value
""")
            .row(
                """{"field_name": "some value"}""",
                """{"field_name": "{#date_time_format:iso_instant#}"}""",
                """field_name: Invalid date time format
Expected: {#date_time_format:iso_instant#}
     got: some value
""")
            .forAll { actual, expected, error ->
                compare(
                    actualJson = actual,
                    expectedJson = expected
                ) {
                    assertAll {
                        assert(it.passed()).isFalse()
                        assert(it.message).isEqualTo(error)
                    }
                }
            }
    }
}