package com.example.financialpair

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.financialpair.ui.theme.FinancialPairTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FinancialPairTheme {
                MovementsScreen()
            }
        }
    }
}

@Composable
fun MovementsScreen() {
    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 25.dp)
        ) {
            Button(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(0.dp),
                onClick = {}
            ) {
                Text(text = "+")
            }
        }
    }
}

@Composable
fun FPMovement(){

}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    FinancialPairTheme {
        MovementsScreen()
    }
}