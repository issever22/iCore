package com.issever.core.util.extensions

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.DrawableRes
import androidx.annotation.LayoutRes
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isGone
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.issever.core.R
import com.issever.core.databinding.DialogCustomBinding

fun AppCompatActivity.showCustomDialog(
    title: String,
    message: String,
    positiveButtonText: String? = null,
    negativeButtonText: String? = null,
    onPositiveClick: (() -> Unit)? = null,
    onNegativeClick: (() -> Unit)? = null,
    useRedPositiveButton: Boolean = false,
    customizeView: (DialogCustomBinding) -> Unit = {},
) {
    val dialogView = DialogCustomBinding.inflate(layoutInflater)
    val dialog = AlertDialog.Builder(this)
        .setCancelable(false)
        .setView(dialogView.root).create()
    dialog.window?.setBackgroundDrawableResource(R.color.transparent)
    dialogView.apply {
        if (useRedPositiveButton) {
            btnPositive.setBackgroundResource(R.drawable.bg_red_alpha)
            btnPositive.setTextColor(getColor(R.color.c_red))
        } else {
            btnPositive.setBackgroundResource(R.drawable.bg_green_alpha)
            btnPositive.setTextColor(getColor(R.color.c_green))
        }
        tvTitle.text = title
        tvMessage.text = message
        btnPositive.text = positiveButtonText ?: getString(R.string.ok)
        btnNegative.text = negativeButtonText ?: getString(R.string.cancel)
        btnPositive.setOnClickListener {
            onPositiveClick?.invoke()
            dialog.dismiss()
        }
        btnNegative.setOnClickListener {
            onNegativeClick?.invoke()
            dialog.dismiss()
        }
        customizeView(dialogView)
    }
    dialog.show()
}

fun AppCompatActivity.showPopupWithAction(
    message: String,
    title: String = "",
    isSuccess: Boolean = false,
    secondButtonText: String = "",
    firstButtonText: String,
    callback: (isFirstButtonClick: Boolean) -> Unit
) {

    val binding = DialogCustomBinding.inflate(this.layoutInflater)

    val builder = AlertDialog.Builder(this, R.style.CustomAlertDialog)

    builder.setView(binding.root)

    builder.setCancelable(false)

    val dialog = builder.create()

    if (dialog.window != null){
        dialog.window!!.setBackgroundDrawable(ColorDrawable(0))
    }

    if (isSuccess) {
        binding.ivIcon.setImageResource(R.drawable.ic_check)
    } else {
        binding.ivIcon.setImageResource(R.drawable.ic_info)
    }

    if (title == "") {
        binding.tvTitle.isGone = true
        binding.tvMessage.text = message
    } else {
        binding.tvTitle.isGone = false
        binding.tvTitle.text = title
        binding.tvMessage.text = message
    }

    binding.btnPositive.text = firstButtonText
    binding.btnNegative.text = secondButtonText

    binding.btnNegative.isGone = secondButtonText == ""

    binding.btnNegative.setOnClickListener {
        callback.invoke(false)
        dialog.dismiss()
    }

    binding.btnPositive.setOnClickListener {
        callback.invoke(true)
        dialog.dismiss()
    }

    dialog.show()
}


fun AppCompatActivity.showSimpleDialog(
    title: String? = null,
    message: String? = null,
    positiveButtonText: String? = null,
    negativeButtonText: String? = null,
    onPositiveClick: (() -> Unit)? = {},
    onNegativeClick: (() -> Unit)? = {}
) {
    MaterialAlertDialogBuilder(this)
        .setTitle(title ?: "")
        .setMessage(message ?: "")
        .setPositiveButton(positiveButtonText ?: getString(R.string.ok)) { _, _ ->
            onPositiveClick?.invoke()
        }
        .setNegativeButton(negativeButtonText ?: getString(R.string.cancel)) { _, _ ->
            onNegativeClick?.invoke()
        }
        .create()
        .show()
}


fun AppCompatActivity.takePicture(requestCode: Int) {
    val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
    startActivityForResult(cameraIntent, requestCode)
}

fun AppCompatActivity.showKeyboard(view: View) {
    view.requestFocus()
    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
}

