plugins {
    id("com.android.library")
    id("kotlin-android")
    kotlin("kapt")
    //id("dagger.hilt.android.plugin")
}

android {
    compileSdk = App.compileSdk

    defaultConfig {
        minSdk = App.minSdk
        targetSdk = App.targetSdk
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.0.1"
    }
}

dependencies {
    // Compose
    implementation("androidx.compose.ui:ui:${Versions.compose}")

    // Navigation
    implementation("androidx.hilt:hilt-navigation-compose:${Versions.hiltNavigationCompose}")
    implementation("androidx.test.ext:junit-ktx:1.1.3")
}