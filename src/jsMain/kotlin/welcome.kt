import it.krzeminski.zoo.api.TestDataClass
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.html.InputType
import kotlinx.html.js.onClickFunction
import react.RBuilder
import react.RComponent
import react.RProps
import react.RState
import react.dom.div
import react.dom.input
import react.setState

external interface WelcomeState : RState {
    var zoosFromBackend: String?
}

class Welcome : RComponent<RProps, WelcomeState>(), CoroutineScope by MainScope() {

    override fun RBuilder.render() {
        div {
            +"From backend: ${state.zoosFromBackend}"
        }
        input(type = InputType.button) {
            attrs {
                value = "Call backend"
                onClickFunction = {
                    launch {
                        fetchZoosFromBackend()
                    }
                }
            }
        }
    }

    private suspend fun fetchZoosFromBackend() {
        with(ZooApiJsClient(url = "http://localhost:8080", coroutineContext)) {
            launch {
                val zoosFromBackendFetched = someFunction(
                    intArg = 123,
                    dataClassArg = TestDataClass(
                        stringField = "FooBar!",
                        optionalIntField = 987,
                    ),
                    listArg = listOf(true, false, true, false),
                )
                setState {
                    zoosFromBackend = zoosFromBackendFetched.joinToString {
                        it.toString()
                    }
                }
            }
        }
    }
}
