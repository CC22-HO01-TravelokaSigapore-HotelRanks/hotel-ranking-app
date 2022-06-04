package com.c22ho01.hotelranking.utils

import io.github.cdimascio.dotenv.Dotenv

object EnvUtils {
    private val dotenv = Dotenv.configure().directory("./assets").filename("env").load()

    fun getGsoClientId(): String {
        return dotenv.get("GSO_CLIENT_ID")
    }
}