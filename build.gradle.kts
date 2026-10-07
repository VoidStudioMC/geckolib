import org.jetbrains.gradle.ext.Gradle
import org.jetbrains.gradle.ext.RunConfigurationContainer
import org.jetbrains.gradle.ext.runConfigurations
import org.jetbrains.gradle.ext.settings

plugins {
    `java-library`
    id("com.gradleup.shadow") version "9.4.1"
    id("org.jetbrains.gradle.plugin.idea-ext") version "1.1.7"
    id("com.gtnewhorizons.retrofuturagradle") version "1.4.9"
}

version = "3.2.1"
group = "software.bernie"

base {
    archivesName.set("SauriaLib3")
}

val shade = configurations.create("shade")
configurations.implementation {
    extendsFrom(shade)
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
    withSourcesJar()
}

minecraft {
    mcVersion.set("1.12.2")
}

tasks.deobfuscateMergedJarToSrg.configure {
    accessTransformerFiles.from("src/main/resources/META-INF/geckolib3_at.cfg")
}

tasks.compileJava.configure {
    sourceCompatibility = "17"
    options.release = 8
    options.encoding = "UTF-8"

    javaCompiler = javaToolchains.compilerFor {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

repositories {
    mavenLocal()
    mavenCentral()

    maven("https://curse.cleanroommc.com")
    maven("https://dl.cloudsmith.io/public/geckolib3/geckolib/maven/")
    maven("https://api.modrinth.com/maven")
}

dependencies {
    shade("com.eliotlash.mclib:mclib:20")
    shade("com.eliotlash.molang:molang:19")

    shade("com.fasterxml.jackson.core:jackson-databind:2.20.1")
    shade("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.20.1")

    implementation(rfg.deobf("curse.maven:livingchest-580230:3754820"))
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
}

tasks {
    shadowJar {
        configurations = listOf(shade)

        mergeServiceFiles()
        exclude("META-INF/services/**")
        relocate("com.eliotlash", "software.bernie.shadowed.eliotlash")
        relocate("com.fasterxml", "software.bernie.shadowed.fasterxml")

        archiveClassifier = "shadow"
    }

    reobfJar {
        inputJar.set(shadowJar.flatMap { it.archiveFile }.get())
        dependsOn(shadowJar)
        archiveClassifier = "shadow-reobf" // Я не знаю почему эта залупа не может выходящий архив shadow реобфнуть, поэтому так
    }

    build {
        dependsOn(reobfJar)
    }
}

idea {
    module {
        isDownloadJavadoc = true
        isDownloadSources = true
        inheritOutputDirs = true
    }
    project {
        settings {
            runConfigurations {
                add(Gradle("1. Run Client").apply {
                    taskNames = listOf("runClient")
                })
            }
        }
    }
}