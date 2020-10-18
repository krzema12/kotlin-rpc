import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpack

plugins {
    kotlin("multiplatform") version "1.4.10"
    kotlin("plugin.serialization") version "1.4.10"
    id("org.jlleitschuh.gradle.ktlint") version "9.4.1"
    application
}
group = "it.krzeminski"
version = "1.0-SNAPSHOT"

buildscript {
    repositories {
        jcenter()
    }

    val ktlint by configurations.creating
    dependencies {
        ktlint("com.pinterest:ktlint:0.39.0")
    }
}

repositories {
    mavenCentral()
    jcenter()
    maven {
        url = uri("https://dl.bintray.com/kotlin/ktor")
    }
    maven {
        url = uri("https://dl.bintray.com/kotlin/kotlinx")
    }
    maven {
        url = uri("https://dl.bintray.com/kotlin/kotlin-js-wrappers")
    }
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
            testTask {
                useKarma {
                    useChromeHeadless()
                    webpackConfig.cssSupport.enabled = true
                }
            }
        }
    }
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.0.0")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
            }
        }
        val jvmMain by getting {
            dependencies {
                implementation("io.ktor:ktor-server-netty:1.4.0")
                implementation("io.ktor:ktor-html-builder:1.4.0")
                implementation("org.jetbrains.kotlinx:kotlinx-html-jvm:0.7.2")
                implementation("org.jetbrains.kotlin:kotlin-reflect:1.4.10")
            }
            kotlin.srcDirs(kotlin.srcDirs, "$buildDir/jvm/generated/")
        }
        val jvmTest by getting {
            dependencies {
                implementation(kotlin("test-junit5"))
            }
        }
        val jsMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-html-js:0.7.2")
                implementation("org.jetbrains:kotlin-react:16.13.1-pre.110-kotlin-1.4.10")
                implementation("org.jetbrains:kotlin-react-dom:16.13.1-pre.110-kotlin-1.4.10")
                implementation("org.jetbrains:kotlin-react-router-dom:5.1.2-pre.110-kotlin-1.4.10")
                implementation("org.jetbrains:kotlin-styled:1.0.0-pre.110-kotlin-1.4.10")
            }
            kotlin.srcDirs(kotlin.srcDirs, "$buildDir/js/generated/")
        }
        val jsTest by getting {
            dependencies {
                implementation(kotlin("test-js"))
            }
        }
    }
}
application {
    mainClassName = "ServerKt"
}
tasks.getByName<KotlinWebpack>("jsBrowserProductionWebpack") {
    outputFileName = "zoo.js"
}
tasks.getByName<Jar>("jvmJar") {
    dependsOn(tasks.getByName("jsBrowserProductionWebpack"))
    val jsBrowserProductionWebpack = tasks.getByName<KotlinWebpack>("jsBrowserProductionWebpack")
    from(File(jsBrowserProductionWebpack.destinationDirectory, jsBrowserProductionWebpack.outputFileName))
}
tasks.getByName<JavaExec>("run") {
    dependsOn(tasks.getByName<Jar>("jvmJar"))
    classpath(tasks.getByName<Jar>("jvmJar"))
}

val generateZooApiJsProxy = tasks.register<JavaExec>("generateZooApiJsClient") {
    group = "build"
    description = "Generate ZooApi JS proxy"
    classpath = sourceSets["main"].runtimeClasspath
    main = "it.krzeminski.zoo.api.generation.JsClientGenerationKt"
    args("it.krzeminski.zoo.api.ZooApi", "$buildDir/js/generated")
}

tasks.getByName("compileKotlinJs").dependsOn(generateZooApiJsProxy)
tasks.getByName("ktlintJsMainSourceSetCheck").dependsOn(tasks.getByName("ktlintJsMainSourceSetFormat"))

val generateZooApiJvmKtorServer = tasks.register<JavaExec>("generateZooApiJvmKtorServer") {
    group = "build"
    description = "Generate ZooApi JVM Ktor server"
    classpath = sourceSets["main"].runtimeClasspath
    main = "it.krzeminski.zoo.api.generation.JvmKtorServerGenerationKt"
    args("it.krzeminski.zoo.api.ZooApi", "$buildDir/jvm/generated")
}

// The below dependency doesn't work well. In order to make it work, one has to comment out the use of the below
// generated server, run 'gradle build', then uncomment it and then 'gradle build' again. When the generation logic is
// moved to a separate module, things should work more logically and it should be possible to define more robust
// dependencies between Gradle tasks.
tasks.getByName("assemble").dependsOn(generateZooApiJvmKtorServer)
tasks.getByName("ktlintJvmMainSourceSetCheck").dependsOn(tasks.getByName("ktlintJvmMainSourceSetFormat"))
