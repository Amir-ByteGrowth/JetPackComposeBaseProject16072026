package com.nyvoratech.composebase.core.auth

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

// DataStoreTokenStore.kt — removed duplication
// DataStoreTokenStore.kt — removed duplication
@Singleton
class DataStoreTokenStore @Inject constructor(
    private val dataStore: DataStore<Preferences>,
    private val crypto: KeystoreCryptoManager
) : TokenStorage {

    override fun observeAccessToken(): Flow<String?> =
        dataStore.data.map { it[ACCESS_TOKEN_KEY]?.let(crypto::decrypt) }

    override suspend fun getAccessToken(): String? = observeAccessToken().first()

    override suspend fun getRefreshToken(): String? =
        dataStore.data.map { it[REFRESH_TOKEN_KEY]?.let(crypto::decrypt) }.first()

    override suspend fun saveTokens(accessToken: String, refreshToken: String) {
        dataStore.edit {
            it[ACCESS_TOKEN_KEY] = crypto.encrypt(accessToken)
            it[REFRESH_TOKEN_KEY] = crypto.encrypt(refreshToken)
        }
    }

    override suspend fun updateAccessToken(newAccessToken: String) {
        dataStore.edit { it[ACCESS_TOKEN_KEY] = crypto.encrypt(newAccessToken) }
    }

    override suspend fun clearTokens() {
        dataStore.edit {
            it.remove(ACCESS_TOKEN_KEY)
            it.remove(REFRESH_TOKEN_KEY)
        }
    }

    override suspend fun hasSession(): Boolean = getAccessToken() != null

    private companion object {
        val ACCESS_TOKEN_KEY = stringPreferencesKey("access_token")
        val REFRESH_TOKEN_KEY = stringPreferencesKey("refresh_token")
    }
}