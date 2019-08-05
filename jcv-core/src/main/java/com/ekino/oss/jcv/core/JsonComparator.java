/*
 * Copyright (c) 2019 ekino (https://www.ekino.com/)
 */
package com.ekino.oss.jcv.core;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import com.ekino.oss.jcv.core.validator.ValidatorTemplateManager;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.skyscreamer.jsonassert.Customization;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.skyscreamer.jsonassert.JSONCompareResult;
import org.skyscreamer.jsonassert.ValueMatcher;
import org.skyscreamer.jsonassert.ValueMatcherException;
import org.skyscreamer.jsonassert.comparator.DefaultComparator;
import org.skyscreamer.jsonassert.comparator.JSONComparator;

import static java.util.stream.Collectors.*;
import static org.skyscreamer.jsonassert.comparator.JSONCompareUtil.*;

/**
 * Custom {@link JSONComparator}.
 *
 * @author Leo Millon
 */
public class JsonComparator extends DefaultComparator {

    private static final String IGNORED_PATH = "";

    private final List<JsonValidator> validators;

    public JsonComparator(JSONCompareMode mode, List<JsonValidator> validators) {
        super(mode);
        this.validators = new LinkedList<>(validators);
    }

    public List<JsonValidator> getValidators() {
        return Collections.unmodifiableList(validators);
    }

    @Override
    public void compareValues(String prefix, Object expectedValue, Object actualValue, JSONCompareResult result) throws JSONException {

        Optional<Customization> matchingValueCustomization = validators.stream()
            .filter(it -> it.getContextMatcher().matches(prefix, expectedValue, actualValue))
            .findFirst()
            .map(toMatcher())
            .map(it -> new Customization(IGNORED_PATH, it));

        if (matchingValueCustomization.isPresent()) {
            try {
                if (!matchingValueCustomization.get().matches(prefix, actualValue, expectedValue, result)) {
                    result.fail(prefix, expectedValue, actualValue);
                }
            } catch (ValueMatcherException e) {
                result.fail(prefix, e);
            }
            return;
        }

        super.compareValues(prefix, expectedValue, actualValue, result);
    }

    @SuppressWarnings("unchecked")
    private static Function<JsonValidator, ValueMatcher<Object>> toMatcher() {
        return it -> (ValueMatcher<Object>) it.getValueComparator();
    }

    @Override
    protected void compareJSONArrayOfJsonObjects(String key, JSONArray expected, JSONArray actual, JSONCompareResult result) throws JSONException {
        String uniqueKey = findUniqueKey(expected);
        if (uniqueKey == null || !isUsableAsUniqueKey(uniqueKey, actual)) {
            // An expensive last resort
            recursivelyCompareJSONArray(key, expected, actual, result);
            return;
        }
        Map<Object, JSONObject> expectedValueMap = arrayOfJsonObjectToMap(expected, uniqueKey);
        Map<Object, JSONObject> actualValueMap = arrayOfJsonObjectToMap(actual, uniqueKey);
        for (Object id : expectedValueMap.keySet()) {
            if (!actualValueMap.containsKey(id)) {
                result.missing(formatUniqueKey(key, uniqueKey, id), expectedValueMap.get(id));
                continue;
            }
            JSONObject expectedValue = expectedValueMap.get(id);
            JSONObject actualValue = actualValueMap.get(id);
            compareValues(formatUniqueKey(key, uniqueKey, id), expectedValue, actualValue, result);
        }
        for (Object id : actualValueMap.keySet()) {
            if (!expectedValueMap.containsKey(id)) {
                result.unexpected(formatUniqueKey(key, uniqueKey, id), actualValueMap.get(id));
            }
        }
    }

    /**
     * @see org.skyscreamer.jsonassert.comparator.JSONCompareUtil#findUniqueKey(JSONArray)
     */
    private static String findUniqueKey(JSONArray expected) throws JSONException {
        // Find a unique key for the object (id, name, whatever)
        JSONObject o = (JSONObject) expected.get(0); // There's at least one at this point
        for (String candidate : getKeys(o)) {
            if (isUsableAsUniqueKey(candidate, expected)) return candidate;
        }
        // No usable unique key :-(
        return null;
    }

