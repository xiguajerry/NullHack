import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinVersion
import org.jetbrains.kotlin.gradle.utils.extendsFrom
import java.net.URI
import java.security.MessageDigest
import java.text.SimpleDateFormat
import java.util.*

plugins {
    java
    kotlin("jvm") version "2.1.0"
    id("com.gradleup.shadow") version "8.3.6" apply false
    id("org.spongepowered.mixin") version "0.7-SNAPSHOT" apply false
    id("fabric-loom") version "1.10-SNAPSHOT" apply false
    id("net.neoforged.moddev") version "2.0.78" apply false
    id("net.minecraftforge.gradle") version "[6.0.30,6.2)" apply false
}

group = "love.xiguajerry"
version = "0.1.0"

val archives_base_name: String by rootProject
val mod_version: String by rootProject
val maven_group: String by rootProject


allprojects {
    apply {
        plugin("java")
    }

    base {
        archivesName = archives_base_name
    }

    version = mod_version
    group = maven_group

    repositories {
        mavenLocal()
        mavenCentral()
        maven("https://maven.fabricmc.net/")
        maven("https://jitpack.io")
        maven("https://maven.luna5ama.dev/")
    }

    tasks.javadoc {
        enabled = false
    }

    tasks.compileJava {
        options.encoding = "UTF-8"
        options.release = 21
    }

    tasks.jar {
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    }

    java {
        withSourcesJar()
    }
}