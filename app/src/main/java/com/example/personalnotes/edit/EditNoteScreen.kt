package com.example.personalnotes.edit

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.rememberScrollableState
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.personalnotes.database.NoteDatabase
import com.example.personalnotes.ui.theme.components.MyFloatingActionButton
import com.example.personalnotes.ui.theme.components.MyTopAppBar
import com.example.personalnotes.ui.theme.components.ScreenParameters

@Composable
fun EditScreen(navController: NavHostController, noteId: Long) {
    val context = LocalContext.current
    val database = remember { NoteDatabase.getInstance(context) }
    val editScreenViewModel: EditScreenViewModel =
        viewModel(factory = EditScreenViewModelFactory(noteId, database))

    val text by editScreenViewModel.text
    val scrollableState = rememberScrollableState(consumeScrollDelta = { it })

    BackHandler {
        onNavigateUp(navController, editScreenViewModel)
    }

    Scaffold(
        Modifier.scrollable(orientation = Orientation.Vertical, state = scrollableState),
        topBar = {
            MyTopAppBar(params = ScreenParameters.HomeEdit) {
                onNavigateUp(navController, editScreenViewModel)
            }
        },
        floatingActionButton = {
            MyFloatingActionButton(icon = Icons.Filled.Done, description = "Save note") {
                onNavigateUp(navController, editScreenViewModel, true)
            }
        }) {

        TextField(
            value = text, onValueChange = { editScreenViewModel.onTextChange(it) },
            Modifier
                .fillMaxSize()
                .background(color = MaterialTheme.colors.background)
        )
    }
}

fun onNavigateUp(
    navController: NavHostController,
    viewModel: EditScreenViewModel,
    saveChanges: Boolean = false,
) {
    viewModel.onNavigateUp(saveChanges)
    navController.popBackStack()
}