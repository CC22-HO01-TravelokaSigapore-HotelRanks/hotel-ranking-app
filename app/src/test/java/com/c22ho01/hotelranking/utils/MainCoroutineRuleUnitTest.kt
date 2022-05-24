package com.c22ho01.hotelranking.utils

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.rules.TestWatcher
import org.junit.runner.Description

/// ref: https://github.com/googlecodelabs/kotlin-coroutines/pull/163/commits/9ce8be39eb2eb22b9315be535a3e0620bfe00ec8#diff-79f69fa27455c6e5ae779e036974ce241799af00e25c4f579f0ac341232e5ad4
@ExperimentalCoroutinesApi
class MainCoroutineRuleUnitTest(val dispatcher: TestDispatcher = UnconfinedTestDispatcher()) :
    TestWatcher() {

    val scope = TestScope(dispatcher)

    override fun starting(description: Description) {
        super.starting(description)
        Dispatchers.setMain(dispatcher)
    }

    override fun finished(description: Description) {
        super.finished(description)
        Dispatchers.resetMain()
    }
}