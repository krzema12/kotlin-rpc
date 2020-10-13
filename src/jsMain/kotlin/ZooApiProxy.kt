import kotlinx.serialization.Serializable
import kotlinx.browser.window
import kotlinx.coroutines.await
import kotlinx.coroutines.withContext
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.w3c.fetch.RequestInit
import kotlin.coroutines.CoroutineContext
import kotlin.js.json

class ZooApiProxy(private val coroutineContext: CoroutineContext) : it.krzeminski.zoo.api.ZooApi {


    override suspend fun otherFunction(

    ): kotlin.String {

        val requestBodyAsString = ""

        val responseBodyAsString = post("http://localhost:8080/api/otherFunction", requestBodyAsString)
        val responseBody = Json.decodeFromString<kotlin.String>(responseBodyAsString)
        return responseBody
    }


    @Serializable
    data class SomeFunctionRequest(
            val intArg: kotlin.Int,
            val dataClassArg: it.krzeminski.zoo.api.TestDataClass,
            val listArg: kotlin.collections.List<kotlin.Boolean?>,
    )


    override suspend fun someFunction(
            intArg: kotlin.Int,
            dataClassArg: it.krzeminski.zoo.api.TestDataClass,
            listArg: kotlin.collections.List<kotlin.Boolean?>,
    ): kotlin.collections.List<kotlin.Float> {
        val requestBody = SomeFunctionRequest(
                intArg,
                dataClassArg,
                listArg,
        )
        val requestBodyAsString = Json.encodeToString(SomeFunctionRequest.serializer(), requestBody)

        val responseBodyAsString = post("http://localhost:8080/api/someFunction", requestBodyAsString)
        val responseBody = Json.decodeFromString<kotlin.collections.List<kotlin.Float>>(responseBodyAsString)
        return responseBody
    }


    private suspend fun post(url: String, body: String): String {
        return withContext(coroutineContext) {
            val response = window.fetch(url, RequestInit("POST", headers = json(
                    "Accept" to "application/json",
                    "Content-Type" to "application/json"),
                    body = body)).await()

            response.text().await()
        }
    }

}