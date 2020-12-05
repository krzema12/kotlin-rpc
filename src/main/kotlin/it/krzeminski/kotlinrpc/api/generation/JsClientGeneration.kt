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
    val generatedCode = generateClass(classToGenerateClientFor)
    File(targetPath).mkdirs()
    File("$targetPath/${classToGenerateClientFor.simpleName}JsClient.kt").writeText(generatedCode)
}

private fun generateClass(klass: KClass<*>): String {
    return """import kotlinx.serialization.Serializable
import kotlinx.browser.window
import kotlinx.coroutines.await
import kotlinx.coroutines.withContext
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.w3c.fetch.RequestInit
import kotlin.coroutines.CoroutineContext
import kotlin.js.json
        
        class ${klass.simpleName}JsClient(private val url: String, private val coroutineContext: CoroutineContext) : ${klass.qualifiedName} {
            ${klass.declaredMemberFunctions.joinToString("\n") { function -> generateProxyFunction(function) }}
            
            ${generatePostMethod()}
        }
    """
}

private fun generateProxyFunction(function: KFunction<*>): String {
    return """
        ${if (function.parameters.drop(1).isNotEmpty()) generateRequestDataClass(function) else ""}
        
        override ${if (function.isSuspend) "suspend" else ""} fun ${function.name}(
            ${function.parameters.drop(1).joinToString("\n") { parameter -> "${parameter.name}: ${parameter.type}," }}
        ): ${function.returnType} {
            ${generateRequestBody(function)}
            val responseBodyAsString = post("${'$'}url/api/${function.name}", requestBodyAsString)
            val responseBody = Json.decodeFromString<${function.returnType}>(responseBodyAsString)
            return responseBody
        }"""
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
