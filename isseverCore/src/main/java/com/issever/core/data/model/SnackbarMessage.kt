package com.issever.core.data.model

import com.issever.core.data.enums.SnackbarType


data class SnackbarMessage(
    val message: String?= "",
    val type: SnackbarType? = null,
    val actionText: String? = "",
    val action: (() -> Unit)? = null
)