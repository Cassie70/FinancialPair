package com.juni7.financialpair

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.juni7.financialpair.di.appModule
import com.juni7.financialpair.ui.screens.MainScreen
import org.koin.compose.KoinApplication
import org.koin.dsl.koinConfiguration

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            KoinApplication(
                configuration = koinConfiguration(declaration = { modules(appModule) }),
                content = { MainScreen() }
            )
        }
    }
}