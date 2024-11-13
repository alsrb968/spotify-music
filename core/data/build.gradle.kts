plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.parcelize)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
}

android {
    namespace = "com.litbig.spotify.core.data"
    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        minSdk = libs.versions.minSdk.get().toInt()

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
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
}

dependencies {
    implementation(projects.core.domain)

    implementation(libs.jakewharton.timber)

    implementation(libs.androidx.core.ktx)

    //----- Dagger Hilt
    implementation(libs.google.hilt.android)
    ksp(libs.google.hilt.compiler)
    // For Robolectric tests.
    testImplementation(libs.google.hilt.testing)
    // ...with Kotlin.
    kspTest(libs.google.hilt.compiler)
    // ...with Java.
    testAnnotationProcessor(libs.google.hilt.compiler)
    // For instrumented tests.
    androidTestImplementation(libs.google.hilt.testing)
    // ...with Kotlin.
    kspAndroidTest(libs.google.hilt.compiler)
    // ...with Java.
    androidTestAnnotationProcessor(libs.google.hilt.compiler)

    //----- Room
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.paging)
    ksp(libs.androidx.room.compiler)
    // optional - Kotlin Extensions and Coroutines support for Room
    implementation(libs.androidx.room.ktx)
    // optional - Test helpers
    testImplementation(libs.androidx.room.testing)
    ksp(libs.xerial.sqlite)

    //----- Paging
    implementation(libs.androidx.paging.runtime)
    testImplementation(libs.androidx.paging.common)

    // ----- Test
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}