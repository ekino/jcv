import org.jetbrains.dokka.gradle.DokkaTask
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")
    `java-library`
    signing
    jacoco
    id("org.jmailen.kotlinter") version "2.1.1"
    id("org.jetbrains.dokka") version "0.9.18"
}

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_1_8
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

val sourcesJar by tasks.creating(Jar::class) {
    archiveClassifier.set("sources")
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
    archiveClassifier.set("javadoc")
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

    api(project(":jcv-core"))
    implementation(group = "org.skyscreamer", name = "jsonassert", version = "${prop("jsonassert.version")}")
    implementation(group = "org.hamcrest", name = "hamcrest", version = "${prop("hamcrest.version")}")

    testImplementation(kotlin("stdlib-jdk8", version = "${prop("kotlin.version")}"))
    testImplementation(group = "org.junit.jupiter", name = "junit-jupiter", version = "${prop("junit-jupiter.version")}")

    testImplementation(group = "org.skyscreamer", name = "jsonassert", version = "${prop("jsonassert.version")}")
    testImplementation(group = "org.hamcrest", name = "hamcrest", version = "${prop("hamcrest.version")}")
    testImplementation(group = "commons-io", name = "commons-io", version = "${prop("commons-io.version")}")
}
