import io.ktor.application.call
import io.ktor.request.receiveText
import io.ktor.response.respond
import io.ktor.routing.Routing
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.route
import it.krzeminski.zoo.api.TestDataClass
import it.krzeminski.zoo.api.ZooApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

fun Routing.zooApiKtorHandlers(zooApiImpl: ZooApi) {
    route("/api") {
        post("/someFunction") {
            val bodyAsStrong = call.receiveText()
            val body = Json.decodeFromString<SomeFunctionRequest>(bodyAsStrong)

            val implResponse = zooApiImpl.someFunction(
                intArg = body.intArg,
                dataClassArg = body.dataClassArg,
                listArg = body.listArg,
            )

            call.respond(Json.encodeToString(implResponse))
        }
    }
}

@Serializable
data class SomeFunctionRequest(
    val intArg: Int,
    val dataClassArg: TestDataClass,
    val listArg: List<Boolean?>,
)
