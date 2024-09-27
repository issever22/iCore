package com.issever.core.util.extensions

import android.animation.Animator
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.content.Context
import android.graphics.Color
import android.transition.Slide
import android.transition.Transition
import android.transition.TransitionManager
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bumptech.glide.Glide
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.issever.core.R
import com.issever.core.data.enums.StateType
import com.issever.core.data.model.SnackbarMessage
import com.issever.core.util.EndlessRecyclerViewScrollListener
import com.issever.core.util.ResourceProvider

fun RecyclerView.addOnEndlessScrollListener(
    visibleThreshold: Int = 5,
    swipeRefreshLayout: SwipeRefreshLayout? = null,
    returnToTopFab: FloatingActionButton? = null,
    onLoadMore: (page: Int, totalItemsCount: Int, view: RecyclerView) -> Unit,
    onRefresh: (() -> Unit)? = null
): EndlessRecyclerViewScrollListener {
    val layoutManager = this.layoutManager
        ?: throw IllegalStateException("RecyclerView needs a LayoutManager")

    val scrollListener = object : EndlessRecyclerViewScrollListener(
        this,
        layoutManager,
        visibleThreshold,
        swipeRefreshLayout,
        returnToTopFab
    ) {
        override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView) {
            onLoadMore(page, totalItemsCount, view)
        }

        override fun onRefresh() {
            onRefresh?.invoke()
        }
    }
    this.addOnScrollListener(scrollListener)
    return scrollListener
}


fun View.clickAnim() {
    this.animate()
        .scaleX(0.9f)
        .scaleY(0.9f)
        .setDuration(100)
        .withEndAction {
            this.animate()
                .scaleX(1f)
                .scaleY(1f)
                .setDuration(100)
        }
}


fun View.slideInFromRight(onEnd: (() -> Unit)? = null) {
    val slideInAnimation = Slide(Gravity.END).apply {
        duration = 300
        addTarget(this@slideInFromRight)
    }
    visibility = View.INVISIBLE

    post {
        TransitionManager.beginDelayedTransition(rootView as ViewGroup, slideInAnimation)
        visibility = View.VISIBLE
    }

    // Do something after the animation ends
    slideInAnimation.addListener(object : Transition.TransitionListener {
        override fun onTransitionEnd(transition: Transition) {
            onEnd?.invoke()
        }

        override fun onTransitionResume(transition: Transition) {}
        override fun onTransitionPause(transition: Transition) {}
        override fun onTransitionCancel(transition: Transition) {}
        override fun onTransitionStart(transition: Transition) {}
    })
}

fun View.slideOutToLeft(onEnd: (() -> Unit)? = null) {
    val slideOutAnimation = Slide(Gravity.START).apply {
        duration = 300
        addTarget(this@slideOutToLeft)
    }

    post {
        TransitionManager.beginDelayedTransition(rootView as ViewGroup, slideOutAnimation)
        visibility = View.GONE
    }

    // Do something after the animation ends
    slideOutAnimation.addListener(object : Transition.TransitionListener {
        override fun onTransitionEnd(transition: Transition) {
            onEnd?.invoke()
        }

        override fun onTransitionResume(transition: Transition) {}
        override fun onTransitionPause(transition: Transition) {}
        override fun onTransitionCancel(transition: Transition) {}
        override fun onTransitionStart(transition: Transition) {}
    })
}

fun ImageView.likeAnimation(
    isLiked: Boolean,
    onLikeStart: (() -> Unit)? = null,
    onDislikeStart: (() -> Unit)? = null,
    @DrawableRes likeDrawable: Int? = null,
    @DrawableRes dislikeDrawable: Int? = null,
) {
    val duration = 500L
    val scaleDown = PropertyValuesHolder.ofFloat(View.SCALE_X, 0.5f)
    val scaleDownY = PropertyValuesHolder.ofFloat(View.SCALE_Y, 0.5f)
    val rotation = PropertyValuesHolder.ofFloat(View.ROTATION, 180f)

    val animator = ObjectAnimator.ofPropertyValuesHolder(this, rotation, scaleDown, scaleDownY)
    animator.duration = duration / 2
    animator.repeatCount = 1
    animator.repeatMode = ObjectAnimator.REVERSE

    animator.addListener(object : Animator.AnimatorListener {
        override fun onAnimationStart(animation: Animator) {
            if (isLiked) {
                onDislikeStart?.invoke()
            } else {
                onLikeStart?.invoke()
            }
        }

        override fun onAnimationEnd(animation: Animator) {
            if (isLiked) {
                this@likeAnimation.setImageResource(likeDrawable ?: R.drawable.ic_heart)
            } else {
                this@likeAnimation.setImageResource(dislikeDrawable ?: R.drawable.ic_heart_selected)
            }
        }

        override fun onAnimationCancel(animation: Animator) {}

        override fun onAnimationRepeat(animation: Animator) {}
    })

    animator.start()
}

