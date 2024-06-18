package com.issever.core.util

import com.issever.core.R


object CoreErrors  {

    val NO_INTERNET_CONNECTION: String
        get() = ResourceProvider.getString(R.string.no_internet_connection)

    val COMMON_ERROR: String
        get() = ResourceProvider.getString(R.string.common_error)

    val NETWORK_ERROR: String
        get() = ResourceProvider.getString(R.string.network_error)

    val WENT_WRONG: String
        get() = ResourceProvider.getString(R.string.went_wrong)

    val OPEN_ERROR: String
        get() = ResourceProvider.getString(R.string.open_error)

    val INVALID_URL: String
        get() = ResourceProvider.getString(R.string.invalid_url)

}