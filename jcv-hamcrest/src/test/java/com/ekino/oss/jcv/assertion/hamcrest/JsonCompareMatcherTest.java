/*
 * Copyright (c) 2019 ekino (https://www.ekino.com/)
 */
package com.ekino.oss.jcv.assertion.hamcrest;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import com.ekino.oss.jcv.core.validator.Validators;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.ValueMatcherException;

import static com.ekino.oss.jcv.assertion.hamcrest.JsonMatchers.*;
import static org.junit.Assert.*;

class JsonCompareMatcherTest {

    @Test
    void should_match_with_default_comparator(){

        assertThat(
            "{\"field_name\": \"hello world!\"}",
            jsonMatcher("{\"field_name\": \"{#contains:llo wor#}\"}")
        );
    }

    @Test
    void should_not_match_with_default_comparator(){

        AssertionError error = Assertions.assertThrows(
            AssertionError.class,
            () -> assertThat(
                "{\"field_name\": \"hello_world!\"}",
                jsonMatcher("{\"field_name\": \"{#contains:llo wor#}\"}")
            )
        );

        assertEquals("\n" +
            "Expected: field_name: Value should contain 'llo wor'\n" +
            "Expected: {#contains:llo wor#}\n" +
            "     got: hello_world!\n" +
            "\n" +
            "     but: was \"{\\\"field_name\\\": \\\"hello_world!\\\"}\"",
            error.getMessage()
        );
    }

    @Test
    void should_validate_sample_json() throws IOException {

        assertThat(
            loadJson("test_sample_json_actual.json"),
            jsonMatcher(loadJson("test_sample_json_expected.json"))
        );
    }

    @Test
    void should_match_with_custom_validator(){

        JsonCompareMatcher customJsonMatcher = JsonMatcherBuilder.create()
            .validators(Validators.<String>templatedValidator(
                "custom_notempty",
                (actual, expected) -> {

                    if (actual == null || actual.isEmpty()) {
                        throw new ValueMatcherException("Value is null or empty", expected, actual);
                    }
                    return true;
                }
            ))
            .build("{\"field_name\": \"{#custom_notempty#}\"}");

        assertThat(
            "{\"field_name\": \"hello world!\"}",
            customJsonMatcher
        );

        AssertionError error = Assertions.assertThrows(
            AssertionError.class,
            () -> assertThat(
                "{\"field_name\": \"\"}",
                customJsonMatcher
            )
        );

        assertEquals("\n" +
                "Expected: field_name: Value is null or empty\n" +
                "Expected: {#custom_notempty#}\n" +
                "     got: \n" +
                "\n" +
                "     but: was \"{\\\"field_name\\\": \\\"\\\"}\"",
            error.getMessage()
        );
    }

    private static String loadJson(String fileName) throws IOException {
        return IOUtils.resourceToString("/" + fileName, StandardCharsets.UTF_8);
    }
}
