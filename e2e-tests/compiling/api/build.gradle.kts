plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization")
}


buildscript {
    repositories {
        mavenLocal()
    }
    dependencies {
        classpath("kotlinRpc:kotlinRpc.gradle.plugin:0.1.0")
    }
}

repositories {
    mavenCentral()
    mavenLocal()
}

kotlin {
    jvm {
        compilations.all {
            kotlinOptions.jvmTarget = "1.8"
        }
        withJava()
    }
    js {
        browser {
            binaries.executable()
            webpackTask {
                cssSupport.enabled = true
            }
            runTask {
                cssSupport.enabled = true
            }
        }
    }
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.0.0")
            }
        }
        val jvmMain by getting {
            dependencies {
                implementation("io.ktor:ktor-server-netty:1.4.0")
                implementation("it.krzeminski.kotlinrpc:kotlin-rpc:0.1.0-SNAPSHOT")
            }
            kotlin.srcDirs(kotlin.srcDirs, "$buildDir/jvm/generated/")
        }
        val jsMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core-js:1.3.9")
            }
            kotlin.srcDirs(kotlin.srcDirs, "$buildDir/js/generated/")
        }
    }
}

apply<it.krzeminski.kotlinrpc.KotlinRpcGradlePlugin>()
