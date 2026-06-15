package com.jdm.alarmlocation.data.repository

import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import com.jdm.alarmlocation.domain.model.Version
import com.jdm.alarmlocation.domain.repository.AppConfigRepository
import com.jdm.alarmlocation.domain.repository.VersionStatus
import kotlinx.coroutines.tasks.await
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Firebase Remote Config 기반 버전 점검.
 * 기존 SplashViewModel 의 로직을 도메인 저장소로 추출 — Compose Splash 가 진입 전에 호출한다.
 */
@Singleton
class AppConfigRepositoryImpl @Inject constructor() : AppConfigRepository {

    private val json = Json { ignoreUnknownKeys = true }

    private val defaults = mapOf(
        "version" to """{ "minimum": "1.0.0", "latest": "1.0.0" }""",
    )

    override suspend fun checkVersion(currentVersion: String): VersionStatus {
        val remoteConfig: FirebaseRemoteConfig = Firebase.remoteConfig
        remoteConfig.setConfigSettingsAsync(
            remoteConfigSettings { minimumFetchIntervalInSeconds = MIN_FETCH_INTERVAL_SEC },
        )
        remoteConfig.setDefaultsAsync(defaults)

        // 네트워크 실패 시에도 기본값/마지막 활성값으로 진행 (실패해도 앱 진입 차단하지 않음).
        runCatching { remoteConfig.fetchAndActivate().await() }

        val version = runCatching {
            json.decodeFromString<Version>(remoteConfig.getString("version"))
        }.getOrDefault(Version())

        return when {
            isLower(currentVersion, version.minimum) -> VersionStatus.FORCE_UPDATE
            isLower(currentVersion, version.latest) -> VersionStatus.OPTIONAL_UPDATE
            else -> VersionStatus.UP_TO_DATE
        }
    }

    /** current < other 이면 true (semantic version, dot-separated). */
    private fun isLower(current: String, other: String): Boolean {
        val a = current.toVersionParts()
        val b = other.toVersionParts()
        val size = maxOf(a.size, b.size)
        for (i in 0 until size) {
            val diff = a.getOrElse(i) { 0 } - b.getOrElse(i) { 0 }
            if (diff > 0) return false
            if (diff < 0) return true
        }
        return false
    }

    private fun String.toVersionParts(): List<Int> =
        split(".").map { it.trim().toIntOrNull() ?: 0 }

    private companion object {
        const val MIN_FETCH_INTERVAL_SEC = 3600L
    }
}
