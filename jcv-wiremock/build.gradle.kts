plugins {
  kotlin("jvm")
  `java-library`
  id("org.jlleitschuh.gradle.ktlint")
  id("io.gitlab.arturbosch.detekt")
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

kotlin {
  jvmToolchain(11)
}

val javadocJar by tasks.registering(Jar::class) {
  dependsOn("dokkaHtml")
  archiveClassifier.set("javadoc")
  from(layout.buildDirectory.dir("dokka"))
}

tasks {
  withType<Test> {
    useJUnitPlatform()
    jvmArgs("-Duser.language=en")
  }

  dokkaHtml {
    dokkaSourceSets {
      configureEach {
        reportUndocumented = false
        jdkVersion = 11
      }
    }
  }

  artifacts {
    archives(jar)
    archives(javadocJar)
  }
}

publishing {
  publications {
    named<MavenPublication>("mavenJava") {
      artifact(javadocJar.get())
      from(components["java"])
    }
  }
}

dependencies {
  api(project(":jcv-core"))
  implementation(kotlin("stdlib"))
  implementation(group = "org.skyscreamer", name = "jsonassert", version = "${prop("jsonassert.version")}")
  implementation(group = "org.wiremock", name = "wiremock", version = "${prop("wiremock.version")}")

  testImplementation(
    group = "org.junit.jupiter",
    name = "junit-jupiter",
    version = "${prop("junit-jupiter.version")}",
  )

  testImplementation(group = "org.skyscreamer", name = "jsonassert", version = "${prop("jsonassert.version")}")
  testImplementation(
    group = "com.willowtreeapps.assertk",
    name = "assertk-jvm",
    version = "${prop("assertk-jvm.version")}",
  ) {
    exclude(group = "org.jetbrains.kotlin", module = "kotlin-reflect")
  }
  testImplementation(group = "org.wiremock", name = "wiremock", version = "${prop("wiremock.version")}")
  testImplementation("io.github.rybalkinsd:kohttp-jackson:0.12.0")
}
