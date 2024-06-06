package com.issever.core.util.extensions

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Bitmap
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.issever.core.util.Errors.OPEN_ERROR
import java.io.ByteArrayOutputStream
import java.time.LocalDateTime
import java.util.Locale


fun Context.hasInternet(): Boolean {
    val connectivityManager =
        this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    val activeNetwork = connectivityManager.activeNetwork ?: return false

    val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false

    return when {
        capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
        capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
        capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
        else -> false
    }
}

fun Context.showToast(message: String, duration: Int = Toast.LENGTH_LONG) {
    Toast.makeText(this, message, duration).show()
}


fun Context.updateLocale(selectedLanguage: String): Context {
    val localeToSwitchTo = Locale(selectedLanguage)
    val configuration: Configuration = resources.configuration
    configuration.setLocale(localeToSwitchTo)
    return createConfigurationContext(configuration)
}


fun Context.openGooglePlay() {
    try {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse("market://details?id=$packageName")
        startActivity(intent)
    } catch (anfe: ActivityNotFoundException) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse("https://play.google.com/store/apps/details?id=$packageName")
        startActivity(intent)
    }
}

fun Context.openWebPage(url: String) {
    val webpage: Uri = Uri.parse(url)
    val intent = Intent(Intent.ACTION_VIEW, webpage)

    if (intent.resolveActivity(packageManager) != null) {
        startActivity(intent)
    } else {
        Toast.makeText(this, OPEN_ERROR, Toast.LENGTH_LONG).show()
    }
}

@RequiresApi(Build.VERSION_CODES.O)
fun Context.triggerSmallVibration(duration: Long = 100) {
    val vibrator = this.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    val effect = VibrationEffect.createOneShot(duration, VibrationEffect.DEFAULT_AMPLITUDE)
    vibrator.vibrate(effect)
}

@RequiresApi(Build.VERSION_CODES.O)
fun Context.showDateTimePicker(initialDateTime: LocalDateTime, onDateTimeSelected: (LocalDateTime) -> Unit) {
    val datePickerDialog = DatePickerDialog(this, { _, year, month, dayOfMonth ->
        val selectedDate = LocalDateTime.of(year, month + 1, dayOfMonth, 0, 0)
        TimePickerDialog(this, { _, hourOfDay, minute ->
            val selectedDateTime = selectedDate.withHour(hourOfDay).withMinute(minute)
            onDateTimeSelected(selectedDateTime)
        }, initialDateTime.hour, initialDateTime.minute, true).show()
    }, initialDateTime.year, initialDateTime.monthValue - 1, initialDateTime.dayOfMonth)

    datePickerDialog.show()
}

fun Context.compressImage(imagePath: String, quality: Int = 20, onComplete: (ByteArray) -> Unit) {
    Glide.with(this)
        .asBitmap()
        .load(imagePath)
        .into(object : CustomTarget<Bitmap>() {
            override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                val byteArrayOutputStream = ByteArrayOutputStream()
                resource.compress(Bitmap.CompressFormat.JPEG, quality, byteArrayOutputStream)
                val byteArray: ByteArray = byteArrayOutputStream.toByteArray()
                Log.e("Compressed File Size", "Compressed file size: ${byteArray.size / (1024 * 1024.0)} MB")
                onComplete(byteArray)
            }

            override fun onLoadCleared(placeholder: android.graphics.drawable.Drawable?) {
                Log.e("TAG", "onLoadCleared: COMPRESS LOAD CLEARED")
            }
        })
}

