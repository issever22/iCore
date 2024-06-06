# iCore

iCore is a library that provides modular and reusable components for Android applications, supporting the MVVM architecture. It includes base classes, extension functions, and various utility classes to reduce code duplication and speed up the development process.

## Key Features

- **Modular Structure**: Easy to integrate and use in any Android project.
- **MVVM Support**: Built-in support for Model-View-ViewModel architecture.
- **Base Classes**: Provides base classes for activities, fragments, view models, and more.
- **Extension Functions**: Includes useful extension functions to simplify common tasks.
- **Utility Classes**: Provides utility classes for network operations, local data management, and more.

## Installation

### Gradle

Add the following line to your project's `build.gradle` file:

```gradle
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}
```

Add the dependency to your module's `build.gradle` file:

```gradle
dependencies {
    implementation 'com.github.issever22:iCore:1.0.0' // Use the latest version
}
```

## Getting Started

Initialize the iCore library in your application's `Application` class:

```kotlin
class YourAppClass : Application() {

    override fun onCreate() {
        super.onCreate()
        val coreOptions = CoreOptions().apply {
            localDataClass = YourLocalData::class.java // Default is CoreLocalData
            errorMessageField = "errorMessage" // Default is "message"
        }

        IsseverCore.init(this, coreOptions)
    }
}
```

### Note:
Using `CoreOptions` is optional. If you do not provide any options, default options will be used. If you provide a `localDataClass`, it must extend `BaseLocalData`.

Example `LocalData` class:

```kotlin
object YourLocalData : BaseLocalData() {

    suspend fun someFunctions() : Resource<SomeModel> {
        // This method utilizes a core function from iCore to perform a database operation
        return databaseOperation({
            getStringData("some_data")
        })
    }
}

```

## Basic Usage

### BaseActivity

Create your activities by extending `BaseActivity`:

```kotlin
class YourActivity : BaseActivity<ActivityYourBinding, YourViewModel>() {

    override fun initViewBinding() = ActivityYourBinding.inflate(layoutInflater)
    override val viewModel: YourViewModel by lazy {
        ViewModelProvider(this)[YourViewModel::class.java]
    }

    override fun init() {
        super.init()
        // Additional initialization here
    }
}
```

#### Note: If your activity or fragment does not require a ViewModel, you can use Nothing in place of YourViewModel. For example:

```kotlin
class YourActivity : BaseActivity<ActivityYourBinding, Nothing>() {

    override fun initViewBinding() = ActivityYourBinding.inflate(layoutInflater)

    override fun init() {
        super.init()
        // Additional initialization here
    }
}
```

### BaseFragment

Create your fragments by extending `BaseFragment`:

```kotlin
class YourFragment : BaseFragment<FragmentYourBinding, YourViewModel>() {
    override fun initViewBinding() = FragmentYourBinding.inflate(layoutInflater)
    override val viewModel: YourViewModel by lazy {
        // currentActivity is a property provided by BaseFragment from iCore
        ViewModelProvider(currentActivity)[YourViewModel::class.java]
    }

    override fun init() {
        super.init()
        // Set the LifecycleOwner for LiveData observation
        binding.lifecycleOwner = this

        // If needed, set the loading view to be shown during loading states
        loadingView = binding.progressBar

        // If needed, set the back button to navigate up when pressed if needed
        backButton = binding.yourBackButtonImageView
    }

    override fun initObservers() {
        super.initObservers()
        // This is an extension function provided by iCore to observe LiveData
        observe(viewModel.navigateToSomewhere) {
            if (it) {
                // This is an extension function provided by iCore to navigation
                navigateToActivity(
                    SomeOtherActivity::class.java, // Required
                    finishActivity = true, // Default is false
                    bundle = null // Default is null
                )
            }
        }
    }
}
```

### BaseViewModel

Create your ViewModels by extending `BaseViewModel`:

```kotlin
class YourViewModel(
    private val repository: YourRepository
) : BaseViewModel() {

    private val _navigateToMain = MutableLiveData<Boolean>()
    val navigateToMain: MutableLiveData<Boolean>
        get() = _navigateToMain

    fun login() {
        collectData(
            {
                repository.login(SomeRequestModel()))
            }, successAction = {
                showSuccessSnackbar(R.string.login_success)
                _navigateToMain.value = true
            }
        )
    }
}
```

### Retrofit Setup

