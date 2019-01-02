/*
 * Copyright (c) 2019 ekino (https://www.ekino.com/)
 */
package com.ekino.oss.jcv.core.comparator;

import java.util.Objects;

import com.ekino.oss.jcv.core.JsonValueComparator;

import org.skyscreamer.jsonassert.ValueMatcherException;

public class NotEmptyComparator implements JsonValueComparator<String> {

    @Override
    public boolean hasCorrectValue(String actual, String expected) {
        if (actual != null && !actual.isEmpty()) {
            return true;
        }
        throw new ValueMatcherException("Value should not be empty", Objects.toString(expected), Objects.toString(actual));
    }
}
