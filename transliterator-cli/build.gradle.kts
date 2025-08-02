plugins {
    kotlin("jvm")
    application
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":transliterator-core"))
}

application {
    mainClass.set("com.singkhmer.cli.MainKt")
}
