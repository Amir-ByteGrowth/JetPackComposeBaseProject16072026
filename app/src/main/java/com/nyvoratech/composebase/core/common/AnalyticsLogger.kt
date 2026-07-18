package com.nyvoratech.composebase.core.common

import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Thin wrapper around FirebaseAnalytics. ViewModels/UseCases depend on this
 * interface-like class instead of the Firebase SDK directly, keeping
 * analytics an implementation detail of the data/core layer.
 */
@Singleton
class AnalyticsLogger @Inject constructor(
    private val firebaseAnalytics: FirebaseAnalytics
) {
    fun logEvent(name: String, params: Map<String, String> = emptyMap()) {
        val bundle = Bundle().apply {
            params.forEach { (key, value) -> putString(key, value) }
        }
        firebaseAnalytics.logEvent(name, bundle)
    }

    fun setUserId(userId: String?) {
        firebaseAnalytics.setUserId(userId)
    }

    companion object {
        const val EVENT_LOGIN_SUCCESS = "login_success"
        const val EVENT_LOGIN_FAILURE = "login_failure"
        const val EVENT_USERS_LOADED = "users_loaded"
    }
}
