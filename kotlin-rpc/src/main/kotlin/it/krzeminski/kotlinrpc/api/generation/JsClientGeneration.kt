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
    val generatedCode = generateClass(classToGenerateClientFor, annotations)
    File(targetPath).mkdirs()
    File("$targetPath/${classToGenerateClientFor.simpleName}JsClient.kt").writeText(generatedCode)
}

private fun generateClass(klass: KClass<*>, annotations: List<String>): String {
    return """import kotlinx.serialization.Serializable
import kotlinx.browser.window
import kotlinx.coroutines.await
import kotlinx.coroutines.withContext
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.w3c.fetch.RequestInit
import kotlin.coroutines.CoroutineContext
import kotlin.js.json

       ${generateResponseClass()}
        
       ${annotations.joinToString("\n")}
        class ${klass.simpleName}JsClient(private val url: String, private val coroutineContext: CoroutineContext) : ${klass.qualifiedName} {
            ${klass.declaredMemberFunctions.joinToString("\n") { function -> generateProxyFunction(function) }}
            
            ${generatePostMethod()}
        }
    """
}

private fun generateProxyFunction(function: KFunction<*>): String {
    if (!function.isSuspend) {
        throw IllegalArgumentException("All functions in the interface should be marked with 'suspend'!")
    }

    return """
        ${if (function.parameters.drop(1).isNotEmpty()) generateRequestDataClass(function) else ""}
        
        ${generateResponseDataClass(function)}
        
        override suspend fun ${function.name}(
            ${function.parameters.drop(1).joinToString("\n") { parameter -> "${parameter.name}: ${parameter.type}," }}
        ): ${function.returnType} {
            ${generateRequestBody(function)}
            val responseAsString = post("${'$'}url/api/${function.name}", requestBodyAsString)
            val response = Json.decodeFromString<KotlinRpcResponse>(responseAsString)
            
            if (response.exception != null) {
                // For now, only top-level message is serialized.
                val exceptionMessage = response.exception
                throw RuntimeException(exceptionMessage)
            }
            
            ${if (function.returnType != Unit::class) {
              """
                val responseBody = Json.decodeFromString<${function.name.capitalize()}Response>(response.body ?: throw RuntimeException("Shouldn't happen - exception or body should be present!"))
                return responseBody.returnValue
              """
            } else {
                ""
            }
        }}"""
}

private fun generateRequestBody(function: KFunction<*>): String {
    if (function.parameters.drop(1).isEmpty()) {
        return """
            val requestBodyAsString = ""
        """
    }

    return """val requestBody = ${function.name.capitalize()}Request(
        ${function.parameters.drop(1).joinToString("\n") { parameter -> "${parameter.name}," }}
        )
        val requestBodyAsString = Json.encodeToString(${function.name.capitalize()}Request.serializer(), requestBody)
        """
}

private fun generateRequestDataClass(function: KFunction<*>): String {
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

private fun generatePostMethod(): String {
    return """
    private suspend fun post(url: String, body: String): String {
        return withContext(coroutineContext) {
            val response = window.fetch(url, RequestInit("POST", headers = json(
                    "Accept" to "application/json",
                    "Content-Type" to "application/json"),
                    body = body)).await()

            response.text().await()
        }
    }
    """
}
