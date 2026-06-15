package com.jdm.alarmlocation.domain.repository

/** 앱 버전 점검 결과 (Firebase Remote Config `version` 기준). */
enum class VersionStatus {
    /** 현재 버전 < 최소 버전 → 강제 업데이트. */
    FORCE_UPDATE,

    /** 최소 ≤ 현재 < 최신 → 선택 업데이트 안내. */
    OPTIONAL_UPDATE,

    /** 최신 → 그대로 진행. */
    UP_TO_DATE,
}

/** 원격 구성/버전 점검 저장소. */
interface AppConfigRepository {
    suspend fun checkVersion(currentVersion: String): VersionStatus
}
