package com.example.hasanzian.newsapp.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.hasanzian.newsapp.model.ErrorResponse
import com.example.hasanzian.newsapp.model.NewsResponse
import com.example.hasanzian.newsapp.network.NewsApi
import com.example.hasanzian.newsapp.utils.DataState
import com.example.hasanzian.newsapp.utils.ViewState
import com.google.gson.Gson
import okhttp3.ResponseBody

class NewsRepository (
    private val newsApi: NewsApi
) {
    lateinit var searchKeyWord: String
    var pageNumber = 1
    private var isLoading: Boolean = false

    private val _viewState = MutableLiveData<ViewState>()
    val viewState: LiveData<ViewState> = _viewState
    private val s = "REPO"

    suspend fun get(){
        isLoading = true
        _viewState.postValue(ViewState.Loading(true))

        val responseAsync = newsApi.getResponseAsync(pageSize = 10,orderBy= "newest",page = 1)

        if (responseAsync.isSuccessful) {
            val results = responseAsync.body()?.response?.results
            Log.d(s, "get: " + (results?.size))
            isLoading = false
            results.let {
                _viewState.postValue(it?.let { list -> ViewState.Success(list) })
            }
        } else {
            val errorBody: ResponseBody? = responseAsync.errorBody()
            val handleErrorResponse = handleErrorResponse(errorBody)
            _viewState.postValue(handleErrorResponse.body?.message?.let { ViewState.Failed(it) })
            isLoading = false
        }

    }

    private fun handleErrorResponse(errorBody: ResponseBody?): ErrorResponse {
        val error = errorBody?.string()
        val gson = Gson()
        val errorResponse: ErrorResponse = gson.fromJson(error, ErrorResponse::class.java)
        Log.d(s, "Error: $error")
        Log.d(s, "msg-> " + errorResponse.body?.message)
        Log.d(s, "status-> " + errorResponse.body?.status)
        return  errorResponse
    }

}