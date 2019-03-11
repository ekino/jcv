/*
 * Copyright (c) 2019 ekino (https://www.ekino.com/)
 */
package com.ekino.oss.jcv.assertion.assertj;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import com.ekino.oss.jcv.assertion.assertj.exception.JsonParseException;
import com.ekino.oss.jcv.core.JsonComparator;
import com.ekino.oss.jcv.core.JsonValidator;
import com.ekino.oss.jcv.core.validator.Validators;
import org.assertj.core.api.AbstractAssert;
import org.json.JSONException;
import org.skyscreamer.jsonassert.JSONCompare;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.skyscreamer.jsonassert.JSONCompareResult;
import org.skyscreamer.jsonassert.comparator.JSONComparator;

/**
 * Assertion to make {@link JsonComparator} available as AssertJ utilities.
 *
 * @author Leo Millon
 */
public class JsonCompareAssert extends AbstractAssert<JsonCompareAssert, String> {

    private final JsonComparator jsonComparator;

    protected JsonCompareAssert(String actualJson, JsonComparator jsonComparator) {
        super(actualJson, JsonCompareAssert.class);
        this.jsonComparator = jsonComparator;
    }

    /**
     * Creates a new instance of <code>{@link JsonCompareAssert}from a {@link String}</code>.
     *
     * @param actualJson the actual value
     *
     * @return the created assertion object
     */

    public static JsonCompareAssert assertThatJson(String actualJson) {
        return new JsonCompareAssert(actualJson, new JsonComparator(JSONCompareMode.NON_EXTENSIBLE, Validators.defaultValidators()));
    }

    /**
     * Creates a new instance of <code>{@link JsonCompareAssert} using a new comparator</code> with the same actual value.
     *
     * @param comparator the new comparator to use
     *
     * @return {@code this} assertion object
     */
    public JsonCompareAssert using(JsonComparator comparator) {
        return new JsonCompareAssert(actual, comparator);
    }

    /**
     * Creates a new instance of <code>{@link JsonCompareAssert} with a custom configuration</code> with the same actual value.
     *
     * @param mode       the compare mode
     * @param validators the validators to use
     *
     * @return {@code this} assertion object
     *
     * @see #using(JsonComparator)
     */
    public JsonCompareAssert using(JSONCompareMode mode, JsonValidator... validators) {
        return using(mode, Arrays.asList(validators));
    }

    /**
     * Creates a new instance of <code>{@link JsonCompareAssert} with a custom configuration</code> with the same actual value.
     *
     * @param mode       the compare mode
     * @param validators the validators to use
     *
     * @return {@code this} assertion object
     *
     * @see #using(JsonComparator)
     */
    public JsonCompareAssert using(JSONCompareMode mode, List<JsonValidator> validators) {
        return new JsonCompareAssert(actual, new JsonComparator(mode, validators));
    }

    /**
     * Creates a new instance of <code>{@link JsonCompareAssert} with a custom configuration</code> with the same actual value.
     *
     * @param validators the validators to use
     *
     * @return {@code this} assertion object
     *
     * @see #using(JsonComparator)
     */
    public JsonCompareAssert using(JsonValidator... validators) {
        return using(Arrays.asList(validators));
    }

    /**
     * Creates a new instance of <code>{@link JsonCompareAssert} with a custom configuration</code> with the same actual value.
     *
     * @param validators the validators to use
     *
     * @return {@code this} assertion object
     *
     * @see #using(JsonComparator)
     */
    public JsonCompareAssert using(List<JsonValidator> validators) {
        return using(JSONCompareMode.NON_EXTENSIBLE, validators);
    }

    /**
     * Verifies that the actual JSON value is valid against the given JSON.
     *
     * @param expectedJson the given value to compare the actual value to.
     *
     * @return {@code this} assertion object.
     *
     * @throws JSONException if the actual or expected string is not a valid JSON format.
     * @see JSONCompare#compareJSON(String, String, JSONComparator)
     */
    public JsonCompareAssert isValidAgainst(String expectedJson) {

        isNotNull();

        Objects.requireNonNull(jsonComparator, "Json comparator definition is missing");

        try {
            JSONCompareResult result = JSONCompare.compareJSON(expectedJson, actual, jsonComparator);
            if (!result.passed()) {
                failWithMessage(result.getMessage());
            }
        } catch (JSONException e) {
            throw new JsonParseException("Error with provided JSON Strings", e);
        }

        return this;
    }
}
