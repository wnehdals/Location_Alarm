import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.hilt)
    id("kotlin-parcelize")
    alias(libs.plugins.ksp)
    alias(libs.plugins.google.service)
    alias(libs.plugins.compose.compiler)
}
android {
    namespace = "com.jdm.alarmlocation"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.jdm.alarmlocation"
        minSdk = 31
        targetSdk = 36
        versionCode = 3
        versionName = "1.0.2"
        buildConfigField("String", "NAVER_CLIEND_ID", getPropertyKey("cliendid"))
        buildConfigField("String", "SEARCH_CLIENT_ID", getPropertyKey("searchCliendId"))
        buildConfigField("String", "SEARCH_SECRET_ID", getPropertyKey("searchCliendSecret"))
        buildConfigField("String", "NCP_API_ID", getPropertyKey("ncpapikey"))
        buildConfigField("String", "NCP_API_SECRET_ID", getPropertyKey("cliendSecret"))

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }
    signingConfigs {
        create("release") {
            keyAlias = "locationalarm"
            keyPassword = getPropertyKey("keyPassword")
            storeFile = file("./Key.jks")
            storePassword = getPropertyKey("storePassword")
        }
    }
    buildTypes {
        getByName("debug") {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            isDebuggable = true
        }
        getByName("release") {
            isMinifyEnabled = true
            signingConfig = signingConfigs.getByName("release")
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            isDebuggable = false
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
        dataBinding = true
        buildConfig = true
        compose = true
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.fragment.ktx)
    implementation(libs.androidx.navigation.fragment)
    implementation(libs.androidx.navigation.ui)
    implementation(libs.jwtdecode)

    // Compose
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material.icons.extended)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.coil.compose)
    debugImplementation(libs.androidx.compose.ui.tooling)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    ksp(libs.androidx.hilt.compiler)
    implementation(kotlin("reflect"))

    // okhttp
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging)
    implementation(libs.okhttp.connection)

    // retrofit + sandwich
    implementation(libs.sandwich.retrofit)
    implementation(libs.sandwich)
    implementation(libs.retrofit.kotlin.serialization)
    implementation(libs.kotlinx.serialization.json)

    implementation(libs.kotlinx.immutable)

    // ui
    implementation(libs.lottie)
    implementation(libs.androidx.recyclerview)
    implementation(libs.androidx.swiperefreshlayout)

    // coroutine
    implementation(libs.coroutines.core)
    implementation(libs.coroutines.android)

    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics.ktx)
    implementation(libs.firebase.messaging)
    implementation(libs.firebase.perf.ktx)
    implementation(libs.firebase.config.ktx)

    //Room
    val room_version = "2.5.1"
    implementation ("androidx.room:room-runtime:$room_version")
    ksp ("androidx.room:room-compiler:$room_version")
    implementation("androidx.room:room-ktx:$room_version")


    implementation("com.google.android.gms:play-services-location:21.0.1")

    //implementation("com.google.android.gms:play-services-ads:22.6.0")


    implementation("com.naver.maps:map-sdk:3.22.1")
    implementation("com.google.code.gson:gson:2.10.1")
}
fun getPropertyKey(propertyKey: String): String {
    val nullableProperty: String? = gradleLocalProperties(rootDir, providers).getProperty(propertyKey)
    return nullableProperty ?: "null"
}