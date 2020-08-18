/*
 * Copyright (c) 2019 ekino (https://www.ekino.com/)
 */
package com.ekino.oss.jcv.core.comparator

import com.ekino.oss.jcv.core.JsonValueComparator
import org.skyscreamer.jsonassert.ValueMatcherException
import java.util.Objects

class NotNullComparator : JsonValueComparator<Any> {

  override fun hasCorrectValue(actual: Any?, expected: Any?): Boolean {
    if (actual != null && actual !is String && "null" != actual.toString()) {
      return true
    }
    throw ValueMatcherException("Value should not be null", Objects.toString(expected), Objects.toString(actual))
  }
}
