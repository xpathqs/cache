import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.gradle.api.tasks.testing.logging.TestLogEvent

group = "org.xpathqs"
version = "0.1"

plugins {
    kotlin("jvm") version "1.5.0"
    id("org.jetbrains.dokka") version "1.4.32"
    `java-library`
    jacoco
   // maven
    `maven-publish`
    signing
    id("io.codearte.nexus-staging") version "0.30.0"
    id("io.gitlab.arturbosch.detekt").version("1.18.0-RC2")
    id("info.solidsoft.pitest").version("1.7.0")
}

java {
    withJavadocJar()
    withSourcesJar()
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

jacoco {
    toolVersion = "0.8.7"
}

repositories {
    mavenCentral()
    mavenLocal()
}

detekt {
    buildUponDefaultConfig = true // preconfigure defaults
    allRules = true // activate all available (even unstable) rules.
    config = files("$projectDir/detekt.yml") // point to your custom config defining rules to run, overwriting default behavior
    reports {
        html.enabled = true // observe findings in your browser with structure and code snippets
        txt.enabled = true // similar to the console output, contains issue signature to manually edit baseline files
    }
}

dependencies {
    implementation("io.codearte.gradle.nexus:gradle-nexus-staging-plugin:0.30.0")
    implementation("org.jetbrains.kotlin:kotlin-reflect:1.5.0")
    implementation("net.oneandone.reflections8:reflections8:0.11.7")

    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.13.0")

    implementation("org.xpathqs:gwt:0.1.1")

    testImplementation(kotlin("test-junit5"))
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.6.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.6.0")

    testImplementation("com.willowtreeapps.assertk:assertk-jvm:0.23.1")
}


publishing {
    publications {
        beforeEvaluate {
            signing.sign(this@publications)
        }
        create<MavenPublication>("mavenJava") {
            pom {
                name.set("XpathQS Cache")
                description.set("A library for working with cache")
                url.set("https://xpathqs.org/")
                licenses {
                    license {
                        name.set("MIT License")
                        url.set("http://www.opensource.org/licenses/mit-license.php")
                    }
                }
                developers {
                    developer {
                        id.set("nachg")
                        name.set("Nikita A. Chegodaev")
                        email.set("nikchgd@gmail.com")
                    }
                }
                scm {
                    connection.set("scm:git:git://github.com/xpathqs/cache.git")
                    developerConnection.set("scm:git:ssh://github.com/xpathqs/cache.git")
                    url.set("https://xpathqs.org/")
                }
            }
            groupId = "org.xpathqs"
            artifactId = "cache"

            from(components["java"])
        }
    }
    repositories {
        maven {
            val releasesRepoUrl = uri("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/")
            val snapshotsRepoUrl = uri("https://s01.oss.sonatype.org/content/repositories/snapshots/")
            url = if (version.toString().endsWith("SNAPSHOT")) snapshotsRepoUrl else releasesRepoUrl
            credentials {
                username = project.property("ossrhUsername").toString()
                password = project.property("ossrhPassword").toString()
            }
        }
    }
}

tasks.jar {
    manifest {
        attributes(
            mapOf(
                "Implementation-Title" to project.name,
                "Implementation-Version" to project.version
            )
        )
    }
}

signing {
    sign(publishing.publications["mavenJava"])
}

nexusStaging {
    serverUrl = "https://s01.oss.sonatype.org/service/local/"
}

tasks.test {
    useJUnitPlatform()
    testLogging {
        showStandardStreams = true
        events = setOf(
            org.gradle.api.tasks.testing.logging.TestLogEvent.FAILED,
            org.gradle.api.tasks.testing.logging.TestLogEvent.STANDARD_OUT,
            org.gradle.api.tasks.testing.logging.TestLogEvent.STANDARD_ERROR
        )
    }
}