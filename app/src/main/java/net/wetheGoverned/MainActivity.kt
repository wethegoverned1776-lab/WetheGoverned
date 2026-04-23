package net.wetheGoverned

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.remember
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import dagger.hilt.android.AndroidEntryPoint
import net.wetheGoverned.ui.WeTheGovernedNavHost
import net.wetheGoverned.ui.SnackbarController
import net.wetheGoverned.ui.VerificationSimulator
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var verificationSimulator: VerificationSimulator

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)

        // Virtual Functionality Verification
        verificationSimulator.runFullTest { log ->
            Log.i("VIRTUAL_CHECK", log)
        }

        setContent {
            val snackbarController = remember { SnackbarController() }
            WeTheGovernedNavHost(
                snackbarController = snackbarController
            )
        }
    }
}
