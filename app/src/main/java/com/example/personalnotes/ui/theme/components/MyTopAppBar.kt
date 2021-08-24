package com.example.personalnotes.ui.theme.components

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight

data class ScreenParameters(
    val appBarTitle: String,
    val icon: ImageVector = Icons.Filled.Menu
) {

    companion object {
        val Default = ScreenParameters("Notes")
        val Home = ScreenParameters("Home")
        val HomeEdit = ScreenParameters("Edit", icon = Icons.Filled.ArrowBack)
        val Settings = ScreenParameters("Settings")
        val Account = ScreenParameters("Account")
    }
}

@Composable
fun MyTopAppBar(params: ScreenParameters, actions: @Composable (RowScope.() -> Unit) = {}, onNavigationClick: () -> Unit) {
    TopAppBar(
        title = { Text(params.appBarTitle, fontWeight = FontWeight.Bold) },
        navigationIcon = {
            IconButton(onClick = onNavigationClick) {
                Icon(params.icon, contentDescription = params.appBarTitle)
            }
        },
        actions = actions
    )
}