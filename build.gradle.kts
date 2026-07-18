plugins {
    id("java-library")
    id("xyz.jpenilla.run-paper") version "3.0.2"
    id("java")
    id("com.gradleup.shadow") version "9.2.2" // or whatever's newest — check https://plugins.gradle.org/plugin/com.gradleup.shadow
}

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://repo.codemc.io/repository/maven-releases/")
    maven("https://jitpack.io") // EntityLib, via JitPack
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:26.2.build.+")

    // NOTE: implementation, not compileOnly — these need to be bundled into the jar
    implementation("com.github.retrooper:packetevents-spigot:2.13.0")
    implementation("com.github.Tofaa2.EntityLib:spigot:master-SNAPSHOT")
}

java {
    toolchain.languageVersion = JavaLanguageVersion.of(25)
}

tasks {
    runServer {
        // Configure the Minecraft version for our task.
        // This is the only required configuration besides applying the plugin.
        // Your plugin's jar (or shadowJar if present) will be used automatically.
        minecraftVersion("26.2")
        jvmArgs("-Xms2G", "-Xmx2G")
    }

    processResources {
        val props = mapOf("version" to version)
        filesMatching("plugin.yml") {
            expand(props)
        }
    }
}

tasks.shadowJar {
    archiveClassifier.set("") // makes the shaded jar the "main" build output

    relocate("com.github.retrooper.packetevents", "io.github.ahelton5.SimpleHealthBar.lib.packetevents")
    relocate("io.github.retrooper.packetevents", "io.github.ahelton5.SimpleHealthBar.lib.packeteventsimpl")
    relocate("me.tofaa.entitylib", "io.github.ahelton5.SimpleHealthBar.lib.entitylib")

    minimize() // strips unused classes from the bundled libs, smaller jar
}

tasks.build {
    dependsOn(tasks.shadowJar)
}