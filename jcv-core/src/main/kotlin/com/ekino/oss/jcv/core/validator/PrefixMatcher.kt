/*
 * Copyright (c) 2019 ekino (https://www.ekino.com/)
 */
package com.ekino.oss.jcv.core.validator

import com.ekino.oss.jcv.core.JsonContextMatcher
import org.skyscreamer.jsonassert.Customization

internal class PrefixMatcher(path: String) : JsonContextMatcher {

    private val pathCustomization: Customization = Customization.customization(path) { _, _ -> false }

    override fun matches(prefix: String, expectedValue: Any?, actualValue: Any?): Boolean {
        return pathCustomization.appliesToPath(prefix)
    }
}
