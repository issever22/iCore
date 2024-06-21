package com.issever.issevercore.data.localData

import com.issever.core.base.BaseLocalData
import com.issever.core.util.Resource
import com.issever.core.util.ResourceProvider
import com.issever.issevercore.R
import com.issever.issevercore.data.model.CatFact
import com.issever.issevercore.utils.Constants.FAVORITE_CAT_FACTS

object LocalData : BaseLocalData() {

    suspend fun setFavoriteCatFact(catFact: CatFact) : Resource<Unit> {
        return databaseOperation({
            setJsonData(FAVORITE_CAT_FACTS, catFact)
        }, successMessage = ResourceProvider.getString(R.string.cat_fact_mark_favorite))
    }

    suspend fun getFavoriteCatFact(): Resource<CatFact> {
        return databaseOperation({
            getJsonData(FAVORITE_CAT_FACTS, CatFact::class.java)
        })
    }
}