/*
 * Copyright (c) 2019 ekino (https://www.ekino.com/)
 */
package com.ekino.oss.jcv.core.validator;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;

import static java.util.Optional.*;

/**
 * Manager of templated validator format.
 * Example : {@code {#some_id:first parameter;the next \; parameter;the last one...#}}
 *
 * @author Leo Millon
 */
@RequiredArgsConstructor
public class ValidatorTemplateManager {

    private static final Pattern VALIDATOR_TEMPLATE_PATTERN = Pattern.compile("^\\{#(.+)#}$");
    private static final Pattern PARAMETERS_PATTERN = Pattern.compile("^(?<id>[\\w-_.]+)(:(?<parameters>(([^;]+))?(;([^;])+)*))?$");

    private final String value;
    private String extractedTemplate;

    /**
     * A method to test if the template format is valid.
     *
     * @return true if the template format is valid
     */
    public boolean isValidTemplate() {
        return extractTemplateContent() != null;
    }

    private Optional<Matcher> templateMatcher() {
        return ofNullable(value)
            .map(VALIDATOR_TEMPLATE_PATTERN::matcher);
    }

    /**
     * Extract the validator definition.
     *
     * For {@code {#some_id:first parameter#}}, the content will be {@code some_id:first parameter#}
     *
     * @return the template content, or null (if format is invalid)
     */
    public String extractTemplateContent() {

        if (extractedTemplate == null) {
            extractedTemplate = templateMatcher()
                .filter(Matcher::matches)
                .map(it -> it.group(1))
                .orElse(null);
        }

        return extractedTemplate;
    }

    /**
     * Extract the validator id.
     *
     * For {@code {#some_id:first parameter#}}, the validator id will be {@code some_id}.
     *
     * @return the validator id, or null (if format is invalid)
     */
    public String extractId() {
        return parameterMatcher()
            .filter(Matcher::matches)
            .map(it -> it.group("id"))
            .orElse(null);
    }

    /**
     * Extract the validator parameters.
     *
     * For {@code {#some_id:first parameter;the next \; parameter;the last one...#}}, the parameters will be :
     * <ul>
     * <li>first parameter</li>
     * <li>the next ; parameter</li>
     * <li>the last one...</li>
     * </ul>
     *
     * @return the ordered list of parameters, or empty (if format is invalid)
     */
    public List<String> extractParameters() {
        return parameterMatcher()
            .filter(Matcher::matches)
            .map(it -> it.group("parameters"))
            .map(it -> it.split("(?<!\\\\);"))
            .map(Arrays::asList)
            .map(espacedParam -> espacedParam.stream()
                .map(it -> it.replaceAll("\\\\;", ";"))
                .collect(Collectors.toList()))
            .orElseGet(Collections::emptyList);
    }

    /**
     * Extract the validator parameters.
     *
     * For {@code {#some_id:first parameter;the next \; parameter;the last one...#}}, the parameter {@code 0} will be {@code first parameter}.
     *
     * @param index the (0-based) index
     *
     * @return the parameter at index (0-based index), or empty (if format is invalid or out of bound)
     */
    public Optional<String> extractParameter(int index) {
        List<String> parameters = extractParameters();

        return of(index)
            .filter(it -> it >= 0)
            .filter(it -> it < parameters.size())
            .map(parameters::get);
    }

    private Optional<Matcher> parameterMatcher() {
        return ofNullable(extractTemplateContent())
            .map(PARAMETERS_PATTERN::matcher);
    }
}
