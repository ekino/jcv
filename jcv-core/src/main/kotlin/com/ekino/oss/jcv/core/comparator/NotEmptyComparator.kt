/*
 * Copyright (c) 2019 ekino (https://www.ekino.com/)
 */
package com.ekino.oss.jcv.core.comparator

import com.ekino.oss.jcv.core.JsonValueComparator
import org.skyscreamer.jsonassert.ValueMatcherException
import java.util.Objects

class NotEmptyComparator : JsonValueComparator<String> {

  override fun hasCorrectValue(actual: String?, expected: String?): Boolean {
    if (actual.isNullOrEmpty().not()) {
      return true
    }
    throw ValueMatcherException("Value should not be empty", Objects.toString(expected), Objects.toString(actual))
  }
}