    /**
     * @see org.skyscreamer.jsonassert.comparator.JSONCompareUtil#isUsableAsUniqueKey(String, JSONArray)
     */
    private static boolean isUsableAsUniqueKey(String candidate, JSONArray array) throws JSONException {
        Collection<Object> seenValues = new HashSet<>();
        for (int i = 0; i < array.length(); i++) {
            Object item = array.get(i);
            if (item instanceof JSONObject) {
                JSONObject o = (JSONObject) item;
                if (o.has(candidate)) {
                    Object value = o.get(candidate);
                    // rewrite original code to evict validator templates from valid unique key
                    if (isSimpleValue(value) && !seenValues.contains(value) && !new ValidatorTemplateManager(value.toString()).isValidTemplate()) {
                        seenValues.add(value);
                    } else {
                        return false;
                    }
                } else {
                    return false;
                }
            } else {
                return false;
            }
        }
        return true;
    }

    @Override
    protected void compareJSONArrayOfSimpleValues(String key, JSONArray expected, JSONArray actual, JSONCompareResult result) throws JSONException {
        List<Object> expectedElements = jsonArrayToList(expected);
        List<Object> actualElements = jsonArrayToList(actual);

        List<ExpectedElement> parsedExpectedElements = parseExpectedElements(key, expectedElements);

        if (parsedExpectedElements.stream().noneMatch(ExpectedElement::hasCustomization)) {
            super.compareJSONArrayOfSimpleValues(key, expected, actual, result);
            return;
        }

        Set<Integer> actualValueMatchedIndexes = new HashSet<>();
        Map<ExpectedElement, Optional<ActualElement>> matchingByValue = getMatchingByValue(parsedExpectedElements, actualElements, actualValueMatchedIndexes);
        Map<ExpectedElement, Collection<ActualElement>> matchingByValidator = getExpectedElementCollectionMap(parsedExpectedElements, key, actualElements, actualValueMatchedIndexes);

        long totalMatched = matchingByValue.values().stream().filter(Optional::isPresent).count()
            + matchingByValidator.values().stream().flatMap(Collection::stream).distinct().count();

        if (totalMatched != actualElements.size()) {

            Map<ExpectedElement, Collection<ActualElement>> allMatches = Stream.concat(
                matchingByValue.entrySet().stream()
                    .map(entry -> new AbstractMap.SimpleEntry<ExpectedElement, Collection<ActualElement>>(entry.getKey(), entry.getValue().map(Collections::singletonList).orElseGet(Collections::emptyList)))
                    .map(it -> (Map.Entry<ExpectedElement, Collection<ActualElement>>) it),
                matchingByValidator.entrySet().stream()
            )
                .collect(toMap(Map.Entry::getKey, Map.Entry::getValue));

            Set<Integer> allMatchedActualIndexes = allMatches.values().stream()
                .flatMap(Collection::stream)
                .map(ActualElement::getIndex)
                .collect(toSet());

            IntStream.range(0, actualElements.size())
                .filter(it -> !allMatchedActualIndexes.contains(it))
                .forEachOrdered(actualIndex -> result.unexpected(key + "[" + actualIndex + "]", actualElements.get(actualIndex)));

            String detailedMatchingDebugMessage = allMatches
                .entrySet()
                .stream()
                .sorted(Comparator.comparing(entry -> entry.getKey().getIndex()))
                .map(entry -> {
                    ExpectedElement expectedElt = entry.getKey();
                    String matchedElements = entry.getValue().stream()
                        .map(it -> "[" + it.getIndex() + "] -> " + it.getValue())
                        .collect(joining(",", "[", "]"));
                    return key + "[" + expectedElt.getIndex() + "] -> " + expectedElt.getKey() + " matched with: " + matchedElements;
                })
                .collect(joining("\n"));
            result.fail(detailedMatchingDebugMessage);
        }
    }

