plugins {
    kotlin("jvm")
}

repositories {
    mavenCentral()
}

// Configure JVM target compatibility
java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

kotlin {
    jvmToolchain(21)
}

dependencies {
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

