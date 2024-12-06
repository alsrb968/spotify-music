plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.parcelize)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
}

android {
    namespace = "com.litbig.spotify"
    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "com.litbig.spotify"
        minSdk = libs.versions.minSdk.get().toInt()
        targetSdk = libs.versions.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    signingConfigs {
        val localKeystore = rootProject.file("release.keystore")
        getByName("debug") {
            storeFile = localKeystore
            storePassword = "12345678"
            keyAlias = "keystore"
            keyPassword = "12345678"
        }
        create("release") {
            storeFile = localKeystore
            storePassword = "12345678"
            keyAlias = "keystore"
            keyPassword = "12345678"
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            signingConfig = signingConfigs.getByName("release")
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
    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation(projects.core.design)
    implementation(projects.core.data)
    implementation(projects.core.domain)

    implementation(libs.jakewharton.timber)

    implementation(libs.androidx.palette.ktx)

    implementation(libs.androidx.core.ktx)
    implementation(libs.jetbrains.kotlinx.serialization)

    //----- Compose
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.bundles.androidx.compose)
    debugImplementation(libs.bundles.androidx.compose.debug)

    //----- Dagger Hilt
    implementation(libs.google.hilt.android)
    ksp(libs.google.hilt.compiler)

    //----- Paging
    implementation(libs.androidx.paging.compose)

    //----- Coil
    implementation(libs.io.coil.compose)

    // ----- Test
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}