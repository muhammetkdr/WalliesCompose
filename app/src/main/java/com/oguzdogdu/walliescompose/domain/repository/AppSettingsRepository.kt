package com.oguzdogdu.walliescompose.domain.repository

import kotlinx.coroutines.flow.Flow

interface AppSettingsRepository {
    suspend fun putThemeStrings(key: String, value: String)
    suspend fun getThemeStrings(key: String): Flow<String?>
    suspend fun putLanguageStrings(key: String, value: String)
    suspend fun getLanguageStrings(key: String): Flow<String?>
    suspend fun putHomeRotateCardVisibility(key: String, value: Boolean?)
    fun getHomeRotateCardVisibility(key: String): Flow<Boolean>
    suspend fun putOnboardingShow(key: String, value: Boolean)
    fun getOnboardingShow(key: String): Flow<Boolean>
}