/*
 * Copyright (c) 2019 ekino (https://www.ekino.com/)
 */
package com.ekino.oss.jcv.core.initializer;

import java.util.Optional;
import java.util.stream.Stream;

import com.ekino.oss.jcv.core.JsonValidator;
import com.ekino.oss.jcv.core.validator.DefaultParameterizedTemplateValidator;
import com.ekino.oss.jcv.core.validator.ValidatorTemplateManager;

public class Initializers {

    /**
     * Prepare "parameterized" validator for a given comparator.
     *
     * @param id                    the validator id
     * @param comparatorInitializer the comparator init with the parameter
     * @param <T>                   the field value type
     *
     * @return the validator
     */
    public static <T> JsonValidator<T> parameterizedValidator(String id,
                                                              TemplatedComparatorInitializer<T> comparatorInitializer) {
        return new DefaultParameterizedTemplateValidator<>(id, comparatorInitializer);
    }

    public static <T> TemplatedComparatorInitializer<T> comparatorWithoutParameter(NoParameterComparatorInitializer<T> comparatorInitializer) {
        return validatorTemplateManager -> comparatorInitializer.initComparator();
    }

    public static <T> TemplatedComparatorInitializer<T> comparatorWith1Parameter(OneParameterComparatorInitializer<T> comparatorInitializer) {
        return comparatorWith1Parameter(true, comparatorInitializer);
    }

    public static <T> TemplatedComparatorInitializer<T> comparatorWith1Parameter(boolean required, OneParameterComparatorInitializer<T> comparatorInitializer) {
        return validatorTemplateManager -> {
            String parameter = getOrThrowParameter(0, required, validatorTemplateManager);
            return comparatorInitializer.initComparator(parameter);
        };
    }

    public static <T> TemplatedComparatorInitializer<T> comparatorWith2Parameters(boolean param1Required, boolean param2Required, TwoParametersComparatorInitializer<T> comparatorInitializer) {
        return validatorTemplateManager -> {
            String parameter1 = getOrThrowParameter(0, param1Required, validatorTemplateManager);
            String parameter2 = getOrThrowParameter(1, param2Required, validatorTemplateManager);
            return comparatorInitializer.initComparator(parameter1, parameter2);
        };
    }

    private static String getOrThrowParameter(int index, boolean required, ValidatorTemplateManager validatorTemplateManager) {
        Optional<String> parameter = validatorTemplateManager.extractParameter(index);
        if (required && !parameter.isPresent()) {
            throw new IllegalArgumentException(String.format(
                "No parameter at index %s found in validator '%s'",
                index,
                validatorTemplateManager.extractTemplateContent()
            ));
        }

        return parameter.orElse(null);
    }

    @SafeVarargs
    public static <T> TemplatedComparatorInitializer<T> allOf(TemplatedComparatorInitializer<T>... initializers) {
        return validatorTemplateManager -> (actual, expected) -> Stream.of(initializers)
            .map(it -> it.initComparator(validatorTemplateManager))
            .allMatch(it -> it.equal(actual, expected));
    }

    @SafeVarargs
    public static <T> TemplatedComparatorInitializer<T> anyOf(TemplatedComparatorInitializer<T>... initializers) {
        return validatorTemplateManager -> (actual, expected) -> Stream.of(initializers)
            .map(it -> it.initComparator(validatorTemplateManager))
            .anyMatch(it -> it.equal(actual, expected));
    }
}
