import it.krzeminski.zoo.api.SomeFunctionRequest
import it.krzeminski.zoo.api.TestDataClass
import it.krzeminski.zoo.api.ZooApi
import kotlinx.browser.window
import kotlinx.coroutines.await
import kotlinx.coroutines.withContext
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.w3c.fetch.RequestInit
import kotlin.coroutines.CoroutineContext
import kotlin.js.json

class ZooClient(private val coroutineContext: CoroutineContext) : ZooApi {
    companion object {
        const val serverBase = "http://localhost:8080"
    }

    override suspend fun someFunction(intArg: Int, dataClassArg: TestDataClass, listArg: List<Boolean>): List<Float> {
        val body = SomeFunctionRequest(intArg, dataClassArg, listArg)
        val bodyAsString = Json.encodeToString(body)
        val responseAsString = post("$serverBase/api/someFunction", bodyAsString)
        val response = Json.decodeFromString<List<Float>>(responseAsString)
        return response
    }

    override suspend fun otherFunction(): String {
        TODO("Not yet implemented")
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