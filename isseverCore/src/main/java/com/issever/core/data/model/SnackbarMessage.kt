package com.issever.core.data.model

import com.issever.core.data.enums.StateType


data class SnackbarMessage(
    val message: String?= "",
    val type: StateType? = null,
    val actionText: String? = "",
    val action: (() -> Unit)? = null
)