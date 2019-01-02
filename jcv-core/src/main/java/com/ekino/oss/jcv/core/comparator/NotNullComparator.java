/*
 * Copyright (c) 2019 ekino (https://www.ekino.com/)
 */
package com.ekino.oss.jcv.core.comparator;

import java.util.Objects;

import com.ekino.oss.jcv.core.JsonValueComparator;

import org.skyscreamer.jsonassert.ValueMatcherException;

public class NotNullComparator implements JsonValueComparator<Object> {

    @Override
    public boolean hasCorrectValue(Object actual, Object expected) {
        if (actual != null && !(actual instanceof String) && !"null".equals(actual.toString())) {
            return true;
        }
        throw new ValueMatcherException("Value should not be null", Objects.toString(expected), Objects.toString(actual));
    }
}
