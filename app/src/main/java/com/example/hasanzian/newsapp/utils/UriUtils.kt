package com.example.hasanzian.newsapp.utils

import android.net.Uri
import com.example.hasanzian.newsapp.constants.NewsConstant.API_KEY_VALUE
import com.example.hasanzian.newsapp.constants.NewsConstant.FORMAT_VALUE
import com.example.hasanzian.newsapp.constants.NewsConstant.NEWS_REQ_URL
import com.example.hasanzian.newsapp.constants.NewsConstant.QUARRY_API_KEY
import com.example.hasanzian.newsapp.constants.NewsConstant.QUARRY_CONTRIBUTOR
import com.example.hasanzian.newsapp.constants.NewsConstant.QUARRY_FORMAT
import com.example.hasanzian.newsapp.constants.NewsConstant.QUARRY_ORDER_BY
import com.example.hasanzian.newsapp.constants.NewsConstant.QUARRY_PAGE
import com.example.hasanzian.newsapp.constants.NewsConstant.QUARRY_PAGE_SIZE
import com.example.hasanzian.newsapp.constants.NewsConstant.QUARRY_SEARCH
import com.example.hasanzian.newsapp.constants.NewsConstant.QUARRY_SHOW_FIELDS
import com.example.hasanzian.newsapp.constants.NewsConstant.QUARRY_SHOW_TAGS
import com.example.hasanzian.newsapp.constants.NewsConstant.THUMBNAIL_VALUE


object UriUtils {
       @JvmStatic
        fun getNewsUri(
            query: String?,
            orderBy: String?,
            minPageSize: String?,
            pageNumber: Int
        ): String {
            val baseUri = Uri.parse(NEWS_REQ_URL)

            // buildUpon prepares the baseUri that we just parsed so we can add query parameters to it
            val uriBuilder = baseUri.buildUpon()

            // Append query parameter and its value. For example, the `format=json`
            uriBuilder.appendQueryParameter(QUARRY_SEARCH, query)
            uriBuilder.appendQueryParameter(QUARRY_FORMAT, FORMAT_VALUE)
            uriBuilder.appendQueryParameter(QUARRY_SHOW_TAGS, QUARRY_CONTRIBUTOR)
            uriBuilder.appendQueryParameter(QUARRY_SHOW_FIELDS, THUMBNAIL_VALUE)
            uriBuilder.appendQueryParameter(QUARRY_ORDER_BY, orderBy)
            uriBuilder.appendQueryParameter(QUARRY_PAGE_SIZE, minPageSize)
            uriBuilder.appendQueryParameter(QUARRY_PAGE, pageNumber.toString())
            uriBuilder.appendQueryParameter(QUARRY_API_KEY, API_KEY_VALUE)
            return uriBuilder.toString()
        }


}