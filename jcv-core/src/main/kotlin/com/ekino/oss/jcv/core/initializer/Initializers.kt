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
     * @param comparatorInitializer the comparator init with the parameter
     * @param <T>                   the field value type
     *
     * @return the validator
     */
    fun <T> parameterizedValidator(
        id: String,
        comparatorInitializer: TemplatedComparatorInitializer<T>
    ): JsonValidator<T> =
        DefaultParameterizedTemplateValidator(id, comparatorInitializer)

    fun <T> comparatorWithoutParameter(comparatorInitializer: NoParameterComparatorInitializer<T>): TemplatedComparatorInitializer<T> =
        object : TemplatedComparatorInitializer<T> {
            override fun initComparator(validatorTemplateManager: ValidatorTemplateManager): ValueMatcher<T> =
                comparatorInitializer.initComparator()
        }

    fun <T> comparatorWith1Parameter(comparatorInitializer: OneParameterComparatorInitializer<T>): TemplatedComparatorInitializer<T> {
        return comparatorWith1Parameter(true, comparatorInitializer)
    }

    fun <T> comparatorWith1Parameter(required: Boolean, comparatorInitializer: OneParameterComparatorInitializer<T>): TemplatedComparatorInitializer<T> =
        object : TemplatedComparatorInitializer<T> {
            override fun initComparator(validatorTemplateManager: ValidatorTemplateManager): ValueMatcher<T> {
                val parameter = getOrThrowParameter(0, required, validatorTemplateManager)
                return comparatorInitializer.initComparator(parameter)
            }
        }

    fun <T> comparatorWith2Parameters(param1Required: Boolean, param2Required: Boolean, comparatorInitializer: TwoParametersComparatorInitializer<T>): TemplatedComparatorInitializer<T> {
        return object : TemplatedComparatorInitializer<T> {
            override fun initComparator(validatorTemplateManager: ValidatorTemplateManager): ValueMatcher<T> {
                val parameter1 = getOrThrowParameter(0, param1Required, validatorTemplateManager)
                val parameter2 = getOrThrowParameter(1, param2Required, validatorTemplateManager)
                return comparatorInitializer.initComparator(parameter1, parameter2)
            }
        }
    }

    private fun getOrThrowParameter(index: Int, required: Boolean, validatorTemplateManager: ValidatorTemplateManager): String? {
        val parameter = validatorTemplateManager.extractParameter(index)
        if (required && parameter == null) {
            throw IllegalArgumentException(String.format(
                "No parameter at index %s found in validator '%s'",
                index,
                validatorTemplateManager.extractTemplateContent()
            ))
        }
        return parameter
    }

    @SafeVarargs
    fun <T> allOf(vararg initializers: TemplatedComparatorInitializer<in T>): TemplatedComparatorInitializer<T> =
        object : TemplatedComparatorInitializer<T> {
            override fun initComparator(validatorTemplateManager: ValidatorTemplateManager): ValueMatcher<T> {
                return ValueMatcher { actual, expected ->
                    sequenceOf(*initializers)
                        .map { it.initComparator(validatorTemplateManager) }
                        .all { it.equal(actual, expected) }
                }
            }
        }

    @SafeVarargs
    fun <T> anyOf(vararg initializers: TemplatedComparatorInitializer<in T>): TemplatedComparatorInitializer<T> =
        object : TemplatedComparatorInitializer<T> {
            override fun initComparator(validatorTemplateManager: ValidatorTemplateManager): ValueMatcher<T> {
                return ValueMatcher { actual, expected ->
                    sequenceOf(*initializers)
                        .map { it.initComparator(validatorTemplateManager) }
                        .any { it.equal(actual, expected) }
                }
            }
        }
}
