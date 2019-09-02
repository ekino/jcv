import net.researchgate.release.ReleasePlugin
import se.bjurr.gitchangelog.plugin.gradle.GitChangelogTask

plugins {
    base
    kotlin("jvm") version "1.3.50" apply false
    id("net.researchgate.release") version "2.8.1"
    id("se.bjurr.gitchangelog.git-changelog-gradle-plugin") version "1.64"
}

allprojects {
    group = "com.ekino.oss.jcv"

    repositories {
        mavenCentral()
        jcenter()
    }

    registerProperties(
        "kotlin.version" to "1.3.50",
        "lombok.version" to "1.18.6",
        "jackson.version" to "2.9.8",
        "commons-io.version" to "2.6",
        "jsonassert.version" to "1.5.0",
        "assertj.version" to "3.12.2",
        "hamcrest.version" to "2.1",
        "junit-jupiter.version" to "5.5.1",
        "assertk-jvm.version" to "0.19"
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

subprojects {

    apply<MavenPublishPlugin>()
    apply<ReleasePlugin>()

    configure<PublishingExtension> {
        publications {
            create<MavenPublication>("mavenJava") {
                pom {
                    name.set("JCV")
                    description.set("JSON Content Validator (JCV) allows you to compare JSON contents with embedded validation.")
                    url.set("https://github.com/ekino/jcv")
                    licenses {
                        license {
                            name.set("MIT License (MIT)")
                            url.set("https://opensource.org/licenses/mit-license")
                        }
                    }
                    developers {
                        developer {
                            name.set("Léo Millon")
                            email.set("leo.millon@ekino.com")
                            organization.set("ekino")
                            organizationUrl.set("https://www.ekino.com/")
                        }
                    }
                    scm {
                        connection.set("scm:git:git://github.com/ekino/jcv.git")
                        developerConnection.set("scm:git:ssh://github.com:ekino/jcv.git")
                        url.set("https://github.com/ekino/jcv")
                    }
                    organization {
                        name.set("ekino")
                        url.set("https://www.ekino.com/")
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
}
