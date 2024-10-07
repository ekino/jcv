/*
 * Copyright (c) 2019 ekino (https://www.ekino.com/)
 */
package com.ekino.oss.jcv.assertion.assertj

import com.ekino.oss.jcv.assertion.assertj.exception.JsonParseException
import com.ekino.oss.jcv.core.JsonComparator
import com.ekino.oss.jcv.core.JsonValidator
import com.ekino.oss.jcv.core.validator.Validators
import org.assertj.core.api.AbstractAssert
import org.json.JSONException
import org.skyscreamer.jsonassert.JSONCompare
import org.skyscreamer.jsonassert.JSONCompareMode
import java.util.Objects

/**
 * Assertion to make [JsonComparator] available as AssertJ utilities.
 *
 * @author Leo Millon
 */
class JsonCompareAssert(actualJson: String, private val jsonComparator: JsonComparator) :
  AbstractAssert<JsonCompareAssert, String>(actualJson, JsonCompareAssert::class.java) {

  companion object {

    /**
     * Creates a new instance of `[JsonCompareAssert] from a [String]`.
     *
     * @param actualJson the actual value
     *
     * @return the created assertion object
     */
    @JvmStatic
    fun assertThatJson(actualJson: String) =
      JsonCompareAssert(
        actualJson,
        JsonComparator(JSONCompareMode.NON_EXTENSIBLE, Validators.defaultValidators()),
      )
  }

  /**
   * Creates a new instance of `[JsonCompareAssert] using a new comparator` with the same actual value.
   *
   * @param comparator the new comparator to use
   *
   * @return `this` assertion object
   */
  fun using(comparator: JsonComparator) = JsonCompareAssert(actual, comparator)

  /**
   * Creates a new instance of `[JsonCompareAssert] with a custom configuration` with the same actual value.
   *
   * @param mode the compare mode
   * @param validators the validators to use
   *
   * @return `this` assertion object
   *
   * @see .using
   */
  @SafeVarargs
  fun <T : JsonValidator<*>> using(mode: JSONCompareMode, vararg validators: T) =
    using(mode, validators.toList())

  /**
   * Creates a new instance of `[JsonCompareAssert] with a custom configuration` with the same actual value.
   *
   * @param mode the compare mode
   * @param validators the validators to use
   *
   * @return `this` assertion object
   *
   * @see .using
   */
  fun <T : JsonValidator<*>> using(mode: JSONCompareMode, validators: List<T>) =
    JsonCompareAssert(actual, JsonComparator(mode, validators))

  /**
   * Creates a new instance of `[JsonCompareAssert] with a custom configuration` with the same actual value.
   *
   * @param validators the validators to use
   *
   * @return `this` assertion object
   *
   * @see .using
   */
  @SafeVarargs
  fun <T : JsonValidator<*>> using(vararg validators: T) = using(validators.toList())

  /**
   * Creates a new instance of `[JsonCompareAssert] with a custom configuration` with the same actual value.
   *
   * @param validators the validators to use
   *
   * @return `this` assertion object
   *
   * @see .using
   */
  fun <T : JsonValidator<*>> using(validators: List<T>) =
    using(JSONCompareMode.NON_EXTENSIBLE, validators)

  /**
   * Verifies that the actual JSON value is valid against the given JSON.
   *
   * @param expectedJson the given value to compare the actual value to.
   *
   * @return `this` assertion object.
   *
   * @throws JSONException if the actual or expected string is not a valid JSON format.
   * @see JSONCompare.compareJSON
   */
  fun isValidAgainst(expectedJson: String): JsonCompareAssert {
    isNotNull

    Objects.requireNonNull<Any>(jsonComparator, "Json comparator definition is missing")

    try {
      JSONCompare.compareJSON(expectedJson, actual, jsonComparator)
        .takeUnless { it.passed() }
        ?.also { failWithMessage(it.message) }
    } catch (e: JSONException) {
      throw JsonParseException("Error with provided JSON Strings", e)
    }

    return this
  }
}
