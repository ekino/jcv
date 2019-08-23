/*
 * Copyright (c) 2019 ekino (https://www.ekino.com/)
 */
package com.ekino.oss.jcv.core.validator

import java.util.regex.Matcher
import java.util.regex.Pattern

/**
 * Manager of templated validator format.
 * Example : `{#some_id:first parameter;the next \; parameter;the last one...#}`
 *
 * @author Leo Millon
 */
class ValidatorTemplateManager(private val value: String?) {

    companion object {

        private val VALIDATOR_TEMPLATE_PATTERN = Pattern.compile("^\\{#(.+)#}$")
        private val PARAMETERS_PATTERN = Pattern.compile("^(?<id>[\\w-_.]+)(:(?<parameters>([^;]+)?(;([^;])+)*))?$")
    }

    private var extractedTemplate: String? = null

    /**
     * A method to test if the template format is valid.
     *
     * @return true if the template format is valid
     */
    val isValidTemplate: Boolean
        get() = extractTemplateContent() != null

    private fun templateMatcher(): Matcher? {
        return value?.let { VALIDATOR_TEMPLATE_PATTERN.matcher(it) }
    }

    /**
     * Extract the validator definition.
     *
     * For `{#some_id:first parameter#}`, the content will be `some_id:first parameter#`
     *
     * @return the template content, or null (if format is invalid)
     */
    fun extractTemplateContent(): String? {

        if (extractedTemplate == null) {
            extractedTemplate = templateMatcher()?.takeIf { it.matches() }?.group(1)
        }

        return extractedTemplate
    }

    /**
     * Extract the validator id.
     *
     * For `{#some_id:first parameter#}`, the validator id will be `some_id`.
     *
     * @return the validator id, or null (if format is invalid)
     */
    fun extractId(): String? {
        return parameterMatcher()
            ?.takeIf { it.matches() }
            ?.group("id")
    }

    /**
     * Extract the validator parameters.
     *
     * For `{#some_id:first parameter;the next \; parameter;the last one...#}`, the parameters will be :
     *
     *  * first parameter
     *  * the next ; parameter
     *  * the last one...
     *
     *
     * @return the ordered list of parameters, or empty (if format is invalid)
     */
    fun extractParameters(): List<String> {
        return parameterMatcher()
            ?.takeIf { it.matches() }
            ?.group("parameters")
            ?.let { it -> it.split("(?<!\\\\);".toRegex()).dropLastWhile { it.isEmpty() }.toList() }
            ?.map { it.replace("\\\\;".toRegex(), ";") }
            .orEmpty()
    }

    /**
     * Extract the validator parameters.
     *
     * For `{#some_id:first parameter;the next \; parameter;the last one...#}`, the parameter `0` will be `first parameter`.
     *
     * @param index the (0-based) index
     *
     * @return the parameter at index (0-based index), or empty (if format is invalid or out of bound)
     */
    fun extractParameter(index: Int): String? {
        val parameters = extractParameters()

        return index
            .takeIf { it in 0 until parameters.size }
            ?.let { parameters[it] }
    }

    private fun parameterMatcher(): Matcher? {
        return extractTemplateContent()
            ?.let { PARAMETERS_PATTERN.matcher(it) }
    }
}
