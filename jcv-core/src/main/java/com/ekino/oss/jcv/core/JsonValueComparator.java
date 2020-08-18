/*
 * Copyright (c) 2019 ekino (https://www.ekino.com/)
 */
package com.ekino.oss.jcv.core;

import org.skyscreamer.jsonassert.ValueMatcher;

/**
 * A JSON value comparator.
 *
 * @param <T> the field value type
 * @author Leo Millon
 */
@FunctionalInterface
public interface JsonValueComparator<T> extends ValueMatcher<T> {

    @Override
    default boolean equal(T o1, T o2) {
        return hasCorrectValue(o1, o2);
    }

    /**
     * Evaluates actual field value against the expected one.
     *
     * @param actual   field actual value
     * @param expected field expected value
     * @return {@code true} if the actual is valid against the expected one, otherwise {@code false}
     */
    boolean hasCorrectValue(T actual, T expected);
}
