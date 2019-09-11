/*
 * Copyright (c) 2019 ekino (https://www.ekino.com/)
 */
package com.ekino.oss.jcv.core;

import org.skyscreamer.jsonassert.ValueMatcher;

/**
 * A validator composed of a matcher and a value comparator.
 *
 * @param <T> a field value type
 *
 * @author Leo Millon
 */
public interface JsonValidator<T> {

    /**
     * The context matcher.
     *
     * @return the matcher
     */
    JsonContextMatcher getContextMatcher();

    /**
     * The field value comparator.
     *
     * @return the comparator
     */
    ValueMatcher<T> getValueComparator();
}
