/*
 * Copyright (c) 2019 ekino (https://www.ekino.com/)
 */
package com.ekino.oss.jcv.core.validator;

import com.ekino.oss.jcv.core.JsonContextMatcher;
import com.ekino.oss.jcv.core.JsonValidator;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.skyscreamer.jsonassert.ValueMatcher;

@Getter
@RequiredArgsConstructor
class DefaultJsonValidator<T> implements JsonValidator<T> {

    private final JsonContextMatcher contextMatcher;
    private final ValueMatcher<T> valueComparator;
}
