/*
 * Copyright (c) 2019 ekino (https://www.ekino.com/)
 */
package com.ekino.oss.jcv.core.validator

import com.ekino.oss.jcv.core.initializer.TemplatedComparatorInitializer
import org.skyscreamer.jsonassert.ValueMatcher

class DefaultParameterizedTemplateValidator<T>(
  validatorId: String,
  private val comparatorInitializer: TemplatedComparatorInitializer<T>
) : ValueParameterizedTemplateValidator<T>(validatorId) {

  override fun getValueComparator(validatorTemplateManager: ValidatorTemplateManager): ValueMatcher<T> {
    return comparatorInitializer.initComparator(validatorTemplateManager)
  }
}
