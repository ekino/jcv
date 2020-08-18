import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")
    `java-library`
    signing
    id("com.ekino.oss.plugin.kotlin-quality")
    id("org.jetbrains.dokka")
}

configurations {
    implementation {
        resolutionStrategy.failOnVersionConflict()
    }
}

java {
    withSourcesJar()
}

val javadocJar by tasks.registering(Jar::class) {
    dependsOn("dokkaHtml")
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

    dokkaHtml {
        dokkaSourceSets {
            configureEach {
                reportUndocumented = false
                jdkVersion = 8
            }
        }
    }

    artifacts {
        archives(jar)
        archives(javadocJar)
    }
}

val publicationName = "mavenJava"

publishing {
    publications {
        named<MavenPublication>(publicationName) {
            artifact(javadocJar.get())
            from(components["java"])
        }
    }
}

signing {
    sign(publishing.publications[publicationName])
}

dependencies {
    implementation(kotlin("stdlib-jdk8", version = "${prop("kotlin.version")}"))
    implementation(kotlin("reflect", version = "${prop("kotlin.version")}"))
    implementation(group = "org.skyscreamer", name = "jsonassert", version = "${prop("jsonassert.version")}")

    testImplementation(
        group = "org.junit.jupiter",
        name = "junit-jupiter",
        version = "${prop("junit-jupiter.version")}"
    )

    testImplementation(group = "org.skyscreamer", name = "jsonassert", version = "${prop("jsonassert.version")}")
    testImplementation(
        group = "com.willowtreeapps.assertk",
        name = "assertk-jvm",
        version = "${prop("assertk-jvm.version")}"
    ) {
        exclude(group = "org.jetbrains.kotlin", module = "kotlin-reflect")
    }
}
