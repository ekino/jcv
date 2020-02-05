import org.jetbrains.dokka.gradle.DokkaTask
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")
    `java-library`
    signing
    jacoco
    id("org.jmailen.kotlinter") version "2.3.0"
    id("org.jetbrains.dokka") version "0.10.1"
}

configurations {
    implementation {
        resolutionStrategy.failOnVersionConflict()
    }
}

val sourcesJar by tasks.registering(Jar::class) {
    archiveClassifier.set("sources")
    from(sourceSets.main.get().allJava)
}

val javadocJar by tasks.registering(Jar::class) {
    dependsOn("dokka")
    archiveClassifier.set("javadoc")
    from(buildDir.resolve("dokka"))
}

tasks {
    withType<KotlinCompile> {
        kotlinOptions {
            jvmTarget = JavaVersion.VERSION_1_8.toString()
        }
    }

    withType<Test> {
        useJUnitPlatform()
        jvmArgs("-Duser.language=en")
    }

    withType<DokkaTask> {
        configuration {
            reportUndocumented = false
        }
    }

    val version: String by project
    if (version.endsWith("-SNAPSHOT")) {
        withType<GenerateModuleMetadata>().configureEach {
            enabled = false
        }
    }

    artifacts {
        archives(jar)
        archives(sourcesJar)
        archives(javadocJar)
    }
}

val publicationName = "mavenJava"

publishing {
    publications {
        named<MavenPublication>(publicationName) {
            artifact(sourcesJar.get())
            artifact(javadocJar.get())

            from(components["java"])
        }
    }
}

signing {
    sign(publishing.publications[publicationName])
}

dependencies {

    implementation(kotlin("stdlib-jdk8"))

    api(project(":jcv-core"))
    implementation(group = "org.skyscreamer", name = "jsonassert", version = "${prop("jsonassert.version")}")
    implementation(group = "org.assertj", name = "assertj-core", version = "${prop("assertj.version")}")

    testImplementation(group = "org.junit.jupiter", name = "junit-jupiter", version = "${prop("junit-jupiter.version")}")

    testImplementation(group = "org.skyscreamer", name = "jsonassert", version = "${prop("jsonassert.version")}")
    testImplementation(group = "org.assertj", name = "assertj-core", version = "${prop("assertj.version")}")
    testImplementation(group = "commons-io", name = "commons-io", version = "${prop("commons-io.version")}")
}
