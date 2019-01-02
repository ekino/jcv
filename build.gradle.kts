plugins {
    base
    kotlin("jvm") version "1.3.10" apply false
}

allprojects {
    group = "com.ekino.oss.jcv"
    version = "1.0.0-SNAPSHOT"

    repositories {
        mavenCentral()
        jcenter()
    }

    registerProperties(
        "kotlin.version" to "1.3.10",
        "lombok.version" to "1.18.4",
        "jackson.version" to "2.9.6",
        "commons-io.version" to "2.6",
        "jsonassert.version" to "1.5.0",
        "assertj.version" to "3.9.1",
        "hamcrest-junit.version" to "2.0.0.0",
        "junit-jupiter.version" to "5.2.0",
        "assertk-jvm.version" to "0.12"
    )
}
