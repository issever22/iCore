plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt")
    id("androidx.navigation.safeargs.kotlin")
    id("maven-publish")
}

android {
    namespace = "com.issever.core"
    compileSdk = 34

    defaultConfig {
        minSdk = 23
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        getByName("debug") {
            isMinifyEnabled = false
            resValue("bool", "IS_DEBUG", "true")
        }
        getByName("release") {
            isMinifyEnabled = false
            resValue("bool", "IS_DEBUG", "false")
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        viewBinding = true
        dataBinding = true
    }

    publishing {
        singleVariant("release") {
            withSourcesJar()
            withJavadocJar()
        }
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.12.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    // Secure shared preferences
    val securityVersion = "1.1.0-alpha06"
    implementation("androidx.security:security-crypto-ktx:$securityVersion")

    // Glide
    val glideVersion = "4.15.1"
    implementation("com.github.bumptech.glide:glide:$glideVersion")
    kapt("com.github.bumptech.glide:compiler:$glideVersion")

    // Retrofit
    val retrofitVersion = "2.9.0"
    implementation("com.squareup.retrofit2:retrofit:$retrofitVersion")
    implementation("com.squareup.retrofit2:converter-gson:$retrofitVersion")
    implementation("com.squareup.retrofit2:converter-moshi:$retrofitVersion")
    implementation("com.squareup.retrofit2:converter-scalars:$retrofitVersion")

    // Moshi
    val moshiVersion = "1.15.0"
    implementation("com.squareup.moshi:moshi-kotlin:$moshiVersion")
    kapt("com.squareup.moshi:moshi-kotlin-codegen:$moshiVersion")

    // OkHttp
    val okHttpVersion = "5.0.0-alpha.2"
    implementation("com.squareup.okhttp3:logging-interceptor:$okHttpVersion")

    // Navigation
    val navVersion = "2.6.0"
    implementation("androidx.navigation:navigation-fragment-ktx:$navVersion")
    implementation("androidx.navigation:navigation-runtime-ktx:$navVersion")
    implementation("androidx.navigation:navigation-ui-ktx:$navVersion")

}

kapt {
    correctErrorTypes = true
}

afterEvaluate {
    publishing {
        publications {
            create<MavenPublication>("release") {
                from(components["release"])
                groupId = "com.github.issever22"
                artifactId = "iCore"
                version = "1.0.4"

                pom {
                    name.set("iCore")
                    description.set("A core module containing base classes, extensions, and utility classes for Android applications.")
                    url.set("https://github.com/issever22/iCore")

                    licenses {
                        license {
                            name.set("The Apache License, Version 2.0")
                            url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                        }
                    }
                    developers {
                        developer {
                            id.set("issever22")
                            name.set("Muhammed Issever")
                            email.set("issever.dev@gmail.com")
                        }
                    }
                    scm {
                        connection.set("scm:git:github.com/issever22/iCore.git")
                        developerConnection.set("scm:git:ssh://github.com/issever22/iCore.git")
                        url.set("https://github.com/issever22/iCore")
                    }
                }
            }
        }
        repositories {
            maven {
                url = uri("$buildDir/outputs/repo")
            }
        }
    }
}
