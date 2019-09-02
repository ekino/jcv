/*
 * Copyright (c) 2019 ekino (https://www.ekino.com/)
 */
package com.ekino.oss.jcv.core.validator

import org.skyscreamer.jsonassert.ValueMatcher

abstract class ValueParameterizedTemplateValidator<T>(validatorId: String) : ValueTemplateIdValidator<T>(validatorId) {

    override val valueComparator: ValueMatcher<T>
        get() = ValueMatcher { actual, expected ->
            if (expected is String) {
                return@ValueMatcher getValueComparator(ValidatorTemplateManager(expected)).equal(actual, expected)
            }
            throw IllegalArgumentException("Invalid template definition : $expected")
        }

    protected abstract fun getValueComparator(validatorTemplateManager: ValidatorTemplateManager): ValueMatcher<T>
}
