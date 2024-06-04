package com.issever.core.base

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.issever.core.data.initialization.IsseverCore

abstract class BaseAdapter<T: Any, VB: ViewBinding>(
    private val itemCallback: DiffUtil.ItemCallback<T>
) : RecyclerView.Adapter<BaseAdapter<T, VB>.BaseViewHolder>() {

    val localData: BaseLocalData by lazy { IsseverCore.getBaseLocalData() }

    inner class BaseViewHolder(val binding: VB): RecyclerView.ViewHolder(binding.root)

    protected val differ = AsyncListDiffer(this, itemCallback)

    override fun getItemCount() = differ.currentList.size

    abstract fun createBinding(inflater: LayoutInflater, parent: ViewGroup): VB

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = createBinding(inflater, parent)
        return BaseViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        bind(holder.binding, differ.currentList[position])
    }

    abstract fun bind(binding: VB, item: T)
}

