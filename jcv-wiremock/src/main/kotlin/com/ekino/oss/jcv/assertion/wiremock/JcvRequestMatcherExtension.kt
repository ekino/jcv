package com.ekino.oss.jcv.assertion.wiremock

import com.github.tomakehurst.wiremock.common.Json
import com.github.tomakehurst.wiremock.extension.Parameters
import com.github.tomakehurst.wiremock.http.Request
import com.github.tomakehurst.wiremock.matching.MatchResult
import com.github.tomakehurst.wiremock.matching.RequestMatcherExtension
import com.github.tomakehurst.wiremock.matching.ValueMatcher

class JcvRequestMatcherExtension : RequestMatcherExtension() {

  companion object {
    const val MATCHER_NAME = "equalToJcv"
    const val PARAM_EXPECTED_JSON_NAME = "json"
    const val PARAM_IGNORE_ARRAY_ORDER_NAME = "ignoreArrayOrder"
    const val PARAM_IGNORE_EXTRA_ELEMENTS_NAME = "ignoreExtraElements"

    @JvmStatic
    @JvmOverloads
    fun toParameters(
      json: String,
      ignoreArrayOrder: Boolean = false,
      ignoreExtraElements: Boolean = false,
    ): Parameters = Parameters.from(
      mapOf(
        PARAM_EXPECTED_JSON_NAME to json,
        PARAM_IGNORE_ARRAY_ORDER_NAME to ignoreArrayOrder,
        PARAM_IGNORE_EXTRA_ELEMENTS_NAME to ignoreExtraElements,
      ),
    )

    @JvmStatic
    @JvmOverloads
    fun toRequestMatcher(
      json: String,
      ignoreArrayOrder: Boolean = false,
      ignoreExtraElements: Boolean = false,
    ): ValueMatcher<Request> = ValueMatcher { request ->
      val body = request.bodyAsString
        ?.takeIf { it.isNotBlank() }
        ?: return@ValueMatcher MatchResult.noMatch()
      return@ValueMatcher EqualToJcvPattern(
        json = json,
        ignoreArrayOrder = ignoreArrayOrder,
        ignoreExtraElements = ignoreExtraElements,
      )
        .match(body)
    }
  }

  override fun getName(): String = MATCHER_NAME

  override fun match(request: Request, parameters: Parameters): MatchResult {
    return toRequestMatcher(
      json = parameters.getJsonAsString(PARAM_EXPECTED_JSON_NAME),
      ignoreArrayOrder = parameters.getBoolean(PARAM_IGNORE_ARRAY_ORDER_NAME, false),
      ignoreExtraElements = parameters.getBoolean(PARAM_IGNORE_EXTRA_ELEMENTS_NAME, false),
    )
      .match(request)
  }
}

private fun Parameters.getJsonAsString(key: String): String {
  return when (val jsonParam = this[key]) {
    is String -> jsonParam
    is Map<*, *> -> Json.write(jsonParam)
    else -> throw IllegalArgumentException("'$key' param of '${JcvRequestMatcherExtension.MATCHER_NAME}' matcher is required and should be of string or json type")
  }
}
