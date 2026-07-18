package com.nyvoratech.composebase

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * Application entry point. Annotating with @HiltAndroidApp triggers Hilt's
 * code generation, including a base class for your Application that serves
 * as the application-level dependency container.
 */
@HiltAndroidApp
class ComposeBaseApplication : Application()
