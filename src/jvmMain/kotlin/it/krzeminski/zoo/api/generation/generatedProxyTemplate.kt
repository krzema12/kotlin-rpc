package it.krzeminski.zoo.api.generation

import it.krzeminski.zoo.api.TestDataClass
import it.krzeminski.zoo.api.ZooApi

class FooBarImplementation(private val url: String) : ZooApi {
    override suspend fun someFunction(intArg: Int, dataClassArg: TestDataClass, listArg: List<Boolean>): List<Float> {
        // TODO create a data object with all arguments
        // TODO serialize using kotlinx.serialization
        // TODO make the call using FETCH
        // TODO Receive the data
        // TODO deserialize to return type
        // TODO allow suspending functions
        TODO()
    }

    override suspend fun otherFunction(): String {
        TODO("Not yet implemented")
    }
}

fun ZooApi.buildProxy(url: String) =
        FooBarImplementation(url)
