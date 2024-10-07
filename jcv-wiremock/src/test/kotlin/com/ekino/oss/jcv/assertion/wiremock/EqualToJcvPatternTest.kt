package com.ekino.oss.jcv.assertion.wiremock

import assertk.assertThat
import assertk.assertions.isBetween
import assertk.assertions.isEqualTo
import assertk.assertions.isFalse
import assertk.assertions.isTrue
import assertk.assertions.isZero
import org.junit.jupiter.api.Test

/**
 * @see <a href="https://github.com/tomakehurst/wiremock/blob/2.27.2/src/test/java/com/github/tomakehurst/wiremock/matching/EqualToJsonTest.java">com.github.tomakehurst.wiremock.matching.EqualToJsonTest</a>
 */
class EqualToJcvPatternTest {

  @Test
  fun `returns 0 distance for exact match for single level object`() {
    assertThat(
      //language=JSON
      """
      {
        "one": 1,
        "two": 2,
        "three": 3,
        "four": 4
      }
      """ equalToJcv """
      {
        "one": 1,
        "two": 2,
        "three": 3,
        "four": 4
      }
      """,
    )
      .transform { it.distance }
      .isZero()
  }

  @Test
  fun `returns non 0 distance for partial match for single level object`() {
    assertThat(
      //language=JSON
      """
      {
        "one": 1,
        "two": 2,
        "three": 3,
        "four": 4
      }
      """ equalToJcv """
      {
        "one": 1,
        "two": 2,
        "three": 7,
        "four": 8
      }
      """,
    )
      .transform { it.distance }
      .isEqualTo(0.5)
  }

  @Test
  fun `returns large distance for totally different documents`() {
    assertThat(
      //language=JSON
      """
      {
        "one": 1,
        "two": 2,
        "three": 3,
        "four": 4
      }
      """ equalToJcv """
      [1, 2, 3]
      """,
    )
      .transform { it.distance }
      .isEqualTo(1.0)
  }

  @Test
  fun `returns large distance when actual doc is an empty object`() {
    assertThat(
      //language=JSON
      """
      {
        "one": 1,
        "two": 2,
        "three": 3,
        "four": 4
      }
      """ equalToJcv """
      {}
      """,
    )
      .transform { it.distance }
      .isEqualTo(1.0)
  }

  @Test
  fun `returns large distance when actual doc is an empty array`() {
    assertThat(
      //language=JSON
      """
      {
        "one": 1,
        "two": 2,
        "three": 3,
        "four": 4
      }
      """ equalToJcv """
      []
      """,
    )
      .transform { it.distance }
      .isEqualTo(1.0)
  }

  @Test
  fun `returns large distance when expected doc is an empty object`() {
    assertThat(
      //language=JSON
      """
      {}
      """ equalToJcv """
      {
        "one": 1,
        "two": 2,
        "three": 3,
        "four": 4
      }
      """,
    )
      .transform { it.distance }
      .isEqualTo(1.0)
  }

  @Test
  fun `returns large distance when expected doc is an empty array`() {
    assertThat(
      //language=JSON
      """
      []
      """ equalToJcv """
      {
        "one": 1,
        "two": 2,
        "three": 3,
        "four": 4
      }
      """,
    )
      .transform { it.distance }
      .isEqualTo(1.0)
  }

  @Test
  fun `returns medium distance when subtree is missing from actual`() {
    assertThat(
      //language=JSON
      """
      {
        "one": "GET",
        "two": 2,
        "three": {
          "four": "FOUR",
          "five": [
            {
              "six": 6,
              "seven": 7
            },
            {
              "eight": 8,
              "nine": 9
            }
          ]
        }
      }
      """ equalToJcv """
      {
        "one": "GET",
        "two": 2,
        "three": {
          "four": "FOUR"
        }
      }
      """,
    )
      .transform { it.distance }
      .isBetween(0.3, 0.4)
  }

  @Test
  fun `returns exact match when object property order differs`() {
    assertThat(
      //language=JSON
      """
      {
        "one": 1,
        "two": 2,
        "three": 3,
        "four": 4
      }
      """ equalToJcv """
      {
        "one": 1,
        "three": 3,
        "two": 2,
        "four": 4
      }
      """,
    )
      .transform { it.isExactMatch }
      .isTrue()
  }

  @Test
  fun `returns non match when array order differs`() {
    assertThat(
      //language=JSON
      """
      [1, 2, 3, 4]
      """ equalToJcv """
      [1, 3, 2, 4]
      """,
    )
      .transform { it.isExactMatch }
      .isFalse()
  }

