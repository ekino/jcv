/*
 * Copyright (c) 2019 ekino (https://www.ekino.com/)
 */
package com.ekino.oss.jcv.core.validator

import com.ekino.oss.jcv.core.JsonValidator
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
import org.json.JSONArray
import org.json.JSONObject
import org.skyscreamer.jsonassert.ValueMatcher

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
  fun <T> templatedValidator(id: String, comparator: ValueMatcher<T>): JsonValidator<T> =
    DefaultValueTemplateIdValidator(id, comparator)

  /**
   * Default validators.
   *
   * @return a list of prepared validators
   */
  @JvmStatic
  fun defaultValidators(): List<JsonValidator<*>> = validators {
    +templatedValidator<String>("contains") {
      comparatorWith1RequiredParameter(::ContainsComparator)
    }
    +templatedValidator<String>("starts_with") {
      comparatorWith1RequiredParameter(::StartsWithComparator)
    }
    +templatedValidator<String>("ends_with") {
      comparatorWith1RequiredParameter(::EndsWithComparator)
    }
    +templatedValidator<String>("regex") {
      comparatorWith1RequiredParameter {
        RegexComparator(it.toRegex().toPattern())
      }
    }
    +templatedValidator("uuid", UUIDComparator())
    +templatedValidator("not_null", NotNullComparator())
    +templatedValidator("not_empty", NotEmptyComparator())
    +templatedValidator("url", URLComparator())
    +templatedValidator<String>("url_ending") {
      allOf {
        +URLComparator()
        +comparatorWith1RequiredParameter(::EndsWithComparator)
      }
    }
    +templatedValidator<String>("url_regex") {
      allOf {
        +URLComparator()
        +comparatorWith1RequiredParameter {
          RegexComparator(it.toRegex().toPattern())
        }
      }
    }
    +templatedValidator("templated_url", TemplatedURLComparator())
    +templatedValidator<String>("templated_url_ending") {
      allOf {
        +TemplatedURLComparator()
        +comparatorWith1RequiredParameter(::EndsWithComparator)
      }
    }
    +templatedValidator<String>("templated_url_regex") {
      allOf {
        +TemplatedURLComparator()
        +comparatorWith1RequiredParameter {
          RegexComparator(it.toRegex().toPattern())
        }
      }
    }
    +templatedValidator("boolean_type", typeComparator<Boolean>())
    +templatedValidator("string_type", typeComparator<String>())
    +templatedValidator("number_type", typeComparator<Number>())
    +templatedValidator("array_type", typeComparator<JSONArray>())
    +templatedValidator("object_type", typeComparator<JSONObject>())
    +templatedValidator<String>("date_time_format") {
      comparatorWithParameters {
        DateTimeFormatComparatorInitializer().initComparator(getFirstRequiredParam(), getSecondParam())
      }
    }
  }
}
