plugins {
    kotlin("jvm")
    application
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

application {
    mainClass.set("com.singkhmer.transliterator.MainKt")
}

dependencies {
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

