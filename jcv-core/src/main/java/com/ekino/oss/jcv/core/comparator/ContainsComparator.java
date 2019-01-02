/*
 * Copyright (c) 2019 ekino (https://www.ekino.com/)
 */
package com.ekino.oss.jcv.core.comparator;

import com.ekino.oss.jcv.core.JsonValueComparator;

import org.skyscreamer.jsonassert.ValueMatcherException;

import static java.util.Objects.*;

/**
 * A contains comparator on text.
 *
 * @author Leo Millon
 *
 * @see String#contains(CharSequence)
 */
public class ContainsComparator implements JsonValueComparator<String> {

    private final String value;

    /**
     * Init comparator with the value to search for.
     *
     * @param value the value to search for
     */
    public ContainsComparator(String value) {
        this.value = requireNonNull(value);
    }

    @Override
    public boolean hasCorrectValue(String actual, String expected) {
        if (actual != null && actual.contains(value)) {
            return true;
        }
        throw new ValueMatcherException("Value should contain '" + value + "'", expected, actual);
    }
}
