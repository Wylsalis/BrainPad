package com.brainpad.app

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.brainpad.app.ui.screens.NotesListScreen
import com.brainpad.app.viewmodel.NotesView
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import com.brainpad.app.ui.screens.NoteEditScreen
import com.brainpad.app.ui.theme.BrainPadTheme

class MainActivity : ComponentActivity() {

    companion object {
        private const val TAG = "BrainPad_Lifecycle"
    }

    private val viewModel: NotesView by viewModels()

    private var activeNoteIdState = mutableStateOf<String?>(null)
    private var isEditingState = mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate")
        enableEdgeToEdge()

        if (savedInstanceState == null) {
            handleDeepLink(intent)
        }

        setContent {

            val listNotes by viewModel.notes.collectAsState()

            val activeNoteId by viewModel.activeNoteId.collectAsState()
            val isEditing by viewModel.isEditing.collectAsState()

            if (isEditing) {
                val currentNote = activeNoteId?.let { viewModel.getNoteById(it) }

                BrainPadTheme() {
                    NoteEditScreen(
                        noteId = activeNoteId ?: "",
                        initialTitle = currentNote?.title.orEmpty(),
                        initialContent = currentNote?.content.orEmpty(),
                        onBackClick = {
                            viewModel.closeNote()
                        },
                        onShareClick = {
                            currentNote?.let { note ->
                                shareNote(note.id)
                            }
                        }
                    )
                }
            } else {
                BrainPadTheme() {
                    NotesListScreen(
                        notes = listNotes,
                        onNoteClick = { noteId ->
                            viewModel.openNote(noteId)
                        }
                    )
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        Log.d(TAG, "onNewIntent")
        handleDeepLink(intent)
    }

    private fun handleDeepLink(intent: Intent?) {
        val data: Uri? = intent?.data ?: return

        if (data!!.scheme == "brainpad") {
            val requestedId = data.lastPathSegment
                ?: data.pathSegments.firstOrNull()
                ?: (if (data.host != "note") data.host else null)

            if (requestedId.isNullOrBlank()) {
                showErrorToast("Ошибка: ссылка повреждена (ID не указан)")
                return
            }

            val note = viewModel.getNoteById(requestedId)
            if (note != null) {
                viewModel.openNote(note.id)
            } else {
                showErrorToast("Заметка с ID '$requestedId' не найдена!")
            }
        }
    }

    private fun shareNote(noteId: String) {
        val deepLinkUrl = "brainpad://note/$noteId"
        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, deepLinkUrl)
            type = "text/plain"
        }
        val shareIntent = Intent.createChooser(sendIntent, "Поделиться заметкой")
        startActivity(shareIntent)
    }

    private fun showErrorToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "onStart")
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume")
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause")
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "onStop")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy")
    }


}