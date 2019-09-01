/*
 * Copyright (c) 2019 ekino (https://www.ekino.com/)
 */
package com.ekino.oss.jcv.core.validator

/**
 * Manager of templated validator format.
 * Example : `{#some_id:first parameter;the next \; parameter;the last one...#}`
 *
 * @author Leo Millon
 */
class ValidatorTemplateManager(private val value: String?) {

    companion object {
        private val VALIDATOR_TEMPLATE_REGEX = "^\\{#(.+)#}$".toRegex()
        private val PARAMETERS_REGEX = "^(?<id>[\\w-_.]+)(:(?<parameters>([^;]+)?(;([^;])+)*))?$".toRegex()
        private val PARAMETER_SEPARATOR_REGEX = "(?<!\\\\);".toRegex()
    }

    private val extractedTemplate: String? by lazy {
        value?.let { VALIDATOR_TEMPLATE_REGEX.find(it) }?.groups?.get(1)?.value
    }

    /**
     * A method to test if the template format is valid.
     *
     * @return true if the template format is valid
     */
    val isValidTemplate: Boolean
        get() = extractTemplateContent() != null

    /**
     * Extract the validator definition.
     *
     * For `{#some_id:first parameter#}`, the content will be `some_id:first parameter#`
     *
     * @return the template content, or null (if format is invalid)
     */
    fun extractTemplateContent(): String? = extractedTemplate

    /**
     * Extract the validator id.
     *
     * For `{#some_id:first parameter#}`, the validator id will be `some_id`.
     *
     * @return the validator id, or null (if format is invalid)
     */
    fun extractId(): String? = extractedTemplate
        .extractRegexGroup(PARAMETERS_REGEX, "id")

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
    fun extractParameters(): List<String> = extractedTemplate
        .extractRegexGroup(PARAMETERS_REGEX, "parameters")
        ?.split(PARAMETER_SEPARATOR_REGEX)
        ?.dropLastWhile { it.isEmpty() }
        ?.map { it.replace("\\\\;".toRegex(), ";") }
        .orEmpty()

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

        return if (index in parameters.indices) parameters[index] else null
    }

    /**
     * Fallback method to handle regex group extraction as the stdlib-jdk8 does not support named group on MatchResult after JDK8.
     *
     * @see <a href="https://youtrack.jetbrains.com/issue/KT-20865">Retrieving groups by name is not supported on Java 9 even with `kotlin-stdlib-jre8` in the classpath</a>
     */
    private fun String?.extractRegexGroup(regex: Regex, groupName: String): String? {
        return this
            ?.let { regex.toPattern().matcher(it) }
            ?.takeIf { it.matches() }
            ?.group(groupName)
    }
}
