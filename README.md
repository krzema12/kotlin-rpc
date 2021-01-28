# kotlin-rpc

**Work in progress!**

It's a Kotlin-centric approach to Remote Procedure Calling, built on top of reflectionless [kotlinx.serialization](https://github.com/Kotlin/kotlinx.serialization), [coroutines](https://github.com/Kotlin/kotlinx.coroutines) and [ktor](https://github.com/ktorio/ktor).
Safe and elegant.

It works like this. First, the API is described with pure Kotlin, as an interface. Then, kotlin-rpc's build-time logic
kicks in and generates clients and server stubs. It works over HTTP (e. g. calling a JVM server from a JS client), but
other medium is possible, like Web workers (communication between the main UI thread and the worker thread).

## Installation

In `build.gradle.kts` with your API, add my private repo:

```
repositories {
    maven {
        url = uri("https://dl.bintray.com/krzema1212/it.krzeminski")
    }
}
```

add kotlin-rpc to dependencies, together with adding custom source sets in `build` directory:

```
...
val jvmMain by getting {
    dependencies {
        ...
        implementation("it.krzeminski.kotlinrpc:kotlin-rpc:0.1.2")
    }
    kotlin.srcDirs(kotlin.srcDirs, "$buildDir/jvm/generated/")
}
val jsMain by getting {
    dependencies {
        ...
    }
    kotlin.srcDirs(kotlin.srcDirs, "$buildDir/js/generated/")
}
...
```

and define tasks to execute the entry points:

```
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
```

This is all pretty complicated, and is going to be simplified in scope of #13.
