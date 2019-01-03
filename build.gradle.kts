import net.researchgate.release.ReleaseExtension
import net.researchgate.release.ReleasePlugin

plugins {
    base
    kotlin("jvm") version "1.3.10" apply false
    id("net.researchgate.release") version "2.6.0"
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
                        val releasesRepoUrl = uri("https://oss.sonatype.org/service/local/staging/deploy/maven2/")
                        val snapshotsRepoUrl = uri("https://oss.sonatype.org/content/repositories/snapshots/")
                        url = if (version.toString().endsWith("SNAPSHOT")) snapshotsRepoUrl else releasesRepoUrl

                        val ossrhUsername: String by project
                        val ossrhPassword: String by project
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
