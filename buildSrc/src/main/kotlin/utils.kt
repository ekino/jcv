/*
 * Copyright (c) 2019 ekino (https://www.ekino.com/)
 */
import org.gradle.api.Project
import org.gradle.kotlin.dsl.PluginDependenciesSpecScope
import org.gradle.kotlin.dsl.extra

fun Project.registerProperties(vararg properties: Pair<String, Any>) = mapOf(*properties)
    .forEach {
        project.extra.set(it.key, it.value)
    }

fun Project.prop(propertyName: String) = project.extra[propertyName]
