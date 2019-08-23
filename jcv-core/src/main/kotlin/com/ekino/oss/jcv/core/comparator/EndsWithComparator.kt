/*
 * Copyright (c) 2019 ekino (https://www.ekino.com/)
 */
package com.ekino.oss.jcv.core.comparator

import com.ekino.oss.jcv.core.JsonValueComparator
import org.skyscreamer.jsonassert.ValueMatcherException

/**
 * A endsWith comparator on text.
 *
 * @author Leo Millon
 *
 * @see String.endsWith
 */
class EndsWithComparator
/**
 * Init comparator with the value to search for.
 *
 * @param value the value to search for
 */
(private val value: String) : JsonValueComparator<String> {

    override fun hasCorrectValue(actual: String?, expected: String?): Boolean {
        if (actual != null && actual.endsWith(value)) {
            return true
        }
        throw ValueMatcherException("Value should end with '$value'", expected, actual)
    }
}
