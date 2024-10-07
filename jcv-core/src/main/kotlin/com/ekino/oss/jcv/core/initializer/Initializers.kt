/*
 * Copyright (c) 2019 ekino (https://www.ekino.com/)
 */
package com.ekino.oss.jcv.core.initializer

import com.ekino.oss.jcv.core.JsonValidator
import com.ekino.oss.jcv.core.validator.DefaultParameterizedTemplateValidator
import com.ekino.oss.jcv.core.validator.ValidatorTemplateManager
import org.skyscreamer.jsonassert.ValueMatcher

object Initializers {

  /**
   * Prepare "parameterized" validator for a given comparator.
   *
   * @param id the validator id
   * @param initializer the comparator init with the parameter
   * @param <T>                   the field value type
   *
   * @return the validator
   */
  @JvmStatic
  fun <T> parameterizedValidator(
    id: String,
    initializer: TemplatedComparatorInitializer<T>,
  ): JsonValidator<T> =
    DefaultParameterizedTemplateValidator(id, initializer)

  @JvmStatic
  fun <T> comparatorWithoutParameter(initializer: NoParameterComparatorInitializer<T>): TemplatedComparatorInitializer<T> =
    TemplatedComparatorInitializer<T> { initializer.initComparator() }

  @JvmStatic
  @JvmOverloads
  fun <T> comparatorWith1Parameter(
    required: Boolean = true,
    initializer: OneParameterComparatorInitializer<T>,
  ): TemplatedComparatorInitializer<T> =
    TemplatedComparatorInitializer<T> { validatorTemplateManager ->
      val parameter = getOrThrowParameter(0, required, validatorTemplateManager)
      initializer.initComparator(parameter)
    }

  @JvmStatic
  fun <T> comparatorWith2Parameters(
    param1Required: Boolean = true,
    param2Required: Boolean = true,
    initializer: TwoParametersComparatorInitializer<T>,
  ): TemplatedComparatorInitializer<T> {
    return TemplatedComparatorInitializer<T> { validatorTemplateManager ->
      val parameter1 = getOrThrowParameter(0, param1Required, validatorTemplateManager)
      val parameter2 = getOrThrowParameter(1, param2Required, validatorTemplateManager)
      initializer.initComparator(parameter1, parameter2)
    }
  }

  private fun getOrThrowParameter(
    index: Int,
    required: Boolean,
    validatorTemplateManager: ValidatorTemplateManager,
  ): String? {
    val parameter = validatorTemplateManager.extractParameter(index)
    require(!(required && parameter == null)) {
      "No parameter at index $index found in validator '${validatorTemplateManager.extractTemplateContent()}'"
    }
    return parameter
  }

  @JvmStatic
  @SafeVarargs
  fun <T> allOf(vararg initializers: TemplatedComparatorInitializer<in T>): TemplatedComparatorInitializer<T> =
    TemplatedComparatorInitializer<T> { validatorTemplateManager ->
      ValueMatcher { actual, expected ->
        sequenceOf(*initializers)
          .map { it.initComparator(validatorTemplateManager) }
          .all { it.equal(actual, expected) }
      }
    }

  @JvmStatic
  @SafeVarargs
  fun <T> anyOf(vararg initializers: TemplatedComparatorInitializer<in T>): TemplatedComparatorInitializer<T> =
    TemplatedComparatorInitializer<T> { validatorTemplateManager ->
      ValueMatcher { actual, expected ->
        sequenceOf(*initializers)
          .map { it.initComparator(validatorTemplateManager) }
          .any { it.equal(actual, expected) }
      }
    }
}
