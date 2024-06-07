package com.issever.core.data.initialization

import android.app.Application
import com.issever.core.base.BaseLocalData
import com.issever.core.data.localData.CoreLocalData
import com.issever.core.util.CoreOptions
import com.issever.core.util.ResourceProvider

/**
 * A singleton object representing the core functionalities of the application.
 * This object provides methods to initialize core components and access configuration options.
 */
object IsseverCore {

    // The instance of BaseLocalData
    private var baseLocalData: BaseLocalData? = null

    // The configuration options for the core
    private lateinit var options: CoreOptions

    /**
     * Initializes the core components of the application with the given options.
     *
     * @param application The Application instance.
     * @param coreOptions The configuration options for initializing the core components.
     */
    fun init(application: Application, coreOptions: CoreOptions = CoreOptions()) {
        options = coreOptions
        ResourceProvider.init(application)

        // LocalData initialization
        baseLocalData = coreOptions.localDataClass?.let {
            val instance = it.getDeclaredConstructor().newInstance()
            instance.init(application)
            instance.saveInitialLocale()
            instance.getSelectedTheme().setTheme()
            instance
        } ?: CoreLocalData.apply {
            init(application)
            saveInitialLocale()
            getSelectedTheme().setTheme()
        }

        if (coreOptions.isCrashlyticsEnabled) {
            // Initialize Crashlytics
        }
    }

    /**
     * Returns the initialized instance of BaseLocalData.
     *
     * @return The instance of BaseLocalData.
     * @throws IllegalStateException if core is not initialized.
     */
    @Suppress("UNCHECKED_CAST")
    fun <T : BaseLocalData> getBaseLocalData(): T {
        return baseLocalData as? T ?: throw IllegalStateException("IsseverCore is not initialized")
    }

    /**
     * Returns the configuration options used to initialize the core components.
     *
     * @return The CoreOptions instance.
     */
    fun getOptions(): CoreOptions {
        return options
    }
}




