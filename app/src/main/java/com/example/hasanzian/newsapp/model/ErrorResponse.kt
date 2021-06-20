package com.example.hasanzian.newsapp.model

import com.google.gson.annotations.SerializedName

data class ErrorResponse(
    @field:SerializedName("response")
    val body: Body? = null
)

data class Body(
    @field:SerializedName("message")
    val message: String? = null,

    @field:SerializedName("status")
    val status: String? = null
)
