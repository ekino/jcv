package com.ekino.oss.jcv.core.validator

import com.ekino.oss.jcv.core.JsonValidator
import com.ekino.oss.jcv.core.JsonValueComparator
import com.ekino.oss.jcv.core.comparator.TypeComparator
import com.ekino.oss.jcv.core.initializer.Initializers
import com.ekino.oss.jcv.core.initializer.KNoParameterComparatorInitializer
import com.ekino.oss.jcv.core.initializer.TemplatedComparatorInitializer
import com.ekino.oss.jcv.core.initializer.asNoParameterComparatorInitializer
import org.skyscreamer.jsonassert.ValueMatcher

fun validators(init: ValidatorsBuilder.() -> Unit) = ValidatorsBuilder().apply(init).build()

fun <T> validator(init: ValidatorBuilder.() -> JsonValidator<T>) = init(ValidatorBuilder())

fun <T> comparator(comparator: (actual: T?, expected: T?) -> Boolean) = ValueMatcher<T>(comparator)

fun <T> forPathPrefix(pathPrefix: String, comparator: ValueMatcher<T>) = Validators.forPath(pathPrefix, comparator)

open class ValidatorBuilder {

  fun <T> templatedValidator(validatorId: String, comparator: ValueMatcher<T>) =
    Validators.templatedValidator(validatorId, comparator)

  fun <T> templatedValidator(
    validatorId: String,
    init: TemplatedComparatorBuilder<T>.() -> TemplatedComparatorInitializer<T>,
  ) =
    Initializers.parameterizedValidator(validatorId, init(TemplatedComparatorBuilder()))

  inline fun <reified T> typeComparator(): JsonValueComparator<Any> = TypeComparator(T::class.java)
}

class ValidatorsBuilder : ValidatorBuilder() {
  private val validators = mutableListOf<JsonValidator<*>>()

  operator fun JsonValidator<*>.unaryPlus() {
    validators.add(this)
  }

  operator fun Collection<JsonValidator<*>>.unaryPlus() {
    validators.addAll(this)
  }

  fun build() = validators.toList()
}

class TemplatedComparatorBuilder<T> {

  fun comparatorWithoutParameter(initializer: KNoParameterComparatorInitializer<T>) =
    Initializers.comparatorWithoutParameter(asNoParameterComparatorInitializer(initializer))

  fun comparatorWithParameters(init: ComparatorWithParameterBuilder<T>.() -> ValueMatcher<T>) =
    object : TemplatedComparatorInitializer<T> {
      override fun initComparator(validatorTemplateManager: ValidatorTemplateManager): ValueMatcher<T> =
        init(ComparatorWithParameterBuilder(validatorTemplateManager))
    }

  inline fun <reified T> typeComparator() = TypeComparator(T::class.java)

  fun comparatorWith1RequiredParameter(comparator: (parameter: String) -> ValueMatcher<T>) =
    comparatorWithParameters { comparator(getFirstRequiredParam()) }

  fun allOf(init: AllOfOperatorBuilder<T>.() -> Unit): TemplatedComparatorInitializer<T> =
    AllOfOperatorBuilder<T>().apply(init).build()

  fun anyOf(init: AnyOfOperatorBuilder<T>.() -> Unit): TemplatedComparatorInitializer<T> =
    AnyOfOperatorBuilder<T>().apply(init).build()
}

class AllOfOperatorBuilder<T> {

  private val validators = mutableListOf<TemplatedComparatorInitializer<T>>()

  operator fun TemplatedComparatorInitializer<T>.unaryPlus() {
    validators.add(this)
  }

  operator fun ValueMatcher<T>.unaryPlus() {
    validators.add(Initializers.comparatorWithoutParameter(asNoParameterComparatorInitializer { this }))
  }

  fun build() = Initializers.allOf(*validators.toTypedArray())
}

class AnyOfOperatorBuilder<T> {

  private val validators = mutableListOf<TemplatedComparatorInitializer<T>>()

  operator fun TemplatedComparatorInitializer<T>.unaryPlus() {
    validators.add(this)
  }

  operator fun ValueMatcher<T>.unaryPlus() {
    validators.add(Initializers.comparatorWithoutParameter(asNoParameterComparatorInitializer { this }))
  }

  fun build() = Initializers.anyOf(*validators.toTypedArray())
}

class ComparatorWithParameterBuilder<T>(private val validatorTemplateManager: ValidatorTemplateManager) {

  fun getFirstRequiredParam() = getRequiredParam(0)

  fun getSecondRequiredParam() = getRequiredParam(1)

  fun getFirstParam() = getParam(0)

  fun getSecondParam() = getParam(1)

  fun getParam(index: Int) = validatorTemplateManager.extractParameter(index)

  fun getRequiredParam(index: Int) = requireNotNull(
    getParam(index),
    { "No parameter at index $index found in validator '${validatorTemplateManager.extractTemplateContent()}'" },
  )
}
