/*
 * Copyright (c) 2019 ekino (https://www.ekino.com/)
 */
package com.ekino.oss.jcv.core.comparator;

import java.util.Objects;

import com.ekino.oss.jcv.core.JsonValueComparator;

import org.skyscreamer.jsonassert.ValueMatcherException;

import static java.util.Objects.*;

public class TypeComparator implements JsonValueComparator<Object> {

    private final Class expectedType;

    public TypeComparator(Class expectedType) {
        this.expectedType = requireNonNull(expectedType);
    }

    @Override
    public boolean hasCorrectValue(Object actual, Object expected) {
        if (expectedType.isInstance(actual)) {
            return true;
        }
        throw new ValueMatcherException("Invalid value type", Objects.toString(expected), Objects.toString(actual));
    }
}
