---
title: How to create custom validators ?
author: Léo Millon
---

# {{ page.title }}

Sometime you need something more specific than the pre-defined validators provided with the library.

So in this situation, you may want to defined your own validators to be reused across various tests.

## Table of contents

* [How does it work ?](#how-does-it-work-)
    * [Validator template](#validator-template)
    * [Comparator](#comparator)
* [A full working example](#a-full-working-example)
    * [Case 1](#case-1)
    * [Case 2](#case-2)
    * [Case 3](#case-3)
* [Inspiration](#inspiration)

## How does it work ?

To create a custom validator you need 2 main things :
- a validator template
- a comparator to assert the content

### Validator template

#### Validator without arg

The basic system used to identify a validator is a unique string (case-insensitive) with a `{#` prefix and a `#}` suffix : `{#some_id#}`.

Example : `{#uuid#}` will match with the validator with the `uuid` string id.

#### Validator with args

To add arguments/parameters to your validator, you sill need to defined the id but then you append `:` followed by your arguments separated by `;` : `{#some_id:param 1;param 2#}`

Examples :
- `{#contains:ello#}` : will match the `contains` validator and `ello` will be the only argument available for the comparator.
- `{#date_time_format:d MMM uuu;fr-FR#}` : will match the `date_time_format` validator and `d MMM uuu` will be the first argument and `fr-FR` will be the second one.

If you want the `;` char to be part of the argument, you can escape it like that : `{#contains:some \\; param 1#}`.

### Comparator

Now that you identified your validator, you will have to assert the content by comparing it to a static treatment or with the provided arguments.

#### Comparator definition

You need to :
- implement the `JsonValueComparator<T>` (where `T` represents the type of the JSON field value to assert)
- throw a `ValueMatcherException` to add details to the assertion error

Here is a simple example using the contains comparator `com.ekino.oss.jcv.core.comparator.ContainsComparator` :
```java
package com.ekino.oss.jcv.core.comparator;

import com.ekino.oss.jcv.core.JsonValueComparator;

import org.skyscreamer.jsonassert.ValueMatcherException;

import static java.util.Objects.*;

/**
 * A contains comparator on text.
 *
 * @author Leo Millon
 *
 * @see String#contains(CharSequence)
 */
public class ContainsComparator implements JsonValueComparator<String> {

    private final String value;

    /**
     * Init comparator with the value to search for.
     *
     * @param value the value to search for
     */
    public ContainsComparator(String value) {
        this.value = requireNonNull(value);
    }

    @Override
    public boolean hasCorrectValue(String actual, String expected) {
        if (actual != null && actual.contains(value)) {
            return true;
        }
        throw new ValueMatcherException("Value should contain '" + value + "'", expected, actual);
    }
}

```

Note : the constructor parameter will be provided to the comparator by the validator template.

## A full working example

You can find the sources here : [jcv-customvalidator-example](https://github.com/ekino/jcv-examples/tree/master/jcv-customvalidator-example)

### Case 1

#### Context

> I want to create a validator that will assert that the JSON field value is a string starting with "REF_" and has a size of 14 characters

So a valid field value will be for example : `REF_0123456789`

Using JCV we would like to assert that this actual JSON :
```json
{
  "id": "fda7a233-99b9-4756-8ecc-826a1c5a9bf5",
  "reference": "REF_0123456789"
}
```

is valid against this expected one :
```json
{
  "id": "{#uuid#}",
  "reference": "{#my_ref#}"
}
```

#### The comparator

To do so, we can create a dedicated comparator :

```java
import com.ekino.oss.jcv.core.JsonValueComparator;
import org.skyscreamer.jsonassert.ValueMatcherException;

class MyRefComparator implements JsonValueComparator<String> {

    @Override
    public boolean hasCorrectValue(String actual, String expected) {
        if (actual != null && actual.startsWith("REF_") && actual.length() == 14) {
            return true;
        }
        throw new ValueMatcherException("Invalid reference format", expected, actual);
    }
}
```

Here we implement the `JsonValueComparator<String>` because we want to assert a string field value by defining the 
`JsonValueComparator#hasCorrectValue(String actual, String expected)` method.

So we check that it is starting with `REF_` and that the length is `14`, else we throw a `ValueMatcherException` to add a detailed error message.

#### The validator

Now we need to plug this comparator to an identified validator definition.

Using util methods in the `com.ekino.oss.jcv.core.validator.Validators`, we can use the pre-defined templating system to create our `{#my_ref#}` validator :

```java
import com.ekino.oss.jcv.core.JsonValidator;

import static com.ekino.oss.jcv.core.validator.Validators.*;

private static JsonValidator myRefValiadtor() {
    return templatedValidator("my_ref", new MyRefComparator());
}
```

#### The test

We will use the jcv-assertj module to create the test.

```java
import java.util.LinkedList;
import java.util.List;

import com.ekino.oss.jcv.core.JsonValidator;
import com.ekino.oss.jcv.core.JsonValueComparator;
import com.ekino.oss.jcv.core.validator.Validators;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.ValueMatcherException;

import static com.ekino.oss.jcv.assertion.assertj.JsonCompareAssert.*;
import static com.ekino.oss.jcv.core.validator.Validators.*;
import static com.ekino.oss.jcv.example.jcvcustomvalidatorexample.util.ResourceLoader.*;

@Test
void should_validate_json_content_with_custom_validator() {

    assertThatJson(loadJson("case1/actual.json"))
        .using(customValidators())
        .isValidAgainst(loadJson("case1/expected.json"));
}

private static List<JsonValidator> customValidators() {
    LinkedList<JsonValidator> validators = new LinkedList<>();

    validators.add(myRefValiadtor());
    validators.addAll(Validators.defaultValidators());

    return validators;
}

private static JsonValidator myRefValiadtor() {
    return templatedValidator("my_ref", new MyRefComparator());
}

private static class MyRefComparator implements JsonValueComparator<String> {

    @Override
    public boolean hasCorrectValue(String actual, String expected) {
        if (actual != null && actual.startsWith("REF_") && actual.length() == 14) {
            return true;
        }
        throw new ValueMatcherException("Invalid reference format", expected, actual);
    }
}
```

**Note** : Keep in mind that you need to add explicitly the default validators if you don't want to only use your custom validators (in our case `{#uuid#}`).

If we use the following failing "actual json" :
```json
{
  "id": "fda7a233-99b9-4756-8ecc-826a1c5a9bf5",
  "reference": "HELLO_42"
}
```

You will get :
```
java.lang.AssertionError: reference: Invalid reference format
Expected: {#my_ref#}
     got: HELLO_42
```

Here is the source code : [com.ekino.oss.jcv.example.jcvcustomvalidatorexample.Case1Test](https://github.com/ekino/jcv-examples/blob/master/jcv-customvalidator-example/src/test/java/com/ekino/oss/jcv/example/jcvcustomvalidatorexample/Case1Test.java)

### Case 2

#### Context

> I want to create a validator that will assert that the JSON field value is a string starting with a parameterized prefix and has a size of 14 characters

So a valid field value will be for example : `REF_0123456789` or `TEST-012345678` ...

Using JCV we would like to assert that this actual JSON :
```json
{
  "id": "fda7a233-99b9-4756-8ecc-826a1c5a9bf5",
  "reference_1": "REF_0123456789",
  "reference_2": "TEST-012345678"
}
```

is valid against this expected one :
```json
{
  "id": "{#uuid#}",
  "reference_1": "{#my_ref:REF_#}",
  "reference_2": "{#my_ref:TEST-#}"
}
```

#### The comparator

To do so, we can create a dedicated comparator :

```java
import com.ekino.oss.jcv.core.JsonValueComparator;
import org.skyscreamer.jsonassert.ValueMatcherException;

class MyRefComparator implements JsonValueComparator<String> {

    private final String prefix;

    private MyRefComparator(String prefix) {
        this.prefix = prefix;
    }

    @Override
    public boolean hasCorrectValue(String actual, String expected) {
        if (actual != null && actual.startsWith(prefix) && actual.length() == 14) {
            return true;
        }
        throw new ValueMatcherException("Reference format should be 14 chars long and start by " + prefix, expected, actual);
    }
}
```

Here we implement the `JsonValueComparator<String>` because we want to assert a string field value by defining the 
`JsonValueComparator#hasCorrectValue(String actual, String expected)` method.

We add the `prefix` as a constructor parameter, it will be provided by the validator parameter.

So we check that it is starting with the given `prefix` and that the length is `14`, else we throw a `ValueMatcherException` to add a detailed error message.

#### The validator

Now we need to plug this comparator to an identified validator definition that handle parameters.

As we now want to pass parameters from the template to the comparator, we should use the util methods in the `com.ekino.oss.jcv.core.initializer.Initializers`, this will allow use to use the pre-defined templating system to create our `{#my_ref:SOME_PREFIX#}` validator :

```java
import com.ekino.oss.jcv.core.JsonValidator;

import static com.ekino.oss.jcv.core.initializer.Initializers.*;

private static JsonValidator myRefValiadtor() {
    return parameterizedValidator("my_ref", comparatorWith1Parameter(MyRefComparator::new));
}
```

The `Initializers#comparatorWith1Parameter` takes a `OneParameterComparatorInitializer<T>` which is a simple functional interface to provider the parameter to the comparator :
```java
/**
 * Comparator intializer given tempated validator with 1 parameter information.
 *
 * @param <T> the field value type
 *
 * @author Leo Millon
 */
@FunctionalInterface
public interface OneParameterComparatorInitializer<T> {

    /**
     * Init a comparator using the current templated validator info.
     *
     * @param parameter the first parameter of the templated validator
     *
     * @return the initalized comparator
     */
    ValueMatcher<T> initComparator(String parameter);
}
```

So the method reference to the comparator `MyRefComparator::new` perfectly matches this interface.

#### The test

We will use the jcv-assertj module to create the test.

```java
import java.util.LinkedList;
import java.util.List;

import com.ekino.oss.jcv.core.JsonValidator;
import com.ekino.oss.jcv.core.JsonValueComparator;
import com.ekino.oss.jcv.core.validator.Validators;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.ValueMatcherException;

import static com.ekino.oss.jcv.assertion.assertj.JsonCompareAssert.*;
import static com.ekino.oss.jcv.core.initializer.Initializers.*;
import static com.ekino.oss.jcv.example.jcvcustomvalidatorexample.util.ResourceLoader.*;

@Test
void should_validate_json_content_with_custom_validator() {

    assertThatJson(loadJson("case2/actual.json"))
        .using(customValidators())
        .isValidAgainst(loadJson("case2/expected.json"));
}

private static List<JsonValidator> customValidators() {
    LinkedList<JsonValidator> validators = new LinkedList<>();

    validators.add(myRefValiadtor());
    validators.addAll(Validators.defaultValidators());

    return validators;
}

private static JsonValidator myRefValiadtor() {
    return parameterizedValidator("my_ref", comparatorWith1Parameter(MyRefComparator::new));
}

private static class MyRefComparator implements JsonValueComparator<String> {

    private final String prefix;

    private MyRefComparator(String prefix) {
        this.prefix = prefix;
    }

    @Override
    public boolean hasCorrectValue(String actual, String expected) {
        if (actual != null && actual.startsWith(prefix) && actual.length() == 14) {
            return true;
        }
        throw new ValueMatcherException("Reference format should be 14 chars long and start by " + prefix, expected, actual);
    }
}
```

**Note** : Keep in mind that you need to add explicitly the default validators if you don't want to only use your custom validators (in our case `{#uuid#}`).

If we use the following failing "actual json" :
```json
{
  "id": "fda7a233-99b9-4756-8ecc-826a1c5a9bf5",
  "reference_1": "REF_01",
  "reference_2": "TEST_012345678"
}

```

You will get :
```
java.lang.AssertionError: reference_1: Reference format should be 14 chars long and start by REF_
Expected: {#my_ref:REF_#}
     got: REF_01
 ; reference_2: Reference format should be 14 chars long and start by TEST-
Expected: {#my_ref:TEST-#}
     got: TEST_012345678
```

Here is the source code : [com.ekino.oss.jcv.example.jcvcustomvalidatorexample.Case2Test](https://github.com/ekino/jcv-examples/blob/master/jcv-customvalidator-example/src/test/java/com/ekino/oss/jcv/example/jcvcustomvalidatorexample/Case2Test.java)

### Case 3

#### Context

> I want to create a validator that will assert that the JSON field value is a string starting with a parameterized prefix and has a parameterized size (or 14 by default) of characters

So a valid field value will be for example : `REF_0123456789` or `TEST-012` ...

Using JCV we would like to assert that this actual JSON :
```json
{
  "id": "fda7a233-99b9-4756-8ecc-826a1c5a9bf5",
  "reference_1": "REF_0123456789",
  "reference_2": "TEST-012"
}
```

is valid against this expected one :
```json
{
  "id": "{#uuid#}",
  "reference_1": "{#my_ref:REF_#}",
  "reference_2": "{#my_ref:TEST-;8#}"
}
```

#### The comparator

To do so, we can create a dedicated comparator :

```java
import com.ekino.oss.jcv.core.JsonValueComparator;
import org.skyscreamer.jsonassert.ValueMatcherException;

class MyRefComparator implements JsonValueComparator<String> {

    private final String prefix;
    private final Integer size;

    private MyRefComparator(String prefix, Integer size) {
        this.prefix = prefix;
        this.size = size;
    }

    @Override
    public boolean hasCorrectValue(String actual, String expected) {
        if (actual != null && actual.startsWith(prefix) && actual.length() == size) {
            return true;
        }
        throw new ValueMatcherException("Reference format should be " + size + " chars long and start by " + prefix, expected, actual);
    }
}
```

Here we implement the `JsonValueComparator<String>` because we want to assert a string field value by defining the 
`JsonValueComparator#hasCorrectValue(String actual, String expected)` method.

We add the `prefix` and `size` as a constructor parameters, they will be provided by the validator parameters.

So we check that it is starting with the given `prefix` and that the length is equal to the `size`, else we throw a `ValueMatcherException` to add a detailed error message.

#### The validator

Now we need to plug this comparator to an identified validator definition that handle parameters.

As we now want to pass parameters from the template to the comparator, we should use the util methods in the `com.ekino.oss.jcv.core.initializer.Initializers`, this will allow use to use the pre-defined templating system to create our `{#my_ref:SOME_PREFIX;SOME_SIZE#}` validator :

```java
import com.ekino.oss.jcv.core.JsonValidator;
import com.ekino.oss.jcv.core.initializer.TwoParametersComparatorInitializer;

import static com.ekino.oss.jcv.core.initializer.Initializers.*;
import static java.util.Optional.*;

private static JsonValidator myRefValiadtor() {
    return parameterizedValidator(
        "my_ref",
        comparatorWith2Parameters(true, false, initReferenceComparator(14))
    );
}

private static TwoParametersComparatorInitializer<String> initReferenceComparator(int defaultValue) {
    return (String param1, String param2) -> {
        Integer size = ofNullable(param2).map(Integer::parseInt).orElse(defaultValue);
        return new MyRefComparator(param1, size);
    };
}
```

Take a closer look at the `comparatorWith2Parameters(true, false, initReferenceComparator(14))` method, it takes :
1. `true` to indicate that the first param is required in the validator definition
2. `false` to indicate that the second param is optional in the validator definition
3. `initReferenceComparator(14)` which will init the comparator with the correct arguments

As you can see in the `initReferenceComparator` function, the parameters are `String` objects 
so we need to parse the second one into an `Integer` value or use the default value if not provided.

#### The test

We will use the jcv-assertj module to create the test.

```java
import java.util.LinkedList;
import java.util.List;

import com.ekino.oss.jcv.core.JsonValidator;
import com.ekino.oss.jcv.core.JsonValueComparator;
import com.ekino.oss.jcv.core.initializer.TwoParametersComparatorInitializer;
import com.ekino.oss.jcv.core.validator.Validators;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.ValueMatcherException;

import static com.ekino.oss.jcv.assertion.assertj.JsonCompareAssert.*;
import static com.ekino.oss.jcv.core.initializer.Initializers.*;
import static com.ekino.oss.jcv.example.jcvcustomvalidatorexample.util.ResourceLoader.*;
import static java.util.Optional.*;

@Test
void should_validate_json_content_with_custom_validator() {

    assertThatJson(loadJson("case3/actual.json"))
        .using(customValidators())
        .isValidAgainst(loadJson("case3/expected.json"));
}

private static List<JsonValidator> customValidators() {
    LinkedList<JsonValidator> validators = new LinkedList<>();

    validators.add(myRefValiadtor());
    validators.addAll(Validators.defaultValidators());

    return validators;
}

private static JsonValidator myRefValiadtor() {
    return parameterizedValidator(
        "my_ref",
        comparatorWith2Parameters(true, false, initReferenceComparator(14))
    );
}

private static TwoParametersComparatorInitializer<String> initReferenceComparator(int defaultValue) {
    return (String param1, String param2) -> {
        Integer size = ofNullable(param2).map(Integer::parseInt).orElse(defaultValue);
        return new MyRefComparator(param1, size);
    };
}

private static class MyRefComparator implements JsonValueComparator<String> {

    private final String prefix;
    private final Integer size;

    private MyRefComparator(String prefix, Integer size) {
        this.prefix = prefix;
        this.size = size;
    }

    @Override
    public boolean hasCorrectValue(String actual, String expected) {
        if (actual != null && actual.startsWith(prefix) && actual.length() == size) {
            return true;
        }
        throw new ValueMatcherException("Reference format should be " + size + " chars long and start by " + prefix, expected, actual);
    }
}
```

**Note** : Keep in mind that you need to add explicitly the default validators if you don't want to only use your custom validators (in our case `{#uuid#}`).

If we use the following failing "actual json" :
```json
{
  "id": "fda7a233-99b9-4756-8ecc-826a1c5a9bf5",
  "reference_1": "REF_01",
  "reference_2": "TEST_012345678"
}
```

You will get :
```
java.lang.AssertionError: reference_1: Reference format should be 14 chars long and start by REF_
Expected: {#my_ref:REF_#}
     got: REF_01
 ; reference_2: Reference format should be 8 chars long and start by TEST-
Expected: {#my_ref:TEST-;8#}
     got: TEST_012345678
```

Here is the source code : [com.ekino.oss.jcv.example.jcvcustomvalidatorexample.Case3Test](https://github.com/ekino/jcv-examples/blob/master/jcv-customvalidator-example/src/test/java/com/ekino/oss/jcv/example/jcvcustomvalidatorexample/Case2Test.java)

## Inspiration

You can have a look at the current default validators definition.

`com.ekino.oss.jcv.core.validator.Validators#defaultValidators`:
````java
public static List<JsonValidator> defaultValidators() {
    return Arrays.asList(
        parameterizedValidator("contains", comparatorWith1Parameter(ContainsComparator::new)),
        parameterizedValidator("starts_with", comparatorWith1Parameter(StartsWithComparator::new)),
        parameterizedValidator("ends_with", comparatorWith1Parameter(EndsWithComparator::new)),
        parameterizedValidator("regex", comparatorWith1Parameter(it -> new RegexComparator(Pattern.compile(it)))),
        templatedValidator("uuid", new UUIDComparator()),
        templatedValidator("not_null", new NotNullComparator()),
        templatedValidator("not_empty", new NotEmptyComparator()),
        templatedValidator("url", new URLComparator()),
        parameterizedValidator("url_ending", allOf(
            comparatorWithoutParameter(URLComparator::new),
            comparatorWith1Parameter(EndsWithComparator::new)
        )),
        parameterizedValidator("url_regex", allOf(
            comparatorWithoutParameter(URLComparator::new),
            comparatorWith1Parameter(it -> new RegexComparator(Pattern.compile(it)))
        )),
        templatedValidator("templated_url", new TemplatedURLComparator()),
        parameterizedValidator("templated_url_ending", allOf(
            comparatorWithoutParameter(TemplatedURLComparator::new),
            comparatorWith1Parameter(EndsWithComparator::new)
        )),
        parameterizedValidator("templated_url_regex", allOf(
            comparatorWithoutParameter(TemplatedURLComparator::new),
            comparatorWith1Parameter(it -> new RegexComparator(Pattern.compile(it)))
        )),
        type("boolean_type", Boolean.class),
        type("string_type", String.class),
        type("number_type", Number.class),
        type("array_type", JSONArray.class),
        type("object_type", JSONObject.class),
        parameterizedValidator("date_time_format", comparatorWith2Parameters(true, false, new DateTimeFormatComparatorInitializer()))
    );
}
````
