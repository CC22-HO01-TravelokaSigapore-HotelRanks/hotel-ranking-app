package com.c22ho01.hotelranking.data.remote.response.error

import com.google.gson.annotations.SerializedName

data class ComplexErrorResponse(
    @field:SerializedName("message")
    val message: ComplexMessage? = null
) : ErrorResponse {
    override fun getError() = message?.name
}

data class ComplexMessageOriginal(

    @field:SerializedName("errno")
    val errno: Int? = null,

    @field:SerializedName("code")
    val code: String? = null,

    @field:SerializedName("sqlState")
    val sqlState: String? = null,

    @field:SerializedName("sqlMessage")
    val sqlMessage: String? = null,

    @field:SerializedName("sql")
    val sql: String? = null
)

data class ComplexMessageParent(

    @field:SerializedName("errno")
    val errno: Int? = null,

    @field:SerializedName("code")
    val code: String? = null,

    @field:SerializedName("sqlState")
    val sqlState: String? = null,

    @field:SerializedName("sqlMessage")
    val sqlMessage: String? = null,

    @field:SerializedName("sql")
    val sql: String? = null
)

data class ComplexMessage(

    @field:SerializedName("parent")
    val parent: ComplexMessageParent? = null,

    @field:SerializedName("original")
    val original: ComplexMessageOriginal? = null,

    @field:SerializedName("name")
    val name: String? = null,

    @field:SerializedName("parameters")
    val parameters: Parameters? = null,

    @field:SerializedName("sql")
    val sql: String? = null
)

data class Parameters(
    val any: Any? = null
)
