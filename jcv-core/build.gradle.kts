import org.jetbrains.dokka.gradle.DokkaTask
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")
    `java-library`
    signing
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

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
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
        named<MavenPublication>("mavenJava") {
            from(components["java"])
            artifact(sourcesJar)
            artifact(dokkaJar)
        }
    }
}

signing {
    sign(publishing.publications["mavenJava"])
}

dependencies {
    compileOnly(group = "org.projectlombok", name = "lombok", version = "${prop("lombok.version")}")
    annotationProcessor(group = "org.projectlombok", name = "lombok", version = "${prop("lombok.version")}")

    implementation(group = "org.skyscreamer", name = "jsonassert", version = "${prop("jsonassert.version")}")

    testImplementation(kotlin("stdlib-jdk8", version = "${prop("kotlin.version")}"))
    testImplementation(group = "org.junit.jupiter", name = "junit-jupiter", version = "${prop("junit-jupiter.version")}")

    testImplementation(group = "org.skyscreamer", name = "jsonassert", version = "${prop("jsonassert.version")}")
    testImplementation(group = "com.willowtreeapps.assertk", name = "assertk-jvm", version = "${prop("assertk-jvm.version")}") {
        exclude(group = "org.jetbrains.kotlin", module = "kotlin-reflect")
    }
    testImplementation(group = "commons-io", name = "commons-io", version = "${prop("commons-io.version")}")
}
