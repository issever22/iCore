package com.issever.issevercore.data.repository

import com.issever.core.base.BaseRepository
import com.issever.core.util.Resource
import com.issever.issevercore.data.localData.LocalData
import com.issever.issevercore.data.model.CatFact
import com.issever.issevercore.data.model.CatFactsResponse
import com.issever.issevercore.data.remote.FactRemoteData
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class FactRepository @Inject constructor(
    private val remoteData: FactRemoteData
) : BaseRepository {

    suspend fun sampleFunction(): Flow<Resource<Unit>> {
        return emitResult({ remoteData.sampleFunction() }, doThenOnMain = { resource ->
            // Here you can perform additional operations on the Main Thread.
        })
    }

    suspend fun getFacts(): Flow<Resource<CatFactsResponse>> {
        return emitResult({ remoteData.getFacts() })
    }

    suspend fun getFavoriteCatFact(): Flow<Resource<CatFact>> {
        return emitResult({ LocalData.getFavoriteCatFact() })
    }

    suspend fun setFavoriteCatFact(catFact: CatFact): Flow<Resource<Unit>> {
        return emitResult({ LocalData.setFavoriteCatFact(catFact) })
    }

}