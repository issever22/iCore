package com.issever.issevercore

import android.app.Application
import com.issever.core.data.initialization.IsseverCore
import com.issever.core.util.CoreOptions
import com.issever.issevercore.data.localData.LocalData
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class IsseverCoreApp : Application() {

    override fun onCreate() {
        super.onCreate()
        val coreOptions = CoreOptions().apply {
            localDataClass = LocalData::class.java
            errorMessageField = "message"
        }

        IsseverCore.init(this, coreOptions)
    }
}