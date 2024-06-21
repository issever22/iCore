package com.issever.issevercore.ui.adapter

import android.content.Context
import com.issever.core.base.BaseAdapter
import com.issever.issevercore.data.model.CatFact
import com.issever.issevercore.databinding.ItemCatFactBinding

class CatFactsAdapter : BaseAdapter<CatFact,ItemCatFactBinding>(ItemCatFactBinding::inflate) {

    override fun bind(holder: BaseViewHolder, item: CatFact, context: Context) {
        holder.binding.model = item
    }

}