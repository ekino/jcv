/*
 * Copyright (c) 2019 ekino (https://www.ekino.com/)
 */
package com.ekino.oss.jcv.core.validator;

import org.skyscreamer.jsonassert.ValueMatcher;

abstract class ValueParameterizedTemplateValidator<T> extends ValueTemplateIdValidator<T> {

    public ValueParameterizedTemplateValidator(String validatorId) {
        super(validatorId);
    }

    @Override
    public ValueMatcher<T> getValueComparator() {
        return (actual, expected) -> {
            if (expected instanceof String) {
                return getValueComparator(new ValidatorTemplateManager((String) expected)).equal(actual, expected);
            }
            throw new IllegalArgumentException("Invalid template definition : " + expected);
        };
    }

    protected abstract ValueMatcher<T> getValueComparator(ValidatorTemplateManager validatorTemplateManager);
}
