package it.krzeminski.zoo.api.generation

import it.krzeminski.zoo.api.ZooApi
import kotlin.reflect.full.declaredMemberFunctions

fun main() {
    val kClass = ZooApi::class

    println("""
class ${kClass.simpleName}Proxy(private val url: String) : ${kClass.qualifiedName} {
    ${kClass.declaredMemberFunctions.map { function -> """
    override fun ${function.name}(
${function.parameters.drop(1).map { parameter ->
            "        ${parameter.name}: ${parameter.type},"
        }.joinToString("\n")}
    ): ${function.returnType}"""
    }.joinToString("\n")}
}
""")
}
