/*
 * Copyright (c) 2019 ekino (https://www.ekino.com/)
 */
package com.ekino.oss.jcv.core

import com.ekino.oss.jcv.core.validator.ValidatorTemplateManager
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import org.skyscreamer.jsonassert.Customization
import org.skyscreamer.jsonassert.JSONCompareMode
import org.skyscreamer.jsonassert.JSONCompareResult
import org.skyscreamer.jsonassert.ValueMatcher
import org.skyscreamer.jsonassert.ValueMatcherException
import org.skyscreamer.jsonassert.comparator.DefaultComparator
import org.skyscreamer.jsonassert.comparator.JSONComparator
import org.skyscreamer.jsonassert.comparator.JSONCompareUtil.arrayOfJsonObjectToMap
import org.skyscreamer.jsonassert.comparator.JSONCompareUtil.formatUniqueKey
import org.skyscreamer.jsonassert.comparator.JSONCompareUtil.getKeys
import org.skyscreamer.jsonassert.comparator.JSONCompareUtil.isSimpleValue
import org.skyscreamer.jsonassert.comparator.JSONCompareUtil.jsonArrayToList

/**
 * Custom [JSONComparator].
 *
 * @author Leo Millon
 */
class JsonComparator(mode: JSONCompareMode, validators: List<JsonValidator<out Any?>>) : DefaultComparator(mode) {

    private val validators: List<JsonValidator<out Any?>> = validators.toList()

    @Throws(JSONException::class)
    override fun compareValues(prefix: String, expectedValue: Any?, actualValue: Any?, result: JSONCompareResult) {

        validators
            .firstOrNull { it.contextMatcher.matches(prefix, expectedValue, actualValue) }
            ?.let { asCustomization(it) }
            ?.let {
                try {
                    if (!it.matches(prefix, actualValue, expectedValue, result)) {
                        result.fail(prefix, expectedValue, actualValue)
                    }
                } catch (e: ValueMatcherException) {
                    result.fail(prefix, e)
                }
                return
            }
            ?: super.compareValues(prefix, expectedValue, actualValue, result)
    }

    @Suppress("UNCHECKED_CAST")
    private fun asCustomization(it: JsonValidator<out Any?>) =
        Customization("", it.valueComparator as ValueMatcher<Any>?)

    @Throws(JSONException::class)
    override fun compareJSONArrayOfJsonObjects(
        key: String,
        expected: JSONArray,
        actual: JSONArray,
        result: JSONCompareResult
    ) {
        val uniqueKey = findUniqueKey(expected)
        if (uniqueKey == null || !isUsableAsUniqueKey(uniqueKey, actual)) {
            // An expensive last resort
            recursivelyCompareJSONArray(key, expected, actual, result)
            return
        }

        val expectedValueMap = arrayOfJsonObjectToMap(expected, uniqueKey)
        val actualValueMap = arrayOfJsonObjectToMap(actual, uniqueKey)

        expectedValueMap
            .asSequence()
            .forEach { (expectedId, expectedValue) ->
                if (!actualValueMap.containsKey(expectedId)) {
                    result.missing(formatUniqueKey(key, uniqueKey, expectedId), expectedValue)
                    return@forEach
                }
                val actualValue = actualValueMap[expectedId]
                compareValues(formatUniqueKey(key, uniqueKey, expectedId), expectedValue, actualValue, result)
            }

        actualValueMap
            .asSequence()
            .filterNot { (actualId, _) -> expectedValueMap.containsKey(actualId) }
            .forEach { (actualId, actualValue) ->
                result.unexpected(formatUniqueKey(key, uniqueKey, actualId), actualValue)
            }
    }

    @Throws(JSONException::class)
    override fun compareJSONArrayOfSimpleValues(
        key: String,
        expected: JSONArray,
        actual: JSONArray,
        result: JSONCompareResult
    ) {
        val expectedElements = jsonArrayToList(expected)
        val actualElements = jsonArrayToList(actual)

        val parsedExpectedElements = parseExpectedElements(key, expectedElements)

        if (parsedExpectedElements.none { it.hasCustomization() }) {
            super.compareJSONArrayOfSimpleValues(key, expected, actual, result)
            return
        }

        val actualValueMatchedIndexes = mutableSetOf<Int>()
        val matchingByValue = getMatchingByValue(parsedExpectedElements, actualElements, actualValueMatchedIndexes)
        val matchingByValidator =
            getExpectedElementCollectionMap(parsedExpectedElements, key, actualElements, actualValueMatchedIndexes)

        val totalMatched =
            matchingByValue.values.asSequence().filterNotNull().count() + matchingByValidator.values.asSequence().flatten().distinct().count()

        if (totalMatched == actualElements.size) {
            return
        }

        val allMatches = sequenceOf(
            matchingByValue.asSequence()
                .map { (key, value) -> key to value?.let { listOf(it) }.orEmpty() },
            matchingByValidator.asSequence().map { it.toPair() }
        )
            .flatten()
            .toMap()

        val allMatchedActualIndexes = allMatches.values.asSequence()
            .flatten()
            .mapNotNull { it.index }
            .toSet()

        actualElements
            .forEachIndexed { index, actualElement ->
                actualElement
                    .takeUnless { allMatchedActualIndexes.contains(index) }
                    ?.also { result.unexpected("$key[$index]", it) }
            }

        val detailedMatchingDebugMessage = allMatches
            .asSequence()
            .sortedBy { it.key.index }
            .map { (expectedElt, value) ->
                val matchedElements = value
                    .joinToString(",", "[", "]") { "[" + it.index + "] -> " + it.value }
                key + "[" + expectedElt.index + "] -> " + expectedElt.key + " matched with: " + matchedElements
            }
            .joinToString("\n")

        result.fail(detailedMatchingDebugMessage)
    }

