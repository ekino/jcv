/*
 * Copyright (c) 2019 ekino (https://www.ekino.com/)
 */
package com.ekino.oss.jcv.core.comparator

import com.ekino.oss.jcv.core.JsonValueComparator
import org.skyscreamer.jsonassert.ValueMatcherException
import java.net.MalformedURLException
import java.net.URL

class URLComparator : JsonValueComparator<String> {

  override fun hasCorrectValue(actual: String?, expected: String?): Boolean {
    try {
      URL(actual)
      return true
    } catch (e: MalformedURLException) {
      throw ValueMatcherException("Value is not a valid URL", e, expected, actual)
    }
  }
}
