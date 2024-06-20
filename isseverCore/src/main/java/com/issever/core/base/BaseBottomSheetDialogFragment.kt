package com.issever.core.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.view.isVisible
import androidx.viewbinding.ViewBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.issever.core.data.initialization.IsseverCore
import com.issever.core.util.extensions.showSnackbar

abstract class BaseBottomSheetDialogFragment<VB : ViewBinding,VM : BaseViewModel?> : BottomSheetDialogFragment() {

    private var _binding: VB? = null
    open val binding get() = _binding!!

    protected val currentActivity: BaseActivity<*, *> by lazy { requireActivity() as BaseActivity<*, *> }
    open val coreLocalData: BaseLocalData by lazy { IsseverCore.getBaseLocalData() }
    protected open val viewModel: VM? = null
    protected abstract fun initViewBinding(): VB
    protected open fun init() {}
    protected open fun initObservers() {}
    open var loadingView : View? = null
    open lateinit var behavior: BottomSheetBehavior<FrameLayout>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = initViewBinding()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val dialog = dialog as BottomSheetDialog
        val bottomSheet = dialog.findViewById<FrameLayout>(com.google.android.material.R.id.design_bottom_sheet)
        behavior = BottomSheetBehavior.from(bottomSheet!!)

        init()

        viewModel?.snackbarText?.observe(viewLifecycleOwner) {
            _binding?.root?.showSnackbar(it)
        }
        viewModel?.isLoading?.observe(viewLifecycleOwner) {
            loadingView?.isVisible = it
        }

        initObservers()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
