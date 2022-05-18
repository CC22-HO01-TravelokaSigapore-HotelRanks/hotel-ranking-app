package com.c22ho01.hotelranking.utils

import com.c22ho01.hotelranking.data.remote.retrofit.APIConfig
import okhttp3.mockwebserver.MockWebServer
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement

class MockAPIRule(private val mockWebServer: MockWebServer) : TestRule {
  override fun apply(base: Statement?, description: Description?): Statement {
    return object : Statement() {
      override fun evaluate() {
        mockWebServer.start()
        APIConfig.BASE_URL = mockWebServer.url("/").toString()
      }
    }
  }
}
