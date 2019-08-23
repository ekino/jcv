/*
 * Copyright (c) 2019 ekino (https://www.ekino.com/)
 */
package com.ekino.oss.jcv.core.comparator

import com.ekino.oss.jcv.core.JsonValueComparator
import org.skyscreamer.jsonassert.ValueMatcherException
import java.util.UUID

class UUIDComparator : JsonValueComparator<String> {

    override fun hasCorrectValue(actual: String?, expected: String?): Boolean {
        try {
            UUID.fromString(actual)
            return true
        } catch (e: IllegalArgumentException) {
            throw ValueMatcherException("Value is not a valid UUID", e, expected, actual)
        }
    }
}
