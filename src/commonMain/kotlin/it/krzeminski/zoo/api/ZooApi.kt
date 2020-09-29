package it.krzeminski.zoo.api

data class TestDataClass(
        val stringField: String,
        val optionalIntField: Int?,
)

interface ZooApi {
    fun someFunction(
            intArg: Int,
            dataClassArg: TestDataClass,
            listArg: List<Boolean>,
    ): List<Float>

    fun otherFunction(
    ): String
}
