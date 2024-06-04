package com.issever.core.base

import android.util.Log
import com.issever.core.util.CoreConstants.CoreTag.RESPONSE_HANDLER_ERROR
import com.issever.core.util.CoreConstants.CoreTag.SAFE_CALL_ERROR
import com.issever.core.util.Errors.COMMON_ERROR
import com.issever.core.util.Errors.NO_INTERNET_CONNECTION
import com.issever.core.util.InternetConnectionHelper
import com.issever.core.util.Resource
import com.issever.core.util.extensions.handleError
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonSyntaxException
import com.issever.core.util.Errors.WENT_WRONG
import retrofit2.Response
import com.issever.core.data.initialization.IsseverCore

interface BaseRemoteData {

    /**
     * Safely executes a network call, ensuring that the device is connected to the internet
     * and handling any exceptions that might occur.
     *
     * @param call The suspend function that makes the network request.
     * @return A Resource containing the result of the network call or an error message.
     */
    private suspend fun <T> safeCall(call: suspend () -> Resource<T>): Resource<T> {
        return try {
            if (InternetConnectionHelper.isInternetOn()) {
                call()
            } else {
                Resource.error(NO_INTERNET_CONNECTION)
            }
        } catch (e: Exception) {
            Log.e(SAFE_CALL_ERROR, e.toString())
            Resource.error(e.handleError())
        }
    }

    /**
     * Handles the response from a network call, converting the response body to a desired entity,
     * and performing any additional actions on the converted entity.
     *
     * @param call The suspend function that makes the network request and returns a Response.
     * @param entityConverter A function that converts the response body to the desired entity.
     * @param doThen A suspend function to perform additional actions on the converted entity.
     * @return A Resource containing the converted entity or an error message.
     */
    private suspend fun <T, E> handleResponse(
        call: suspend () -> Response<T>,
        entityConverter: (T) -> E,
        doThen: suspend (E) -> Unit
    ): Resource<E> {

        val response = call.invoke()
        return if (response.isSuccessful) {
            val body = response.body()
            if (body != null) {
                val successData = entityConverter(body)
                doThen.invoke(successData)
                Resource.success(successData)
            } else {
                Resource.error(response.message() ?: COMMON_ERROR)
            }
        } else {
            Log.e(RESPONSE_HANDLER_ERROR, response.toString())
            val errorBody = response.errorBody()?.string()
            val errorMessage = if (errorBody != null) {
                try {
                    val gson = Gson()
                    val jsonObject = gson.fromJson(errorBody, JsonObject::class.java)
                    jsonObject[IsseverCore.getOptions().errorMessageField]?.asString ?: WENT_WRONG
                } catch (e: JsonSyntaxException) {
                    WENT_WRONG
                }
            } else {
                COMMON_ERROR
            }
            Log.e(RESPONSE_HANDLER_ERROR, errorMessage)
            Resource.error(errorMessage)
        }
    }

    /**
     * Makes a network request and handles the response, performing an additional action on the response body.
     *
     * @param call The suspend function that makes the network request and returns a Response.
     * @param doThen A suspend function to perform additional actions on the response body.
     * @return A Resource containing the response body or an error message.
     */
    suspend fun <T> responseHandler(
        call: suspend () -> Response<T>,
        doThen: suspend (T) -> Unit = {}
    ): Resource<T> {
        return safeCall {
            handleResponse(call, { it }, doThen)
        }
    }

    /**
     * Makes a network request, converts the response body to a desired entity, and handles the response,
     * performing an additional action on the converted entity.
     *
     * @param call The suspend function that makes the network request and returns a Response.
     * @param entityConverter A function that converts the response body to the desired entity.
     * @param doThen A suspend function to perform additional actions on the converted entity.
     * @return A Resource containing the converted entity or an error message.
     */
    suspend fun <T, E> responseHandler(
        call: suspend () -> Response<T>,
        entityConverter: (T) -> E,
        doThen: suspend (E) -> Unit = {}
    ): Resource<E> {
        return safeCall {
            handleResponse(call, entityConverter, doThen)
        }
    }
}
