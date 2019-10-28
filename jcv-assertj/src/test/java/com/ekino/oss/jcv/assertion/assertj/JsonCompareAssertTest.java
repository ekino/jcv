/*
 * Copyright (c) 2019 ekino (https://www.ekino.com/)
 */
package com.ekino.oss.jcv.assertion.assertj;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.ekino.oss.jcv.core.JsonValidator;
import com.ekino.oss.jcv.core.validator.Validators;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.ValueMatcherException;

import static com.ekino.oss.jcv.assertion.assertj.JsonCompareAssert.*;
import static org.assertj.core.api.Assertions.*;

class JsonCompareAssertTest {

    @Test
    void should_match_with_default_comparator() {
        assertThatJson(
            // language=json
            "{\"field_name\": \"hello world!\"}"
        )
            .isValidAgainst(
                // language=json
                "{\"field_name\": \"{#contains:llo wor#}\"}"
            );
    }

    @Test
    void should_not_match_with_default_comparator() {

        assertThatThrownBy(() ->
            assertThatJson(
                // language=json
                "{\"field_name\": \"hello_world!\"}"
            )
                .isValidAgainst(
                    // language=json
                    "{\"field_name\": \"{#contains:llo wor#}\"}"
                )
        )
            .isInstanceOf(AssertionError.class)
            .hasMessage(
                "field_name: Value should contain 'llo wor'\n" +
                    "Expected: {#contains:llo wor#}\n" +
                    "     got: hello_world!\n" +
                    ""
            );
    }

    @Test
    void should_validate_sample_json() {

        assertThatJson(loadJson("test_sample_json_actual.json"))
            .isValidAgainst(loadJson("test_sample_json_expected.json"));
    }

    @Test
    void should_match_with_prefix() {

        assertThatJson(loadJson("test_prefix_matcher_actual.json"))
            .using(Validators.forPath("child.child.level", (actual, expected) -> actual.equals(9999)))
            .isValidAgainst(loadJson("test_prefix_matcher_expected.json"));
    }

    @Test
    void should_match_with_custom_validator() {

        assertThatThrownBy(() ->
            assertThatJson(loadJson("test_validator_id_in_value_matcher_actual_invalid.json"))
                .using(customValidator())
                .isValidAgainst(loadJson("test_validator_id_in_value_matcher_expected.json"))
        )
            .isInstanceOf(AssertionError.class)
            .hasMessageStartingWith("field_2: Value should be 'THE_VALUE'");
    }

    @Test
    void should_match_with_default_and_custom_validator() {

        assertThatThrownBy(() ->
            assertThatJson(loadJson("test_validator_id_in_value_matcher_actual_invalid.json"))
                .using(defaultAndCustomValidators())
                .isValidAgainst(loadJson("test_validator_id_in_value_matcher_expected.json"))
        )
            .isInstanceOf(AssertionError.class)
            .hasMessageStartingWith("field_2: Value should be 'THE_VALUE'");
    }

    private static JsonValidator<String> customValidator() {
        return Validators.templatedValidator(
            "someSpecificValue",
            (actual, expected) -> {
                String specificValue = "THE_VALUE";
                if (specificValue.equals(actual)) {
                    return true;
                }
                throw new ValueMatcherException(
                    "Value should be '" + specificValue + "'",
                    specificValue,
                    Objects.toString(actual)
                );
            }
        );
    }

    private static List<JsonValidator> defaultAndCustomValidators() {
        List<JsonValidator> validators = new ArrayList<>(Validators.defaultValidators());
        validators.add(customValidator());
        return validators;
    }

    private static String loadJson(String fileName) {
        try {
            return IOUtils.resourceToString("/" + fileName, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
