package com.issever.core.util

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton

abstract class EndlessRecyclerViewScrollListener(
    private val recyclerView: RecyclerView,
    private val layoutManager: RecyclerView.LayoutManager,
    private var visibleThreshold: Int = 5,
    swipeRefreshLayout: SwipeRefreshLayout? = null,
    private val returnToTopFab: FloatingActionButton? = null
) : RecyclerView.OnScrollListener() {

    private var currentPage = 1
    private var previousTotalItemCount = 0
    private var loading = true
    private val startingPageIndex = 1

    init {
        swipeRefreshLayout?.setOnRefreshListener {
            resetState()
            onRefresh()
        }

        returnToTopFab?.setOnClickListener {
            recyclerView.smoothScrollToPosition(0)
            returnToTopFab.hide()
        }
    }

    override fun onScrolled(view: RecyclerView, dx: Int, dy: Int) {
        val totalItemCount = layoutManager.itemCount
        val lastVisibleItemPosition = when (layoutManager) {
            is LinearLayoutManager -> layoutManager.findLastVisibleItemPosition()
            else -> throw IllegalStateException("Unsupported LayoutManager.")
        }

        if (totalItemCount < previousTotalItemCount) {
            this.currentPage = this.startingPageIndex
            this.previousTotalItemCount = totalItemCount
            if (totalItemCount == 0) {
                this.loading = true
            }
        }

        if (loading && (totalItemCount > previousTotalItemCount)) {
            loading = false
            previousTotalItemCount = totalItemCount
        }

        if (!loading && (lastVisibleItemPosition + visibleThreshold) > totalItemCount) {
            currentPage++
            onLoadMore(currentPage, totalItemCount, view)
            loading = true
        }

        returnToTopFab?.let {
            if (dy > 0 || dy < 0) {
                it.show()
            }

            if (!view.canScrollVertically(-1)) {
                it.hide()
            }
        }
    }

    private fun resetState() {
        this.currentPage = this.startingPageIndex
        this.previousTotalItemCount = 0
        this.loading = true
    }

    abstract fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView)
    abstract fun onRefresh()
}


