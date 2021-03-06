package it.krzeminski.zoo.api

import kotlinx.serialization.Serializable

//@Serializable - not present on purpose.
data class TestDataClass(
    val stringField: String,
    val optionalIntField: Int?,
)

interface ZooApi {
    suspend fun someFunction(
        intArg: Int,
        dataClassArg: TestDataClass,
        listArg: List<Boolean?>,
    ): List<Float>

    suspend fun otherFunction(): String

    suspend fun setValue(value: String)

    suspend fun getValue(): String
}
