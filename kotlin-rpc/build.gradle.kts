plugins {
    kotlin("jvm") version "1.4.10"
    `maven-publish`
}

val kotlinRpcVersion: String by rootProject.extra

group = "it.krzeminski.kotlinrpc"
version = kotlinRpcVersion

repositories {
    mavenCentral()
    jcenter()
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation("org.jetbrains.kotlin:kotlin-reflect:1.4.10")
    implementation("com.pinterest:ktlint:0.40.0")
}

tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
    compileTestKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
}

publishing {
    repositories {
        maven {
            name = "bintray"
            url = uri("https://api.bintray.com/maven/krzema1212/it.krzeminski/kotlin-rpc/;publish=1;override=1")
            credentials {
                username = System.getenv("BINTRAY_USER")
                password = System.getenv("BINTRAY_API_KEY")
            }
        }
    }
    publications {
        create<MavenPublication>("default") {
            from(components["java"])
        }
    }
}