fun AppCompatActivity.hideKeyboard(view: View) {
    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(view.windowToken, 0)
}

fun <T> AppCompatActivity.showChoiceDialog(
    map: Map<String, T>,
    selectedKey: String,
    title: String,
    setSelected: (T) -> Unit
) {
    val keys = map.keys.toTypedArray()
    val selected = map.entries.find { it.value.toString() == selectedKey }?.key ?: keys[0]
    val checkedItem = keys.indexOf(selected)

    val builder = AlertDialog.Builder(this)
    builder.setTitle(title)

    builder.setSingleChoiceItems(keys, checkedItem) { dialog, which ->
        val chosenKey = keys[which]
        val chosenValue = map[chosenKey] ?: map.values.first()
        setSelected(chosenValue)
        this.recreate()
        dialog.dismiss()
    }

    builder.setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
        dialog.dismiss()
    }

    builder.show()
}

inline fun <reified T> AppCompatActivity.showChoiceDialog(
    map: Map<String, T>,
    selectedValue: T? = null,
    title: String,
    crossinline setSelected: (String, T) -> Unit
) {
    val keys = map.keys.toTypedArray()
    val values = map.values.toTypedArray()
    val checkedItem = selectedValue?.let { values.indexOf(it) } ?: -1

    val builder = AlertDialog.Builder(this)
    builder.setTitle(title)
    val showingValues = values.map { it.toString() }.toTypedArray()

    builder.setSingleChoiceItems(showingValues, checkedItem) { dialog, which ->
        val chosenKey = keys[which]
        val chosenValue = values[which]
        setSelected(chosenKey, chosenValue)
        dialog.dismiss()
    }

    builder.setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
        dialog.dismiss()
    }

    builder.show()
}

inline fun <reified T> AppCompatActivity.showChoiceDialog(
    list: List<T>,
    selectedValue: T? = null,
    title: String,
    crossinline setSelected: (T) -> Unit
) {
    val checkedItem = selectedValue?.let { list.indexOf(it) } ?: -1

    val builder = AlertDialog.Builder(this)
    builder.setTitle(title)
    val showingValues = list.map { it.toString() }.toTypedArray()

    builder.setSingleChoiceItems(showingValues, checkedItem) { dialog, which ->
        val chosenValue = list[which]
        setSelected(chosenValue)
        dialog.dismiss()
    }

    builder.setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
        dialog.dismiss()
    }

    builder.show()
}

fun AppCompatActivity.showCustomBottomSheet(
    @LayoutRes layoutResId: Int,
    clickableResIds: List<Int>,
    clickListeners: List<() -> Unit>
) {
    if (clickableResIds.size != clickListeners.size) {
        Log.e("showCustomBottomSheet", "clickableResIds and clickListeners must have same size")
        return
    }

    val bottomSheetDialog = BottomSheetDialog(this)
    val sheetView = layoutInflater.inflate(layoutResId, null)
    bottomSheetDialog.setContentView(sheetView)

    clickableResIds.forEachIndexed { index, resId ->
        sheetView.findViewById<View>(resId).setOnClickListener {
            bottomSheetDialog.dismiss()
            clickListeners[index]()
        }
    }

    bottomSheetDialog.show()
}

fun AppCompatActivity.showEditTextDialog(
    title: String? = null,
    message: String? = null,
    hint: String? = null,
    @DrawableRes icon: Int? = null,
    positiveButtonText: String? = null,
    negativeButtonText: String? = null,
    onPositiveClick: (String) -> Unit? = {},
    onNegativeClick: () -> Unit? = {}
) {
    val builder = AlertDialog.Builder(this)
    val dialogView = layoutInflater.inflate(R.layout.custom_edittext_dialog, null)
    val editText = dialogView.findViewById<EditText>(R.id.dialogEditText)
    editText.hint = hint ?: ""
    if (icon != null) builder.setIcon(icon)
    builder.setView(dialogView)
    builder.setTitle(title ?: "")
    builder.setMessage(message ?: "")
    builder.setPositiveButton(positiveButtonText ?: getString(R.string.ok)) { dialog, _ ->
        val input = editText.text.toString()
        onPositiveClick.invoke(input)
        dialog.dismiss()
    }
    builder.setNegativeButton(negativeButtonText ?: getString(R.string.cancel)) { dialog, _ ->
        onNegativeClick.invoke()
        dialog.dismiss()
    }

    val dialog = builder.create()

    dialog.setOnShowListener {
        0.1.delayed {
            showKeyboard(editText)
        }
    }
    dialog.show()
}


