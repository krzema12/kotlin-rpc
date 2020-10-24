plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization")
}

group = "it.krzeminski.kotlinrpc"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

kotlin {
    jvm {
        compilations.all {
            kotlinOptions.jvmTarget = "11"
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
                implementation(project(":codegen"))
            }
        }
        val jsMain by getting {
            dependencies {
            }
        }
    }
}

val generateZooApiJsProxy = tasks.register<JavaExec>("generateZooApiJsClient") {
    group = "build"
    description = "Generate ZooApi JS proxy"
    classpath = sourceSets["main"].runtimeClasspath
    main = "it.krzeminski.zoo.api.generation.JsClientGenerationKt"
    args("it.krzeminski.zoo.api.ZooApi", "${parent?.buildDir}/js/generated")
}

val generateZooApiJvmKtorServer = tasks.register<JavaExec>("generateZooApiJvmKtorServer") {
    group = "build"
    description = "Generate ZooApi JVM Ktor server"
    classpath = sourceSets["main"].runtimeClasspath
    main = "it.krzeminski.zoo.api.generation.JvmKtorServerGenerationKt"
    args("it.krzeminski.zoo.api.ZooApi", "${parent?.buildDir}/jvm/generated")
}

tasks.getByName("jvmJar").dependsOn(generateZooApiJvmKtorServer)
tasks.getByName("jsJar").dependsOn(generateZooApiJsProxy)
