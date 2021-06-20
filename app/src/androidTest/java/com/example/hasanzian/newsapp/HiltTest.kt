package com.example.hasanzian.newsapp

import com.example.hasanzian.newsapp.constants.NewsConstant
import com.example.hasanzian.newsapp.network.NewsApi
import com.example.hasanzian.newsapp.repository.NewsRepository
import com.google.gson.Gson
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockWebServer
import org.checkerframework.checker.nullness.qual.AssertNonNullIfNonNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Inject
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThat
import org.robolectric.res.android.Asset

@HiltAndroidTest
class HiltTest {
    @get:Rule
    val hiltRule = HiltAndroidRule(this)
    private  var mockWebServer: MockWebServer = MockWebServer()

    @Inject
    lateinit var gson: Gson
    lateinit var tmdbService: NewsApi


    @Before
    fun setUp() {
        hiltRule.inject()

        tmdbService =  Retrofit.Builder()
            .baseUrl(NewsConstant.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build().create(NewsApi::class.java)
    }

    @Test
    fun testGson() {
        assertEquals(true,true)

        val repository = NewsRepository(tmdbService)

        //repository.viewState.observe(view ,list ->)

        val runBlocking = runBlocking {
            repository.get()
        }

       /* val results = runBlocking
        val size = results?.size
        println("List : "+ results?.size)
        if (results != null) {
            for (i in results){
                println("News : "+i.webTitle)
            }
        }
        assertEquals(size,10)*/
    }


}