/*
 * Copyright (c) 2019 ekino (https://www.ekino.com/)
 */
package com.ekino.oss.jcv.core;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

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
}
