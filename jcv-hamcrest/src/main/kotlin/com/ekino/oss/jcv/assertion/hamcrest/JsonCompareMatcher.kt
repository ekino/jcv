/*
 * Copyright (c) 2019 ekino (https://www.ekino.com/)
 */
package com.ekino.oss.jcv.assertion.hamcrest

import org.hamcrest.Description
import org.hamcrest.TypeSafeMatcher
import org.json.JSONException
import org.skyscreamer.jsonassert.JSONCompare
import org.skyscreamer.jsonassert.JSONCompareResult
import org.skyscreamer.jsonassert.comparator.JSONComparator

/**
 * A Hamcrest matcher to compare json contents.
 *
 * @author Leo Millon
 * @see JSONComparator
 */
class JsonCompareMatcher(private val jsonComparator: JSONComparator, private val expectedJson: String) :
  TypeSafeMatcher<String>() {
  private var result: JSONCompareResult? = null

  override fun describeTo(description: Description) {

    description.appendText(result?.message ?: "A valid JSON")
  }

  override fun matchesSafely(item: String): Boolean {
    try {
      return JSONCompare.compareJSON(expectedJson, item, jsonComparator)
        .also { result = it }
        .passed()
    } catch (e: JSONException) {
      throw IllegalArgumentException("Unable to parse expected JSON", e)
    }
  }
}
