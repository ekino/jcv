/*
 * Copyright (c) 2019 ekino (https://www.ekino.com/)
 */
package com.ekino.oss.jcv.core.initializer;

import org.skyscreamer.jsonassert.ValueMatcher;

/**
 * Comparator intializer.
 *
 * @param <T> the field value type
 *
 * @author Leo Millon
 */
@FunctionalInterface
public interface NoParameterComparatorInitializer<T> {

    /**
     * Init a comparator without template parameter.
     *
     * @return the initalized comparator
     */
    ValueMatcher<T> initComparator();
}
