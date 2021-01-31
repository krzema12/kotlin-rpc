plugins {
    `kotlin-dsl`
    `maven-publish`
}

group = "it.krzeminski"
version = "0.1.0"

repositories {
    mavenLocal()
    mavenCentral()
    google()
    maven("https://plugins.gradle.org/m2/")
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:1.4.10")
}
