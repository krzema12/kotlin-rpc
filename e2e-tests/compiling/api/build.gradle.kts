plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization")
    id("kotlinRpc")
}

//buildscript {
//    repositories {
//        mavenLocal()
//    }
//}

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

val generateJsProxy = tasks.register<JavaExec>("generateJsClient") {
    group = "build"
    description = "Generate JS proxy"
    classpath = sourceSets["main"].runtimeClasspath
    main = "it.krzeminski.kotlinrpc.api.generation.JsClientGenerationKt"
    args("it.krzeminski.zoo.api.ZooApi", "$buildDir/js/generated")
}

val generateJvmKtorServer = tasks.register<JavaExec>("generateJvmKtorServer") {
    group = "build"
    description = "Generate JVM Ktor server"
    classpath = sourceSets["main"].runtimeClasspath
    main = "it.krzeminski.kotlinrpc.api.generation.JvmKtorServerGenerationKt"
    args("it.krzeminski.zoo.api.ZooApi", "$buildDir/jvm/generated")
}

tasks.getByName("jvmJar").dependsOn(generateJvmKtorServer)
tasks.getByName("jsJar").dependsOn(generateJsProxy)
