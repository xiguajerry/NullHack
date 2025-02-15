import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinVersion

plugins {
    kotlin("jvm")
    id("multiloader-loader")
    id("net.minecraftforge.gradle")
    id("com.gradleup.shadow")
    id("org.spongepowered.mixin")
}

group = "love.xiguajerry"
version = "0.1.0"

base {
    archivesName = "${property("mod_name")}-forge-${property("minecraft_version")}"
}

mixin {
    config("${property("mod_id")}.mixins.json")
    config("${property("mod_id")}.forge.mixins.json")
}

tasks.jar {
    manifest {
        attributes["MixinConfigs"] = "${project.property("mod_id")}.mixins.json,${project.property("mod_id")}.forge.mixins.json"
    }
}

repositories {
    mavenCentral()
}

minecraft {
    mappings(mapOf("channel" to "official", "version" to property("minecraft_version")!!.toString()))

    copyIdeResources = true // Calls processResources when in dev

    reobf = false // Forge 1.20.6+ uses official mappings at runtime, so we shouldn't reobf from official to SRG

    // Automatically enable forge AccessTransformers if the file exists
    // This location is hardcoded in Forge and can not be changed.
    // https://github.com/MinecraftForge/MinecraftForge/blob/be1698bb1554f9c8fa2f58e32b9ab70bc4385e60/fmlloader/src/main/java/net/minecraftforge/fml/loading/moddiscovery/ModFile.java#L123
    // Forge still uses SRG names during compile time, so we cannot use the common AT's
    val at = project(":common").file("src/main/resources/META-INF/accesstransformer.cfg")
    if (at.exists()) {
        accessTransformer(at)
    }

    runs {
        create("client") {
            workingDirectory(file("runs/client"))
            ideaModule("${rootProject.name}.${project.name}.main")
            taskName("Forge Client")
            mods {
                create("modClientRun") {
                    source(sourceSets["main"])
                }
            }
        }
    }
}

val modLibrary by configurations.creating

dependencies {
    "minecraft"("net.minecraftforge:forge:${property("minecraft_version")}-${property("forge_version")}")
    annotationProcessor("org.spongepowered:mixin:0.8.5-SNAPSHOT:processor")

    // Forge's hack fix
    implementation("net.sf.jopt-simple:jopt-simple:5.0.4") { version { strictly("5.0.4") } }

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

    forEach {
        val dir = layout.buildDirectory.dir("sourcesSets/$it.name")
        it.output.setResourcesDir(dir)
        it.java.destinationDirectory = dir
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
