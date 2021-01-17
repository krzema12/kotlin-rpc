package it.krzeminski.kotlinrpc.api.generation

fun generateResponseClass(): String {
    return """
        @Serializable
        data class KotlinRpcResponse(
            val body: String? = null,
            val exception: String? = null,
        )
"""
}
