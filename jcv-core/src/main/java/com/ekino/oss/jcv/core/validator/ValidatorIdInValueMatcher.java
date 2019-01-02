/*
 * Copyright (c) 2019 ekino (https://www.ekino.com/)
 */
package com.ekino.oss.jcv.core.validator;

import com.ekino.oss.jcv.core.JsonContextMatcher;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import static java.util.Optional.*;

@RequiredArgsConstructor
class ValidatorIdInValueMatcher implements JsonContextMatcher {

    @Getter
    private final String validatorId;

    @Override
    public boolean matches(String prefix, Object expectedValue, Object actualValue) {

        return ofNullable(expectedValue)
            .filter(String.class::isInstance)
            .map(String.class::cast)
            .map(ValidatorTemplateManager::new)
            .map(ValidatorTemplateManager::extractId)
            .map(validatorId::equalsIgnoreCase)
            .orElse(false);
    }
}
