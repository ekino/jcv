/*
 * Copyright (c) 2019 ekino (https://www.ekino.com/)
 */
package com.ekino.oss.jcv.core.comparator;

import java.util.UUID;

import com.ekino.oss.jcv.core.JsonValueComparator;

import org.skyscreamer.jsonassert.ValueMatcherException;

public class UUIDComparator implements JsonValueComparator<String> {

    @Override
    public boolean hasCorrectValue(String actual, String expected) {
        try {
            //noinspection ResultOfMethodCallIgnored
            UUID.fromString(actual);
            return true;
        } catch (IllegalArgumentException e) {
            throw new ValueMatcherException("Value is not a valid UUID", e, expected, actual);
        }
    }
}
