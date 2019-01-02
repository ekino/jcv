/*
 * Copyright (c) 2019 ekino (https://www.ekino.com/)
 */
package com.ekino.oss.jcv.core.comparator;

import java.net.MalformedURLException;
import java.net.URL;

import com.ekino.oss.jcv.core.JsonValueComparator;

import org.skyscreamer.jsonassert.ValueMatcherException;

public class TemplatedURLComparator implements JsonValueComparator<String> {

    @Override
    public boolean hasCorrectValue(String actual, String expected) {
        try {
            String urlWithoutTemplate = actual.replaceAll("\\{\\?.+}$", "");
            //noinspection ResultOfMethodCallIgnored
            new URL(urlWithoutTemplate);
            return true;
        } catch (MalformedURLException e) {
            throw new ValueMatcherException("Value is not a valid templated URL", e, expected, actual);
        }
    }
}
