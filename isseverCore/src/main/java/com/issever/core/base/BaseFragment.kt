package com.issever.core.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.viewbinding.ViewBinding
import com.issever.core.data.initialization.IsseverCore
import com.issever.core.util.extensions.showSnackbar

abstract class BaseFragment<VB : ViewBinding, VM : BaseViewModel?> : Fragment() {

    private var _binding: VB? = null
    open val binding get() = _binding!!

    protected val currentActivity: BaseActivity<*, *> by lazy { requireActivity() as BaseActivity<*, *> }
    open val coreLocalData: BaseLocalData by lazy { IsseverCore.getBaseLocalData() }
    protected open val viewModel: VM? = null
    protected abstract fun initViewBinding(): VB
    protected open fun init() {}
    protected open fun initObservers() {}
    open var loadingView : View? = null
    open var backButton : View? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = initViewBinding()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()

        viewModel?.snackbarText?.observe(viewLifecycleOwner) {
            _binding?.root?.showSnackbar(it)
        }
        viewModel?.isLoading?.observe(viewLifecycleOwner) {
            loadingView?.isVisible = it
        }

        viewModel?.navigateBack?.observe(viewLifecycleOwner) {
            findNavController().navigateUp()
        }

        backButton?.setOnClickListener {
            findNavController().navigateUp()
        }

        initObservers()
    }

    override fun onDestroyView() {
        viewModel?.snackbarText?.removeObservers(viewLifecycleOwner)
        viewModel?.isLoading?.removeObservers(viewLifecycleOwner)
        super.onDestroyView()
        _binding = null
    }
}