/*
 * Copyright (c) 2019 ekino (https://www.ekino.com/)
 */
package com.ekino.oss.jcv.core;

/**
 * Matcher used to identify a specific field.
 *
 * @author Leo Millon
 */
@FunctionalInterface
public interface JsonContextMatcher {

    /**
     * Evaluates the current json parsing context.
     *
     * @param prefix        the current json field path
     * @param expectedValue the expected field value
     * @param actualValue   the actual field value
     * @return {@code true} if the context matches, otherwise {@code false}
     */
    boolean matches(String prefix, Object expectedValue, Object actualValue);
}
