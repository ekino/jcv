import net.researchgate.release.ReleasePlugin
import se.bjurr.gitchangelog.plugin.gradle.GitChangelogTask

plugins {
  base
  `maven-publish`
  signing
  alias(libs.plugins.kotlin.jvm) apply false
  alias(libs.plugins.ktlint) apply false
  alias(libs.plugins.detekt)
  alias(libs.plugins.release)
  alias(libs.plugins.changelog)
  alias(libs.plugins.dokka)
}

allprojects {
  group = "com.ekino.oss.jcv"

  repositories {
    mavenCentral()
  }
}

tasks {
  register("printVersion") {
    doLast {
      println(project.version.toString())
    }
  }

  register<GitChangelogTask>("gitChangelogTask") {
    file = File("CHANGELOG.md")
    templateContent = file("template_changelog.mustache").readText()
  }
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
