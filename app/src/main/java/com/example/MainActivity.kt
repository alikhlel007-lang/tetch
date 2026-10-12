package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.data.CafeRepository
import com.example.model.AppLanguage
import com.example.ui.screens.AuthScreen
import com.example.ui.screens.CashierScreen
import com.example.ui.screens.CustomerMenuScreen
import com.example.ui.screens.QrGeneratorDialog
import com.example.ui.theme.CafeTheme

enum class AppScreen {
    AUTH,
    CASHIER,
    CUSTOMER_MENU
}

class MainActivity : ComponentActivity() {

    private val repository = CafeRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CafeTheme {
                MainAppNavHost(repository = repository)
            }
        }
    }
}

@Composable
fun MainAppNavHost(repository: CafeRepository) {
    var currentScreen by remember { mutableStateOf(AppScreen.CASHIER) }
    var language by remember { mutableStateOf(AppLanguage.ARABIC) }
    var showQrDialog by remember { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        when (currentScreen) {
            AppScreen.AUTH -> {
                AuthScreen(
                    repository = repository,
                    onAuthSuccess = { currentScreen = AppScreen.CASHIER },
                    onSkipToCustomerMenu = { currentScreen = AppScreen.CUSTOMER_MENU }
                )
            }

            AppScreen.CASHIER -> {
                CashierScreen(
                    repository = repository,
                    cafeId = "cafe_main",
                    language = language,
                    onLanguageChange = { language = it },
                    onOpenCustomerMenu = { currentScreen = AppScreen.CUSTOMER_MENU },
                    onOpenQrDialog = { showQrDialog = true },
                    onLogout = { currentScreen = AppScreen.AUTH }
                )
            }

            AppScreen.CUSTOMER_MENU -> {
                BackHandler {
                    currentScreen = AppScreen.CASHIER
                }
                CustomerMenuScreen(
                    repository = repository,
                    cafeId = "cafe_main",
                    initialTableNumber = 5,
                    language = language,
                    onLanguageToggle = {
                        language = if (language == AppLanguage.ARABIC) AppLanguage.ENGLISH else AppLanguage.ARABIC
                    },
                    onBackToCashier = { currentScreen = AppScreen.CASHIER }
                )
            }
        }

        if (showQrDialog) {
            QrGeneratorDialog(
                cafeId = "cafe_main",
                onDismiss = { showQrDialog = false }
            )
        }
    }
}
