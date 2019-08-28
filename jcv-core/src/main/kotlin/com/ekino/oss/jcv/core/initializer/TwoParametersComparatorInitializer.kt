/*
 * Copyright (c) 2019 ekino (https://www.ekino.com/)
 */
package com.ekino.oss.jcv.core.initializer

import org.skyscreamer.jsonassert.ValueMatcher

/**
 * Comparator intializer given tempated validator with 2 parameters information.
 *
 * @param <T> the field value type
 *
 * @author Leo Millon
 */
@FunctionalInterface
interface TwoParametersComparatorInitializer<T> {

    /**
     * Init a comparator using the current templated validator info.
     *
     * @param param1 the first parameter of the templated validator
     * @param param2 the second parameter of the templated validator
     *
     * @return the initalized comparator
     */
    fun initComparator(param1: String?, param2: String?): ValueMatcher<T>
}

typealias KTwoParametersComparatorInitializer<T> = (param1: String?, param2: String?) -> ValueMatcher<T>

fun <T> KTwoParametersComparatorInitializer<T>.asTwoParametersComparatorInitializer() =
    object : TwoParametersComparatorInitializer<T> {
        override fun initComparator(param1: String?, param2: String?): ValueMatcher<T> = invoke(param1, param2)
    }
