import AndroidX.APP_COMPAT
import AndroidX.CONSTRAINT_LAYOUT
import AndroidX.CORE
import AndroidX.LIVECYCLE_RUNTIME
import AndroidX.MATERIAL
import ComposeX.ACTIVITY_COMPOSE
import ComposeX.COMPOSE_BOM
import ComposeX.COMPOSE_RUNTIME
import Test.ESPRESSO_CORE
import Test.EXT_JUNIT
import Test.JUNIT
import ThirdParty.GSON_CONVERTER
import ThirdParty.HILT
import ThirdParty.HILT_COMPILER
import ThirdParty.OKHTTP
import ThirdParty.OKHTTP_LOGGING_INTERCEPTOR
import ThirdParty.OKTTHP_URL_CONNECTION
import ThirdParty.RETROFIT
import ThirdParty.TIMBER
import Versions.kotlin_compiler_version

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    kotlin("kapt")
    id("com.google.dagger.hilt.android")
    id("org.jetbrains.kotlin.plugin.serialization")
    id("kotlin-parcelize")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.jdm.alarmlocation"
    compileSdk = AppConfig.compileSdk

    defaultConfig {
        applicationId = "com.jdm.alarmlocation"
        minSdk = AppConfig.minSdk
        targetSdk = AppConfig.targetSdk
        versionCode = AppConfig.versionCode
        versionName = AppConfig.versionName

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_17.toString()
    }
    buildFeatures {
        compose = true
        buildConfig = true
        dataBinding = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = kotlin_compiler_version
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    api(project(mapOf("path" to ":designsystem")))
    implementation(project(mapOf("path" to ":core")))
    implementation(project(mapOf("path" to ":model")))
    implementation(project(mapOf("path" to ":data")))

    implementation(CORE)
    implementation(LIVECYCLE_RUNTIME)
    implementation(ACTIVITY_COMPOSE)
    implementation(platform(COMPOSE_BOM))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.navigation:navigation-fragment-ktx:2.7.6")
    implementation("androidx.navigation:navigation-ui-ktx:2.7.6")
    implementation("com.google.android.gms:play-services-location:21.0.1")
    implementation("androidx.compose.material3:material3")
    implementation(COMPOSE_RUNTIME)
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.3")

    implementation(APP_COMPAT)
    implementation(MATERIAL)
    implementation(CONSTRAINT_LAYOUT)

    implementation(TIMBER)

    implementation(HILT)
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    kapt(HILT_COMPILER)

    implementation(OKHTTP)
    implementation(OKHTTP_LOGGING_INTERCEPTOR)
    implementation(OKTTHP_URL_CONNECTION)
    implementation(RETROFIT)
    implementation(GSON_CONVERTER)

    implementation("com.airbnb.android:lottie-compose:6.3.0")

    testImplementation(JUNIT)
    androidTestImplementation(EXT_JUNIT)
    androidTestImplementation(ESPRESSO_CORE)
    androidTestImplementation(platform(COMPOSE_BOM))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")

    implementation(platform("com.google.firebase:firebase-bom:32.7.1"))
    implementation("com.google.firebase:firebase-analytics")
}