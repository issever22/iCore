package com.issever.core.util

import com.issever.core.data.enums.ResourceStatus

data class Resource<out T>(
    val data: T? = null,
    val message: String? = null,
    val status: ResourceStatus
) {

    companion object {
        fun <T> success(data: T? = null, message: String? = null): Resource<T> {
            return Resource(data, message, ResourceStatus.SUCCESS)
        }

        fun <T> error(message: String?, data: T? = null): Resource<T> {
            return Resource(data, message, ResourceStatus.ERROR)
        }

        fun <T> warning(message: String?, data: T? = null): Resource<T> {
            return Resource(data, message, ResourceStatus.WARNING)
        }

        fun <T> info(message: String?, data: T? = null): Resource<T> {
            return Resource(data, message, ResourceStatus.INFO)
        }

        fun <T> loading(data: T? = null): Resource<T> {
            return Resource(data, null, ResourceStatus.LOADING)
        }
    }
}