package com.issever.issevercore.ui.fragments.catFacts

import android.util.Log
import androidx.fragment.app.activityViewModels
import com.issever.core.base.BaseFragment
import com.issever.core.util.extensions.observe
import com.issever.core.util.extensions.showCustomDialog
import com.issever.issevercore.R
import com.issever.issevercore.databinding.FragmentCatFactsBinding
import com.issever.issevercore.ui.adapter.CatFactsAdapter
import com.issever.issevercore.ui.viewModel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CatFactsFragment : BaseFragment<FragmentCatFactsBinding, MainViewModel>() {

    override val viewModel: MainViewModel by activityViewModels()

    override fun initViewBinding() = FragmentCatFactsBinding.inflate(layoutInflater)

    private val adapter = CatFactsAdapter()

    override fun init() {
        super.init()
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        loadingView = binding.progressBar

        viewModel.getCatFacts()

        binding.rvFacts.adapter = adapter

        // `view` is the specific View within the item layout that was clicked.
        // This allows you to perform actions or access properties of the clicked View directly.
        adapter.setOnItemClickListener { item, view ->
            viewModel.setFavoriteCatFact(item)
        }
    }

    override fun initObservers() {
        super.initObservers()

        observe(viewModel.facts) {
            adapter.submitList(it)
        }

        observe(viewModel.showFavoriteFact) { fact ->
            currentActivity.showCustomDialog(
                title = getString(R.string.favorite_fact),
                message = fact.fact
            )
        }

    }

}