/*
 * Copyright (c) 2019 ekino (https://www.ekino.com/)
 */
package com.ekino.oss.jcv.core.validator

import assertk.assertAll
import assertk.assertThat
import assertk.assertions.containsExactly
import assertk.assertions.isEqualTo
import assertk.assertions.isNotNull
import assertk.assertions.isNull
import org.junit.jupiter.api.Test

class ValidatorTemplateManagerTest {

    companion object {
        private const val TEXT_VALIDATOR_VALUE =
            "{#my_validator:some \\; param 1;and another one \\\\; ...;and the last one#}"
        private val defaultTemplateManager = ValidatorTemplateManager(TEXT_VALIDATOR_VALUE)
    }

    @Test
    fun `simple validator id extraction`() {

        val manager = ValidatorTemplateManager("{#my_validator#}")

        assertThat(manager.extractId()).isEqualTo("my_validator")
    }

    @Test
    fun `validator id extraction`() {

        assertThat(defaultTemplateManager.extractId()).isEqualTo("my_validator")
    }

    @Test
    fun `parameters extraction`() {

        assertThat(defaultTemplateManager.extractParameters()).containsExactly(
            "some ; param 1",
            "and another one \\; ...",
            "and the last one"
        )
    }

    @Test
    fun `parameter extraction by index`() {

        val manager = defaultTemplateManager

        assertAll {
            manager.extractParameter(0).let {
                assertThat(it).isNotNull()
                assertThat(it!!).isEqualTo("some ; param 1")
            }
            manager.extractParameter(1).let {
                assertThat(it).isNotNull()
                assertThat(it!!).isEqualTo("and another one \\; ...")
            }
            manager.extractParameter(2).let {
                assertThat(it).isNotNull()
                assertThat(it!!).isEqualTo("and the last one")
            }
            assertThat(manager.extractParameter(3)).isNull()
        }
    }
}
