package com.issever.issevercore.ui.fragments.catFacts

import androidx.fragment.app.activityViewModels
import com.issever.core.base.BaseFragment
import com.issever.core.util.extensions.addOnEndlessScrollListener
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
    private var currentPage = 0

    override fun init() {
        super.init()
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        loadingView = binding.progressBar

        binding.rvFacts.adapter = adapter

        viewModel.getCatFacts(currentPage)

        // Adds an endless scroll listener to the RecyclerView, enabling infinite scrolling and pull-to-refresh functionality.
        binding.rvFacts.addOnEndlessScrollListener(
            // The minimum number of items below your current scroll position before loading more (optional, default is 5).
            visibleThreshold = 10,
            // FloatingActionButton used to scroll the list back to the top (optional).
            returnToTopFab = binding.fabReturnTop,
            // SwipeRefreshLayout that handles the pull-to-refresh gesture (optional).
            swipeRefreshLayout = binding.swpFacts,
            // Callback invoked when more data needs to be loaded (required).
            onLoadMore = { page, totalItemsCount, view ->
                // Update the current page number.
                currentPage = page
                // Fetch more cat facts based on the current page.
                viewModel.getCatFacts(currentPage)
            },
            // Callback invoked when a refresh is requested (optional).
            onRefresh = {
                // Reset to the first page.
                currentPage = 0
                // Fetch the initial set of cat facts.
                viewModel.getCatFacts(currentPage)
            }
        )


        // `view` is the specific View within the item layout that was clicked.
        // This allows you to perform actions or access properties of the clicked View directly.
        adapter.setOnItemViewClickListener { item, view ->
            when (view.id) {
                //R.id.someID -> Do something
                else -> viewModel.setFavoriteCatFact(item)
            }
        }
    }

    override fun initObservers() {
        super.initObservers()

        observe(viewModel.facts) {
            if (currentPage == 1) {
                adapter.submitList(it)
            } else {
                adapter.addItems(it)
            }
            binding.swpFacts.isRefreshing = false
        }

        observe(viewModel.showFavoriteFact) { fact ->
            currentActivity.showCustomDialog(
                title = getString(R.string.favorite_fact),
                message = fact.fact
            )
        }

    }

}