plugins {
    `kotlin-dsl`
}

gradlePlugin {
    plugins {
        register("kotlin-rpc-gradle-plugin") {
            id = "kotlinRpc"
            version = "0.1.0"
            implementationClass = "KotlinRpcGradlePlugin"
        }
    }
}

repositories {
    mavenLocal()
    mavenCentral()
    google()
    maven("https://plugins.gradle.org/m2/")
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:1.4.10")
}