To create a Retrofit instance using iCore, follow these steps:

#### Network Module

Initialize Retrofit in a singleton object or a class. You can add headers if needed.

```kotlin
object RetrofitInstance {

    private val headers = mapOf("SomeKey" to "SomeValue")

    val retrofit: Retrofit by lazy {
        CoreNetwork.provideRetrofit("https://yourapi.baseurl.com/", headers)
    }
}
```

### Remote Data

Create a remote data source class by extending `BaseRemoteData`:

```kotlin
class SomeRemoteData(private val service: SomeService) : BaseRemoteData {

    suspend fun login(user: SomeRequestModel): Resource<SomeResponseModel> {
        return responseHandler { service.login(user) }
    }

    // If needed, you can add additional actions to be performed on a successful response.
    suspend fun login(user: SomeRequestModel): Resource<SomeResponseModel> {
        return responseHandler({ service.login(user) }, doThen = {
            // Do something after success
        })
    }

    // If needed, you can convert the response body to the desired entity.
    suspend fun login(user: SomeRequestModel): Resource<SomeResponseConvertedEntity> {
        return responseHandler({ service.login(user) }, entityConverter = { response ->
            // Convert SomeResponseModel to SomeResponseConvertedEntity
            SomeResponseConvertedEntity(response.someField, response.anotherField)
        })
    }
}
```

### Repository

Create a repository class by extending `BaseRepository`:

```kotlin
class SomeRepository(private val remoteData: SomeRemoteData) : BaseRepository {

    suspend fun login(user: SomeRequestModel): Flow<Resource<SomeResponseModel>> {
        return emitResult({ remoteData.login(user) })
    }

    // If needed, you can add additional actions to be performed on a successful response.
    suspend fun login(user: SomeRequestModel): Flow<Resource<SomeResponseModel>> {
        return emitResult({ remoteData.login(user) }, { resource ->
            resource.data?.let { YourLocalData.someAction(it) }
        })
    }
}
```


## Extension Functions

### Example Usages

iCore provides various useful extension functions. Here are a few examples:

#### Observing LiveData

```kotlin
observe(viewModel.someLiveData) {
    // Handle the observed data
}
```

#### Loading Images

```kotlin
myImageView.loadImage("ImageUrl, ImageUri or ImageSource")
```

#### Custom Dialog

```kotlin
 showCustomDialog(
            title = "Title", // Required
            message = "Message", // Required
            positiveButtonText = "Positive Button", //Default is "Okay"
            negativeButtonText = "Negative Button", // Default is "Cancel"
            useRedPositiveButton = true, //Default is false
            onPositiveClick = {
                // Do something
            },
            onNegativeClick = {
                // Do something
            },
            customizeView = { dilogViewBindig ->
                // Do something
            })
```

For a complete list of extension functions and their usage, please refer to the source code.


## Accessing Application Context

You can use `ResourceProvider` to access the application context and resources from anywhere in your application:

```kotlin
// Get application context
val appContext = ResourceProvider.getAppContext()

// Get a string resource
val myString = ResourceProvider.getString(R.string.my_string)

// Get a drawable resource
val myDrawable = ResourceProvider.getDrawable(R.drawable.my_drawable)

// Get a color resource
val myColor = ResourceProvider.getColor(R.color.my_color)

// Get a dimension resource
val myDimension = ResourceProvider.getDimension(R.dimen.my_dimension)

// Get a boolean resource
val myBoolean = ResourceProvider.getBoolean(R.bool.my_boolean)

// Get an integer resource
val myInteger = ResourceProvider.getInteger(R.integer.my_integer)
```

## Contributing

If you would like to contribute to this project, please follow these steps:

1. Fork this repository
2. Create a new branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -am 'Add an amazing feature'`)
4. Push your branch (`git push origin feature/amazing-feature`)
5. Create a new Pull Request

## License

This project is licensed under the [Apache 2.0 License](LICENSE).

Your feedback and contributions are valuable. Happy coding!

---

<p align="center">
  <b>Made with ❤️ by Muhammed Issever</b><br>
  <a href="mailto:muhammed@issever.co">muhammed@issever.co</a> |
  <a href="mailto:isseverdev@gmail.com">isseverdev@gmail.com</a><br><br>
  <a href="https://www.issever.co">
    <img src="https://issever.co/images/isseverCoLogo.png" width="200">
  </a>
</p> 
