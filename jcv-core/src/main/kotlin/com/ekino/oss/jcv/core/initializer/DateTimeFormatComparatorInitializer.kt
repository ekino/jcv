/*
 * Copyright (c) 2019 ekino (https://www.ekino.com/)
 */
package com.ekino.oss.jcv.core.initializer

import com.ekino.oss.jcv.core.comparator.DateTimeFormatComparator
import org.skyscreamer.jsonassert.ValueMatcher
import java.time.format.DateTimeFormatter
import java.util.Locale

class DateTimeFormatComparatorInitializer : TwoParametersComparatorInitializer<String> {

  companion object {

    private val PREDEFINED_FORMATTERS = initPredefinedFormatters()

    private fun initPredefinedFormatters(): Map<String, DateTimeFormatter> = mapOf(
      "iso_local_date" to DateTimeFormatter.ISO_LOCAL_DATE,
      "iso_offset_date" to DateTimeFormatter.ISO_OFFSET_DATE,
      "iso_date" to DateTimeFormatter.ISO_DATE,
      "iso_local_time" to DateTimeFormatter.ISO_LOCAL_TIME,
      "iso_offset_time" to DateTimeFormatter.ISO_OFFSET_TIME,
      "iso_time" to DateTimeFormatter.ISO_TIME,
      "iso_local_date_time" to DateTimeFormatter.ISO_LOCAL_DATE_TIME,
      "iso_offset_date_time" to DateTimeFormatter.ISO_OFFSET_DATE_TIME,
      "iso_zoned_date_time" to DateTimeFormatter.ISO_ZONED_DATE_TIME,
      "iso_date_time" to DateTimeFormatter.ISO_DATE_TIME,
      "iso_ordinal_date" to DateTimeFormatter.ISO_ORDINAL_DATE,
      "iso_week_date" to DateTimeFormatter.ISO_WEEK_DATE,
      "iso_instant" to DateTimeFormatter.ISO_INSTANT,
      "basic_iso_date" to DateTimeFormatter.BASIC_ISO_DATE,
      "rfc_1123_date_time" to DateTimeFormatter.RFC_1123_DATE_TIME,
    )

    private fun initFormatter(pattern: String, languageTag: String?): DateTimeFormatter {
      getPredefinedFormatter(pattern)
        ?.let { return it }

      return languageTag
        ?.let { createLocalizedFormatter(pattern, it) }
        ?: DateTimeFormatter.ofPattern(pattern)
    }

    private fun createLocalizedFormatter(pattern: String, languageTag: String): DateTimeFormatter {
      val locale = Locale.forLanguageTag(languageTag)

      require("und" != locale.toLanguageTag()) { "Invalid language tag $languageTag" }

      return DateTimeFormatter.ofPattern(pattern, locale)
    }

    private fun getPredefinedFormatter(pattern: String): DateTimeFormatter? =
      PREDEFINED_FORMATTERS[pattern.lowercase()]
  }

  override fun initComparator(param1: String?, param2: String?): ValueMatcher<String> {
    return DateTimeFormatComparator(initFormatter(param1!!, param2))
  }
}
