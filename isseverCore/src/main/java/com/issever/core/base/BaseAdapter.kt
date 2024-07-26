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

abstract class BaseAdapter<T : Any, VB : ViewBinding>(
    private val bindingInflater: (LayoutInflater, ViewGroup, Boolean) -> VB
) : RecyclerView.Adapter<BaseAdapter<T, VB>.BaseViewHolder>() {

    open val coreLocalData: BaseLocalData by lazy { IsseverCore.getBaseLocalData() }

    inner class BaseViewHolder(val binding: VB) : RecyclerView.ViewHolder(binding.root)

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
    protected fun getViewClickListener(): ((T, View) -> Unit)? = onItemViewClickListener

    private var onItemClickListener: ((T) -> Unit)? = null
    protected fun getClickListener(): ((T) -> Unit)? = onItemClickListener

    fun setOnItemViewClickListener(listener: (T, View) -> Unit) {
        onItemViewClickListener = listener
    }

    fun setOnItemClickListener(listener: (T) -> Unit) {
        onItemClickListener = listener
    }

    private var onItemViewLongClickListener: ((T, View) -> Unit)? = null
    protected fun getViewLongClickListener(): ((T, View) -> Unit)? = onItemViewLongClickListener

    private var onItemLongClickListener: ((T) -> Unit)? = null
    protected fun getLongClickListener(): ((T) -> Unit)? = onItemLongClickListener

    fun setOnItemViewLongClickListener(listener: (T, View) -> Unit) {
        onItemViewLongClickListener = listener
    }

    fun setOnItemLongClickListener(listener: (T) -> Unit) {
        onItemLongClickListener = listener
    }

    private var doubleClickTimeout: Long = 300L
    private var lastClickTime: Long = 0L

    private var onItemViewDoubleClickListener: ((T, View) -> Unit)? = null
    protected fun getViewDoubleClickListener(): ((T, View) -> Unit)? = onItemViewDoubleClickListener

    private var onItemDoubleClickListener: ((T) -> Unit)? = null
    protected fun getDoubleClickListener(): ((T) -> Unit)? = onItemDoubleClickListener

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
        bind(holder, item, holder.binding.root.context)

        setClickListenerForView(item, holder.binding.root)
    }

    private val originalClickListeners = mutableMapOf<View, View.OnClickListener?>()

    private val originalLongClickListeners = mutableMapOf<View, View.OnLongClickListener?>()

    private fun setClickListenerForView(item: T, view: View) {
        if (view.id != View.NO_ID || view.parent == null) {
            if (!originalClickListeners.containsKey(view)) {
                originalClickListeners[view] = try {
                    val field = View::class.java.getDeclaredField("mListenerInfo")
                    field.isAccessible = true
                    val listenerInfo = field.get(view)
                    val listenerField = listenerInfo.javaClass.getDeclaredField("mOnClickListener")
                    listenerField.isAccessible = true
                    listenerField.get(listenerInfo) as? View.OnClickListener
                } catch (e: Exception) {
                    null
                }
            }

            if (!originalLongClickListeners.containsKey(view)) {
                originalLongClickListeners[view] = try {
                    val field = View::class.java.getDeclaredField("mListenerInfo")
                    field.isAccessible = true
                    val listenerInfo = field.get(view)
                    val listenerField =
                        listenerInfo.javaClass.getDeclaredField("mOnLongClickListener")
                    listenerField.isAccessible = true
                    listenerField.get(listenerInfo) as? View.OnLongClickListener
                } catch (e: Exception) {
                    null
                }
            }

            view.setOnClickListener {
                originalClickListeners[view]?.onClick(view)

                val currentTime = System.currentTimeMillis()
                if (currentTime - lastClickTime < doubleClickTimeout) {
                    onItemViewDoubleClickListener?.invoke(item, view)
                    onItemDoubleClickListener?.invoke(item)
                } else {
                    onItemViewClickListener?.invoke(item, view)
                    onItemClickListener?.invoke(item)
                }
                lastClickTime = currentTime
            }

            view.setOnLongClickListener {
                originalLongClickListeners[view]?.onLongClick(view)
                onItemViewLongClickListener?.invoke(item, view)
                onItemLongClickListener?.invoke(item)
                true
            }
        }

        if (view is ViewGroup) {
            for (i in 0 until view.childCount) {
                setClickListenerForView(item, view.getChildAt(i))
            }
        }
    }

    abstract fun bind(holder: BaseViewHolder, item: T, context: Context)

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