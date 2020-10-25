import io.ktor.application.ApplicationCall
import io.ktor.application.call
import io.ktor.html.respondHtml
import io.ktor.http.HttpStatusCode
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
            TODO("Not yet implemented")
        }
    }

    embeddedServer(Netty, port = 8080, host = "127.0.0.1") {
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
