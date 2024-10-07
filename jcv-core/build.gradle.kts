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
  implementation(kotlin("reflect"))
  implementation(libs.jsonassert)

  testImplementation(libs.junit.jupiter)
  testImplementation(libs.jsonassert)
  testImplementation(libs.assertk) {
    exclude(group = "org.jetbrains.kotlin", module = "kotlin-reflect")
  }
}
