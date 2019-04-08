/*
 * Copyright (c) 2019 ekino (https://www.ekino.com/)
 */
package com.ekino.oss.jcv.core.validator

import assertk.assertThat
import assertk.assertions.contains
import assertk.assertions.containsExactly
import assertk.assertions.isEqualTo
import assertk.assertions.isFalse
import assertk.assertions.isTrue
import org.junit.jupiter.api.Test

class ValidatorTemplateManagerTest {

    companion object {
        private const val TEXT_VALIDATOR_VALUE = "{#my_validator:some \\; param 1;and another one \\\\; ...;and the last one#}"
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

        manager.extractParameter(0).let {
            assertThat(it.isPresent).isTrue()
            assertThat(it.get()).contains("some ; param 1")
        }
        manager.extractParameter(1).let {
            assertThat(it.isPresent).isTrue()
            assertThat(it.get()).contains("and another one \\; ...")
        }
        manager.extractParameter(2).let {
            assertThat(it.isPresent).isTrue()
            assertThat(it.get()).contains("and the last one")
        }
        assertThat(manager.extractParameter(3).isPresent).isFalse()
    }
}
