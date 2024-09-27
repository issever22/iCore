package com.issever.core.util.extensions

import android.os.Build
import android.util.Patterns
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.issever.core.data.enums.StateType
import com.issever.core.data.model.SnackbarMessage
import com.issever.core.util.CoreErrors.INVALID_URL
import com.issever.core.util.ResourceProvider
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.Locale
import java.util.regex.Pattern

fun String?.emptyIfNull(): String {
    return when {
        this.isNullOrEmpty() -> ""
        else -> this
    }
}

fun String?.showToast(duration: Int = Toast.LENGTH_SHORT) {
    this?.let {
        Toast.makeText(ResourceProvider.getAppContext(), it, duration).show()
    }
}

fun String?.isValidEmailAddress(): Boolean {
    val regex = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$"
    val pattern = Pattern.compile(regex)
    val matcher = pattern.matcher(this.toString())
    return matcher.matches()
}

fun String.isValidAndFormatUrl(onValidUrl: (String) -> Unit, onError: (SnackbarMessage) -> Unit) {
    val isValid = Patterns.WEB_URL.matcher(this).matches()
    if (isValid) {
        val formattedUrl = when {
            this.startsWith("https://") -> this
            this.startsWith("http://") -> this
            else -> "https://${this}"
        }
        onValidUrl(formattedUrl)
    } else {
        val message = SnackbarMessage(
            INVALID_URL,
            StateType.WARNING
        )
        onError(message)
    }
}

fun String?.trimBrackets(): String {
    return this?.replace(Regex("[()]"), "") ?: ""
}

fun String.capitalizeFirstLetter(): String {
    return this.substring(0, 1).uppercase() + this.substring(1)
}

@RequiresApi(Build.VERSION_CODES.O)
fun String.convertToSystemDateFormat(): String {
    val sourceFormats = listOf(
        DateTimeFormatter.ofPattern("yyyy-MM-dd"),
        DateTimeFormatter.ofPattern("dd/MM/yyyy")
    )

    var date: LocalDate? = null
    for (sourceFormat in sourceFormats) {
        try {
            date = LocalDate.parse(this, sourceFormat)
            break
        } catch (e: DateTimeParseException) {
            continue
        }
    }

    if (date == null) {
        return this
    }

    val targetFormat = java.text.DateFormat.getDateInstance(java.text.DateFormat.SHORT, Locale.getDefault())
    val utilDate = java.util.Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant())
    return targetFormat.format(utilDate)
}

fun String.toNumericMillis(): Long? {
    return try {
        (this.toDouble() * 1000).toLong()
    } catch (e: NumberFormatException) {
        null
    }
}


