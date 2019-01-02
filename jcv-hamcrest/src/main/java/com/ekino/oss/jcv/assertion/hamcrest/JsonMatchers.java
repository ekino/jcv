/*
 * Copyright (c) 2019 ekino (https://www.ekino.com/)
 */
package com.ekino.oss.jcv.assertion.hamcrest;

import lombok.experimental.UtilityClass;
import org.hamcrest.Matcher;

/**
 * Utility class to quickly use json matchers.
 */
@UtilityClass
public class JsonMatchers {

    /**
     * The default JSON matcher.
     *
     * @param expectedJson the expected json to compare with the actual one
     *
     * @return the matcher
     *
     * @see JsonMatcherBuilder#create()
     */
    public static Matcher<String> jsonMatcher(String expectedJson) {
        return JsonMatcherBuilder.create().build(expectedJson);
    }
}
