/*
 * Copyright (c) 2019 ekino (https://www.ekino.com/)
 */
package com.ekino.oss.jcv.core.initializer;

import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

import com.ekino.oss.jcv.core.comparator.DateTimeFormatComparator;
import org.skyscreamer.jsonassert.ValueMatcher;

import static java.util.Optional.*;

public class DateTimeFormatComparatorInitializer implements TwoParametersComparatorInitializer<String> {

    private static final Map<String, DateTimeFormatter> PREDEFINED_FORMATTERS = initPredefinedFormatters();

    private static Map<String, DateTimeFormatter> initPredefinedFormatters() {
        Map<String, DateTimeFormatter> formatters = new HashMap<>();
        formatters.put("iso_local_date", DateTimeFormatter.ISO_LOCAL_DATE);
        formatters.put("iso_offset_date", DateTimeFormatter.ISO_OFFSET_DATE);
        formatters.put("iso_date", DateTimeFormatter.ISO_DATE);
        formatters.put("iso_local_time", DateTimeFormatter.ISO_LOCAL_TIME);
        formatters.put("iso_offset_time", DateTimeFormatter.ISO_OFFSET_TIME);
        formatters.put("iso_time", DateTimeFormatter.ISO_TIME);
        formatters.put("iso_local_date_time", DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        formatters.put("iso_offset_date_time", DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        formatters.put("iso_zoned_date_time", DateTimeFormatter.ISO_ZONED_DATE_TIME);
        formatters.put("iso_date_time", DateTimeFormatter.ISO_DATE_TIME);
        formatters.put("iso_ordinal_date", DateTimeFormatter.ISO_ORDINAL_DATE);
        formatters.put("iso_week_date", DateTimeFormatter.ISO_WEEK_DATE);
        formatters.put("iso_instant", DateTimeFormatter.ISO_INSTANT);
        formatters.put("basic_iso_date", DateTimeFormatter.BASIC_ISO_DATE);
        formatters.put("rfc_1123_date_time", DateTimeFormatter.RFC_1123_DATE_TIME);
        return formatters;
    }

    @Override
    public ValueMatcher<String> initComparator(String param1, String param2) {
        return new DateTimeFormatComparator(initFormatter(param1, param2));
    }

    private static DateTimeFormatter initFormatter(String pattern, String languageTag) {
        Optional<DateTimeFormatter> predefinedFormatter = getPredefinedFormatter(pattern);
        if (predefinedFormatter.isPresent()) {
            return predefinedFormatter.get();
        }

        if (languageTag != null) {
            return createLocalizedFormatter(pattern, languageTag);
        }

        return DateTimeFormatter.ofPattern(pattern);
    }

    private static DateTimeFormatter createLocalizedFormatter(String pattern, String languageTag) {

        Locale locale = Locale.forLanguageTag(languageTag);

        if ("und".equals(locale.toLanguageTag())) {
            throw new IllegalArgumentException("Invalid language tag " + languageTag);
        }

        return DateTimeFormatter.ofPattern(pattern, locale);
    }

    private static Optional<DateTimeFormatter> getPredefinedFormatter(String pattern) {
        return ofNullable(pattern)
            .map(String::toLowerCase)
            .map(PREDEFINED_FORMATTERS::get);
    }
}
