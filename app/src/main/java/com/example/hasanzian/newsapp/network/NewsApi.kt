package com.example.hasanzian.newsapp.network

import com.example.hasanzian.newsapp.constants.NewsConstant
import com.example.hasanzian.newsapp.constants.NewsConstant.API_KEY_VALUE
import com.example.hasanzian.newsapp.constants.NewsConstant.FORMAT_VALUE
import com.example.hasanzian.newsapp.constants.NewsConstant.QUARRY_CONTRIBUTOR
import com.example.hasanzian.newsapp.constants.NewsConstant.QUARRY_SHOW_FIELDS
import com.example.hasanzian.newsapp.constants.NewsConstant.QUARRY_SHOW_TAGS
import com.example.hasanzian.newsapp.constants.NewsConstant.THUMBNAIL_VALUE
import com.example.hasanzian.newsapp.model.NewsResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface NewsApi {
    //https://content.guardianapis.com/search?q=
    // &format=json
// &show-tags=contributor
// &show-fields=thumbnail
// &order-by=newest
// &page-size=10
// &page=1
// &api-key=f4ebdbc6-3d1f-4350-9b1b-07a48efb4764
    @GET("search?")
    suspend fun getResponseAsync(
        @Query("api-key") apiKey: String = API_KEY_VALUE,
        @Query("format") format: String = FORMAT_VALUE,
        @Query("show-fields") showFields: String = THUMBNAIL_VALUE,
        @Query("page-size") pageSize: Int,
        @Query("order-by") orderBy: String,
        @Query("page") page: Int,
        @Query("show-tags") showTags: String = QUARRY_CONTRIBUTOR
    ): Response<NewsResponse>
}