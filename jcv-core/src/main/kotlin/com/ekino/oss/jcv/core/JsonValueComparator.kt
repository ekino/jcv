/*
 * Copyright (c) 2019 ekino (https://www.ekino.com/)
 */
package com.ekino.oss.jcv.core

import org.skyscreamer.jsonassert.ValueMatcher

/**
 * A JSON value comparator.
 *
 * @param <T> the field value type
 *
 * @author Leo Millon
 */
@FunctionalInterface
interface JsonValueComparator<T> : ValueMatcher<T> {

    @JvmDefault
    override fun equal(o1: T?, o2: T?): Boolean {
        return hasCorrectValue(o1, o2)
    }

    /**
     * Evaluates actual field value against the expected one.
     *
     * @param actual field actual value
     * @param expected field expected value
     *
     * @return `true` if the actual is valid against the expected one, otherwise `false`
     */
    fun hasCorrectValue(actual: T?, expected: T?): Boolean
}
