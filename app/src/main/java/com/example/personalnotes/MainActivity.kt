package com.example.personalnotes

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope

import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color


import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.*
import com.example.personalnotes.edit.EditScreen
import com.example.personalnotes.home.HomeScreen
import com.example.personalnotes.splashscreen.SplashScreen
import com.example.personalnotes.ui.theme.PersonalNotesTheme
import com.example.personalnotes.ui.theme.components.DrawerItem
import com.example.personalnotes.ui.theme.components.DrawerMenuItem
import kotlinx.coroutines.launch


val drawerItems = listOf(
    DrawerMenuItem("Home page", Icons.Filled.Home, Routes.Home),
    DrawerMenuItem("Settings", Icons.Filled.Settings, Routes.Settings),
    DrawerMenuItem("Account", Icons.Filled.AccountCircle, Routes.Account),
)

object Routes {
    const val Home = "home"
    const val Settings = "settings"
    const val Account = "account"
}




class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PersonalNotesTheme {
                MyApp()
            }
        }
    }
}

@Composable
fun MyApp() {
    val navController = rememberNavController()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute: String = navBackStackEntry?.destination?.route ?: "default"

    ModalDrawer(
        gesturesEnabled = drawerState.isOpen,
        drawerState = drawerState,
        drawerContent = {
            Column {
                drawerItems.forEach {
                    DrawerItem(
                        text = it.text,
                        icon = it.icon,
                        isActive = it.route == currentRoute,
                        onClick = {
                            navController.navigate(it.route) {
                                popUpTo("home") {}
                            }
                            scope.launch {
                                drawerState.close()
                            }
                        }
                    )
                }
            }
        }) {

        Column(
            modifier = Modifier
                .background(color = MaterialTheme.colors.background)
                .fillMaxSize()
        ) {
            MyNavHost(
                navController = navController,
                drawerState = drawerState
            )
        }

    }
}

suspend fun changeDrawerState(drawerState: DrawerState) {
    if (drawerState.isClosed)
        drawerState.open()
    else
        drawerState.close()
}

@Composable
fun MyNavHost(navController: NavHostController, drawerState: DrawerState) {
    NavHost(navController = navController, startDestination = "splash_screen") {

        composable("splash_screen"){
            SplashScreen(navController = navController)
        }

        composable(Routes.Home) {
            HomeScreen(navController, drawerState)
        }
        composable("home/edit/{noteId}",
            arguments = listOf(navArgument("noteId") { type = NavType.LongType })) {
            EditScreen(navController, it.arguments?.getLong("noteId")!!)
        }
        composable("settings") { Text("Settings", color = Color.White) }
        composable("account") { Text("Account", color = Color.White) }
    }
}


