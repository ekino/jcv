/*
 * Copyright (c) 2019 ekino (https://www.ekino.com/)
 */
package com.ekino.oss.jcv.core.validator;

import com.ekino.oss.jcv.core.JsonContextMatcher;

import org.skyscreamer.jsonassert.Customization;

class PrefixMatcher implements JsonContextMatcher {

    private final Customization pathCustomization;

    public PrefixMatcher(String path) {
        pathCustomization = Customization.customization(path, (o1, o2) -> false);
    }

    @Override
    public boolean matches(String prefix, Object expectedValue, Object actualValue) {
        return pathCustomization.appliesToPath(prefix);
    }
}
