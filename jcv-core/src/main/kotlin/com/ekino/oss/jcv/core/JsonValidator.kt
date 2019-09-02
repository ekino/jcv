/*
 * Copyright (c) 2019 ekino (https://www.ekino.com/)
 */
package com.ekino.oss.jcv.core

import org.skyscreamer.jsonassert.ValueMatcher

/**
 * A validator composed of a matcher and a value comparator.
 *
 * @param <T> a field value type
 *
 * @author Leo Millon
 */
interface JsonValidator<T> {

    /**
     * The context matcher.
     *
     * @return the matcher
     */
    val contextMatcher: JsonContextMatcher

    /**
     * The field value comparator.
     *
     * @return the comparator
     */
    val valueComparator: ValueMatcher<T>
}
