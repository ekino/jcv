/*
 * Copyright (c) 2019 ekino (https://www.ekino.com/)
 */
package com.ekino.oss.jcv.core.comparator;

import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import com.ekino.oss.jcv.core.JsonValueComparator;
import lombok.RequiredArgsConstructor;
import org.skyscreamer.jsonassert.ValueMatcherException;

@RequiredArgsConstructor
public class DateTimeFormatComparator implements JsonValueComparator<String> {

    private final DateTimeFormatter dateTimeFormatter;

    @Override
    public boolean hasCorrectValue(String actual, String expected) {
        try {
            //noinspection ResultOfMethodCallIgnored
            dateTimeFormatter.parse(actual);
            return true;
        } catch (DateTimeParseException e) {
            throw new ValueMatcherException(
                "Invalid date time format",
                e, expected, actual);
        }
    }
}
