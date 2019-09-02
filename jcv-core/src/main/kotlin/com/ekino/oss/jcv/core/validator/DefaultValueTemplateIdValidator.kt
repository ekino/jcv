/*
 * Copyright (c) 2019 ekino (https://www.ekino.com/)
 */
package com.ekino.oss.jcv.core.validator

import org.skyscreamer.jsonassert.ValueMatcher

internal class DefaultValueTemplateIdValidator<T>(validatorId: String, private val comparator: ValueMatcher<T>) :
    ValueTemplateIdValidator<T>(validatorId) {

    override val valueComparator: ValueMatcher<T>
        get() = comparator
}