  @Test
  fun `ignores array order difference when configured`() {
    assertThat(
      //language=JSON
      """
      [1, 2, 3, 4]
      """.equalToJcv(ignoreArrayOrder = true) {
        """
        [1, 3, 2, 4]
        """
      },
    )
      .transform { it.isExactMatch }
      .isTrue()
  }

  @Test
  fun `ignores nested array order difference when configured`() {
    assertThat(
      //language=JSON
      """
      {
        "one": 1,
        "two": [
          {
            "val": 1
          },
          {
            "val": 2
          },
          {
            "val": 3
          }
        ]
      }
      """.equalToJcv(ignoreArrayOrder = true) {
        """
        {
          "one": 1,
          "two": [
            {
              "val": 3
            },
            {
              "val": 2
            },
            {
              "val": 1
            }
          ]
        }
        """
      },
    )
      .transform { it.isExactMatch }
      .isTrue()
  }

  @Test
  fun `ignores extra object attributes when configured`() {
    assertThat(
      //language=JSON
      """
      {
        "one": 1,
        "two": 2,
        "three": 3,
        "four": 4
      }
      """.equalToJcv(ignoreExtraElements = true) {
        """
        {
          "one": 1,
          "two": 2,
          "three": 3,
          "four": 4,
          "five": 5,
          "six": 6
        }
        """
      },
    )
      .transform { it.isExactMatch }
      .isTrue()
  }

  @Test
  fun `ignores extra object attributes and array order when configured`() {
    assertThat(
      //language=JSON
      """
      {
        "one": 1,
        "two": 2,
        "three": 3,
        "four": [
          1,
          2,
          3
        ]
      }
      """.equalToJcv(ignoreArrayOrder = true, ignoreExtraElements = true) {
        """
        {
          "one": 1,
          "three": 3,
          "two": 2,
          "four": [
            2,
            1,
            3
          ],
          "five": 5,
          "six": 6
        }
        """
      },
    )
      .transform { it.isExactMatch }
      .isTrue()
  }

  @Test
  fun `returns no exact match for very similar nested docs`() {
    assertThat(
      //language=JSON
      """
      {
        "outer": {
          "inner:": {
            "wrong": 1
          }
        }
      }
      """ equalToJcv """
      {
        "outer": {
          "inner:": {
            "thing": 1
          }
        }
      }
      """,
    )
      .transform { it.isExactMatch }
      .isFalse()
  }

  @Test
  fun `does not match when value is null`() {
    val result =
      //language=JSON
      """
      {
        "outer": {
          "inner:": {
            "wrong": 1
          }
        }
      }
      """ equalToJcv null
    assertThat(result.isExactMatch).isFalse()
    assertThat(result.distance).isEqualTo(1.0)
  }

  @Test
  fun `does not match when value is empty string`() {
    val result =
      //language=JSON
      """
      {
        "outer": {
          "inner:": {
            "wrong": 1
          }
        }
      }
      """ equalToJcv ""
    assertThat(result.isExactMatch).isFalse()
    assertThat(result.distance).isEqualTo(1.0)
  }

  @Test
  fun `does not match when value is not json`() {
    val result =
      //language=JSON
      """
      {
        "outer": {
          "inner:": {
            "wrong": 1
          }
        }
      }
      """.equalToJcv(
        //language=XML
        """
        <some-xml />
        """,
      )
    assertThat(result.isExactMatch).isFalse()
    assertThat(result.distance).isEqualTo(1.0)
  }

  @Test
  fun `does not break when comparing nested arrays of different sizes`() {
    assertThat(
      //language=JSON
      """
      {
        "columns": [
          {
            "name": "agreementnumber",
            "a": 1
          },
          {
            "name": "utilizerstatus",
            "b": 2
          }
        ]
      }
      """ equalToJcv """
      {
        "columns": [
          {
            "name": "x",
            "y": 3
          },
          {
            "name": "agreementnumber",
            "a": 1
          },
          {
            "name": "agreementstatus",
            "b": 2
          }
        ]
      }
      """,
    )
      .transform { it.isExactMatch }
      .isFalse()
  }

  @Test
  fun `does not break when comparing top level arrays of different sizes with common elements`() {
    assertThat(
      //language=JSON
      """
      [
        {
          "one": 1
        },
        {
          "two": 2
        },
        {
          "three": 3
        }
      ]
      """ equalToJcv """
      [
        {
          "zero": 0
        },
        {
          "one": 1
        },
        {
          "two": 2
        },
        {
          "four": 4
        }
      ]
      """,
    )
      .transform { it.isExactMatch }
      .isFalse()
  }

