package com.issever.issevercore.data.service

import com.issever.issevercore.data.model.CatFactsResponse
import retrofit2.Response
import retrofit2.http.GET

interface FactService {

    @GET("facts")
    suspend fun getFacts(): Response<CatFactsResponse>

    // This endpoint will return an error
    @GET("sample")
    suspend fun getSampleFunction(): Response<Unit>

}