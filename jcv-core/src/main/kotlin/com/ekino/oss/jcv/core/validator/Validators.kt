/*
 * Copyright (c) 2019 ekino (https://www.ekino.com/)
 */
package com.ekino.oss.jcv.core.validator

import com.ekino.oss.jcv.core.JsonValidator
import com.ekino.oss.jcv.core.JsonValueComparator
import com.ekino.oss.jcv.core.comparator.ContainsComparator
import com.ekino.oss.jcv.core.comparator.EndsWithComparator
import com.ekino.oss.jcv.core.comparator.NotEmptyComparator
import com.ekino.oss.jcv.core.comparator.NotNullComparator
import com.ekino.oss.jcv.core.comparator.RegexComparator
import com.ekino.oss.jcv.core.comparator.StartsWithComparator
import com.ekino.oss.jcv.core.comparator.TemplatedURLComparator
import com.ekino.oss.jcv.core.comparator.TypeComparator
import com.ekino.oss.jcv.core.comparator.URLComparator
import com.ekino.oss.jcv.core.comparator.UUIDComparator
import com.ekino.oss.jcv.core.initializer.DateTimeFormatComparatorInitializer
import com.ekino.oss.jcv.core.initializer.Initializers.allOf
import com.ekino.oss.jcv.core.initializer.Initializers.comparatorWith1Parameter
import com.ekino.oss.jcv.core.initializer.Initializers.comparatorWith2Parameters
import com.ekino.oss.jcv.core.initializer.Initializers.comparatorWithoutParameter
import com.ekino.oss.jcv.core.initializer.Initializers.parameterizedValidator
import com.ekino.oss.jcv.core.initializer.asNoParameterComparatorInitializer
import com.ekino.oss.jcv.core.initializer.asOneParameterComparatorInitializer
import org.json.JSONArray
import org.json.JSONObject
import org.skyscreamer.jsonassert.ValueMatcher
import java.util.regex.Pattern

/**
 * Prepared validators.
 *
 * @author Leo Millon
 */
object Validators {

    /**
     * Validator for a specific json field path.
     *
     * @param path the json field path
     * @param comparator a json value comparator
     * @param <T>        the field value type
     *
     * @return the validator
     */
    @JvmStatic
    fun <T> forPath(path: String, comparator: ValueMatcher<T>): JsonValidator<T> =
        DefaultJsonValidator(PrefixMatcher(path), comparator)

    /**
     * Validator for a specific json field value type.
     *
     * @param id the validator id
     * @param expectedType the expected value type
     *
     * @return the validator
     */
    @JvmStatic
    fun type(id: String, expectedType: Class<*>): JsonValidator<Any> =
        templatedValidator(id, TypeComparator(expectedType))

    /**
     * Prepare "templated" validator for a given comparator.
     *
     * @param id the validator id
     * @param comparator a custom comparator
     * @param <T>        the field value type
     *
     * @return the validator
     */
    @JvmStatic
    fun <T> templatedValidator(id: String, comparator: JsonValueComparator<T>): JsonValidator<T> =
        DefaultValueTemplateIdValidator(id, comparator)

    /**
     * Default validators.
     *
     * @return a list of prepared validators
     */
    @JvmStatic
    fun defaultValidators(): List<JsonValidator<*>> {
        return listOf(
            parameterizedValidator(
                "contains",
                comparatorWith1Parameter(initializer = asOneParameterComparatorInitializer { ContainsComparator(it!!) })
            ),
            parameterizedValidator(
                "starts_with",
                comparatorWith1Parameter(initializer = asOneParameterComparatorInitializer { StartsWithComparator(it!!) })
            ),
            parameterizedValidator(
                "ends_with",
                comparatorWith1Parameter(initializer = asOneParameterComparatorInitializer { EndsWithComparator(it!!) })
            ),
            parameterizedValidator(
                "regex",
                comparatorWith1Parameter(initializer = asOneParameterComparatorInitializer {
                    RegexComparator(Pattern.compile(it!!))
                })
            ),
            templatedValidator("uuid", UUIDComparator()),
            templatedValidator("not_null", NotNullComparator()),
            templatedValidator("not_empty", NotEmptyComparator()),
            templatedValidator("url", URLComparator()),
            parameterizedValidator(
                "url_ending",
                allOf(
                    comparatorWithoutParameter(initializer = asNoParameterComparatorInitializer(::URLComparator)),
                    comparatorWith1Parameter(initializer = asOneParameterComparatorInitializer { EndsWithComparator(it!!) })
                )
            ),
            parameterizedValidator(
                "url_regex",
                allOf(
                    comparatorWithoutParameter(initializer = asNoParameterComparatorInitializer(::URLComparator)),
                    comparatorWith1Parameter(initializer = asOneParameterComparatorInitializer {
                        RegexComparator(Pattern.compile(it!!))
                    })
                )
            ),
            templatedValidator("templated_url", TemplatedURLComparator()),
            parameterizedValidator(
                "templated_url_ending",
                allOf(
                    comparatorWithoutParameter(initializer = asNoParameterComparatorInitializer(::TemplatedURLComparator)),
                    comparatorWith1Parameter(initializer = asOneParameterComparatorInitializer { EndsWithComparator(it!!) })
                )
            ),
            parameterizedValidator(
                "templated_url_regex",
                allOf(
                    comparatorWithoutParameter(initializer = asNoParameterComparatorInitializer(::TemplatedURLComparator)),
                    comparatorWith1Parameter(initializer = asOneParameterComparatorInitializer {
                        RegexComparator(Pattern.compile(it!!))
                    })
                )
            ),
            type("boolean_type", Boolean::class.java),
            type("string_type", String::class.java),
            type("number_type", Number::class.java),
            type("array_type", JSONArray::class.java),
            type("object_type", JSONObject::class.java),
            parameterizedValidator(
                "date_time_format",
                comparatorWith2Parameters(
                    param1Required = true,
                    param2Required = false,
                    initializer = DateTimeFormatComparatorInitializer()
                )
            )
        )
    }
}
