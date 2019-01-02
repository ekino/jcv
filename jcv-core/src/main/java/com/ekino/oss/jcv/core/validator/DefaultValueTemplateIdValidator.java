/*
 * Copyright (c) 2019 ekino (https://www.ekino.com/)
 */
package com.ekino.oss.jcv.core.validator;

import org.skyscreamer.jsonassert.ValueMatcher;

class DefaultValueTemplateIdValidator<T> extends ValueTemplateIdValidator<T> {

    private final ValueMatcher<T> comparator;

    public DefaultValueTemplateIdValidator(String validatorId, ValueMatcher<T> comparator) {
        super(validatorId);
        this.comparator = comparator;
    }

    @Override
    public ValueMatcher<T> getValueComparator() {
        return comparator;
    }
}
