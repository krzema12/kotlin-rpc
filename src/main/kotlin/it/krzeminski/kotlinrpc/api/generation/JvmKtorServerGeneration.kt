package it.krzeminski.kotlinrpc.api.generation

import java.io.File
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.full.declaredMemberFunctions

fun main(args: Array<String>) {
    val className = args[0]
    val targetPath = args[1]

    println("Class name: $className")
    println("Target path: $targetPath")

    val classToGenerateClientFor = Class.forName(className).kotlin
    val generatedCode = generateServerCode(classToGenerateClientFor)
    File(targetPath).mkdirs()
    File("$targetPath/${classToGenerateClientFor.simpleName}JvmKtorServer.kt").writeText(generatedCode)
}

private fun generateServerCode(klass: KClass<*>): String {
    return """import io.ktor.application.call
import io.ktor.request.receiveText
import io.ktor.response.respond
import io.ktor.routing.Routing
import io.ktor.routing.post
import io.ktor.routing.route
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
        
        fun Routing.${klass.simpleName?.decapitalize()}KtorHandlers(${klass.simpleName?.decapitalize()}Impl: ${klass.qualifiedName}) {
            route("api/") {
                ${klass.declaredMemberFunctions.joinToString("\n") { function -> generateHandlerFunction(function, klass) }}
            }
        }
        
        ${klass.declaredMemberFunctions.joinToString("\n") { function -> generateRequestDataClass(function) }}
    """
}

private fun generateHandlerFunction(function: KFunction<*>, klass: KClass<*>): String {
    return """
        post("/${function.name}") {
            ${if (function.parameters.drop(1).isNotEmpty())
        """val bodyAsString = call.receiveText()
        val body = Json.decodeFromString<${function.name.capitalize()}Request>(bodyAsString)"""
    else
        ""
    }
            
            val implResponse = ${klass.simpleName?.decapitalize()}Impl.${function.name}(
                ${function.parameters.drop(1).joinToString("\n") { parameter -> "${parameter.name} = body.${parameter.name}," }}
            )

            call.respond(Json.encodeToString(implResponse))
        }"""
}

private fun generateRequestDataClass(function: KFunction<*>): String {
    if (function.parameters.drop(1).isEmpty())
        return ""

    return """
        @Serializable
        data class ${function.name.capitalize()}Request(
            ${function.parameters.drop(1).joinToString("\n") { parameter -> "val ${parameter.name}: ${parameter.type}," }}
        )
    """
}
