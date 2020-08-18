/*
 * Copyright (c) 2019 ekino (https://www.ekino.com/)
 */
package com.ekino.oss.jcv.core.initializer;

import org.skyscreamer.jsonassert.ValueMatcher;

/**
 * Comparator intializer given tempated validator with 2 parameters information.
 *
 * @param <T> the field value type
 * @author Leo Millon
 */
@FunctionalInterface
public interface TwoParametersComparatorInitializer<T> {

    /**
     * Init a comparator using the current templated validator info.
     *
     * @param param1 the first parameter of the templated validator
     * @param param2 the second parameter of the templated validator
     * @return the initalized comparator
     */
    ValueMatcher<T> initComparator(String param1, String param2);
}
