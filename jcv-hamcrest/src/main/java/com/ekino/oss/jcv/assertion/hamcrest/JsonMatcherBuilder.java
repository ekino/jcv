/*
 * Copyright (c) 2019 ekino (https://www.ekino.com/)
 */
package com.ekino.oss.jcv.assertion.hamcrest;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import com.ekino.oss.jcv.core.JsonComparator;
import com.ekino.oss.jcv.core.JsonValidator;
import com.ekino.oss.jcv.core.validator.Validators;
import org.skyscreamer.jsonassert.JSONCompareMode;

import static java.util.Objects.*;

/**
 * Builder to make {@link JsonCompareMatcher} easy to configure.
 *
 * @author Leo Millon
 */
public class JsonMatcherBuilder {

    private JSONCompareMode mode;
    private List<JsonValidator<?>> validators;

    protected JsonMatcherBuilder(JSONCompareMode mode, List<JsonValidator<?>> validators) {
        this.mode = requireNonNull(mode);
        this.validators = new LinkedList<>(requireNonNull(validators));
    }

    /**
     * Create a new instance of the builder with default values.
     *
     * @return a new builder instance ({@code mode} is {@link JSONCompareMode#NON_EXTENSIBLE} and
     * {@code validators} are {@link Validators#defaultValidators()}
     */
    public static JsonMatcherBuilder create() {
        return new JsonMatcherBuilder(JSONCompareMode.NON_EXTENSIBLE, Validators.defaultValidators());
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
    public JsonMatcherBuilder mode(JSONCompareMode mode) {
        this.mode = requireNonNull(mode);
        return this;
    }

    /**
     * Use the given validators.
     *
     * @param validators the validators to use for the matcher
     *
     * @return the current builder
     *
     * @see Validators#defaultValidators()
     * @see #validators(JsonValidator[])
     */
    public JsonMatcherBuilder validators(List<JsonValidator<?>> validators) {
        this.validators = new LinkedList<>(requireNonNull(validators));
        return this;
    }

    /**
     * Use the given validators.
     *
     * @param validators the validators to use for the matcher
     *
     * @return the current builder
     *
     * @see Validators#defaultValidators()
     * @see #validators(List)
     */
    public JsonMatcherBuilder validators(JsonValidator<?>... validators) {
        return validators(Arrays.asList(validators));
    }

    /**
     * Build the matcher to be valid agains the given json.
     *
     * @param expectedJson the expected json to compare with the actual one
     *
     * @return the matcher
     */
    public JsonCompareMatcher build(String expectedJson) {
        return new JsonCompareMatcher(new JsonComparator(mode, validators), expectedJson);
    }
}
