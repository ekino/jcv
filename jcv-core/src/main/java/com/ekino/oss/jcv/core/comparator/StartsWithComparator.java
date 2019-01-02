/*
 * Copyright (c) 2019 ekino (https://www.ekino.com/)
 */
package com.ekino.oss.jcv.core.comparator;

import com.ekino.oss.jcv.core.JsonValueComparator;

import org.skyscreamer.jsonassert.ValueMatcherException;

import static java.util.Objects.*;

public class StartsWithComparator implements JsonValueComparator<String> {

    private final String value;

    public StartsWithComparator(String value) {
        this.value = requireNonNull(value);
    }

    @Override
    public boolean hasCorrectValue(String actual, String expected) {
        if (actual != null && actual.startsWith(value)) {
            return true;
        }
        throw new ValueMatcherException("Value should start with '" + value + "'", expected, actual);
    }
}
