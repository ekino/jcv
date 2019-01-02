import org.jetbrains.dokka.gradle.DokkaTask
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")
    `java-library`
    `maven-publish`
    jacoco
    id("org.jmailen.kotlinter") version "1.20.1"
    id("org.jetbrains.dokka") version "0.9.17"
}

configurations {
    implementation {
        resolutionStrategy.failOnVersionConflict()
    }
}

sourceSets {
    main {
        java.srcDir("src/main/java")
    }
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

tasks {
    test {
        testLogging.showExceptions = true
    }
}

val sourcesJar by tasks.creating(Jar::class) {
    classifier = "sources"
    from(sourceSets["main"].allSource)
}

val dokka by tasks.getting(DokkaTask::class) {
    outputFormat = "html"
    outputDirectory = "$buildDir/javadoc"
    jdkVersion = 8
}

val dokkaJar by tasks.creating(Jar::class) {
    group = JavaBasePlugin.DOCUMENTATION_GROUP
    description = "Assembles Kotlin docs with Dokka"
    classifier = "javadoc"
    from(dokka)
}

publishing {
    publications {
        create("default", MavenPublication::class.java) {
            from(components["java"])
            artifact(sourcesJar)
            artifact(dokkaJar)
        }
    }
}

dependencies {

    compileOnly(group = "org.projectlombok", name = "lombok", version = "${prop("lombok.version")}")
    annotationProcessor(group = "org.projectlombok", name = "lombok", version = "${prop("lombok.version")}")

    api(project(":jcv-core"))
    implementation(group = "org.skyscreamer", name = "jsonassert", version = "${prop("jsonassert.version")}")
    implementation(group = "org.assertj", name = "assertj-core", version = "${prop("assertj.version")}")

    testImplementation(kotlin("stdlib-jdk8", version = "${prop("kotlin.version")}"))
    testImplementation(group = "org.junit.jupiter", name = "junit-jupiter-api", version = "${prop("junit-jupiter.version")}")
    testImplementation(group = "org.junit.jupiter", name = "junit-jupiter-params", version = "${prop("junit-jupiter.version")}")
    testRuntimeOnly(group = "org.junit.jupiter", name = "junit-jupiter-engine", version = "${prop("junit-jupiter.version")}")

    testImplementation(group = "org.skyscreamer", name = "jsonassert", version = "${prop("jsonassert.version")}")
    testImplementation(group = "org.assertj", name = "assertj-core", version = "${prop("assertj.version")}")
    testImplementation(group = "commons-io", name = "commons-io", version = "${prop("commons-io.version")}")
}
