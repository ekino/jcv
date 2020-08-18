/*
 * Copyright (c) 2019 ekino (https://www.ekino.com/)
 */
package com.ekino.oss.jcv.core.comparator

import com.ekino.oss.jcv.core.JsonValueComparator
import org.skyscreamer.jsonassert.ValueMatcherException

class TypeComparator(private val expectedType: Class<*>) : JsonValueComparator<Any> {

  override fun hasCorrectValue(actual: Any?, expected: Any?): Boolean {
    if (expectedType.kotlin.isInstance(actual)) {
      return true
    }
    throw ValueMatcherException("Invalid value type", expected.toString(), actual.toString())
  }
}
