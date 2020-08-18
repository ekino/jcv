/*
 * Copyright (c) 2019 ekino (https://www.ekino.com/)
 */
package com.ekino.oss.jcv.core.initializer

import org.skyscreamer.jsonassert.ValueMatcher

typealias KNoParameterComparatorInitializer<T> = () -> ValueMatcher<T>

fun <T> asNoParameterComparatorInitializer(initializer: KNoParameterComparatorInitializer<T>) =
  NoParameterComparatorInitializer<T> { initializer() }
