package com.issever.core.util.extensions

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.annotation.RequiresApi
import com.issever.core.util.Errors.COMMON_ERROR
import com.issever.core.util.Errors.NETWORK_ERROR
import com.issever.core.util.Errors.WENT_WRONG
import java.io.Serializable
import java.net.SocketTimeoutException
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Date
import java.util.Locale

fun Exception?.handleError(): String {
    return if (this is SocketTimeoutException) {
        NETWORK_ERROR
    } else {
        if (this?.message == "" || this?.message == " ") {
            COMMON_ERROR
        } else {
            this?.message?: WENT_WRONG
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
fun LocalDateTime.toFormattedDateWithClock(): String {
    val formatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM, FormatStyle.SHORT)
        .withLocale(Locale.getDefault())
    return this.format(formatter)
}

@RequiresApi(Build.VERSION_CODES.O)
fun Date.toZonedDateTime(): ZonedDateTime {
    return Instant.ofEpochMilli(this.time).atZone(ZoneId.systemDefault())
}

fun Any.delayed(action: () -> Unit) {
    val delayMillis: Long? = when (this) {
        is Int -> this * 1000L
        is Long -> this * 1000
        is Float -> (this * 1000).toLong()
        is Double -> (this * 1000).toLong()
        is String -> this.toNumericMillis()
        else -> null
    }

    if (delayMillis != null) {
        Handler(Looper.getMainLooper()).postDelayed({ action() }, delayMillis)
    } else {
        Log.e("Delayed Function", "Unsupported type: ${this::class.java.name}")
    }
}

@Suppress("DEPRECATION")
inline fun <reified T : Serializable> Bundle.customGetSerializable(key: String): T? {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        getSerializable(key, T::class.java)
    } else {
        getSerializable(key) as? T
    }
}

fun ByteArray.toBitmap(): Bitmap {
    return BitmapFactory.decodeByteArray(this, 0, size)
}
