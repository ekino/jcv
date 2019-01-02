/*
 * Copyright (c) 2019 ekino (https://www.ekino.com/)
 */
package com.ekino.oss.jcv.core.validator;

import com.ekino.oss.jcv.core.JsonContextMatcher;
import com.ekino.oss.jcv.core.JsonValidator;

abstract class ValueTemplateIdValidator<T> implements JsonValidator<T> {

    private final ValidatorIdInValueMatcher matcher;

    public ValueTemplateIdValidator(String validatorId) {
        this.matcher = new ValidatorIdInValueMatcher(validatorId);
    }

    @Override
    public JsonContextMatcher getContextMatcher() {
        return matcher;
    }
}
