/*
 * Copyright (c) 2019 ekino (https://www.ekino.com/)
 */
package com.ekino.oss.jcv.core.comparator;

import java.util.regex.Pattern;

import com.ekino.oss.jcv.core.JsonValueComparator;

import lombok.RequiredArgsConstructor;
import org.skyscreamer.jsonassert.ValueMatcherException;

@RequiredArgsConstructor
public class RegexComparator implements JsonValueComparator<String> {

    private final Pattern pattern;

    @Override
    public boolean hasCorrectValue(String actual, String expected) {
        if (actual != null && pattern.matcher(actual).matches()) {
            return true;
        }
        throw new ValueMatcherException("Value does not match pattern /" + pattern + "/", expected, actual);
    }
}
