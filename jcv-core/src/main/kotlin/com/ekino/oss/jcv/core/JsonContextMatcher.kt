/*
 * Copyright (c) 2019 ekino (https://www.ekino.com/)
 */
package com.ekino.oss.jcv.core

/**
 * Matcher used to identify a specific field.
 *
 * @author Leo Millon
 */
@FunctionalInterface
interface JsonContextMatcher {

    /**
     * Evaluates the current json parsing context.
     *
     * @param prefix the current json field path
     * @param expectedValue the expected field value
     * @param actualValue the actual field value
     *
     * @return `true` if the context matches, otherwise `false`
     */
    fun matches(prefix: String, expectedValue: Any?, actualValue: Any?): Boolean
}
