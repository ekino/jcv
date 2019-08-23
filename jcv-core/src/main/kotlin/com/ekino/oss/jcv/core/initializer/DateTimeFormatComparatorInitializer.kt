/*
 * Copyright (c) 2019 ekino (https://www.ekino.com/)
 */
package com.ekino.oss.jcv.core.initializer

import com.ekino.oss.jcv.core.comparator.DateTimeFormatComparator
import org.skyscreamer.jsonassert.ValueMatcher
import java.time.format.DateTimeFormatter
import java.util.HashMap
import java.util.Locale

class DateTimeFormatComparatorInitializer : TwoParametersComparatorInitializer<String> {

    companion object {

        private val PREDEFINED_FORMATTERS = initPredefinedFormatters()

        private fun initPredefinedFormatters(): Map<String, DateTimeFormatter> {
            val formatters = HashMap<String, DateTimeFormatter>()
            formatters["iso_local_date"] = DateTimeFormatter.ISO_LOCAL_DATE
            formatters["iso_offset_date"] = DateTimeFormatter.ISO_OFFSET_DATE
            formatters["iso_date"] = DateTimeFormatter.ISO_DATE
            formatters["iso_local_time"] = DateTimeFormatter.ISO_LOCAL_TIME
            formatters["iso_offset_time"] = DateTimeFormatter.ISO_OFFSET_TIME
            formatters["iso_time"] = DateTimeFormatter.ISO_TIME
            formatters["iso_local_date_time"] = DateTimeFormatter.ISO_LOCAL_DATE_TIME
            formatters["iso_offset_date_time"] = DateTimeFormatter.ISO_OFFSET_DATE_TIME
            formatters["iso_zoned_date_time"] = DateTimeFormatter.ISO_ZONED_DATE_TIME
            formatters["iso_date_time"] = DateTimeFormatter.ISO_DATE_TIME
            formatters["iso_ordinal_date"] = DateTimeFormatter.ISO_ORDINAL_DATE
            formatters["iso_week_date"] = DateTimeFormatter.ISO_WEEK_DATE
            formatters["iso_instant"] = DateTimeFormatter.ISO_INSTANT
            formatters["basic_iso_date"] = DateTimeFormatter.BASIC_ISO_DATE
            formatters["rfc_1123_date_time"] = DateTimeFormatter.RFC_1123_DATE_TIME
            return formatters
        }

        private fun initFormatter(pattern: String, languageTag: String?): DateTimeFormatter {
            getPredefinedFormatter(pattern)
                ?.let { return it }

            return languageTag
                ?.let { createLocalizedFormatter(pattern, it) }
                ?: DateTimeFormatter.ofPattern(pattern)
        }

        private fun createLocalizedFormatter(pattern: String, languageTag: String): DateTimeFormatter {

            val locale = Locale.forLanguageTag(languageTag)

            if ("und" == locale.toLanguageTag()) {
                throw IllegalArgumentException("Invalid language tag $languageTag")
            }

            return DateTimeFormatter.ofPattern(pattern, locale)
        }

        private fun getPredefinedFormatter(pattern: String): DateTimeFormatter? = PREDEFINED_FORMATTERS[pattern.toLowerCase()]
    }

    override fun initComparator(param1: String?, param2: String?): ValueMatcher<String> {
        return DateTimeFormatComparator(initFormatter(param1!!, param2))
    }
}
