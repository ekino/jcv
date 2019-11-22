# Contributing to JCV

The following is a set of guidelines for contributing to JCV, which are hosted in the Ekino Organization on GitHub. These are mostly guidelines, not rules. Use your best judgment, and feel free to propose changes to this document in a pull request.

#### Table Of Contents

[How Can I Contribute?](#how-can-i-contribute)
  * [Reporting Bugs](#reporting-bugs)
  * [Suggesting Enhancements](#suggesting-enhancements)
  * [Your First Code Contribution](#your-first-code-contribution)
  * [Pull Requests](#pull-requests)

[Styleguides](#styleguides)
  * [Git Commit Messages](#git-commit-messages)
  * [Java Styleguide](#java-styleguide)
  * [Kotlin Styleguide](#kotlin-styleguide)

## How Can I Contribute?

### Reporting Bugs

This section guides you through submitting a bug report for JCV. Following these guidelines helps maintainers and the community understand your report :pencil:, reproduce the behavior :computer: :computer:, and find related reports :mag_right:.

#### How Do I Submit A (Good) Bug Report?

Bugs can be reported in github as issues (with labels bug). Explain the problem and include additional details to help maintainers reproduce the problem:

* **Use a clear and descriptive title** for the issue to identify the problem.
* **Describe the exact steps which reproduce the problem** in as many details as possible.
* **Provide specific examples to demonstrate the use case**. Include links to files or GitHub projects, or copy/pasteable snippets, which you use in those examples. If you're providing snippets in the issue, use [Markdown code blocks](https://help.github.com/articles/markdown-basics/#multiple-lines).
* **Explain which behavior you expected to see instead and why.**

### Suggesting Enhancements

As bugs, we use github issues to manage enhancements (with label enhancement).

* **Use a clear and descriptive title** for the issue to identify the suggestion.
* **Provide a step-by-step description of the suggested enhancement** in as many details as possible.
* **Provide specific examples to demonstrate the steps**. Include copy/pasteable snippets which you use in those examples, as [Markdown code blocks](https://help.github.com/articles/markdown-basics/#multiple-lines).

### Your First Code Contribution

Be sure to assign yo yourself the related bug or enhancement

### Pull Requests

2. Follow the [styleguides](#styleguides)
3. After you submit your pull request, verify that travis build (can be seen in your PR status)

## Styleguides

### Git Commit Messages

As git commit messages are used to generate changelog, it can be usefull to have the same formalism.
We suggest to use [Conventional commits guidelines](https://www.conventionalcommits.org )

### Java Styleguide

These are mostly standard Java coding conventions:

* Names use camel case:
    * class names start with an upper case character: 

    ```java
    class ClassName {} 
    ```

    * field names, variable names and method names start with a lower case: 

    ```java
    private String firstName;
    int varName;
    public boolean doSomething() {}
    constant are in upper snake case:
    private static final String EMPTY_STRING = "";
    ```

    * test methods are an exception, it's easier to write long, readable descriptions using snake case:

    ```java
    @Test
    public void should_persist_the_entity_when_saving() {}
    ```

* Indentation uses spaces (4), not tabs
* Wrap lines at 120 characters, to avoid overly long lines
* Fields do not need to be separated from each others, but there should be a blank line between static fields and instance fields, between fields and methods, between methods, and between methods and inner or nested classes.
* Use explicit imports instead of wildcards:

    ```java
    import java.util.Collection;
    ```
    instead of
    ```java
    import java.util.*;
    ```

* The exception is static imports (which mostly happen in test classes):

    ```java
    import static org.assertj.core.api.Assertions.*;
    ```

> **_NOTE:_** When using IntelliJ IDEA, go in the Preferences window, then Editor > Code Style > Java, then the Imports tab, and set "Class count to use import with '*'" to 999. 

* Use named constants instead of "magic" values, with a name describing what it's for, not what it is, e.g.
    ```java
    private static final String SEPARATOR = ",";
    ```
instead of
    ```java
    private static final String COMMA = ",";
    ```

* Control flow keywords are not method calls, keep a space between the keyword and the condition:
    ```java
    if (bool) {
    ```    
    instead of
    ```java
    if(bool) { 
    ```

* Generally spaces help for the readability, keep them between method parameters as well:
    ```java
    new HashMap<>(16, 0.75)
    ```
    instead of
    ```java
    new HashMap<>(16,0.75)
    ```

* Don't use one-liners in the control flow, always use curly braces for blocks:
    ```java
    if (bool) {
        doSomething();
    }
    ```
    instead of
    ```java
    if (bool)
        doSomething();
    ```
    or
    ```java
    if (bool) doSomething(); 
    ```

* Adding a second line to the block and forgetting to add the braces is a common error cause, even if most Java IDEs will now indent the added line correctly, showing that it's not really part of the block.
* Keep methods short, don't hesitate to refactor by extracting other methods (replacing complex conditions in the control flow, or replacing large blocks).
* Use parentheses in composite boolean expressions, as nobody remembers the precedence between && and || and it's better to be explicit in the grouping:
    ```java
    (a && b) || c
    ```
    instead of
    ```java
    a && b || c 
    ```

* Use final fields as much as possible to promote immutability: dependencies can be injected through the constructor, which naturally forces the developer to think before having too many dependencies in a single class, and makes the class easier to use in a non-Spring context (unit tests, for example).
    ```java
    @Service
    @RequiredArgsConstructor
    public class AdminService {
        private final GarageRest garageRest;
        private final BusinessConfigRest businessConfigRest;
    }
    ```
* Class elements should be defined in a consistent order:
    * Constants
    * Static fields (class fields)
    * Instance fields
    * Constructors
    * Factory methods, if applicable
    * All other methods, static or not
    * Inner or nested classes
A lot of these rules are checked by Sonar anyway, but if we can avoid most roundtrips with Sonar, it makes everyone's life easier.

Google coding convention should also be followed for most of them : https://google.github.io/styleguide/javaguide.html

* Classes and interfaces
Don't create an interface when there is a single implementation (typically, the business services): the frameworks, whether it's Spring or Mockito, have been advanced enough for quite a while so that they don't require an interface to create a proxy (Spring, for transactional purposes, for example) or a mock.


### Kotlin Styleguide

Because we use Kotlin Linter on our project, you can refer to standard [Kotlin coding convention](https://kotlinlang.org/docs/reference/coding-conventions.html)


