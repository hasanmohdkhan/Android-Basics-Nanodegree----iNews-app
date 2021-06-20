package com.example.hasanzian.newsapp.model


import com.google.gson.annotations.SerializedName

data class Result(
    @SerializedName("apiUrl")
    val apiUrl: String,
    @SerializedName("fields")
    val fields: Fields,
    @SerializedName("id")
    val id: String,
    @SerializedName("isHosted")
    val isHosted: Boolean,
    @SerializedName("pillarId")
    val pillarId: String,
    @SerializedName("pillarName")
    val pillarName: String,
    @SerializedName("sectionId")
    val sectionId: String,
    @SerializedName("sectionName")
    val sectionName: String,
    @SerializedName("tags")
    val tags: List<Tag>,
    @SerializedName("type")
    val type: String,
    @SerializedName("webPublicationDate")
    val webPublicationDate: String,
    @SerializedName("webTitle")
    val webTitle: String,
    @SerializedName("webUrl")
    val webUrl: String
)