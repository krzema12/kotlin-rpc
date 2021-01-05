import it.krzeminski.zoo.api.TestDataClass
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.promise
import kotlin.test.Test
import kotlin.test.assertEquals

class EndToEndCompilingTest : CoroutineScope by GlobalScope {
    @Test
    fun genericCase() = promise {
        with(ZooApiJsClient(url = "http://localhost:8080", coroutineContext)) {
            val fetchedData = someFunction(
                intArg = 123,
                dataClassArg = TestDataClass(
                    stringField = "FooBar!",
                    optionalIntField = 987,
                ),
                listArg = listOf(true, false, true, false),
            )
            assertEquals(listOf(11.22f, 33.44f), fetchedData)
        }
    }
}
