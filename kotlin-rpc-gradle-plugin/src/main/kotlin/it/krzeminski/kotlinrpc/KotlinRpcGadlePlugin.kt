package it.krzeminski.kotlinrpc

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.JavaExec
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.kotlin.dsl.get
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.getting
import org.gradle.kotlin.dsl.invoke
import org.gradle.kotlin.dsl.register
import org.gradle.kotlin.dsl.the
import org.jetbrains.kotlin.gradle.dsl.*

//val Project.kotlin get() = extensions.getByType(KotlinMultiplatformExtension::class.java)
//fun Project.kotlin(callback: KotlinMultiplatformExtension.() -> Unit) = kotlin.apply(callback)

class KotlinRpcGradlePlugin : Plugin<Project> {
    override fun apply(project: Project) {
        val sourceSetContainer = project.extensions.getByType(SourceSetContainer::class)

        with (project) {
            val generateJsProxy = tasks.register<JavaExec>("generateJsClient") {
                group = "build"
                description = "Generate JS proxy"
                classpath = sourceSetContainer.getByName("main").runtimeClasspath
                main = "it.krzeminski.kotlinrpc.api.generation.JsClientGenerationKt"
                args("it.krzeminski.zoo.api.ZooApi", "$buildDir/js/generated")
            }

            val generateJvmKtorServer = tasks.register<JavaExec>("generateJvmKtorServer") {
                group = "build"
                description = "Generate JVM Ktor server"
                classpath = sourceSetContainer.getByName("main").runtimeClasspath
                main = "it.krzeminski.kotlinrpc.api.generation.JvmKtorServerGenerationKt"
                args("it.krzeminski.zoo.api.ZooApi", "$buildDir/jvm/generated")
            }

            tasks.getByName("jvmJar").dependsOn(generateJvmKtorServer)
            tasks.getByName("jsJar").dependsOn(generateJsProxy)
        }
    }
}
