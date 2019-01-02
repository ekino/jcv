/*
 * Copyright (c) 2019 ekino (https://www.ekino.com/)
 */
package com.ekino.oss.jcv.assertion.hamcrest;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;
import org.json.JSONException;
import org.skyscreamer.jsonassert.JSONCompare;
import org.skyscreamer.jsonassert.JSONCompareResult;
import org.skyscreamer.jsonassert.comparator.JSONComparator;

/**
 * A Hamcrest matcher to compare json contents.
 *
 * @author Leo Millon
 * @see JSONComparator
 */
public class JsonCompareMatcher extends TypeSafeMatcher<String> {

    private final JSONComparator jsonComparator;
    private final String expectedJson;
    private JSONCompareResult result;

    public JsonCompareMatcher(JSONComparator jsonComparator, String expectedJson) {
        this.jsonComparator = jsonComparator;
        this.expectedJson = expectedJson;
    }

    @Override
    public void describeTo(Description description) {

        if (result != null) {
            description.appendText(result.getMessage());
        } else {
            description.appendText("A valid JSON");
        }
    }

    @Override
    protected boolean matchesSafely(String item) {
        try {
            result = JSONCompare.compareJSON(expectedJson, item, jsonComparator);
            return result.passed();
        } catch (JSONException e) {
            throw new IllegalArgumentException("Unable to parse expected JSON", e);
        }
    }
}
