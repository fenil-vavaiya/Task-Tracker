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
    implementation("com.google.android.gms:play-services-auth:21.1.1") // Google Sign-In SDK

    // sdp
    implementation("com.intuit.sdp:sdp-android:1.1.1")
    implementation("com.intuit.ssp:ssp-android:1.1.1")

    // androidx.security
    implementation("androidx.security:security-crypto:1.1.0-alpha06")

    // google.gson
    implementation("com.google.code.gson:gson:2.12.1")

    // permissionX
    implementation("com.guolindev.permissionx:permissionx:1.8.0")

    // joda-time
    implementation("joda-time:joda-time:2.12.7")

    implementation (libs.glide)

    // Hilt Dependencies
    implementation("com.google.dagger:hilt-android:2.50")
    kapt("com.google.dagger:hilt-compiler:2.50")

    // Hilt with Navigation (For Fragments)
    implementation("androidx.hilt:hilt-navigation-fragment:1.0.0")

    // ViewModel & LiveData
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.1")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.1")

    // Activity KTX (For ViewModel delegation)
    implementation("androidx.activity:activity-ktx:1.8.2")

    // Room
    implementation("androidx.room:room-runtime:2.6.0")
    kapt("androidx.room:room-compiler:2.6.0")

    // Kotlin Extensions and Coroutines support for Room
    implementation("androidx.room:room-ktx:2.6.0")

    implementation("androidx.fragment:fragment-ktx:1.8.5")


}