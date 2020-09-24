import io.ktor.application.*
import io.ktor.html.respondHtml
import io.ktor.http.HttpStatusCode
import io.ktor.routing.get
import io.ktor.routing.routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.http.content.resources
import io.ktor.http.content.static
import io.ktor.util.pipeline.*
import kotlinx.html.*

fun main() {
    embeddedServer(Netty, port = 8080, host = "127.0.0.1") {
        routing {
            get("/") {
                webApp()
            }
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
