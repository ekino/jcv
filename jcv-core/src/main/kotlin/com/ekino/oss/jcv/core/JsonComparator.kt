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
import java.util.ArrayList
import java.util.HashSet
import java.util.LinkedList
import java.util.Objects

/**
 * Custom [JSONComparator].
 *
 * @author Leo Millon
 */
class JsonComparator(mode: JSONCompareMode, validators: List<JsonValidator<out Any?>>) : DefaultComparator(mode) {

    companion object {
        private const val IGNORED_PATH = ""
    }

    private val validators: List<JsonValidator<out Any?>>

    init {
        this.validators = LinkedList(validators)
    }

    @Throws(JSONException::class)
    override fun compareValues(prefix: String, expectedValue: Any?, actualValue: Any?, result: JSONCompareResult) {

        val matchingValueCustomization = validators
            .firstOrNull { it.contextMatcher.matches(prefix, expectedValue, actualValue) }
            ?.let { asCustomization(it) }

        if (matchingValueCustomization != null) {
            try {
                if (!matchingValueCustomization.matches(prefix, actualValue, expectedValue, result)) {
                    result.fail(prefix, expectedValue, actualValue)
                }
            } catch (e: ValueMatcherException) {
                result.fail(prefix, e)
            }

            return
        }

        super.compareValues(prefix, expectedValue, actualValue, result)
    }

    @Suppress("UNCHECKED_CAST")
    private fun asCustomization(it: JsonValidator<out Any?>) =
        Customization(IGNORED_PATH, it.valueComparator as ValueMatcher<Any>?)

    @Throws(JSONException::class)
    override fun compareJSONArrayOfJsonObjects(key: String, expected: JSONArray, actual: JSONArray, result: JSONCompareResult) {
        val uniqueKey = findUniqueKey(expected)
        if (uniqueKey == null || !isUsableAsUniqueKey(uniqueKey, actual)) {
            // An expensive last resort
            recursivelyCompareJSONArray(key, expected, actual, result)
            return
        }
        val expectedValueMap = arrayOfJsonObjectToMap(expected, uniqueKey)
        val actualValueMap = arrayOfJsonObjectToMap(actual, uniqueKey)
        for (id in expectedValueMap.keys) {
            if (!actualValueMap.containsKey(id)) {
                result.missing(formatUniqueKey(key, uniqueKey, id), expectedValueMap[id])
                continue
            }
            val expectedValue = expectedValueMap[id]
            val actualValue = actualValueMap[id]
            compareValues(formatUniqueKey(key, uniqueKey, id), expectedValue, actualValue, result)
        }
        for (id in actualValueMap.keys) {
            if (!expectedValueMap.containsKey(id)) {
                result.unexpected(formatUniqueKey(key, uniqueKey, id), actualValueMap[id])
            }
        }
    }

    @Throws(JSONException::class)
    override fun compareJSONArrayOfSimpleValues(key: String, expected: JSONArray, actual: JSONArray, result: JSONCompareResult) {
        val expectedElements = jsonArrayToList(expected)
        val actualElements = jsonArrayToList(actual)

        val parsedExpectedElements = parseExpectedElements(key, expectedElements)

        if (parsedExpectedElements.none { it.hasCustomization() }) {
            super.compareJSONArrayOfSimpleValues(key, expected, actual, result)
            return
        }

        val actualValueMatchedIndexes = HashSet<Int>()
        val matchingByValue = getMatchingByValue(parsedExpectedElements, actualElements, actualValueMatchedIndexes)
        val matchingByValidator = getExpectedElementCollectionMap(parsedExpectedElements, key, actualElements, actualValueMatchedIndexes)

        val totalMatched = matchingByValue.values.asSequence().filterNotNull().count() + matchingByValidator.values.asSequence().flatten().distinct().count()

        if (totalMatched != actualElements.size) {

            val allMatches = sequenceOf(
                matchingByValue.entries.asSequence()
                    .map { entry -> entry.key to entry.value?.let { listOf(it) }.orEmpty() },
                matchingByValidator.entries.asSequence().map { it.toPair() }
            )
                .flatten()
                .toMap()

            val allMatchedActualIndexes = allMatches.values.asSequence()
                .flatten()
                .mapNotNull { it.index }
                .toSet()

            (0 until actualElements.size)
                .asSequence()
                .filterNot { allMatchedActualIndexes.contains(it) }
                .forEach { actualIndex -> result.unexpected("$key[$actualIndex]", actualElements[actualIndex]) }

            val detailedMatchingDebugMessage = allMatches
                .entries
                .asSequence()
                .sortedBy { it.key.index }
                .map { entry ->
                    val expectedElt = entry.key
                    val matchedElements = entry.value.joinToString(",", "[", "]") { "[" + it.index + "] -> " + it.value }
                    key + "[" + expectedElt.index + "] -> " + expectedElt.key + " matched with: " + matchedElements
                }
                .joinToString("\n")
            result.fail(detailedMatchingDebugMessage)
        }
    }

