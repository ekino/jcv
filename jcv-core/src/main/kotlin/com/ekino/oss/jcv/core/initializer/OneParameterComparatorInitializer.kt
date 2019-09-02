/*
 * Copyright (c) 2019 ekino (https://www.ekino.com/)
 */
package com.ekino.oss.jcv.core.initializer

import org.skyscreamer.jsonassert.ValueMatcher

/**
 * Comparator intializer given tempated validator with 1 parameter information.
 *
 * @param <T> the field value type
 *
 * @author Leo Millon
 */
@FunctionalInterface
interface OneParameterComparatorInitializer<T> {

    /**
     * Init a comparator using the current templated validator info.
     *
     * @param parameter the first parameter of the templated validator
     *
     * @return the initalized comparator
     */
    fun initComparator(parameter: String?): ValueMatcher<T>
}

typealias KOneParameterComparatorInitializer<T> = (parameter: String?) -> ValueMatcher<T>

fun <T> asOneParameterComparatorInitializer(initializer: KOneParameterComparatorInitializer<T>) =
    object : OneParameterComparatorInitializer<T> {
        override fun initComparator(parameter: String?): ValueMatcher<T> = initializer(parameter)
    }
