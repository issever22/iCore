package com.issever.core.data.remote

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.issever.core.R
import com.issever.core.util.CoreConstants.CoreRemote.APPLICATION_JSON
import com.issever.core.util.CoreConstants.CoreRemote.CONTENT_TYPE
import com.issever.core.util.ResourceProvider
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

object CoreNetwork {

    val gson: Gson by lazy {
        GsonBuilder().setLenient().create()
    }

    val moshi: Moshi by lazy {
        Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
    }

    private val moshiConverterFactory: MoshiConverterFactory by lazy {
        MoshiConverterFactory.create(moshi)
    }

    private val gsonConverterFactory: GsonConverterFactory by lazy {
        GsonConverterFactory.create(gson)
    }

    private val loggingInterceptor: HttpLoggingInterceptor by lazy {
        HttpLoggingInterceptor().apply {
            if (ResourceProvider.getBoolean(R.bool.IS_DEBUG)) {
                level = HttpLoggingInterceptor.Level.BODY
            }
        }
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
            .addConverterFactory(moshiConverterFactory)
            .addConverterFactory(gsonConverterFactory)
            .client(createClient(headers))
            .build()
    }
}


