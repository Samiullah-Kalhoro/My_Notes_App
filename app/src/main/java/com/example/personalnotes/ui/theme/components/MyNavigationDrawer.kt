package com.example.personalnotes.ui.theme.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

data class DrawerMenuItem(val text: String, val icon: ImageVector, val route: String)

@Composable
fun MyDrawer(
    drawerState: DrawerState,
    onRouteClick: (String) -> Unit,
    items: List<DrawerMenuItem>,
    defaultItemRoute: String,
    content: @Composable () -> Unit,
) {
    var lastChoice by remember { mutableStateOf(defaultItemRoute) }

    ModalDrawer(
        drawerState = drawerState,
        drawerContent = {
            Column {
                items.forEach {
                    DrawerItem(
                        text = it.text,
                        icon = it.icon,
                        isActive = lastChoice == it.route
                    ) {
                        lastChoice = it.route
                        onRouteClick(it.route)
                    }
                }
            }
        }, content = content
    )
}

@Composable
fun DrawerItem(text: String, icon: ImageVector, isActive: Boolean, onClick: () -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick), verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            Modifier.padding(16.dp),
            tint = if (isActive) MaterialTheme.colors.primary else MaterialTheme.colors.onBackground
        )
        Text(
            text = text,
            fontWeight = FontWeight.Bold,
            color = if (isActive) MaterialTheme.colors.primary else MaterialTheme.colors.onBackground
        )
    }
}