fun AppCompatActivity.navigateToActivity(
    targetActivity: Class<*>,
    finishActivity: Boolean? = false,
    bundle: Bundle? = null
) {
    val intent = Intent(this, targetActivity)
    bundle?.let {
        intent.putExtras(it)
    }
    this.startActivity(intent)
    if (finishActivity == true) this.finish()
}

fun AppCompatActivity.openAppSettings() {
    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
    with(intent) {
        data = Uri.fromParts("package", this@openAppSettings.packageName, null)
        addCategory(Intent.CATEGORY_DEFAULT)
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
        addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)
    }
    startActivity(intent)
}

fun AppCompatActivity.openAppSettings(packageName: String) {
    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
    with(intent) {
        data = Uri.fromParts("package", packageName, null)
    }
    startActivity(intent)
}

fun AppCompatActivity.openPackageUsageStatsSetting() {
    val intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)
    with(intent) {
        data = Uri.fromParts("package", packageName, null)
    }
    startActivity(intent)
}

@RequiresApi(Build.VERSION_CODES.R)
fun AppCompatActivity.openManageStorageSetting() {
    val intent = Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION)
    startActivity(intent)
}

fun AppCompatActivity.openWriteSetting() {
    val intent = Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS)
    with(intent) {
        data = Uri.fromParts("package", packageName, null)
    }
    startActivity(intent)
}


fun AppCompatActivity.disableTouch() {
    this.window.setFlags(
        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
    )
}

fun AppCompatActivity.enableTouch() {
    this.window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
}

fun AppCompatActivity.setupFullScreenMode() {
    supportActionBar?.hide()
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        window.insetsController?.hide(WindowInsets.Type.statusBars())
    } else {
        @Suppress("DEPRECATION")
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
    }
}

fun AppCompatActivity.exitFullScreenMode() {
    supportActionBar?.show()
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        window.insetsController?.show(WindowInsets.Type.statusBars())
    } else {
        @Suppress("DEPRECATION")
        window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
    }
}


fun AppCompatActivity.isAppInstalled(packageName: String): Boolean {
    return try {
        this.packageManager.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES)
        true
    } catch (e: PackageManager.NameNotFoundException) {
        false
    }
}

fun AppCompatActivity.openApp(packageName: String) {
    if (isAppInstalled(packageName)) {
        val launchIntent = packageManager.getLaunchIntentForPackage(packageName)
        if (launchIntent != null) {
            startActivity(launchIntent)
        }
    } else {
        val uri = Uri.parse("market://details?id=$packageName")
        val goToMarket = Intent(Intent.ACTION_VIEW, uri)
        startActivity(goToMarket)
    }
}

fun AppCompatActivity.isServiceRunning(serviceClass: Class<*>): Boolean {
    val activityManager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
    return activityManager.getRunningServices(Integer.MAX_VALUE).any { it.service.className == serviceClass.name }
}

fun AppCompatActivity.startServiceIfNeeded(serviceIntent: Intent,serviceClass: Class<*>) {
    if (!isServiceRunning(serviceClass)) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(serviceIntent)
        }else{
            startService(serviceIntent)
        }
    }
}

fun AppCompatActivity.pickAndCompressImage(
    imageView: ImageView? = null,
    imageLoadedCallback: ((Bitmap) -> Unit)? = null,
) {
    registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        uri?.let {
            compressImage(it.toString(), onComplete = { byteArray ->
                val bitmap = byteArray.toBitmap()
                imageView?.loadImage(bitmap)
                imageLoadedCallback?.invoke(bitmap)
            })
        }
    }.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
}

fun AppCompatActivity.pickImage(
    imageView: ImageView? = null,
    imageLoadedCallback: ((Uri) -> Unit)? = null,
) {
    registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        uri?.let {
            imageView?.loadImage(it)
            imageLoadedCallback?.invoke(it)
        }
    }.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
}






