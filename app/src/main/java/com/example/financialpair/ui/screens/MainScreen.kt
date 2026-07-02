package com.example.financialpair.ui.screens

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.example.financialpair.ui.screens.movements.MovementsScreen
import com.example.financialpair.ui.screens.topics.TopicsScreen
import kotlinx.serialization.Serializable

@Serializable
data object Movements: NavKey
@Serializable
data object Topics: NavKey

@Composable
fun MainScreen(){
    val backStack = rememberNavBackStack(Movements)

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar(
                modifier = Modifier.fillMaxWidth()
            ) {
                NavigationBarItem(
                    selected = backStack.last() == Movements,
                    onClick = { if(backStack.last() != Movements) backStack.add(Movements) },
                    icon = {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.List,
                            contentDescription = null
                        )
                    },
                    label = {
                        Text(text = "Movimientos")
                    }
                )
                NavigationBarItem(
                    selected = backStack.last() == Topics,
                    onClick = { if(backStack.last() != Topics) backStack.add(Topics) },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null
                        )
                    },
                    label = {
                        Text(text = "Temas")
                    }
                )
            }
        }
    ){ innerPadding ->
            NavDisplay(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                backStack = backStack,
                transitionSpec = { EnterTransition.None togetherWith ExitTransition.None },
                popTransitionSpec = { EnterTransition.None togetherWith ExitTransition.None },
                predictivePopTransitionSpec = { EnterTransition.None togetherWith ExitTransition.None },
                entryProvider = entryProvider {
                    entry<Movements>() {
                        MovementsScreen()
                    }
                    entry<Topics> {
                        TopicsScreen()
                    }
                }
            )
    }
}