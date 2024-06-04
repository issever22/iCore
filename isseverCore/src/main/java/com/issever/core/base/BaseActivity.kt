package com.issever.core.base

import android.content.Context
import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.viewbinding.ViewBinding
import com.issever.core.util.CoreConstants.CoreLocalData.FOLLOW_SYSTEM
import com.issever.core.data.initialization.IsseverCore
import com.issever.core.util.extensions.loadImage
import com.issever.core.util.extensions.setupFullScreenMode
import com.issever.core.util.extensions.showSnackbar
import com.issever.core.util.extensions.updateLocale


abstract class BaseActivity<VB : ViewBinding, VM : BaseViewModel?> : AppCompatActivity() {
    private var _binding: VB? = null
    val binding get() = _binding!!

    val localData: BaseLocalData by lazy { IsseverCore.getBaseLocalData() }
    protected open val viewModel: VM? = null
    protected abstract fun initViewBinding(): VB
    protected open fun init() {}
    protected open fun initObservers() {}
    open var loadingView: View? = null
    open var imageViewForMedia: ImageView? = null
    private lateinit var pickMedia: ActivityResultLauncher<PickVisualMediaRequest>

    override fun attachBaseContext(newBase: Context) {
        val selectedLanguage = localData.getSelectedLanguage()
        val language =
            if (selectedLanguage == FOLLOW_SYSTEM) localData.getInitialLocale() else selectedLanguage
        val updatedContext = newBase.updateLocale(language)
        super.attachBaseContext(updatedContext)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = initViewBinding()
        setContentView(binding.root)
        setupFullScreenMode()

        pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri != null) {

            } else {
                onMediaPickFailed(Exception("No media selected"))
            }
        }

        viewModel?.snackbarText?.observe(this) {
            _binding?.root?.showSnackbar(it)
        }

        viewModel?.isLoading?.observe(this) {
            loadingView?.isVisible = it
        }

        init()
        initObservers()
    }

    protected open fun onMediaPicked(bitmap: Bitmap? = null, url: String? = null) {
        if (bitmap == null && url == null) {
            return
        } else {
            when {
                url != null -> {
                    imageViewForMedia?.loadImage(url)
                }
                bitmap != null -> {
                    imageViewForMedia?.setImageBitmap(bitmap)
                }

            }
        }
    }

    protected open fun onMediaPickFailed(exception: Exception) {
        // Medya seçimi başarısız olduğunda yapılacak işlemler.
        // Örneğin hata mesajı gösterme.
    }

    protected fun launchMediaPicker() {
        pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    /*open fun showImagePickerDialog(onlyFromGallery: Boolean = false) {
        if (onlyFromGallery) {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        } else {
            showCustomBottomSheet(
                layoutResId = R.layout.choose_image_soruce,
                clickableResIds = listOf(
                    R.id.chooseImageFromGallery,
                    R.id.chooseImageFromUrl
                ),
                clickListeners = listOf(
                    { pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) },
                    {
                        showEditTextDialog(
                            icon = R.drawable.ic_add_image,
                            title = getString(R.string.add_image),
                            message = getString(R.string.add_image_message),
                            hint = getString(R.string.https),
                            onPositiveClick = { url ->
                                url.isValidAndFormatUrl({
                                    viewModel?.setImageUrl(it)
                                }, {
                                    viewModel?.showSnackbar(it)
                                })
                            }
                        )
                    }
                )
            )
        }

    }*/

    override fun onDestroy() {
        viewModel?.snackbarText?.removeObservers(this)
        viewModel?.isLoading?.removeObservers(this)
        super.onDestroy()
    }
}
