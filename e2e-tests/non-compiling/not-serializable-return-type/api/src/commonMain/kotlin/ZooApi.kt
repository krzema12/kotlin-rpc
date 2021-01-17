package it.krzeminski.zoo.api

class SomeNonSerializableType

interface ZooApi {
    suspend fun someFunction(): SomeNonSerializableType
}
