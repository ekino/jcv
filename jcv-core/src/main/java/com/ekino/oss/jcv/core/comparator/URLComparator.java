/*
 * Copyright (c) 2019 ekino (https://www.ekino.com/)
 */
package com.ekino.oss.jcv.core.comparator;

import java.net.MalformedURLException;
import java.net.URL;

import com.ekino.oss.jcv.core.JsonValueComparator;

import org.skyscreamer.jsonassert.ValueMatcherException;

public class URLComparator implements JsonValueComparator<String> {

    @Override
    public boolean hasCorrectValue(String actual, String expected) {
        try {
            //noinspection ResultOfMethodCallIgnored
            new URL(actual);
            return true;
        } catch (MalformedURLException e) {
            throw new ValueMatcherException("Value is not a valid URL", e, expected, actual);
        }
    }
}
