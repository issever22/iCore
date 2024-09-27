package com.issever.issevercore.data.service

import com.issever.issevercore.data.model.CatFactsResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface FactService {

    @GET("facts")
    suspend fun getFacts(
        @Query("page") page: Int,
    ): Response<CatFactsResponse>

    // This endpoint will return an error
    @GET("sample")
    suspend fun getSampleFunction(): Response<Unit>

}