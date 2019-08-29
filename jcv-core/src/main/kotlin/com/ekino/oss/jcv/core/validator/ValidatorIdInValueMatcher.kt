/*
 * Copyright (c) 2019 ekino (https://www.ekino.com/)
 */
package com.ekino.oss.jcv.core.validator

import com.ekino.oss.jcv.core.JsonContextMatcher

internal class ValidatorIdInValueMatcher(private val validatorId: String) : JsonContextMatcher {

    override fun matches(prefix: String, expectedValue: Any?, actualValue: Any?): Boolean {
        return expectedValue
            ?.takeIf { it is String }
            ?.let { ValidatorTemplateManager(it as String).extractId() }
            ?.let { validatorId.equals(it, ignoreCase = true) }
            ?: false
    }
}
