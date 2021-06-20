package com.example.hasanzian.newsapp.utils

import com.example.hasanzian.newsapp.model.Result

sealed class ViewState {
    // loading return a boolean
    data class Loading(val isLoading: Boolean) : ViewState()

    //success must return real object in this case cache objects list
    data class Success(val List: List<Result>) : ViewState()

    // failed return a error msg
    data class Failed(val error: String) : ViewState()

}