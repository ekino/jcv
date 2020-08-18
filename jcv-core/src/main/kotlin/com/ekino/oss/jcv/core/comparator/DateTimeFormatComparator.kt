/*
 * Copyright (c) 2019 ekino (https://www.ekino.com/)
 */
package com.ekino.oss.jcv.core.comparator

import com.ekino.oss.jcv.core.JsonValueComparator
import org.skyscreamer.jsonassert.ValueMatcherException
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

class DateTimeFormatComparator(private val dateTimeFormatter: DateTimeFormatter) : JsonValueComparator<String> {

  override fun hasCorrectValue(actual: String?, expected: String?): Boolean {
    try {
      dateTimeFormatter.parse(actual)
      return true
    } catch (e: DateTimeParseException) {
      throw ValueMatcherException("Invalid date time format", e, expected, actual)
    }
  }
}
