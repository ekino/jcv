/*
 * Copyright (c) 2019 ekino (https://www.ekino.com/)
 */
package com.ekino.oss.jcv.core.validator

import com.ekino.oss.jcv.core.JsonContextMatcher
import com.ekino.oss.jcv.core.JsonValidator
import org.skyscreamer.jsonassert.ValueMatcher

internal class DefaultJsonValidator<T>(
    override val contextMatcher: JsonContextMatcher,
    override val valueComparator: ValueMatcher<T>
) : JsonValidator<T>
