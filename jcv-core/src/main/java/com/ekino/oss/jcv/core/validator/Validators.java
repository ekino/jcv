/*
 * Copyright (c) 2019 ekino (https://www.ekino.com/)
 */
package com.ekino.oss.jcv.core.validator;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import com.ekino.oss.jcv.core.JsonValidator;
import com.ekino.oss.jcv.core.JsonValueComparator;
import com.ekino.oss.jcv.core.comparator.ContainsComparator;
import com.ekino.oss.jcv.core.comparator.EndsWithComparator;
import com.ekino.oss.jcv.core.comparator.NotEmptyComparator;
import com.ekino.oss.jcv.core.comparator.NotNullComparator;
import com.ekino.oss.jcv.core.comparator.RegexComparator;
import com.ekino.oss.jcv.core.comparator.StartsWithComparator;
import com.ekino.oss.jcv.core.comparator.TemplatedURLComparator;
import com.ekino.oss.jcv.core.comparator.TypeComparator;
import com.ekino.oss.jcv.core.comparator.URLComparator;
import com.ekino.oss.jcv.core.comparator.UUIDComparator;
import com.ekino.oss.jcv.core.initializer.DateTimeFormatComparatorInitializer;
import lombok.experimental.UtilityClass;
import org.json.JSONArray;
import org.json.JSONObject;
import org.skyscreamer.jsonassert.ValueMatcher;

import static com.ekino.oss.jcv.core.initializer.Initializers.*;

/**
 * Prepared validators.
 *
 * @author Leo Millon
 */
@UtilityClass
public class Validators {

    /**
     * Validator for a specific json field path.
     *
     * @param path       the json field path
     * @param comparator a json value comparator
     * @param <T>        the field value type
     *
     * @return the validator
     */
    public static <T> JsonValidator<T> forPath(String path, ValueMatcher<T> comparator) {
        return new DefaultJsonValidator<>(new PrefixMatcher(path), comparator);
    }

    /**
     * Validator for a specific json field value type.
     *
     * @param id           the validator id
     * @param expectedType the expected value type
     *
     * @return the validator
     */
    public static JsonValidator<Object> type(String id, Class expectedType) {
        return templatedValidator(id, new TypeComparator(expectedType));
    }

    /**
     * Prepare "templated" validator for a given comparator.
     *
     * @param id         the validator id
     * @param comparator a custom comparator
     * @param <T>        the field value type
     *
     * @return the validator
     */
    public static <T> JsonValidator<T> templatedValidator(String id, JsonValueComparator<T> comparator) {
        return new DefaultValueTemplateIdValidator<>(id, comparator);
    }

    /**
     * Default validators.
     *
     * @return a list of prepared validators
     */
    public static List<JsonValidator> defaultValidators() {
        return Arrays.asList(
            parameterizedValidator("contains", comparatorWith1Parameter(ContainsComparator::new)),
            parameterizedValidator("starts_with", comparatorWith1Parameter(StartsWithComparator::new)),
            parameterizedValidator("ends_with", comparatorWith1Parameter(EndsWithComparator::new)),
            parameterizedValidator("regex", comparatorWith1Parameter(it -> new RegexComparator(Pattern.compile(it)))),
            templatedValidator("uuid", new UUIDComparator()),
            templatedValidator("not_null", new NotNullComparator()),
            templatedValidator("not_empty", new NotEmptyComparator()),
            templatedValidator("url", new URLComparator()),
            parameterizedValidator("url_ending", allOf(
                comparatorWithoutParameter(URLComparator::new),
                comparatorWith1Parameter(EndsWithComparator::new)
            )),
            parameterizedValidator("url_regex", allOf(
                comparatorWithoutParameter(URLComparator::new),
                comparatorWith1Parameter(it -> new RegexComparator(Pattern.compile(it)))
            )),
            templatedValidator("templated_url", new TemplatedURLComparator()),
            parameterizedValidator("templated_url_ending", allOf(
                comparatorWithoutParameter(TemplatedURLComparator::new),
                comparatorWith1Parameter(EndsWithComparator::new)
            )),
            parameterizedValidator("templated_url_regex", allOf(
                comparatorWithoutParameter(TemplatedURLComparator::new),
                comparatorWith1Parameter(it -> new RegexComparator(Pattern.compile(it)))
            )),
            type("boolean_type", Boolean.class),
            type("string_type", String.class),
            type("number_type", Number.class),
            type("array_type", JSONArray.class),
            type("object_type", JSONObject.class),
            parameterizedValidator("date_time_format", comparatorWith2Parameters(true, false, new DateTimeFormatComparatorInitializer()))
        );
    }
}
