package com.issever.issevercore.ui.fragments.home

import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.issever.core.base.BaseFragment
import com.issever.core.util.extensions.observe
import com.issever.core.util.extensions.showChoiceDialog
import com.issever.core.util.extensions.showLanguageChoiceDialog
import com.issever.core.util.extensions.showThemeChoiceDialog
import com.issever.issevercore.databinding.FragmentHomeBinding
import com.issever.issevercore.ui.fragments.home.viewModel.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding, HomeViewModel>() {

    override fun initViewBinding() = FragmentHomeBinding.inflate(layoutInflater)

    override val viewModel: HomeViewModel by viewModels()

    private val languages = mapOf(
        "English" to "en",
        "Türkçe" to "tr",
    )

    override fun init() {
        super.init()
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
    }

    override fun initObservers() {
        super.initObservers()
        observe(viewModel.navigateToCatFacts){
            findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToCatFactsFragment())
        }

        observe(viewModel.showThemeDialog){
            currentActivity.showThemeChoiceDialog()
        }

        observe(viewModel.showLanguageDialog){
            currentActivity.showLanguageChoiceDialog(languages)
        }
    }

}