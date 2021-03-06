/*
 * Copyright (c) 2019 ekino (https://www.ekino.com/)
 */
package com.ekino.oss.jcv.core.comparator

import com.ekino.oss.jcv.core.JsonValueComparator
import org.skyscreamer.jsonassert.ValueMatcherException
import java.net.MalformedURLException
import java.net.URL

class TemplatedURLComparator : JsonValueComparator<String> {

  override fun hasCorrectValue(actual: String?, expected: String?): Boolean {
    try {
      val urlWithoutTemplate = actual?.replace("\\{\\?.+}$".toRegex(), "")
      URL(urlWithoutTemplate)
      return true
    } catch (e: MalformedURLException) {
      throw ValueMatcherException("Value is not a valid templated URL", e, expected, actual)
    }
  }
}
