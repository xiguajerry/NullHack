import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinVersion

plugins {
    kotlin("jvm")
    id("multiloader-loader")
    id("net.neoforged.moddev")
    id("com.gradleup.shadow")
}

group = "love.xiguajerry"
version = "0.1.0"

repositories {
    mavenLocal()
    mavenCentral()
}

val modLibrary by configurations.creating

dependencies {
    modLibrary(project(path = ":common", configuration = "library")) {
        exclude("org.apache.commons", "commons-lang3")
        exclude("org.slf4j", "slf4j-api")
        exclude("com.google.code.findbugs", "jsr305")
    }
}

sourceSets {
    main {
        resources {
            srcDir("src/generated/resources")
        }
        compileClasspath += modLibrary
        runtimeClasspath += modLibrary
    }
}

kotlin {
    jvmToolchain(21)
}

tasks.compileKotlin {
    compilerOptions {
        jvmTarget = JvmTarget.JVM_21
        apiVersion = KotlinVersion.KOTLIN_2_2
        languageVersion = KotlinVersion.KOTLIN_2_2
        optIn = listOf("kotlin.RequiresOptIn", "kotlin.contracts.ExperimentalContracts")
        freeCompilerArgs = listOf(
            "-Xjvm-default=all-compatibility",
            "-Xcontext-receivers"
        )
    }
}

neoForge {
    version = property("neoforge_version")!!.toString()
    // Automatically enable neoforge AccessTransformers if the file exists
    val at = project(":common").file("src/main/resources/META-INF/accesstransformer.cfg")
    if (at.exists()) {
        accessTransformers.from(at.absolutePath)
    }
    parchment {
        minecraftVersion = property("parchment_minecraft")!!.toString()
        mappingsVersion = property("parchment_version")!!.toString()
    }
    runs {
        configureEach {
            systemProperty("neoforge.enabledGameTestNamespaces", property("mod_id")!!.toString())
            ideName = "NeoForge ${name.capitalize()} (${project.path})" // Unify the run config names with fabric
            additionalRuntimeClasspathConfiguration.extendsFrom(modLibrary)
        }
        create("client") {
            client()
        }
    }
    mods {
        create("${property("mod_id")}") {
            sourceSet(sourceSets["main"])
        }
    }
}

tasks.build {
    finalizedBy(tasks.shadowJar)
}

tasks.shadowJar {
    configurations = listOf(modLibrary)
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

tasks.javadoc { enabled = false }
tasks.javadocJar { enabled = false }
tasks.sourcesJar { enabled = false }