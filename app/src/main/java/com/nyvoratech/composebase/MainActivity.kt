package com.nyvoratech.composebase

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.nyvoratech.composebase.core.auth.session.SessionManager
import com.nyvoratech.composebase.core.navigation.ComposeBaseNavGraph
import com.nyvoratech.composebase.core.ui.theme.ComposeBaseTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/**
 * Single-Activity host. All screens are Composables reached through the
 * Navigation Compose graph in [ComposeBaseNavGraph].
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ComposeBaseTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    ComposeBaseNavGraph(sessionManager = sessionManager)
                }
            }
        }
    }
}
