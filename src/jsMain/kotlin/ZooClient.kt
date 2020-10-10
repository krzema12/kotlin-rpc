import kotlinx.browser.window
import kotlinx.coroutines.await
import kotlinx.coroutines.withContext
import org.w3c.fetch.RequestInit
import kotlin.coroutines.CoroutineContext
import kotlin.js.json

class ZooClient(private val coroutineContext: CoroutineContext) {
    suspend fun getZoos(): String {
        val response = fetch("http://localhost:8080/api/zoos")
        return response
    }

    private suspend fun fetch(url: String): String {
        return withContext(coroutineContext) {
            val response = window.fetch(url, RequestInit("GET", headers = json(
                    "Accept" to "application/json",
                    "Content-Type" to "application/json"
            ))).await()

            response.text().await()
        }
    }
}