package com.issever.core.base

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.viewbinding.ViewBinding
import com.issever.core.data.initialization.IsseverCore
import com.issever.core.util.CoreConstants.CoreLocalData.FOLLOW_SYSTEM
import com.issever.core.util.extensions.setupFullScreenMode
import com.issever.core.util.extensions.showSnackbar
import com.issever.core.util.extensions.updateLocale


abstract class BaseActivity<VB : ViewBinding, VM : BaseViewModel?> : AppCompatActivity() {
    private var _binding: VB? = null
    open val binding get() = _binding!!

    open val coreLocalData: BaseLocalData by lazy { IsseverCore.getBaseLocalData() }
    protected open val viewModel: VM? = null
    protected abstract fun initViewBinding(): VB
    protected open fun init() {}
    protected open fun initObservers() {}
    open var loadingView: View? = null
    private lateinit var pickMedia: ActivityResultLauncher<PickVisualMediaRequest>

    override fun attachBaseContext(newBase: Context) {
        val selectedLanguage = coreLocalData.getSelectedLanguage()
        val language =
            if (selectedLanguage == FOLLOW_SYSTEM) coreLocalData.getInitialLocale() else selectedLanguage
        val updatedContext = newBase.updateLocale(language)
        super.attachBaseContext(updatedContext)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = initViewBinding()
        setContentView(binding.root)
        setupFullScreenMode()

        pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->

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


    override fun onDestroy() {
        viewModel?.snackbarText?.removeObservers(this)
        viewModel?.isLoading?.removeObservers(this)
        super.onDestroy()
    }
}
