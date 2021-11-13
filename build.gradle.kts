import java.net.URI
import java.util.Calendar
import java.util.TimeZone

plugins {
    kotlin("jvm") version "1.5.31"
    id("com.github.johnrengelman.shadow") version "7.1.0"
    id("org.jetbrains.dokka") version "1.5.30"
    `maven-publish`
    signing
}

val powerNukkitVersion: String by project
val ktorVersion: String by project
val kotlinPluginVersion: String by project
val ossrhUsername: String by project
val ossrhPassword: String by project

group = "org.powernukkit.plugins"
val cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"))!!
version = "$ktorVersion+0.1.0+${cal[Calendar.YEAR]}.${cal[Calendar.MONTH]+1}.${cal[Calendar.DAY_OF_MONTH]}-SNAPSHOT"

repositories {
    mavenCentral()
}

val included by configurations.creating

dependencies {
    implementation("org.powernukkit:powernukkit:$powerNukkitVersion")
    api("org.powernukkit.plugins:kotlin-plugin-lib:$kotlinPluginVersion")
    includedApi(ktor("ktor-server-core"))
    includedApi(ktor("ktor-server-cio"))
    includedApi(ktor("ktor-serialization"))
    includedApi(ktor("ktor-auth"))
    includedApi(ktor("ktor-metrics-micrometer"))
    includedApi("io.micrometer:micrometer-registry-prometheus:1.7.1")
}

kotlin {
    explicitApi()
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
    //withJavadocJar()
    withSourcesJar()
}

tasks {
    shadowJar {
        configurations = listOf(included)
    }

    build {
        finalizedBy(shadowJar)
    }

    withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions {
            jvmTarget = "1.8"
            freeCompilerArgs = freeCompilerArgs + "-Xopt-in=kotlin.RequiresOptIn,kotlin.contracts.ExperimentalContracts"
        }
    }

    processResources {
        eachFile {
            filter<org.apache.tools.ant.filters.ReplaceTokens>(mapOf(
                "tokens" to mapOf(
                    "version" to project.version
                )
            ))
        }
    }
}

val dokkaHtmlJar: TaskProvider<Jar> by tasks.registering(Jar::class) {
    dependsOn(tasks.dokkaHtml)
    archiveClassifier.set("javadoc")
    from(tasks.dokkaHtml.get().outputDirectory)
}

publishing {
    repositories {
        maven {
            val local = false
            val releasesRepoUrl: URI
            val snapshotsRepoUrl: URI
            if (local) {
                releasesRepoUrl = uri(layout.buildDirectory.dir("repos/releases"))
                snapshotsRepoUrl = uri(layout.buildDirectory.dir("repos/snapshots"))
            } else {
                releasesRepoUrl = uri("https://oss.sonatype.org/service/local/staging/deploy/maven2/")
                snapshotsRepoUrl = uri("https://oss.sonatype.org/content/repositories/snapshots/")
                credentials {
                    username = ossrhUsername
                    password = ossrhPassword
                }
            }

            url = if (version.toString().endsWith("SNAPSHOT")) snapshotsRepoUrl else releasesRepoUrl
        }
    }

    publications {
        create<MavenPublication>("mavenJava") {
            artifactId = "ktor-plugin-lib"
            from(components["java"])
            artifact(dokkaHtmlJar)
            versionMapping {
                usage("java-api") {
                    fromResolutionOf("runtimeClasspath")
                }
                usage("java-runtime") {
                    fromResolutionResult()
                }
            }
            pom {
                name.set("PowerNukkit Ktor Plugin Library")
                description.set("Provides Ktor Server libs for building awesome Kotlin plugins which needs to provide builtin HTTP servers.")
                url.set("https://github.com/PowerNukkit/KtorPlugin")
                inceptionYear.set("2021")
                packaging = "jar"
                issueManagement {
                    url.set("https://github.com/PowerNukkit/KtorPlugin/issues")
                    system.set("GitHub")
                }
                organization {
                    name.set("PowerNukkit")
                    url.set("https://powernukkit.org")
                }
                licenses {
                    license {
                        name.set("The Apache License, Version 2.0")
                        url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                    }
                }
                developers {
                    developer {
                        id.set("joserobjr")
                        name.set("José Roberto de Araújo Júnior")
                        email.set("joserobjr@powernukkit.org")
                    }
                }
                scm {
                    connection.set("scm:git:git://github.com/PowerNukkit/KtorPlugin.git")
                    developerConnection.set("scm:git:ssh://github.com/PowerNukkit/KtorPlugin.git")
                    url.set("https://github.com/PowerNukkit/KtorPlugin")
                }
            }
        }
    }
}

