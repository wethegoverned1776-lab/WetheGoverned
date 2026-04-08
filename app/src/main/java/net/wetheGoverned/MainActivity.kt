package net.wetheGoverned

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import dagger.hilt.android.AndroidEntryPoint
import net.wetheGoverned.ui.SplashScreen
import net.wetheGoverned.ui.WeTheGovernedNavHost
import net.wetheGoverned.ui.SnackbarController

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            var showSplash by remember { mutableStateOf(true) }
            val snackbarController = remember { SnackbarController() }

            if (showSplash) {
                SplashScreen(
                    onSplashFinished = { showSplash = false }
                )
            } else {
                WeTheGovernedNavHost(
                    snackbarController = snackbarController
                )
            }
        }
    }
}