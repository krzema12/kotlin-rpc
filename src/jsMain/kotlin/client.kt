import react.dom.render
import kotlinx.browser.document
import kotlinx.browser.window
import react.router.dom.browserRouter
import react.router.dom.route
import react.router.dom.switch

fun main() {
    window.onload = {
        render(document.getElementById("root")) {
            browserRouter {
                switch {
                    route("/", exact = true) {
                        child(Welcome::class) {
                            attrs {
                                initName = "Root page"
                            }
                        }
                    }
                    route("/zoos") {
                        child(Welcome::class) {
                            attrs {
                                initName = "Zoos"
                            }
                        }
                    }
                }
            }
        }
    }
}
