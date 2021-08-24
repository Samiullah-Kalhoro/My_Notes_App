package com.example.personalnotes.edit

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.personalnotes.database.Note
import com.example.personalnotes.database.NoteDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.joda.time.DateTime

class EditScreenViewModel(noteId: Long, database: NoteDatabase): ViewModel() {
    private val notesDao = database.notesDao()
    private val _text: MutableState<String> = mutableStateOf("")
    val text: State<String> = _text
    private lateinit var note: Note

    init {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                note = notesDao.get(noteId)
            }
            _text.value = note.text
        }
    }

    fun onTextChange(newText: String) {
        _text.value = newText
    }

    private fun saveChanges() {
        viewModelScope.launch(Dispatchers.IO) {
            val millis = DateTime().millis
            val newNote = note.copy(text = _text.value, dateTime = millis)
            notesDao.update(newNote)
        }
    }

    fun onNavigateUp(saveChanges: Boolean) {
        if(saveChanges) {
            if(text.value.isBlank()) {
                deleteCurrentNote()
                return
            }
            saveChanges()
            return
        }

        if(note.text.isBlank()) {
            deleteCurrentNote()
            return
        }
    }

    private fun deleteCurrentNote() {
        viewModelScope.launch(Dispatchers.IO) {
            notesDao.delete(note)
        }
    }
}

@Suppress("UNCHECKED_CAST")
class EditScreenViewModelFactory(private val noteId: Long, private val database: NoteDatabase): ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(EditScreenViewModel::class.java)) {
            return EditScreenViewModel(noteId = noteId, database = database) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}