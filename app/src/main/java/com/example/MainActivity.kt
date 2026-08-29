package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.local.entity.ScanRecordEntity
import com.example.domain.parser.QrContentParser
import com.example.ui.components.CassBottomNavigation
import com.example.ui.components.CassNavDestination
import com.example.ui.screens.AnalyticsScreen
import com.example.ui.screens.CreateQrScreen
import com.example.ui.screens.DigitalBusinessCardScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.LibraryScreen
import com.example.ui.screens.OcrScannerScreen
import com.example.ui.screens.ScanResultBottomSheet
import com.example.ui.screens.ScannerScreen
import com.example.ui.screens.SecurityCenterScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.SplashScreen
import com.example.ui.theme.CassEasyTheme
import com.example.viewmodel.MainViewModel

enum class AppSubScreen {
    NONE,
    DIGITAL_CARD,
    OCR_SCANNER,
    SECURITY_CENTER,
    ANALYTICS
}

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val themeMode by viewModel.themeMode.collectAsStateWithLifecycle()

            CassEasyTheme(themeMode = themeMode) {
                CassEasyApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun CassEasyApp(viewModel: MainViewModel) {
    var showSplash by remember { mutableStateOf(true) }
    var currentDestination by remember { mutableStateOf(CassNavDestination.HOME) }
    var subScreen by remember { mutableStateOf(AppSubScreen.NONE) }

    val activeScanResult by viewModel.activeScanResult.collectAsStateWithLifecycle()

    if (showSplash) {
        SplashScreen(onFinished = { showSplash = false })
    } else {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            bottomBar = {
                if (currentDestination != CassNavDestination.SCAN && subScreen == AppSubScreen.NONE) {
                    CassBottomNavigation(
                        currentDestination = currentDestination,
                        onNavigate = { destination ->
                            subScreen = AppSubScreen.NONE
                            currentDestination = destination
                        }
                    )
                }
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(if (currentDestination != CassNavDestination.SCAN && subScreen == AppSubScreen.NONE) innerPadding else androidx.compose.foundation.layout.PaddingValues())
                    .background(MaterialTheme.colorScheme.background)
            ) {
                AnimatedContent(
                    targetState = subScreen to currentDestination,
                    transitionSpec = {
                        (fadeIn() + slideInHorizontally { it / 6 }) togetherWith (fadeOut() + slideOutHorizontally { -it / 6 })
                    },
                    label = "screen_transition"
                ) { (currentSub, currentNav) ->
                    when (currentSub) {
                        AppSubScreen.DIGITAL_CARD -> {
                            DigitalBusinessCardScreen(
                                viewModel = viewModel,
                                onBack = { subScreen = AppSubScreen.NONE }
                            )
                        }
                        AppSubScreen.OCR_SCANNER -> {
                            OcrScannerScreen(
                                viewModel = viewModel,
                                onBack = { subScreen = AppSubScreen.NONE },
                                onNavigateToCreateQr = {
                                    subScreen = AppSubScreen.NONE
                                    currentDestination = CassNavDestination.CREATE
                                }
                            )
                        }
                        AppSubScreen.SECURITY_CENTER -> {
                            SecurityCenterScreen(
                                onBack = { subScreen = AppSubScreen.NONE }
                            )
                        }
                        AppSubScreen.ANALYTICS -> {
                            AnalyticsScreen(
                                viewModel = viewModel,
                                onBack = { subScreen = AppSubScreen.NONE }
                            )
                        }
                        AppSubScreen.NONE -> {
                            when (currentNav) {
                                CassNavDestination.HOME -> {
                                    HomeScreen(
                                        viewModel = viewModel,
                                        onNavigate = { currentDestination = it },
                                        onOpenOcr = { subScreen = AppSubScreen.OCR_SCANNER },
                                        onOpenBusinessCard = { subScreen = AppSubScreen.DIGITAL_CARD },
                                        onOpenSecurityCenter = { subScreen = AppSubScreen.SECURITY_CENTER },
                                        onOpenAnalytics = { subScreen = AppSubScreen.ANALYTICS },
                                        onOpenScanDetail = { scan ->
                                            val parsed = QrContentParser.parse(scan.rawText)
                                            viewModel.onBarcodeDetected(parsed.rawText)
                                        }
                                    )
                                }
                                CassNavDestination.SCAN -> {
                                    ScannerScreen(
                                        viewModel = viewModel,
                                        onBack = { currentDestination = CassNavDestination.HOME },
                                        onOpenOcr = { subScreen = AppSubScreen.OCR_SCANNER }
                                    )
                                }
                                CassNavDestination.CREATE -> {
                                    CreateQrScreen(viewModel = viewModel)
                                }
                                CassNavDestination.LIBRARY -> {
                                    LibraryScreen(
                                        viewModel = viewModel,
                                        onOpenScanDetail = { scan ->
                                            val parsed = QrContentParser.parse(scan.rawText)
                                            viewModel.onBarcodeDetected(parsed.rawText)
                                        }
                                    )
                                }
                                CassNavDestination.SETTINGS -> {
                                    SettingsScreen(
                                        viewModel = viewModel,
                                        onOpenBusinessCard = { subScreen = AppSubScreen.DIGITAL_CARD },
                                        onOpenSecurityCenter = { subScreen = AppSubScreen.SECURITY_CENTER }
                                    )
                                }
                            }
                        }
                    }
                }

                // Active Scan Result Bottom Sheet
                activeScanResult?.let { parsed ->
                    ScanResultBottomSheet(
                        parsed = parsed,
                        viewModel = viewModel,
                        onDismiss = { viewModel.clearActiveScan() }
                    )
                }
            }
        }
    }
}