    private fun parseExpectedElements(key: String, expectedElements: List<Any>): List<ExpectedElement> {
        val parsedExpectedElements = ArrayList<ExpectedElement>()
        for (i in expectedElements.indices) {
            val expectedElement = expectedElements[i]
            parsedExpectedElements.add(ExpectedElement(
                i,
                expectedElement,
                validators
                    .firstOrNull { it.contextMatcher.matches(key, expectedElement, null) }
                    ?.let { asCustomization(it) }
            ))
        }
        return parsedExpectedElements
    }

    private fun getMatchingByValue(
        parsedExpectedElements: List<ExpectedElement>,
        actualElements: List<Any>,
        actualValueMatchedIndexes: MutableSet<Int>
    ): Map<ExpectedElement, ActualElement?> {
        return parsedExpectedElements.asSequence()
            .filter { !it.hasCustomization() }
            .map { expectedElement ->
                for (i in actualElements.indices) {
                    if (actualValueMatchedIndexes.contains(i)) {
                        continue
                    }
                    val actualElement = actualElements[i]
                    if (expectedElement.key == actualElement) {
                        actualValueMatchedIndexes.add(i)
                        return@map expectedElement to ActualElement(i, actualElement)
                    }
                }
                expectedElement to null
            }
            .toMap()
    }

    private fun getExpectedElementCollectionMap(
        parsedExpectedElements: List<ExpectedElement>,
        key: String?,
        actualElements: List<Any>,
        actualValueMatchedIndexes: Set<Int>
    ): Map<ExpectedElement, Collection<ActualElement>> {
        return parsedExpectedElements.asSequence()
            .filter { it.hasCustomization() }
            .map { expectedElement ->
                val matched = HashSet<ActualElement>()
                for (i in actualElements.indices) {
                    if (actualValueMatchedIndexes.contains(i)) {
                        continue
                    }
                    val actualElement = actualElements[i]
                    try {
                        if (expectedElement.customization!!.matches(key, actualElement, expectedElement.key, JSONCompareResult())) {
                            matched.add(ActualElement(i, actualElement))
                        }
                    } catch (e: ValueMatcherException) {
                        // Do nothing
                    }
                }
                expectedElement to matched
            }
            .toMap()
    }

    private class ExpectedElement constructor(internal val index: Int?, internal val key: Any, internal val customization: Customization?) {

        internal fun hasCustomization(): Boolean {
            return customization != null
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other == null || javaClass != other.javaClass) return false
            val that = other as ExpectedElement?
            return index == that!!.index && key == that.key
        }

        override fun hashCode(): Int {
            return Objects.hash(index, key)
        }
    }

    private class ActualElement internal constructor(internal val index: Int?, internal val value: Any) {

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other == null || javaClass != other.javaClass) return false
            val that = other as ActualElement?
            return index == that!!.index && value == that.value
        }

        override fun hashCode(): Int {
            return Objects.hash(index, value)
        }
    }

    /**
     * @see org.skyscreamer.jsonassert.comparator.JSONCompareUtil.findUniqueKey
     */
    @Throws(JSONException::class)
    private fun findUniqueKey(expected: JSONArray): String? {
        // Find a unique key for the object (id, name, whatever)
        val o = expected.get(0) as JSONObject // There's at least one at this point
        for (candidate in getKeys(o)) {
            if (isUsableAsUniqueKey(candidate, expected)) return candidate
        }
        // No usable unique key :-(
        return null
    }

    /**
     * @see org.skyscreamer.jsonassert.comparator.JSONCompareUtil.isUsableAsUniqueKey
     */
    @Throws(JSONException::class)
    private fun isUsableAsUniqueKey(candidate: String, array: JSONArray): Boolean {
        val seenValues = HashSet<Any>()
        for (i in 0 until array.length()) {
            val item = array.get(i)
            if (item is JSONObject) {
                if (item.has(candidate)) {
                    val value = item.get(candidate)
                    // rewrite original code to evict validator templates from valid unique key
                    if (isSimpleValue(value) && !seenValues.contains(value) && !ValidatorTemplateManager(value.toString()).isValidTemplate) {
                        seenValues.add(value)
                    } else {
                        return false
                    }
                } else {
                    return false
                }
            } else {
                return false
            }
        }
        return true
    }
}
