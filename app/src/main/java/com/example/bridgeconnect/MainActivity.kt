package com.example.bridgeconnect

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.bridgeconnect.ui.navigation.AppNavHost
import com.example.bridgeconnect.ui.theme.BridgeConnectTheme
import com.example.bridgeconnect.viewmodel.SettingsViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val settingsViewModel: SettingsViewModel = viewModel()
            val settings by settingsViewModel.settings.collectAsState()

            BridgeConnectTheme(themeMode = settings.themeMode) {
                AppNavHost(
                    modifier = Modifier.fillMaxSize(),
                    settingsViewModel = settingsViewModel
                )
            }
        }
    }
}