signing {
    sign(publishing.publications["mavenJava"])
}

tasks.javadoc {
    if (JavaVersion.current().isJava9Compatible) {
        (options as StandardJavadocDocletOptions).addBooleanOption("html5", true)
    }
}

fun DependencyHandlerScope.includedApi(
    dependencyNotation: Any?,
) {
    requireNotNull(dependencyNotation)
    included(dependencyNotation)
    api(dependencyNotation)
}

fun DependencyHandlerScope.includedApi(
    dependencyNotation: String?,
) {
    requireNotNull(dependencyNotation)
    included(dependencyNotation)
    api(dependencyNotation)
}

fun DependencyHandlerScope.includedApi(
    dependencyNotation: String?,
    dependencyConfiguration: ExternalModuleDependency.()->Unit
) {
    requireNotNull(dependencyNotation)
    included(dependencyNotation, dependencyConfiguration)
    api(dependencyNotation, dependencyConfiguration)
}

fun DependencyHandlerScope.includedImplementation(
    dependencyNotation: String?,
    dependencyConfiguration: ExternalModuleDependency.()->Unit
) {
    requireNotNull(dependencyNotation)
    included(dependencyNotation, dependencyConfiguration)
    implementation(dependencyNotation, dependencyConfiguration)
}

fun kotlinx(name: String, version: String) = "org.jetbrains.kotlinx:kotlinx-$name:$version"
fun ktor(name: String, version: String = ktorVersion) = "io.ktor:ktor-$name:$version"


///////////////////////////////////////////////////////////////////
// Some fancy functions to allow you to debug your plugin easily //
// Just run ./gradlew run -q to run PowerNukkit with your plugin //
// Or execute the "debug" task in debug mode with your IDE       //
///////////////////////////////////////////////////////////////////

tasks {
    register<JavaExec>("debug") {
        dependsOn("createDebugJar")
        group = "Execution"
        description = "Run PowerNukkit with your plugin in debug mode (without Watchdog Thread)"
        workingDir = file("run")
        systemProperties = mapOf("file.encoding" to "UTF-8", "disableWatchdog" to true)
        mainClass.set("cn.nukkit.Nukkit")
        standardInput = System.`in`
        classpath = sourceSets.main.get().runtimeClasspath
    }

    register<JavaExec>("run") {
        dependsOn("createDebugJar")
        group = "Execution"
        description = "Run PowerNukkit with your plugin"
        mainClass.set("cn.nukkit.Nukkit")
        workingDir = file("run")
        systemProperties = mapOf("file.encoding" to "UTF-8")
        standardInput = System.`in`
        classpath = sourceSets.main.get().runtimeClasspath
    }

    register<Jar>("createDebugJar") {
        dependsOn(classes)
        group = "Execution"
        description = "Creates a fake jar to make PowerNukkit load your plugin directly from the compiled classes"

        from(sourceSets.main.get().output.resourcesDir!!) {
            include("plugin.yml")
            include("nukkit.yml")
        }

        destinationDirectory.set(file("run/plugins"))
        archiveBaseName.set("__plugin_loader")
        archiveExtension.set("jar")
        archiveAppendix.set("")
        archiveClassifier.set("")
        archiveVersion.set("")
    }
}
