package it.krzeminski.kotlinrpc.api.generation

import java.io.File
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.full.declaredMemberFunctions

fun main(args: Array<String>) {
    val className = args[0]
    val targetPath = args[1]
    val annotations = if (args.size >= 3 ) {
        args[2].split(",")
    } else {
        emptyList()
    }

    println("Class name: $className")
    println("Target path: $targetPath")

    val classToGenerateClientFor = Class.forName(className).kotlin
    val generatedCode = generateServerCode(classToGenerateClientFor, annotations)
    File(targetPath).mkdirs()
    File("$targetPath/${classToGenerateClientFor.simpleName}JvmKtorServer.kt").writeText(generatedCode)
}

private fun generateServerCode(klass: KClass<*>, annotations: List<String>): String {
    return """import io.ktor.application.call
import io.ktor.request.receiveText
import io.ktor.response.respond
import io.ktor.routing.Routing
import io.ktor.routing.post
import io.ktor.routing.route
import io.ktor.http.HttpStatusCode
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
       
       ${generateResponseClass()}
      
       ${annotations.joinToString("\n")}
        fun Routing.${klass.simpleName?.decapitalize()}KtorHandlers(${klass.simpleName?.decapitalize()}Impl: ${klass.qualifiedName}) {
            route("api/") {
                ${klass.declaredMemberFunctions.joinToString("\n") { function -> generateHandlerFunction(function, klass) }}
            }
        }
        
        ${klass.declaredMemberFunctions.joinToString("\n") { function ->
            """
            ${generateRequestDataClass(function)}
            ${generateResponseDataClass(function)}
            """
        }}
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
            
            try {
                val implResponse = ${klass.simpleName?.decapitalize()}Impl.${function.name}(
                    ${function.parameters.drop(1).joinToString("\n") { parameter -> "${parameter.name} = body.${parameter.name}," }}
                )
                val implResponseWrapped = ${function.name.capitalize()}Response(returnValue = implResponse)
                val kotlinRpcResponse = KotlinRpcResponse(body = Json.encodeToString(${function.name.capitalize()}Response.serializer(), implResponseWrapped))

                call.respond(HttpStatusCode.OK, Json.encodeToString(kotlinRpcResponse))
            } catch (e: Exception) {
                val kotlinRpcResponse = KotlinRpcResponse(exception = e.message)
                call.respond(HttpStatusCode.InternalServerError, Json.encodeToString(kotlinRpcResponse))
            }
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

private fun generateResponseDataClass(function: KFunction<*>): String {
    if (function.returnType == Unit::class) {
        return ""
    }

    return """
        @Serializable
        data class ${function.name.capitalize()}Response(
            val returnValue: ${function.returnType},
        )
    """
}
