# iCore

iCore is a library designed to simplify Android application development. It provides a comprehensive and standardized structure for applications using the MVVM (Model-View-ViewModel) architecture.

- **Reducing Code Duplication:** By abstracting commonly used operations, it prevents the writing of repetitive code.
  
- **Quick Start:** With ready-to-use core components, it allows for a quick start to projects.
  
- **Easy Integration:** Easily integrates common operations like Retrofit, LiveData observation, and theme/language selection.
  
- **Reactive Data Management with Kotlin Flow and LiveData:** iCore manages asynchronous data streams using Kotlin Flow and handles UI updates with LiveData, offering a more reactive and modern data processing model.
  
- **Extensive Extensions:** Its extensible structure allows for customization according to application needs.
  
- **Centralized Management:** Provides centralized management by allowing easy access to application resources with `ResourceProvider`.


## Sample Application
To better understand how to use the iCore library and to see its architecture in action, check out the sample application included in the app module of this repository.

You can find the sample application [here](./app).

## Installation

### Gradle

Add the following line to your project's `build.gradle` file:

```gradle
allprojects {
    repositories {
        ...
        maven { url = uri("https://jitpack.io") }
    }
}
```

Add the dependency to your module's `build.gradle` file:


[![](https://jitpack.io/v/issever22/iCore.svg)](https://jitpack.io/#issever22/iCore)


```gradle
dependencies {
    implementation("com.github.issever22:iCore:v1.0.9")
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

    fun sampleFunction() {
        collectData({
            // The function to be executed to fetch data from the repository
            repository.sampleFunction()
        }, successAction = {
            // Code to be executed when the data fetching is successful
            // This is typically where you would handle the successful response
        }, errorAction = { message, errorBody ->
            // Code to be executed when there is an error in fetching data
            // Here you can handle the error
        }, loadingAction = {
            // Code to be executed while the data is being loaded
        }, snackbarType = SnackbarType.DEFAULT,
            actionText = "Action",
            snackBarAction = {
                // Action to be performed when the snackbar action is triggered
                // This is typically used for actions that the user can perform directly from the snackbar, such as retrying a failed operation
            })
    }
}
```

### BaseAdapter

#### Adapter Class

Create your adapter by extending `BaseAdapter`:

```kotlin
class YourAdapter : BaseAdapter<YourModel, ItemYourBinding>(ItemYourBinding::inflate) {

    override fun bind(holder: BaseViewHolder, item: YourModel, context: Context) {
        holder.apply {
            binding.tvYourTextView.text = item.someProperty
        }
    }
}
```

#### Activity or Fragment
Use your adapter in an Activity or Fragment:
```kotlin
val adapter = YourAdapter()
binding.recyclerView.adapter = adapter
adapter.submitList(yourList)

// `view` is the specific View within the item layout that was clicked.
// This allows you to perform actions or access properties of the clicked View directly.
adapter.setOnItemClickListener { item, view ->
    // Do something with item or view
}
adapter.setOnItemLongClickListener { item, view ->
    // Do something with item or view
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
        return responseHandler({ service.login(user) }
    }

    // If needed, you can add additional actions or convert the response body to the desired entity.
    suspend fun login(user: SomeRequestModel): Resource<SomeResponseConvertedEntity> {
        return responseHandler({ service.login(user) }, entityConverter = { response ->
            // Here you can convert the response to the desired entity.
        }, doThenOnIO = { response ->
            // Here you can perform additional operations in the IO thread.
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

    // If needed, you can add additional actions.
    suspend fun login(user: SomeRequestModel): Flow<Resource<SomeResponseModel>> {
        return emitResult({ remoteData.login(user) }, doThenOnMain = { resource ->
            // Here you can perform additional operations on the Main Thread.
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

#### Theme Selection

```kotlin
currentActivity.showThemeChoiceDialog()
```

#### Language Selection

```kotlin
private val languages = mapOf(
    "English" to "en",
    "Türkçe" to "tr"
)

currentActivity.showLanguageChoiceDialog(languages)
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


For a detailed list of changes, please refer to the [CHANGELOG.md](./CHANGELOG.md) file.


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
