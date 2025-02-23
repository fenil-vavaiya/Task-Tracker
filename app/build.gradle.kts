plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)

    alias(libs.plugins.google.services)

    id("com.google.dagger.hilt.android")
    kotlin("kapt")

    id("kotlin-parcelize")
}

android {
    namespace = "com.example.googletaskproject"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.googletaskproject"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // auth
    implementation(libs.play.services.auth) // Google Sign-In SDK

    // sdp
    implementation(libs.sdp.android)
    implementation(libs.ssp.android)

    // google.gson
    implementation(libs.google.gson)

    // permissionX
    implementation(libs.permissionx)

    // joda-time
    implementation(libs.joda.time)

    implementation (libs.glide)

    // Hilt Dependencies
    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)

    // Hilt with Navigation (For Fragments)
    implementation(libs.androidx.hilt.navigation.fragment)

    // ViewModel & LiveData
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)

    // Activity KTX (For ViewModel delegation)
    implementation(libs.androidx.activity.ktx)

    // Room
    implementation(libs.androidx.room.runtime)
    kapt(libs.androidx.room.compiler)

    // Kotlin Extensions and Coroutines support for Room
    implementation(libs.androidx.room.ktx)

    implementation(libs.androidx.fragment.ktx)

    // Firebase BoM (always use the latest version)
    implementation(platform("com.google.firebase:firebase-bom:33.9.0"))

    // Firestore Database
    implementation("com.google.firebase:firebase-firestore-ktx")


    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.1")
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.1")




}