  @Test
  fun `does not match empty arrays when not ignoring extra elements`() {
    assertThat(
      //language=JSON
      """
      {
        "client": "AAA",
        "name": "BBB"
      }
      """ equalToJcv """
      {
        "client": "AAA",
        "name": "BBB",
        "addresses": []
      }
      """,
    )
      .transform { it.isExactMatch }
      .isFalse()
  }

  /**
   * The original test name seems incorrect with the actual test configuration.
   * Should be "when ignoring array order" instead of "ignoring extra array elements"
   *
   * @see <a href="https://github.com/tomakehurst/wiremock/blob/2.27.2/src/test/java/com/github/tomakehurst/wiremock/matching/EqualToJsonTest.java#L523">EqualToJsonTest#doesNotMatchEmptyArrayWhenIgnoringExtraArrayElementsAndNotIgnoringExtraElements</a>
   */
  @Test
  fun `does not match empty array when ignoring extra array elements and not ignoring extra elements`() {
    assertThat(
      //language=JSON
      """
      {
        "client": "AAA",
        "name": "BBB"
      }
      """.equalToJcv(ignoreArrayOrder = true) {
        """
        {
          "client": "AAA",
          "name": "BBB",
          "addresses": []
        }
        """
      },
    )
      .transform { it.isExactMatch }
      .isFalse()
  }

  /**
   * The original test name seems incorrect with the actual test configuration.
   * Should be "when ignoring array order" instead of "ignoring extra array elements"
   *
   * @see <a href="https://github.com/tomakehurst/wiremock/blob/2.27.2/src/test/java/com/github/tomakehurst/wiremock/matching/EqualToJsonTest.java#L533">EqualToJsonTest#doesNotMatchEmptyObjectWhenIgnoringExtraArrayElementsAndNotIgnoringExtraElements</a>
   */
  @Test
  fun `does not match empty object when ignoring extra array elements and not ignoring extra elements`() {
    assertThat(
      //language=JSON
      """
      {
        "client": "AAA",
        "name": "BBB"
      }
      """.equalToJcv(ignoreArrayOrder = true) {
        """
        {
          "client": "AAA",
          "name": "BBB",
          "addresses": {}
        }
        """
      },
    )
      .transform { it.isExactMatch }
      .isFalse()
  }

  @Test
  fun `treats two top levels arrays with differing order as same when ignoring order`() {
    assertThat(
      //language=JSON
      """
      ["a","b", "c","d","e","f","g","h"]
      """.equalToJcv(ignoreArrayOrder = true) {
        """
        ["a","b", "d","c","e","f","g","h"]
        """
      },
    )
      .transform { it.isExactMatch }
      .isTrue()
  }

  /**
   * Equivalent of original "junit placeholders" test.
   *
   * @see <a href="https://github.com/tomakehurst/wiremock/blob/2.27.2/src/test/java/com/github/tomakehurst/wiremock/matching/EqualToJsonTest.java#L556">EqualToJsonTest#supportsPlaceholders</a>
   * @see <a href="https://ekino.github.io/jcv/#examples">JCV - Examples</a>
   */
  @Test
  fun `supports jcv validators`() {
    assertThat(
      //language=JSON
      """
      {
        "field_1": "some value",
        "field_2": "{#uuid#}",
        "date": "{#date_time_format:iso_instant#}",
        "other_fields": [
          {
            "id": "1",
            "link": "{#url#}"
          },
          {
            "id": "2",
            "link": "{#url_ending:query?param1=true#}"
          }
        ]
      }
      """.equalToJcv(ignoreArrayOrder = true) {
        """
        {
          "field_1": "some value",
          "field_2": "3716a0cf-850e-46c3-bd97-ac1f34437c43",
          "date": "2011-12-03T10:15:30Z",
          "other_fields": [
            {
              "id": "2",
              "link": "https://another.url.com/my-base-path/query?param1=true"
            },
            {
              "id": "1",
              "link": "https://some.url.com"
            }
          ]
        }
        """
      },
    )
      .transform { it.isExactMatch }
      .isTrue()
  }
}

private infix fun String.equalToJcv(actual: String?) = this.equalToJcv { actual }

private fun String.equalToJcv(ignoreArrayOrder: Boolean? = null, ignoreExtraElements: Boolean? = null, actual: () -> String?) = EqualToJcvPattern(
  json = this,
  ignoreArrayOrder = ignoreArrayOrder,
  ignoreExtraElements = ignoreExtraElements,
)
  .match(actual())
