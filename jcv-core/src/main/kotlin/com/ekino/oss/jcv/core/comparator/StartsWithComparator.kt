/*
 * Copyright (c) 2019 ekino (https://www.ekino.com/)
 */
package com.ekino.oss.jcv.core.comparator

import com.ekino.oss.jcv.core.JsonValueComparator
import org.skyscreamer.jsonassert.ValueMatcherException

class StartsWithComparator(private val value: String) : JsonValueComparator<String> {

    override fun hasCorrectValue(actual: String?, expected: String?): Boolean {
        if (actual != null && actual.startsWith(value)) {
            return true
        }
        throw ValueMatcherException("Value should start with '$value'", expected, actual)
    }
}
