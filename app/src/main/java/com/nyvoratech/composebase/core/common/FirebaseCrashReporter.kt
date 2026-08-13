package com.nyvoratech.composebase.core.common

import com.google.firebase.crashlytics.FirebaseCrashlytics
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseCrashReporter @Inject constructor() : CrashReporter {

    private val crashlytics = FirebaseCrashlytics.getInstance()

    override fun recordException(
        throwable: Throwable,
        message: String?
    ) {
        message?.let {
            crashlytics.log(it)
        }

        crashlytics.recordException(throwable)
    }

    override fun setCustomKey(
        key: String,
        value: String
    ) {
        crashlytics.setCustomKey(key, value)
    }
}