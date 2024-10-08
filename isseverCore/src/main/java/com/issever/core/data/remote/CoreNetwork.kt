package com.issever.core.data.remote

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.issever.core.R
import com.issever.core.util.CoreConstants.CoreRemote.APPLICATION_JSON
import com.issever.core.util.CoreConstants.CoreRemote.CONTENT_TYPE
import com.issever.core.util.ResourceProvider
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object CoreNetwork {

    val gson: Gson by lazy {
        GsonBuilder().setLenient().create()
    }

    private val gsonConverterFactory: GsonConverterFactory by lazy {
        GsonConverterFactory.create(gson)
    }

    private val loggingInterceptor: HttpLoggingInterceptor by lazy {
        val loggingInterceptor = HttpLoggingInterceptor()
        if (ResourceProvider.getBoolean(R.bool.IS_DEBUG)) {
            loggingInterceptor.apply { level = HttpLoggingInterceptor.Level.BODY }
        }
        return@lazy loggingInterceptor
    }

    private fun createClient(headers: Map<String, String>): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor { chain ->
                val original = chain.request()
                val requestBuilder = original.newBuilder()
                    .header(CONTENT_TYPE, APPLICATION_JSON)
                headers.forEach { (key, value) ->
                    requestBuilder.header(key, value)
                }
                val request = requestBuilder.method(original.method, original.body).build()
                chain.proceed(request)
            }
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .build()
    }

    fun provideRetrofit(baseUrl: String, headers: Map<String, String> = emptyMap()): Retrofit {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(gsonConverterFactory)
            .client(createClient(headers))
            .build()
    }
}


