package com.issever.core.base

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.issever.core.data.initialization.IsseverCore

abstract class BaseAdapter<T: Any, VB: ViewBinding>(
    private val bindingInflater: (LayoutInflater, ViewGroup, Boolean) -> VB
) : RecyclerView.Adapter<BaseAdapter<T, VB>.BaseViewHolder>() {

    open val coreLocalData: BaseLocalData by lazy { IsseverCore.getBaseLocalData() }
    inner class BaseViewHolder(val binding: VB): RecyclerView.ViewHolder(binding.root)

    open val differ by lazy {
        AsyncListDiffer(this, object : DiffUtil.ItemCallback<T>() {
            override fun areItemsTheSame(oldItem: T, newItem: T): Boolean {
                return this@BaseAdapter.areItemsTheSame(oldItem, newItem)
            }

            override fun areContentsTheSame(oldItem: T, newItem: T): Boolean {
                return this@BaseAdapter.areContentsTheSame(oldItem, newItem)
            }
        })
    }

    private var onItemViewClickListener: ((T, View) -> Unit)? = null
    private var onItemClickListener: ((T) -> Unit)? = null

    fun setOnItemViewClickListener(listener: (T, View) -> Unit) {
        onItemViewClickListener = listener
    }

    fun setOnItemClickListener(listener: (T) -> Unit) {
        onItemClickListener = listener
    }

    private var onItemViewLongClickListener: ((T, View) -> Unit)? = null
    private var onItemLongClickListener: ((T) -> Unit)? = null

    fun setOnItemViewLongClickListener(listener: (T, View) -> Unit) {
        onItemViewLongClickListener = listener
    }

    fun setOnItemLongClickListener(listener: (T) -> Unit) {
        onItemLongClickListener = listener
    }

    private var doubleClickTimeout: Long = 300L

    private var onItemViewDoubleClickListener: ((T, View) -> Unit)? = null
    private var onItemDoubleClickListener: ((T) -> Unit)? = null

    fun setOnItemViewDoubleClickListener(listener: (T, View) -> Unit) {
        onItemViewDoubleClickListener = listener
    }

    fun setOnItemDoubleClickListener(listener: (T) -> Unit) {
        onItemDoubleClickListener = listener
    }

    open fun setDoubleClickTimeout(timeout: Long) {
        doubleClickTimeout = timeout
    }

    override fun getItemCount() = differ.currentList.size

    override fun getItemViewType(position: Int) = position

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = bindingInflater.invoke(inflater, parent, false)
        return BaseViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        val item = getItem(position)
        bind(holder, item,holder.binding.root.context)

        setClickListenerForView(item,holder.binding.root)
    }

    private fun setClickListenerForView(item: T, view: View) {
        var lastClickTime = 0L
        var clickCount = 0

        val clickRunnable = Runnable {
            if (clickCount == 1) {
                onItemViewClickListener?.invoke(item, view)
                onItemClickListener?.invoke(item)
            } else if (clickCount == 2) {
                onItemViewDoubleClickListener?.invoke(item, view)
                onItemDoubleClickListener?.invoke(item)
            }
            clickCount = 0
        }

        val isDoubleClickEnabled = onItemViewDoubleClickListener != null || onItemDoubleClickListener != null

        if (view.parent == null) {
            view.setOnClickListener {
                if (isDoubleClickEnabled) {
                    val currentTime = System.currentTimeMillis()
                    if (currentTime - lastClickTime < doubleClickTimeout) {
                        clickCount++
                        view.removeCallbacks(clickRunnable)
                    } else {
                        clickCount = 1
                    }
                    lastClickTime = currentTime
                    view.postDelayed(clickRunnable, doubleClickTimeout)
                } else {
                    onItemViewClickListener?.invoke(item, it)
                    onItemClickListener?.invoke(item)
                }
            }

            view.setOnLongClickListener {
                onItemViewLongClickListener?.invoke(item, it)
                onItemLongClickListener?.invoke(item)
                true
            }
        }

        if (view is ViewGroup) {
            for (i in 0 until view.childCount) {
                val child = view.getChildAt(i)
                setClickListenerForView(item, child)
            }
        } else {
            view.setOnClickListener {
                if (isDoubleClickEnabled) {
                    val currentTime = System.currentTimeMillis()
                    if (currentTime - lastClickTime < doubleClickTimeout) {
                        clickCount++
                        view.removeCallbacks(clickRunnable)
                    } else {
                        clickCount = 1
                    }
                    lastClickTime = currentTime
                    view.postDelayed(clickRunnable, doubleClickTimeout)
                } else {
                    onItemViewClickListener?.invoke(item, it)
                    onItemClickListener?.invoke(item)
                }
            }

            view.setOnLongClickListener {
                onItemViewLongClickListener?.invoke(item, it)
                onItemLongClickListener?.invoke(item)
                true
            }
        }
    }

    abstract fun bind(holder: BaseViewHolder,item: T, context: Context)

    open fun areItemsTheSame(oldItem: T, newItem: T): Boolean {
        return oldItem == newItem
    }

    open fun areContentsTheSame(oldItem: T, newItem: T): Boolean {
        return oldItem == newItem
    }

    open fun submitList(list: List<T>) {
        differ.submitList(list)
    }

    open fun getItem(position: Int): T {
        return differ.currentList[position]
    }

    open fun getAllItems(): List<T> {
        return differ.currentList
    }

    open fun addItem(item: T, position: Int = -1) {
        val updatedList = ArrayList(differ.currentList)
        if (position == -1) {
            updatedList.add(item)
        } else {
            updatedList.add(position, item)
        }
        submitList(updatedList)
    }

    open fun addItems(items: List<T>, position: Int = -1) {
        val updatedList = ArrayList(differ.currentList)
        if (position == -1) {
            updatedList.addAll(items)
        } else {
            updatedList.addAll(position, items)
        }
        submitList(updatedList)
    }

    open fun removeItem(position: Int) {
        val updatedList = ArrayList(differ.currentList)
        if (position >= 0 && position < updatedList.size) {
            updatedList.removeAt(position)
            submitList(updatedList)
        }
    }

    open fun removeItem(item: T) {
        val updatedList = ArrayList(differ.currentList)
        val position = updatedList.indexOf(item)
        if (position >= 0) {
            updatedList.removeAt(position)
            submitList(updatedList)
        }
    }

    open fun removeItems(items: Set<T>) {
        val updatedList = ArrayList(differ.currentList)
        updatedList.removeAll(items)
        submitList(updatedList)
    }

    open fun updateItem(position: Int, newItem: T) {
        val updatedList = ArrayList(differ.currentList)
        if (position >= 0 && position < updatedList.size) {
            updatedList[position] = newItem
            submitList(updatedList)
        }
    }

    open fun updateItem(oldItem: T, newItem: T) {
        val updatedList = ArrayList(differ.currentList)
        val position = updatedList.indexOf(oldItem)
        if (position >= 0) {
            updatedList[position] = newItem
            submitList(updatedList)
        }
    }

    open fun updateItems(items: List<T>) {
        val updatedList = ArrayList(differ.currentList)
        items.forEach { newItem ->
            val position = updatedList.indexOfFirst { it == newItem }
            if (position >= 0) {
                updatedList[position] = newItem
            }
        }
        submitList(updatedList)
    }

    open fun moveItem(fromPosition: Int, toPosition: Int) {
        val updatedList = ArrayList(differ.currentList)
        if (fromPosition in 0 until updatedList.size && toPosition in 0 until updatedList.size) {
            val item = updatedList.removeAt(fromPosition)
            updatedList.add(if (toPosition > fromPosition) toPosition - 1 else toPosition, item)
            submitList(updatedList)
        }
    }

    open fun sortItems(comparator: Comparator<T>) {
        val updatedList = ArrayList(differ.currentList)
        updatedList.sortWith(comparator)
        submitList(updatedList)
    }

    open fun filterItems(predicate: (T) -> Boolean): List<T> {
        val filteredList = differ.currentList.filter(predicate)
        submitList(filteredList)
        return filteredList
    }

    open fun swapItems(fromPosition: Int, toPosition: Int) {
        val updatedList = ArrayList(differ.currentList)
        if (fromPosition in 0 until updatedList.size && toPosition in 0 until updatedList.size) {
            val temp = updatedList[fromPosition]
            updatedList[fromPosition] = updatedList[toPosition]
            updatedList[toPosition] = temp
            submitList(updatedList)
        }
    }

    open fun swapItems(firstItem: T, secondItem: T) {
        val updatedList = ArrayList(differ.currentList)
        val firstPosition = updatedList.indexOf(firstItem)
        val secondPosition = updatedList.indexOf(secondItem)

        if (firstPosition in 0 until updatedList.size && secondPosition in 0 until updatedList.size) {
            val temp = updatedList[firstPosition]
            updatedList[firstPosition] = updatedList[secondPosition]
            updatedList[secondPosition] = temp
            submitList(updatedList)
        }
    }

    open fun scrollToItem(recyclerView: RecyclerView, item: T) {
        val position = differ.currentList.indexOf(item)
        if (position >= 0) {
            recyclerView.scrollToPosition(position)
        }
    }

    open fun findItem(predicate: (T) -> Boolean): T? {
        return differ.currentList.find(predicate)
    }

    open fun containsItem(item: T): Boolean {
        return differ.currentList.contains(item)
    }

    open fun clearItems() {
        submitList(emptyList())
    }
}