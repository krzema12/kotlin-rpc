package it.krzeminski.zoo.api

import kotlinx.serialization.Serializable

@Serializable
data class SomeFunctionRequest(
    val intArg: Int,
    val dataClassArg: TestDataClass,
    val listArg: List<Boolean>,
)
