/*
 * Copyright (c) 2019 ekino (https://www.ekino.com/)
 */
package com.ekino.oss.jcv.core.validator

import com.ekino.oss.jcv.core.JsonContextMatcher
import com.ekino.oss.jcv.core.JsonValidator

abstract class ValueTemplateIdValidator<T>(validatorId: String) : JsonValidator<T> {

  private val matcher: ValidatorIdInValueMatcher = ValidatorIdInValueMatcher(validatorId)

  override fun getContextMatcher(): JsonContextMatcher = matcher
}
