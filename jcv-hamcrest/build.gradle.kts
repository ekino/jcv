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
      apiVersion = "1.3"
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
  api(project(":jcv-core"))
  implementation(kotlin("stdlib-jdk8"))
  implementation(group = "org.skyscreamer", name = "jsonassert", version = "${prop("jsonassert.version")}")
  implementation(group = "org.hamcrest", name = "hamcrest", version = "${prop("hamcrest.version")}")

  testImplementation(
    group = "org.junit.jupiter",
    name = "junit-jupiter",
    version = "${prop("junit-jupiter.version")}"
  )

  testImplementation(group = "org.skyscreamer", name = "jsonassert", version = "${prop("jsonassert.version")}")
  testImplementation(group = "org.hamcrest", name = "hamcrest", version = "${prop("hamcrest.version")}")
  testImplementation(group = "commons-io", name = "commons-io", version = "${prop("commons-io.version")}")
}
