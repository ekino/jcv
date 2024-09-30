package com.ekino.oss.jcv.assertion.wiremock

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isTrue
import com.github.tomakehurst.wiremock.http.ContentTypeHeader
import com.github.tomakehurst.wiremock.http.Cookie
import com.github.tomakehurst.wiremock.http.FormParameter
import com.github.tomakehurst.wiremock.http.HttpHeader
import com.github.tomakehurst.wiremock.http.HttpHeaders
import com.github.tomakehurst.wiremock.http.QueryParameter
import com.github.tomakehurst.wiremock.http.Request
import com.github.tomakehurst.wiremock.http.RequestMethod
import org.junit.jupiter.api.Test
import java.util.Optional

class JcvRequestMatcherExtensionTest {

  @Test
  fun `should create parameters from extension utils`() {
    // Given
    val json =
      //language=JSON
      """
      { "field_1": "{#not_empty#}" }
      """
    val ignoreArrayOrder = true
    val ignoreExtraElements = true

    // When
    val parameters = JcvRequestMatcherExtension.toParameters(
      json,
      ignoreArrayOrder = ignoreArrayOrder,
      ignoreExtraElements = ignoreExtraElements,
    )

    // Then
    assertThat(parameters["json"]).isEqualTo(json)
    assertThat(parameters["ignoreArrayOrder"]).isEqualTo(ignoreArrayOrder)
    assertThat(parameters["ignoreExtraElements"]).isEqualTo(ignoreExtraElements)
  }

  @Test
  fun `should create value matcher from extension utils`() {
    // Given
    val json =
      //language=JSON
      """
      { "field_1": "{#not_empty#}" }
      """
    val ignoreArrayOrder = true
    val ignoreExtraElements = true

    // When
    val valueMatcher = JcvRequestMatcherExtension.toRequestMatcher(
      json,
      ignoreArrayOrder = ignoreArrayOrder,
      ignoreExtraElements = ignoreExtraElements,
    )

    // Then
    val request: Request = mockRequest {
      //language=JSON
      """
      {
        "field_1": "some value"
      }
      """
    }

    assertThat(valueMatcher.match(request))
      .transform { it.isExactMatch }
      .isTrue()
  }

  private fun mockRequest(stringBodyProvider: () -> String): Request {
    return object : Request {
      override fun getUrl(): String {
        throw UnsupportedOperationException()
      }

      override fun getAbsoluteUrl(): String {
        throw UnsupportedOperationException()
      }

      override fun getMethod(): RequestMethod {
        throw UnsupportedOperationException()
      }

      override fun getScheme(): String {
        throw UnsupportedOperationException()
      }

      override fun getHost(): String {
        throw UnsupportedOperationException()
      }

      override fun getPort(): Int {
        throw UnsupportedOperationException()
      }

      override fun getClientIp(): String {
        throw UnsupportedOperationException()
      }

      override fun getHeader(key: String?): String {
        throw UnsupportedOperationException()
      }

      override fun header(key: String?): HttpHeader {
        throw UnsupportedOperationException()
      }

      override fun contentTypeHeader(): ContentTypeHeader {
        throw UnsupportedOperationException()
      }

      override fun getHeaders(): HttpHeaders {
        throw UnsupportedOperationException()
      }

      override fun containsHeader(key: String?): Boolean {
        throw UnsupportedOperationException()
      }

      override fun getAllHeaderKeys(): MutableSet<String> {
        throw UnsupportedOperationException()
      }

      override fun getCookies(): MutableMap<String, Cookie> {
        throw UnsupportedOperationException()
      }

      override fun queryParameter(key: String?): QueryParameter {
        throw UnsupportedOperationException()
      }

      override fun formParameter(p0: String?): FormParameter {
        throw UnsupportedOperationException()
      }

      override fun formParameters(): MutableMap<String, FormParameter> {
        throw UnsupportedOperationException()
      }

      override fun getBody(): ByteArray = bodyAsString.toByteArray()

      override fun getBodyAsString(): String = stringBodyProvider()

      override fun getBodyAsBase64(): String {
        throw UnsupportedOperationException()
      }

      override fun isMultipart(): Boolean {
        throw UnsupportedOperationException()
      }

      override fun getParts(): MutableCollection<Request.Part> {
        throw UnsupportedOperationException()
      }

      override fun getPart(name: String?): Request.Part {
        throw UnsupportedOperationException()
      }

      override fun isBrowserProxyRequest(): Boolean {
        throw UnsupportedOperationException()
      }

      override fun getOriginalRequest(): Optional<Request> {
        throw UnsupportedOperationException()
      }

      override fun getProtocol(): String {
        throw UnsupportedOperationException()
      }
    }
  }
}
