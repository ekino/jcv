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
  implementation(kotlin("stdlib"))

  api(project(":jcv-core"))
  implementation(group = "org.skyscreamer", name = "jsonassert", version = "${prop("jsonassert.version")}")
  implementation(group = "org.assertj", name = "assertj-core", version = "${prop("assertj.version")}")

  testImplementation(
    group = "org.junit.jupiter",
    name = "junit-jupiter",
    version = "${prop("junit-jupiter.version")}",
  )

  testImplementation(group = "org.skyscreamer", name = "jsonassert", version = "${prop("jsonassert.version")}")
  testImplementation(group = "org.assertj", name = "assertj-core", version = "${prop("assertj.version")}")
  testImplementation(group = "commons-io", name = "commons-io", version = "${prop("commons-io.version")}")
}
