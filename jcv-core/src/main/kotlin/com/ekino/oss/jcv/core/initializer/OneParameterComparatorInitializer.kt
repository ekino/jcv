/*
 * Copyright (c) 2019 ekino (https://www.ekino.com/)
 */
package com.ekino.oss.jcv.core.initializer

import org.skyscreamer.jsonassert.ValueMatcher

typealias KOneParameterComparatorInitializer<T> = (parameter: String?) -> ValueMatcher<T>

fun <T> asOneParameterComparatorInitializer(initializer: KOneParameterComparatorInitializer<T>) =
  OneParameterComparatorInitializer<T> { parameter -> initializer(parameter) }
