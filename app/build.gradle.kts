plugins {
    id("com.android.application")
    id("kotlin-android")
    kotlin("kapt")
    id("dagger.hilt.android.plugin")
}

android {
    compileSdk = App.compileSdk

    defaultConfig {
        applicationId = "com.pivoto.simplesms"
        minSdk = App.minSdk
        targetSdk = App.targetSdk
        versionCode = App.targetSdk
        versionName = App.versionName
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
        kotlinCompilerExtensionVersion = Versions.compose
    }
}

dependencies {
    implementation("androidx.appcompat:appcompat:${Versions.appCompat}")
    implementation("androidx.constraintlayout:constraintlayout:${Versions.constraintLayout}")
    implementation("androidx.core:core-ktx:${Versions.kotlinCore}")

    // Compose
    implementation("androidx.compose.ui:ui:${Versions.compose}")

    // Navigation
    implementation("androidx.hilt:hilt-navigation-compose:${Versions.hiltNavigationCompose}")

    // Dagger
    implementation("com.google.dagger:hilt-android:${Versions.dagger}")
    kapt("com.google.dagger:hilt-android-compiler:${Versions.dagger}")

    // Internal
    implementation(project(":features:permissions"))
    implementation(project(":features:inbox"))
    implementation(project(":features:conversation"))
    implementation(project(":repository:message"))
    implementation(project(":repository:contact"))
    implementation(project(":components:notification"))
    implementation(project(":components:receiver"))
    implementation(project(":components:service"))
}