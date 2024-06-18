package com.issever.core.base

import com.issever.core.util.Resource
import com.issever.core.util.extensions.handleError
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext

interface BaseRepository {

    /**
     * Emits a given Resource result as a Flow.
     *
     * @param result The Resource to emit.
     * @return A Flow emitting the given Resource.
     */
    fun <T> emitResult(
        result: Resource<T>
    ): Flow<Resource<T>> {
        return flow {
            emit(result)
        }.flowOn(Dispatchers.IO)
    }

    /**
     * Performs a suspend operation, emits the loading state, executes the operation,
     * and then emits the result as a Flow. If the result is successful, an optional
     * action can be performed.
     *
     * @param suspendOperation The suspend function that performs the operation and returns a Resource.
     * @param doThenOnMain An optional suspend function to perform additional actions on the result. This action will be performed on the main thread.
     * @return A Flow emitting the loading state followed by the result of the operation.
     */
    fun <T> emitResult(
        suspendOperation: suspend () -> Resource<T>,
        doThenOnMain: (suspend (Resource<T>) -> Unit)? = null
    ): Flow<Resource<T>> = flow {
        emit(Resource.loading())
        try {
            val result = suspendOperation()
            emit(result)
            doThenOnMain?.let {
                withContext(Dispatchers.Main) {
                    it(result)
                }
            }
        } catch (e: Exception) {
            val errorResource = Resource.error<T>(e.handleError())
            emit(errorResource)
        }
    }.flowOn(Dispatchers.IO)
}

