import ThirdParty.HILT
import ThirdParty.HILT_COMPILER
import ThirdParty.ROOM
import ThirdParty.ROOM_COMPILER
import ThirdParty.ROOM_KTX

plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    kotlin("kapt")
    id("com.google.dagger.hilt.android")
}

android {
    namespace = "com.jdm.data"
    compileSdk = 33

    defaultConfig {
        minSdk = 26

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
        sourceCompatibility =  JavaVersion.VERSION_17
        targetCompatibility =  JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget =  JavaVersion.VERSION_17.toString()
    }
}

dependencies {
    api(project(mapOf("path" to ":model")))
    implementation(ROOM)
    kapt(ROOM_COMPILER)
    implementation(ROOM_KTX)

    implementation(HILT)
    kapt(HILT_COMPILER)
}