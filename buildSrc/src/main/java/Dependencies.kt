import Versions.activity_compose_version
import Versions.android_async_http_version
import Versions.appcompat_version
import Versions.coil_version
import Versions.compore_runtime_lifecycle
import Versions.constraintlayout_version
import Versions.core_ktx_version
import Versions.coroutine_version
import Versions.espreso_core_version
import Versions.exoplayer_version
import Versions.ext_junit_version
import Versions.hds_compose_version
import Versions.hds_version
import Versions.hilt_version
import Versions.junit_verseion
import Versions.lifecycle_runtime_version
import Versions.lifecycle_version
import Versions.lottie_version
import Versions.material_version
import Versions.okhttp_version
import Versions.retrofit_version
import Versions.room_version
import Versions.timber_version

// ktlint-disable filename

object Versions {
    const val kotlin_compiler_version = "1.4.3"
    const val okhttp_version = "4.9.0"
    const val retrofit_version = "2.9.0"
    const val coroutine_version = "1.5.2"
    const val hilt_version = "2.44"
    const val room_version = "2.5.0"
    const val lifecycle_version = "2.5.1"
    const val lifecycle_runtime_version = "2.6.2"
    const val activity_compose_version = "1.8.2"
    const val hds_version = "1.0.3"
    const val hds_compose_version = "1.0.0"
    const val coil_version = "2.5.0"
    const val core_ktx_version = "1.9.0"
    const val junit_verseion = "4.13.2"
    const val ext_junit_version = "1.1.5"
    const val espreso_core_version = "3.5.1"
    const val appcompat_version = "1.6.1"
    const val material_version = "1.11.0"
    const val constraintlayout_version = "2.1.4"
    const val timber_version = "4.7.1"
    const val lottie_version = "6.3.0"
    const val exoplayer_version = "2.17.1"
    const val android_async_http_version = "1.4.9"
    const val compore_runtime_lifecycle = "1.3.3"
}

object Kotlin {
}

object ComposeX {
    const val COMPOSE_BOM = "androidx.compose:compose-bom:2023.03.00"
    const val ACTIVITY_COMPOSE = "androidx.activity:activity-compose:$activity_compose_version"
    const val VIEWMODEL = "androidx.lifecycle:lifecycle-viewmodel-compose:$lifecycle_version"
    const val COMPOSE_RUNTIME = "androidx.lifecycle:lifecycle-runtime-compose:$lifecycle_version"
    const val COMPORE_LIFECYCLE = "androidx.compose.runtime:runtime:$compore_runtime_lifecycle"
}

object AndroidX {
    const val CORE = "androidx.core:core-ktx:$core_ktx_version"
    const val LIVECYCLE_RUNTIME = "androidx.lifecycle:lifecycle-runtime-ktx:$lifecycle_runtime_version"
    const val MATERIAL = "com.google.android.material:material:$material_version"
    const val APP_COMPAT = "androidx.appcompat:appcompat:$appcompat_version"
    const val CONSTRAINT_LAYOUT = "androidx.constraintlayout:constraintlayout:$constraintlayout_version"

    const val NAVIGATION = "androidx.navigation:navigation-compose:2.5.3"

}

object Coroutines {
    const val COROUTINES = "org.jetbrains.kotlinx:kotlinx-coroutines-android:$coroutine_version"
    const val ANDROID_COROUTINE = "org.jetbrains.kotlinx:kotlinx-coroutines-android:$coroutine_version"
}
object ThirdParty {
    const val OKHTTP = "com.squareup.okhttp3:okhttp:$okhttp_version"
    const val OKHTTP_LOGGING_INTERCEPTOR = "com.squareup.okhttp3:logging-interceptor:$okhttp_version"
    const val OKTTHP_URL_CONNECTION = "com.squareup.okhttp3:okhttp-urlconnection:$okhttp_version"

    const val RETROFIT = "com.squareup.retrofit2:retrofit:$retrofit_version"
    const val GSON_CONVERTER = "com.squareup.retrofit2:converter-gson:$retrofit_version"

    const val HILT = "com.google.dagger:hilt-android:$hilt_version"
    const val HILT_COMPILER = "com.google.dagger:hilt-android-compiler:$hilt_version"

    const val COIL = "io.coil-kt:coil-compose:$coil_version"


    const val TIMBER = "com.jakewharton.timber:timber:$timber_version"

    const val LOTTIE_COMPOSE = "com.airbnb.android:lottie-compose:$lottie_version"

    const val ROOM = "androidx.room:room-runtime:$room_version"
    const val ROOM_COMPILER = "androidx.room:room-compiler:$room_version"
    const val ROOM_KTX = "androidx.room:room-ktx:$room_version"
}

object Test {
    const val JUNIT = "junit:junit:$junit_verseion"
    const val EXT_JUNIT = "androidx.test.ext:junit:$ext_junit_version"
    const val ESPRESSO_CORE = "androidx.test.espresso:espresso-core:$espreso_core_version"
}