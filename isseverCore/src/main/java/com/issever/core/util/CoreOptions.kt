package com.issever.core.util

import com.issever.core.base.BaseLocalData
import com.issever.core.data.localData.CoreLocalData

/**
 * A class representing configuration options for the core of the application.
 * This class provides a way to configure various aspects of the core functionalities.
 */
class CoreOptions {

    /**
     * The class type of the local data implementation.
     * Default value is [CoreLocalData].
     */
    var localDataClass: Class<out BaseLocalData>? = CoreLocalData::class.java

    /**
     * The field name used to extract error messages from server responses.
     * Default value is "message".
     */
    var errorMessageField: String = "message"

    /**
     * A flag indicating whether Crashlytics is enabled.
     * Default value is false.
     */
    var isCrashlyticsEnabled: Boolean = false

    /**
     * Applies the given configuration to the [CoreOptions] instance.
     * This function allows the caller to configure the options using a lambda.
     *
     * @param configure A lambda function to configure the [CoreOptions] instance.
     * @return The configured [CoreOptions] instance.
     */
    fun apply(configure: CoreOptions.() -> Unit): CoreOptions {
        this.configure()
        return this
    }
}
