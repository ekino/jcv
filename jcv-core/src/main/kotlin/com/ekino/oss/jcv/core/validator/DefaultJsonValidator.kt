/*
 * Copyright (c) 2019 ekino (https://www.ekino.com/)
 */
package com.ekino.oss.jcv.core.validator

import com.ekino.oss.jcv.core.JsonContextMatcher
import com.ekino.oss.jcv.core.JsonValidator
import org.skyscreamer.jsonassert.ValueMatcher

internal class DefaultJsonValidator<T>(
  private val contextMatcher: JsonContextMatcher,
  private val valueComparator: ValueMatcher<T>,
) : JsonValidator<T> {
  override fun getContextMatcher(): JsonContextMatcher = contextMatcher

  override fun getValueComparator(): ValueMatcher<T> = valueComparator
}