    private List<ExpectedElement> parseExpectedElements(String key, List<Object> expectedElements) {
        List<ExpectedElement> parsedExpectedElements = new ArrayList<>();
        for (int i = 0; i < expectedElements.size(); i++) {
            Object expectedElement = expectedElements.get(i);
            parsedExpectedElements.add(new ExpectedElement(
                i,
                expectedElement,
                validators.stream()
                    .filter(it -> it.getContextMatcher().matches(key, expectedElement, null))
                    .findFirst()
                    .map(toMatcher())
                    .map(it -> new Customization(IGNORED_PATH, it))
                    .orElse(null)
            ));
        }
        return parsedExpectedElements;
    }

    private Map<ExpectedElement, Optional<ActualElement>> getMatchingByValue(List<ExpectedElement> parsedExpectedElements,
                                                                             List<Object> actualElements,
                                                                             Set<Integer> actualValueMatchedIndexes) {
        return parsedExpectedElements.stream()
            .filter(it -> !it.hasCustomization())
            .map(expectedElement -> {
                for (int i = 0; i < actualElements.size(); i++) {
                    if (actualValueMatchedIndexes.contains(i)) {
                        continue;
                    }
                    Object actualElement = actualElements.get(i);
                    if (expectedElement.getKey().equals(actualElement)) {
                        actualValueMatchedIndexes.add(i);
                        return new AbstractMap.SimpleEntry<>(expectedElement, Optional.of(new ActualElement(i, actualElement)));
                    }
                }
                return new AbstractMap.SimpleEntry<>(expectedElement, Optional.<ActualElement>empty());
            })
            .collect(toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue));
    }

    private Map<ExpectedElement, Collection<ActualElement>> getExpectedElementCollectionMap(List<ExpectedElement> parsedExpectedElements,
                                                                                            String key,
                                                                                            List<Object> actualElements,
                                                                                            Set<Integer> actualValueMatchedIndexes) {
        return parsedExpectedElements.stream()
            .filter(ExpectedElement::hasCustomization)
            .map(expectedElement -> {
                Set<ActualElement> matched = new HashSet<>();
                for (int i = 0; i < actualElements.size(); i++) {
                    if (actualValueMatchedIndexes.contains(i)) {
                        continue;
                    }
                    Object actualElement = actualElements.get(i);
                    try {
                        if (expectedElement.getCustomization().matches(key, actualElement, expectedElement.getKey(), new JSONCompareResult())) {
                            matched.add(new ActualElement(i, actualElement));
                        }
                    } catch (ValueMatcherException e) {
                        // Do nothing
                    }
                }
                return new AbstractMap.SimpleEntry<>(expectedElement, matched);
            })
            .collect(toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue));
    }

    private static class ExpectedElement {
        private final Integer index;
        private final Object key;
        private final Customization customization;

        private ExpectedElement(Integer index, Object key, Customization customization) {
            this.index = index;
            this.key = key;
            this.customization = customization;
        }

        Integer getIndex() {
            return index;
        }

        Object getKey() {
            return key;
        }

        Customization getCustomization() {
            return customization;
        }

        boolean hasCustomization() {
            return customization != null;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ExpectedElement that = (ExpectedElement) o;
            return Objects.equals(index, that.index) &&
                Objects.equals(key, that.key);
        }

        @Override
        public int hashCode() {
            return Objects.hash(index, key);
        }
    }

    private static class ActualElement {
        private final Integer index;
        private final Object value;

        ActualElement(Integer index, Object value) {
            this.index = index;
            this.value = value;
        }

        Integer getIndex() {
            return index;
        }

        Object getValue() {
            return value;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ActualElement that = (ActualElement) o;
            return Objects.equals(index, that.index) &&
                Objects.equals(value, that.value);
        }

        @Override
        public int hashCode() {
            return Objects.hash(index, value);
        }
    }
}
