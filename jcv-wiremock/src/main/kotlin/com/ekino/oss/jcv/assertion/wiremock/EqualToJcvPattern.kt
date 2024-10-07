package com.ekino.oss.jcv.assertion.wiremock

import com.ekino.oss.jcv.core.JsonComparator
import com.ekino.oss.jcv.core.validator.Validators.defaultValidators
import com.fasterxml.jackson.annotation.JsonProperty
import com.github.tomakehurst.wiremock.common.Json
import com.github.tomakehurst.wiremock.matching.MatchResult
import com.github.tomakehurst.wiremock.matching.StringValuePattern
import org.json.JSONException
import org.skyscreamer.jsonassert.JSONCompare
import org.skyscreamer.jsonassert.JSONCompareMode
import org.skyscreamer.jsonassert.JSONCompareResult

class EqualToJcvPattern(
  @JsonProperty("equalToJcv")
  val json: String,
  @JsonProperty("ignoreArrayOrder")
  val ignoreArrayOrder: Boolean? = null,
  @JsonProperty("ignoreExtraElements")
  val ignoreExtraElements: Boolean? = null,
) : StringValuePattern(json) {

  override fun match(value: String?): MatchResult {
    if (value.isNullOrBlank()) {
      return MatchResult.noMatch()
    }

    return computeMatchResult(value)
  }

  private fun computeMatchResult(value: String): MatchResult {
    val jsonComparator = buildJsonComparator()
    val actual = try {
      JSONCompare.compareJSON(json, value, jsonComparator).getDistance()
    } catch (e: JSONException) {
      return MatchResult.noMatch()
    } catch (e: IllegalArgumentException) {
      return MatchResult.noMatch()
    }
    val maxDistanceJson = createJsonReferenceForMaxDistance(value)

    return JSONCompare.compareJSON(json, maxDistanceJson, jsonComparator).getDistance()
      .takeIf { it > 0 }
      ?.let { maxDistance ->
        toMatchResult(actual, maxDistance)
      }
      ?: MatchResult.noMatch()
  }

  private fun buildJsonComparator(): JsonComparator {
    val jsonCompareMode = JSONCompareMode.entries
      .find { it.isExtensible == (ignoreExtraElements ?: false) && it.hasStrictOrder() == !(ignoreArrayOrder ?: false) }
      ?: throw IllegalArgumentException("Cannot find json compare mode for ignoreExtraElements=$ignoreExtraElements and ignoreArrayOrder=$ignoreArrayOrder")
    return JsonComparator(jsonCompareMode, defaultValidators())
  }

  private fun createJsonReferenceForMaxDistance(value: String): String =
    when (value.firstOrNull { it == '{' || it == '[' }) {
      '[' -> "[]"
      else -> "{}"
    }

  private fun toMatchResult(actual: Int, maxDistance: Int): MatchResult = object : MatchResult() {
    override fun isExactMatch(): Boolean = actual == 0

    override fun getDistance(): Double = actual.toDouble() / maxDistance.toDouble()
  }

  private fun JSONCompareResult.getDistance(): Int {
    val fieldErrors = listOfNotNull(fieldFailures, fieldMissing, fieldUnexpected).sumOf { it.count() }
    return fieldErrors + if (failed() && fieldErrors == 0) 1 else 0
  }

  override fun getExpected(): String = Json.prettyPrint(value)
}
