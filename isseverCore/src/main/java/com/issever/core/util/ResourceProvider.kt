package com.issever.core.util

import android.app.Application
import android.content.Context
import android.graphics.drawable.Drawable
import androidx.annotation.BoolRes
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.DimenRes
import androidx.annotation.DrawableRes
import androidx.annotation.IntegerRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat

/**
 * A utility class to provide resources in a way that is decoupled from the Android Context.
 *
 * This class abstracts Android's resource handling system, enabling resources to be retrieved
 * without requiring a direct reference to a Context. This is particularly useful in situations
 * where a Context isn't readily available or where passing a Context might lead to memory leaks,
 * such as in ViewModels or Repositories.
 *
 * By using this class instead of directly accessing the Context, we ensure that our code is
 * more modular and testable. This allows for easier mocking of this class in unit tests,
 * enhancing testability and separation of concerns.
 */
object ResourceProvider {
    private var context: Application? = null

    fun init(application: Application) {
        context = application
    }

    private fun getContext(): Application {
        return context ?: throw IllegalStateException("ResourceProvider is not initialized. Call init() with Application context.")
    }

    fun getAppContext(): Context = getContext().applicationContext

    fun getString(@StringRes resId: Int): String = getContext().getString(resId)

    fun getString(@StringRes resId: Int, vararg formatArgs: Any): String = getContext().getString(resId, *formatArgs)

    fun getDrawable(@DrawableRes resId: Int): Drawable? = ContextCompat.getDrawable(getContext(), resId)

    @ColorInt
    fun getColor(@ColorRes resId: Int): Int = ContextCompat.getColor(getContext(), resId)

    fun getDimension(@DimenRes resId: Int): Float = getContext().resources.getDimension(resId)

    fun getBoolean(@BoolRes resId: Int): Boolean = getContext().resources.getBoolean(resId)

    fun getInteger(@IntegerRes resId: Int): Int = getContext().resources.getInteger(resId)
}

