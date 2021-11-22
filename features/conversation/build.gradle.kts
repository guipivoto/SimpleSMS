plugins {
    id("com.android.library")
    id("kotlin-android")
    kotlin("kapt")
    id("dagger.hilt.android.plugin")
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
        kotlinCompilerExtensionVersion = Versions.compose
    }
}

dependencies {
    // Compose
    implementation("androidx.compose.ui:ui:${Versions.compose}")
    implementation("androidx.compose.material:material:${Versions.compose}")
    implementation("androidx.compose.runtime:runtime-livedata:${Versions.compose}")

    // Dagger
    implementation("com.google.dagger:hilt-android:${Versions.dagger}")
    kapt("com.google.dagger:hilt-android-compiler:${Versions.dagger}")

    // Navigation
    implementation("androidx.hilt:hilt-navigation-compose:${Versions.hiltNavigationCompose}")
    implementation("androidx.test.ext:junit-ktx:1.1.3")

    // Internal
    implementation(project(":repository:message"))
}