import net.researchgate.release.ReleasePlugin
import se.bjurr.gitchangelog.plugin.gradle.GitChangelogTask

plugins {
  base
  `maven-publish`
  signing
  kotlin("jvm") version "1.9.24" apply false
  id("org.jlleitschuh.gradle.ktlint") version "12.1.1" apply false
  id("io.gitlab.arturbosch.detekt") version "1.23.7"
  id("net.researchgate.release") version "3.0.2"
  id("se.bjurr.gitchangelog.git-changelog-gradle-plugin") version "2.1.2"
  id("org.jetbrains.dokka") version "1.9.20"
}

allprojects {
  group = "com.ekino.oss.jcv"

  repositories {
    mavenCentral()
  }

  registerProperties(
    "commons-io.version" to "2.17.0",
    "jsonassert.version" to "1.5.3",
    "assertj.version" to "3.26.3",
    "hamcrest.version" to "2.2",
    "junit-jupiter.version" to "5.11.1",
    "assertk-jvm.version" to "0.28.1",
    "wiremock.version" to "2.27.2"
  )
}

tasks.create("printVersion") {
  doLast {
    val version: String by project
    println(version)
  }
}

tasks.create<GitChangelogTask>("gitChangelogTask") {
  file = File("CHANGELOG.md")
  templateContent = file("template_changelog.mustache").readText()
}

detekt {
  buildUponDefaultConfig = true
  config.setFrom("config/detekt.yml")
}

subprojects {

  apply<MavenPublishPlugin>()
  apply<ReleasePlugin>()
  apply<SigningPlugin>()

  publishing {
    publications {
      register<MavenPublication>("mavenJava") {
        pom {
          name = "JCV"
          description = "JSON Content Validator (JCV) allows you to compare JSON contents with embedded validation."
          url = "https://github.com/ekino/jcv"
          licenses {
            license {
              name = "MIT License (MIT)"
              url = "https://opensource.org/licenses/mit-license"
            }
          }
          developers {
            developer {
              name = "Léo Millon"
              email = "leo.millon@ekino.com"
              organization = "ekino"
              organizationUrl = "https://www.ekino.com/"
            }
          }
          scm {
            connection = "scm:git:git://github.com/ekino/jcv.git"
            developerConnection = "scm:git:ssh://github.com:ekino/jcv.git"
            url = "https://github.com/ekino/jcv"
          }
          organization {
            name = "ekino"
            url = "https://www.ekino.com/"
          }
        }
        repositories {
          maven {
            val ossrhUrl: String? by project
            val ossrhUsername: String? by project
            val ossrhPassword: String? by project

            url = uri(ossrhUrl ?: "")

            credentials {
              username = ossrhUsername
              password = ossrhPassword
            }
          }
        }
      }
    }
  }

  signing {
    setRequired { gradle.taskGraph.hasTask("publish") }
    sign(publishing.publications["mavenJava"])
  }
}
