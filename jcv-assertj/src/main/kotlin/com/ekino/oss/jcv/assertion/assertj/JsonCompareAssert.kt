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
        fun assertThatJson(actualJson: String): JsonCompareAssert {
            return JsonCompareAssert(
                actualJson,
                JsonComparator(JSONCompareMode.NON_EXTENSIBLE, Validators.defaultValidators())
            )
        }
    }

    /**
     * Creates a new instance of `[JsonCompareAssert] using a new comparator` with the same actual value.
     *
     * @param comparator the new comparator to use
     *
     * @return `this` assertion object
     */
    fun using(comparator: JsonComparator): JsonCompareAssert {
        return JsonCompareAssert(actual, comparator)
    }

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
    fun using(mode: JSONCompareMode, vararg validators: JsonValidator<*>): JsonCompareAssert {
        return using(mode, validators.toList())
    }

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
    fun using(mode: JSONCompareMode, validators: List<JsonValidator<*>>): JsonCompareAssert {
        return JsonCompareAssert(actual, JsonComparator(mode, validators))
    }

    /**
     * Creates a new instance of `[JsonCompareAssert] with a custom configuration` with the same actual value.
     *
     * @param validators the validators to use
     *
     * @return `this` assertion object
     *
     * @see .using
     */
    fun using(vararg validators: JsonValidator<*>): JsonCompareAssert {
        return using(validators.toList())
    }

    /**
     * Creates a new instance of `[JsonCompareAssert] with a custom configuration` with the same actual value.
     *
     * @param validators the validators to use
     *
     * @return `this` assertion object
     *
     * @see .using
     */
    fun using(validators: List<JsonValidator<*>>): JsonCompareAssert {
        return using(JSONCompareMode.NON_EXTENSIBLE, validators)
    }

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
            val result = JSONCompare.compareJSON(expectedJson, actual, jsonComparator)
            if (!result.passed()) {
                failWithMessage(result.message)
            }
        } catch (e: JSONException) {
            throw JsonParseException("Error with provided JSON Strings", e)
        }

        return this
    }
}
