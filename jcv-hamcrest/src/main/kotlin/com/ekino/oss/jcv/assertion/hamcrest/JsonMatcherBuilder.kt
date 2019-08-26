/*
 * Copyright (c) 2019 ekino (https://www.ekino.com/)
 */
package com.ekino.oss.jcv.assertion.hamcrest

import com.ekino.oss.jcv.core.JsonComparator
import com.ekino.oss.jcv.core.JsonValidator
import com.ekino.oss.jcv.core.validator.Validators
import org.skyscreamer.jsonassert.JSONCompareMode

/**
 * Builder to make [JsonCompareMatcher] easy to configure.
 *
 * @author Leo Millon
 */
class JsonMatcherBuilder {

    constructor()

    constructor(mode: JSONCompareMode, validators: List<JsonValidator<*>>) {
        this.mode = mode
        this.validators = validators
    }

    private lateinit var mode: JSONCompareMode
    private lateinit var validators: List<JsonValidator<*>>

    companion object {

        /**
         * Create a new instance of the builder with default values.
         *
         * @return a new builder instance (`mode` is [JSONCompareMode.NON_EXTENSIBLE] and
         * `validators` are [Validators.defaultValidators]
         */
        @JvmStatic
        fun create(): JsonMatcherBuilder {
            return JsonMatcherBuilder(JSONCompareMode.NON_EXTENSIBLE, Validators.defaultValidators())
        }
    }

    /**
     * Use the given mode.
     *
     * @param mode the mode to use for the matcher
     *
     * @return the current builder
     *
     * @see JSONCompareMode
     */
    fun mode(mode: JSONCompareMode): JsonMatcherBuilder {
        this.mode = mode
        return this
    }

    /**
     * Use the given validators.
     *
     * @param validators the validators to use for the matcher
     *
     * @return the current builder
     *
     * @see Validators.defaultValidators
     * @see .validators
     */
    fun <T : JsonValidator<*>> validators(validators: List<T>): JsonMatcherBuilder {
        this.validators = validators
        return this
    }

    /**
     * Use the given validators.
     *
     * @param validators the validators to use for the matcher
     *
     * @return the current builder
     *
     * @see Validators.defaultValidators
     * @see .validators
     */
    @SafeVarargs
    fun <T : JsonValidator<*>> validators(vararg validators: T): JsonMatcherBuilder {
        return validators(validators.toList())
    }

    /**
     * Build the matcher to be valid agains the given json.
     *
     * @param expectedJson the expected json to compare with the actual one
     *
     * @return the matcher
     */
    fun build(expectedJson: String): JsonCompareMatcher {
        return JsonCompareMatcher(JsonComparator(mode, validators), expectedJson)
    }
}
