/*
 * Copyright (c) 2019 ekino (https://www.ekino.com/)
 */
package com.ekino.oss.jcv.assertion.hamcrest

import org.hamcrest.Matcher

/**
 * Utility class to quickly use json matchers.
 */
object JsonMatchers {
  /**
   * The default JSON matcher.
   *
   * @param expectedJson the expected json to compare with the actual one
   *
   * @return the matcher
   *
   * @see JsonMatcherBuilder.create
   */
  @JvmStatic
  fun jsonMatcher(expectedJson: String): Matcher<String> = JsonMatcherBuilder.create().build(expectedJson)
}
