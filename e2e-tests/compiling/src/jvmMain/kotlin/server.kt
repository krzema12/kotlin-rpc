import io.ktor.application.*
import io.ktor.features.*
import io.ktor.html.respondHtml
import io.ktor.http.*
import io.ktor.http.content.resources
import io.ktor.http.content.static
import io.ktor.routing.get
import io.ktor.routing.routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.util.pipeline.PipelineContext
import it.krzeminski.zoo.api.TestDataClass
import it.krzeminski.zoo.api.ZooApi
import kotlinx.html.body
import kotlinx.html.div
import kotlinx.html.head
import kotlinx.html.id
import kotlinx.html.script
import kotlinx.html.title

fun main() {
    val zooImpl = object : ZooApi {
        override suspend fun someFunction(
            intArg: Int,
            dataClassArg: TestDataClass,
            listArg: List<Boolean?>
        ): List<Float> {
            return listOf(11.22f, 33.44f)
        }

        override suspend fun otherFunction(): String {
            return "String returned from server"
        }

        private var storedValue: String = "No value stored"

        override suspend fun setValue(value: String) {
            storedValue = value
        }

        override suspend fun getValue(): String {
            return storedValue
        }

        override suspend fun functionThrowingException(value: String) {
            throw RuntimeException("Exception message, value: $value")
        }
    }

    embeddedServer(Netty, port = 8080, host = "127.0.0.1") {
        // Allow cross-origin requests solely for test purposes.
        install(CORS) {
            anyHost()
            method(HttpMethod.Options)
            method(HttpMethod.Get)
            method(HttpMethod.Post)
            method(HttpMethod.Put)
            method(HttpMethod.Delete)
            method(HttpMethod.Patch)
            header(HttpHeaders.AccessControlAllowHeaders)
            header(HttpHeaders.ContentType)
            header(HttpHeaders.AccessControlAllowOrigin)
        }
        routing {
            get("/") { webApp() }
            zooApiKtorHandlers(zooImpl)
            static("/static") {
                resources()
            }
        }
    }.start(wait = true)
}

private suspend fun PipelineContext<Unit, ApplicationCall>.webApp() {
    call.respondHtml(HttpStatusCode.OK) {
        head {
            title("Zoo!")
        }
        body {
            div {
                id = "root"
            }
            script(src = "/static/zoo.js") {}
        }
    }
}
