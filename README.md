# kotlin-rpc

**Work in progress!**

It's a Kotlin-centric approach to Remote Procedure Calling, built on top of reflectionless kotlinx.serialization and ktor.
Safe and elegant.

It works like this. First, the API is described with pure Kotlin, as an interface. Then, kotlin-rpc's build-time logic
kicks in and generates clients and server stubs. It works over HTTP (e. g. calling a JVM server from a JS client), but
other medium is possible, like Web workers (communication between the main UI thread and the worker thread).
