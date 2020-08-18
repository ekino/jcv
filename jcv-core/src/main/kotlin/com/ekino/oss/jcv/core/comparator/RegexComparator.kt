/*
 * Copyright (c) 2019 ekino (https://www.ekino.com/)
 */
package com.ekino.oss.jcv.core.comparator

import com.ekino.oss.jcv.core.JsonValueComparator
import org.skyscreamer.jsonassert.ValueMatcherException
import java.util.regex.Pattern

class RegexComparator(private val pattern: Pattern) : JsonValueComparator<String> {

  override fun hasCorrectValue(actual: String?, expected: String?): Boolean {
    if (actual != null && pattern.matcher(actual).matches()) {
      return true
    }
    throw ValueMatcherException("Value does not match pattern /$pattern/", expected, actual)
  }
}
