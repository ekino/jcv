/*
 * Copyright (c) 2019 ekino (https://www.ekino.com/)
 */
package com.ekino.oss.jcv.core.validator

import com.ekino.oss.jcv.core.initializer.OneParameterComparatorInitializer
import org.skyscreamer.jsonassert.ValueMatcher

internal class OneParameterTemplateValidator<T>(validatorId: String, private val parameterRequired: Boolean, private val comparatorProvider: OneParameterComparatorInitializer<T>) : ValueParameterizedTemplateValidator<T>(validatorId) {

    override fun getValueComparator(validatorTemplateManager: ValidatorTemplateManager): ValueMatcher<T> {
        val parameter = validatorTemplateManager.extractParameter(0)

        require(!(parameterRequired && parameter == null)) { "No parameter found in validator '" + validatorTemplateManager.extractTemplateContent() + "'" }

        return comparatorProvider.initComparator(parameter)
    }
}
