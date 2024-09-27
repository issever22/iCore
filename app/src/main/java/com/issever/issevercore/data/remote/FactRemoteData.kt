package com.issever.issevercore.data.remote

import com.issever.core.base.BaseRemoteData
import com.issever.core.util.Resource
import com.issever.issevercore.data.model.CatFactsResponse
import com.issever.issevercore.data.service.FactService
import javax.inject.Inject

class FactRemoteData @Inject constructor(
    private val service: FactService
) : BaseRemoteData {

    suspend fun sampleFunction(): Resource<Unit> {
        return responseHandler({ service.getSampleFunction() }, entityConverter = { response ->
            // Here you can convert the response to the desired entity.
        }, doThenOnIO = { response ->
            // Here you can perform additional operations in the IO thread.
        })
    }

    suspend fun getFacts(page : Int): Resource<CatFactsResponse> {
        return responseHandler({ service.getFacts(page) })
    }

}