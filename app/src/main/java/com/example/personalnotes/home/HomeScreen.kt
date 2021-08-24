package com.example.personalnotes.home

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.personalnotes.changeDrawerState
import com.example.personalnotes.database.Note
import com.example.personalnotes.database.NoteDatabase
import com.example.personalnotes.ui.theme.components.MyTopAppBar
import com.example.personalnotes.ui.theme.components.ScreenParameters
import com.example.personalnotes.utils.dateTimeToString
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.joda.time.DateTime
import org.joda.time.LocalDateTime
import kotlin.math.max


@Composable
fun HomeScreen(navController: NavHostController, drawerState: DrawerState) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val database = remember { NoteDatabase.getInstance(context) }

    val today = remember { LocalDateTime() }

    val homeScreenViewModel: HomeScreenViewModel =
        viewModel(factory = HomeScreenViewModelFactory(database = database))
    val notes by homeScreenViewModel.allNotes.collectAsState(initial = listOf())

    Scaffold(
        topBar = {
            MyTopAppBar(params = ScreenParameters.Home) {
                scope.launch {
                    changeDrawerState(drawerState)
                }
            }
        },
        floatingActionButton = {
            AddNoteFloatingButton {
                scope.launch {
                    var id: Long
                    val millis = DateTime().millis
                    withContext(Dispatchers.IO) {
                        id = database.notesDao().insert(Note(dateTime = millis))
                    }
                    navController.navigate("home/edit/$id")
                }
            }
        }) {

        Column {

            NoteCardList(
                notes = notes,
                today,
                onClick = { noteId ->
                    homeScreenViewModel.onNoteClick()
                    navController.navigate("home/edit/$noteId")
                },
                onSwipe = { note ->
                    homeScreenViewModel.onNoteSwiped(note)
                })
        }
    }
}


@Composable
fun AddNoteFloatingButton(onClick: () -> Unit) {
    FloatingActionButton(
        onClick = onClick
    ) {
        Icon(imageVector = Icons.Filled.Add, contentDescription = "Add note")
    }
}

@Composable
fun NoteCardList(
    notes: List<Note>,
    today: LocalDateTime,
    onClick: (Long) -> Unit,
    onSwipe: (Note) -> Unit,
) {
    LazyColumn(Modifier.padding(8.dp)) {
        item {
            Text(text = "MY NOTES",
                fontWeight = FontWeight.ExtraBold,
                fontSize = 20.sp,
                fontFamily = FontFamily.SansSerif, modifier = Modifier.fillMaxSize().padding(vertical = 8.dp), textAlign = TextAlign.Center)
        }
        items(notes.size) { pos ->
            notes[pos].let { note ->
                NoteCard(
                    noteText = note.text,
                    date = dateTimeToString(LocalDateTime(note.dateTime), today),
                    noteId = note.noteId,
                    onClick = {
                        onClick(note.noteId)
                    },
                    onSwipe = {
                        onSwipe(note)
                    })
            }
        }
    }
}

@Composable
fun NoteCard(
    noteText: String,
    date: String,
    noteId: Long,
    onSwipe: () -> Unit,
    onClick: (() -> Unit),
) {
    Card(
        Modifier
            .padding(vertical = 8.dp, horizontal = 8.dp)
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .mySwappable(noteId = noteId, onSwipe = onSwipe),
        shape = RoundedCornerShape(10.dp), elevation = 4.dp
    ) {
        Column {
            Text(
                text = date,
                fontSize = 14.sp,
                color = Color.Gray,
                modifier = Modifier.padding(8.dp)
            )
            Text(
                text = noteText,
                fontSize = 16.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            )
        }
    }
}

@Composable
fun NotesTitleCard(

) {
    Card(
        Modifier
            .padding(vertical = 8.dp)
            .fillMaxWidth(0.4f)
            .fillMaxHeight(0.1f)
    ) {
        Box(modifier = Modifier.fillMaxSize(), Alignment.Center) {
            Text(text = "NOTES",
                fontWeight = FontWeight.ExtraBold,
                fontSize = 20.sp,
                fontFamily = FontFamily.SansSerif)
        }
    }
}


@SuppressLint("UnnecessaryComposedModifier")
fun Modifier.mySwappable(noteId: Long, onSwipe: () -> Unit): Modifier = composed {
    val offsetX = remember(noteId) { Animatable(0f) }
    val scope = rememberCoroutineScope()

    val draggableState = rememberDraggableState(onDelta = {
        scope.launch {
            offsetX.snapTo(max(offsetX.value + it, 0f))
        }
    })
    val width = LocalContext.current.resources.displayMetrics.widthPixels


    Modifier
        .absoluteOffset(x = offsetX.value.dp)
        .draggable(draggableState, orientation = Orientation.Horizontal, onDragStopped = {
            Log.i("DRAG", "off = ${offsetX.value} velo = $it")
            if (offsetX.value > width * 0.6f * 0.5f && it < 0) {
                onSwipe()
                launch {
                    offsetX.animateTo(width.toFloat())
                }
            }
            launch {
                if (!offsetX.isRunning)
                    offsetX.animateTo(0f)
            }
        }, onDragStarted = {
            offsetX.snapTo(max(offsetX.value + it.x, 0f))
        })
}