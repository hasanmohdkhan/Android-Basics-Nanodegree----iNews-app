package com.example.hasanzian.newsapp.model


import com.google.gson.annotations.SerializedName

data class NewsResponse(
    @SerializedName("response")
    val response: Response
)