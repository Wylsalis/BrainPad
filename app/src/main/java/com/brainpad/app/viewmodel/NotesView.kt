package com.brainpad.app.viewmodel

import androidx.lifecycle.ViewModel
import com.brainpad.app.model.Note
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class NotesView : ViewModel() {

    private val _notes = MutableStateFlow<List<Note>>(
        listOf(
            Note(
                id = "949a5444-46bc-4464-894e-289b57dff8da",
                title = "Note 1",
                content = "Lecture 1\nIntroduction to Android\nAndroid is an operating system " +
                        "developed by Google, designed primarily for smartphones and tablets " +
                        "(touchscreen-based mobile devices originally; later for other devices, " +
                        "such as TVs), based on a modified version of the Linux kernel and other " +
                        "open-source software."
            ), Note(
                id = "672f8d2b-3651-4e7a-b8c2-34dfb9aad232",
                title = "Note 2",
                content = "Content 2"
            )
        )
    )

    val notes: StateFlow<List<Note>> = _notes.asStateFlow()

    val activeNoteId = MutableStateFlow<String?>(null)
    val isEditing = MutableStateFlow(false)

    fun openNote(id: String) {
        activeNoteId.value = id
        isEditing.value = true
    }

    fun closeNote() {
        activeNoteId.value = null
        isEditing.value = false
    }

    fun getNoteById(id: String): Note? {
        return _notes.value.find { it.id == id }
    }


}