/*
 * Copyright (c) 2019 ekino (https://www.ekino.com/)
 */
package com.ekino.oss.jcv.core.validator;

import com.ekino.oss.jcv.core.initializer.OneParameterComparatorInitializer;
import org.skyscreamer.jsonassert.ValueMatcher;

class OneParameterTemplateValidator<T> extends ValueParameterizedTemplateValidator<T> {

    private final boolean parameterRequired;
    private final OneParameterComparatorInitializer<T> comparatorProvider;

    public OneParameterTemplateValidator(String validatorId, boolean parameterRequired, OneParameterComparatorInitializer<T> comparatorProvider) {
        super(validatorId);
        this.parameterRequired = parameterRequired;
        this.comparatorProvider = comparatorProvider;
    }

    @Override
    protected ValueMatcher<T> getValueComparator(ValidatorTemplateManager validatorTemplateManager) {
        String parameter = validatorTemplateManager.extractParameter(0).orElse(null);

        if (parameterRequired && parameter == null) {
            throw new IllegalArgumentException("No parameter found in validator '" + validatorTemplateManager.extractTemplateContent() + "'");
        }

        return comparatorProvider.initComparator(parameter);
    }
}