fun View.toggleWithAnimation(show: Boolean, duration: Long = 200, directionUp: Boolean = false) {
    if (show) {
        if (!isVisible) {
            isVisible = true
            animate().translationY(0f).setDuration(duration).start()
        }
    } else {
        if (isVisible) {
            val translationValue = if (directionUp) -height.toFloat() else height.toFloat()
            animate().translationY(translationValue).setDuration(duration).withEndAction {
                isVisible = false
            }.start()
        }
    }
}

fun View.toggleSlideAnimation(
    show: Boolean,
    duration: Long = 200,
    distance: Float = 300f
) {
    if (show) {
        if (!isVisible) {
            translationX = -distance
            isVisible = true
            animate().translationX(0f).setDuration(duration).start()
        }
    } else {
        if (isVisible) {
            animate().translationX(distance).setDuration(duration).withEndAction {
                isVisible = false
            }.start()
        }
    }
}


fun ImageView.loadImage(
    source: Any,
    overrideWidth: Int? = null,
    overrideHeight: Int? = null,
    quality: Int? = null,
    placeHolder: Boolean = false,
    @DrawableRes errorPlaceholder: Int? = null,
    showLoading: Boolean = false
) {
    val glide = Glide.with(context).load(source)

    if (showLoading){
        glide.placeholder(CircularProgressDrawable(context).apply {
            strokeWidth = 8f
            centerRadius = 40f
            start()
        })
    }

    if (overrideWidth != null && overrideHeight != null) {
        glide.override(overrideWidth, overrideHeight)
    }
    if (placeHolder) {
        glide.placeholder(R.drawable.ic_image)
    }

    if (errorPlaceholder != null) {
        glide.error(errorPlaceholder)
    }

    if (quality != null) {
        glide.encodeQuality(quality)
    }

    glide.into(this)

}


fun View.showSnackbar(message: SnackbarMessage) {
    val snackbar = Snackbar.make(this, message.message.toString(), Snackbar.LENGTH_LONG)
    val textView =
        snackbar.view.findViewById<TextView>(com.google.android.material.R.id.snackbar_text)
    val actionView =
        snackbar.view.findViewById<Button>(com.google.android.material.R.id.snackbar_action)
    snackbar.setTextColor(ContextCompat.getColor(context, R.color.c_midnight))

    with(snackbar.view) {
        when (message.type) {
            StateType.SUCCESS -> snackbar.setTheme(
                R.color.c_green,
                textView,
                R.drawable.ic_check
            )

            StateType.ERROR -> snackbar.setTheme(
                R.color.c_red,
                textView,
                R.drawable.ic_error
            )

            StateType.INFO -> snackbar.setTheme(
                R.color.c_blue,
                textView,
                R.drawable.ic_info
            )

            StateType.WARNING -> snackbar.setTheme(
                R.color.c_orange,
                textView,
                R.drawable.ic_warning
            )

            else -> {
                setBackgroundColor(ContextCompat.getColor(context, R.color.c_grey_tank))
            }
        }
    }

    if (!message.actionText.isNullOrEmpty() && message.action != null) {
        actionView.apply {
            visibility = View.VISIBLE
            if (message.type == StateType.DEFAULT){
                setTextColor(ContextCompat.getColor(context, R.color.c_midnight))
            }else{
                setTextColor(Color.WHITE)
            }
            text = message.actionText
            setOnClickListener { message.action.invoke() }
        }
    } else {
        actionView.visibility = View.GONE
    }
    hideKeyboard()
    snackbar.show()

}

fun Snackbar.setTheme(color: Int, textView: TextView, @DrawableRes icon: Int) {
    setBackgroundTint(ContextCompat.getColor(context, color))
    val drawable = ContextCompat.getDrawable(textView.context, icon)
    drawable?.setTint(Color.WHITE)
    setTextColor(Color.WHITE)
    textView.setCompoundDrawablesRelativeWithIntrinsicBounds(drawable, null, null, null)
    textView.compoundDrawablePadding =
        textView.resources.getDimensionPixelSize(R.dimen.snackbar_icon_padding)
    textView.gravity = GravityCompat.START
}

fun View.showKeyboard() {
    this.requestFocus()
    val imm = ResourceProvider.getAppContext()
        .getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
}

fun View.hideKeyboard() {
    val imm = ResourceProvider.getAppContext()
        .getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(this.windowToken, 0)
}