    private fun parseExpectedElements(key: String, expectedElements: List<Any>): List<ExpectedElement> {
        return expectedElements
            .asSequence()
            .mapIndexed { index, expectedElement ->
                validators
                    .firstOrNull { it.contextMatcher.matches(key, expectedElement, null) }
                    ?.let { asCustomization(it) }
                    .let { ExpectedElement(index, expectedElement, it) }
            }
            .toList()
    }

    private fun getMatchingByValue(
        parsedExpectedElements: List<ExpectedElement>,
        actualElements: List<Any>,
        actualValueMatchedIndexes: MutableSet<Int>
    ): Map<ExpectedElement, ActualElement?> {
        return parsedExpectedElements
            .asSequence()
            .filterNot { it.hasCustomization() }
            .associateWith { expectedElement ->
                actualElements
                    .asSequence()
                    .mapIndexedNotNull { index, actualElement ->
                        actualElement
                            .takeUnless { actualValueMatchedIndexes.contains(index) }
                            ?.takeIf { it -> expectedElement.key == it }
                            ?.also { actualValueMatchedIndexes.add(index) }
                            ?.let { ActualElement(index, actualElement) }
                    }
                    .firstOrNull()
            }
    }

    private fun getExpectedElementCollectionMap(
        parsedExpectedElements: List<ExpectedElement>,
        key: String?,
        actualElements: List<Any>,
        actualValueMatchedIndexes: Set<Int>
    ): Map<ExpectedElement, Collection<ActualElement>> {
        return parsedExpectedElements
            .asSequence()
            .filter { it.hasCustomization() }
            .associateWith { expectedElement ->
                actualElements
                    .asSequence()
                    .mapIndexedNotNull { index, actualElement ->
                        actualElement
                            .takeUnless { actualValueMatchedIndexes.contains(index) }
                            ?.takeIf { it ->
                                try {
                                    expectedElement.customization
                                        ?.matches(key, it, expectedElement.key, JSONCompareResult())
                                        ?: false
                                } catch (e: ValueMatcherException) {
                                    false
                                }
                            }
                            ?.let { ActualElement(index, it) }
                    }
                    .toSet()
            }
    }

    private data class ExpectedElement(val index: Int?, val key: Any, val customization: Customization?) {

        fun hasCustomization(): Boolean {
            return customization != null
        }
    }

    private data class ActualElement(val index: Int?, val value: Any)

    /**
     * @see org.skyscreamer.jsonassert.comparator.JSONCompareUtil.findUniqueKey
     */
    @Throws(JSONException::class)
    private fun findUniqueKey(expected: JSONArray): String? {
        // Find a unique key for the object (id, name, whatever)
        return getKeys((expected[0] as JSONObject))
            .firstOrNull { isUsableAsUniqueKey(it, expected) } // if null, no usable unique key :-(
    }

    /**
     * @see org.skyscreamer.jsonassert.comparator.JSONCompareUtil.isUsableAsUniqueKey
     */
    @Throws(JSONException::class)
    private fun isUsableAsUniqueKey(candidate: String, array: JSONArray): Boolean {
        val seenValues = mutableSetOf<Any>()
        for (i in 0 until array.length()) {
            array[i]
                .takeIfInstanceOf<JSONObject>()
                ?.takeIf { it.has(candidate) }
                ?.let { it[candidate] }
                ?.takeIf { isSimpleValue(it) && !seenValues.contains(it) && !ValidatorTemplateManager(it.toString()).isValidTemplate }
                ?.also { seenValues.add(it) }
                ?: return false
        }
        return true
    }

    private inline fun <reified T> Any?.takeIfInstanceOf(): T? {
        return if (this is T) this else null
    }
}
