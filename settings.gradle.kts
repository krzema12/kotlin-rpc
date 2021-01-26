rootProject.name = "kotlin-rpc"

include(":kotlin-rpc")
include(":kotlin-rpc-gradle-plugin")

include(":e2e-tests:compiling")
include(":e2e-tests:compiling:api")

include(":e2e-tests:non-compiling:not-serializable-parameter:api")
include(":e2e-tests:non-compiling:not-serializable-return-type:api")
