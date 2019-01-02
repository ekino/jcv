/*
 * Copyright (c) 2019 ekino (https://www.ekino.com/)
 */
package com.ekino.oss.jcv.core.validator;

import com.ekino.oss.jcv.core.initializer.TemplatedComparatorInitializer;
import org.skyscreamer.jsonassert.ValueMatcher;

public class DefaultParameterizedTemplateValidator<T> extends ValueParameterizedTemplateValidator<T> {

    private final TemplatedComparatorInitializer<T> comparatorInitializer;

    public DefaultParameterizedTemplateValidator(String validatorId, TemplatedComparatorInitializer<T> comparatorInitializer) {
        super(validatorId);
        this.comparatorInitializer = comparatorInitializer;
    }

    @Override
    protected ValueMatcher<T> getValueComparator(ValidatorTemplateManager validatorTemplateManager) {
        return comparatorInitializer.initComparator(validatorTemplateManager);
    }
}
