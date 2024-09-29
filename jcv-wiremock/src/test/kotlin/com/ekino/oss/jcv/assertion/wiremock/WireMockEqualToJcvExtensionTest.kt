package com.ekino.oss.jcv.assertion.wiremock

import assertk.assertThat
import assertk.assertions.isEqualTo
import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock.aResponse
import com.github.tomakehurst.wiremock.client.WireMock.post
import com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo
import com.github.tomakehurst.wiremock.core.WireMockConfiguration
import io.github.rybalkinsd.kohttp.client.client
import io.github.rybalkinsd.kohttp.client.defaultHttpClient
import io.github.rybalkinsd.kohttp.client.fork
import io.github.rybalkinsd.kohttp.dsl.httpPost
import io.github.rybalkinsd.kohttp.ext.url
import org.intellij.lang.annotations.Language
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.time.Duration

private val wireMockServer by lazy {
  WireMockServer(
    WireMockConfiguration()
      .dynamicPort()
      .extensions(JcvRequestMatcherExtension())
      .usingFilesUnderClasspath("src/test/resources/wiremock/config"),
  )
}

private val httpClient by lazy {
  defaultHttpClient.fork {
    client {
      connectTimeout = Duration.ofMinutes(1).toMillis()
      readTimeout = Duration.ofMinutes(1).toMillis()
    }
  }
}

class WireMockEqualToJcvExtensionTest {

  companion object {

    @Suppress("unused")
    @JvmStatic
    @BeforeAll
    fun setUpAll() {
      wireMockServer.start()
    }

    @Suppress("unused")
    @JvmStatic
    @AfterAll
    fun tearDownAll() {
      wireMockServer.stop()
    }

    @Language("JSON")
    private val defaultExpectedJson =
      """
      {
        "field_1": "Some value",
        "field_2": "{#uuid#}"
      }
      """.trimIndent()

    @Suppress("unused")
    @JvmStatic
    private fun viaCodeDataProvider() = listOf(
      Arguments.of(
        defaultExpectedJson,
        //language=JSON
        """
        {
          "field_1": "Some value",
          "field_2": "fa04e3fc-1e14-43e1-9097-d7817755435e"
        }
        """.trimIndent(),
        200,
      ),
      Arguments.of(
        defaultExpectedJson,
        //language=JSON
        """
        {
          "field_1": "Some value",
          "field_2": "non uuid value"
        }
        """.trimIndent(),
        404,
      ),
    )

    @Suppress("unused")
    @JvmStatic
    private fun viaFileDataProvider() = listOf(
      Arguments.of(
        //language=JSON
        """
        {
          "field_1": "Some value",
          "field_2": "fa04e3fc-1e14-43e1-9097-d7817755435e"
        }
        """.trimIndent(),
        200,
      ),
      Arguments.of(
        //language=JSON
        """
        {
          "field_1": "Some value",
          "field_2": "non uuid value"
        }
        """.trimIndent(),
        404,
      ),
    )
  }

  @BeforeEach
  fun setUp() {
    wireMockServer.resetAll()
  }

  @AfterEach
  fun tearDown() {
    wireMockServer.resetAll()
  }

  @ParameterizedTest
  @MethodSource("viaCodeDataProvider")
  fun `should use 'equalToJcv' request matcher via code config`(
    expectedJson: String,
    actualJson: String,
    expectedResponseCode: Int,
  ) {
    val targetPath = "/via_code/test"
    wireMockServer.stubFor(
      post(urlEqualTo(targetPath))
        .andMatching(
          JcvRequestMatcherExtension.toRequestMatcher(json = expectedJson),
        )
        .willReturn(
          aResponse()
            .withHeader("Content-Type", "text/plain")
            .withStatus(200)
            .withBody("OK"),
        ),
    )

    @Suppress("unused")
    callWireMock(targetPath = targetPath, jsonBody = actualJson).use {
      assertThat(it.code()).isEqualTo(expectedResponseCode)
    }
  }

  @ParameterizedTest
  @MethodSource("viaFileDataProvider")
  fun `should use 'equalToJcv' request matcher via file config`(
    actualJson: String,
    expectedResponseCode: Int,
  ) {
    val targetPath = "/via_file/test"

    callWireMock(targetPath = targetPath, jsonBody = actualJson).use {
      assertThat(it.code()).isEqualTo(expectedResponseCode)
    }
  }

  private fun callWireMock(targetPath: String, jsonBody: String) = httpPost(httpClient) {
    url(wireMockServer.url(targetPath))
    body {
      json(jsonBody)
    }
  }
}
