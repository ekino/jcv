/*
 * Copyright (c) 2019 ekino (https://www.ekino.com/)
 */
package com.ekino.oss.jcv.core.initializer;

import org.skyscreamer.jsonassert.ValueMatcher;

/**
 * Comparator intializer given tempated validator with 1 parameter information.
 *
 * @param <T> the field value type
 *
 * @author Leo Millon
 */
@FunctionalInterface
public interface OneParameterComparatorInitializer<T> {

    /**
     * Init a comparator using the current templated validator info.
     *
     * @param parameter the first parameter of the templated validator
     *
     * @return the initalized comparator
     */
    ValueMatcher<T> initComparator(String parameter);
}
