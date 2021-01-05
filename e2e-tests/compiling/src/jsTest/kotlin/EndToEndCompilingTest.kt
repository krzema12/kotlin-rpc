import it.krzeminski.zoo.api.TestDataClass
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.promise
import kotlin.js.Date
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

    @Test
    fun functionAcceptingNoArguments() = promise {
        with(ZooApiJsClient(url = "http://localhost:8080", coroutineContext)) {
            val fetchedData = otherFunction()
            assertEquals("String returned from server", fetchedData)
        }
    }

    @Test
    fun functionReturningNoValue() = promise {
        with(ZooApiJsClient(url = "http://localhost:8080", coroutineContext)) {
            val testValue = "Some test value at ${Date.now()}"
            setValue(testValue)
            val fetchedData = getValue()
            assertEquals(testValue, fetchedData)
        }
    }
}
