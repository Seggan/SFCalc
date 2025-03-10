plugins {
    java
    id("com.gradleup.shadow") version "8.3.2"
}

repositories {
    mavenLocal()
    maven("https://jitpack.io/")
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    maven("https://repo.maven.apache.org/maven2/")
}

dependencies {
    compileOnly("org.spigotmc:spigot-api:1.16.5-R0.1-SNAPSHOT")
    compileOnly("com.github.Slimefun:Slimefun4:RC-36")

    implementation("org.bstats:bstats-bukkit:3.0.2")
    implementation("com.github.seggan:ErrorReporter-Java:1.1.0")

    compileOnly("com.google.code.findbugs:jsr305:3.0.2")
    compileOnly("org.projectlombok:lombok:1.18.20")
    annotationProcessor("org.projectlombok:lombok:1.18.20")
}

group = "io.github.seggan.sfcalc"
version = "UNOFFICIAL"

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

tasks.shadowJar {
    archiveClassifier.set("")

    relocate("io.github.seggan.errorreporter", "io.github.seggan.sfcalc.errorreporter")
    relocate("org.bstats", "io.github.seggan.sfcalc.bstats")
}
