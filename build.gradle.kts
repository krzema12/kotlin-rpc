buildscript {
    // Having this version in one place keeps version of the library in sync, both what's exposed and what's consumed by
    // the end-to-end tests.
    val kotlinRpcVersion by extra { "0.1.3" }
}
