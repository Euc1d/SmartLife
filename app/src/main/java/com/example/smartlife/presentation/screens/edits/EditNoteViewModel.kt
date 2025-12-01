package com.example.smartlife.presentation.screens.edits

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartlife.data.NoteRepositoryImpl
import com.example.smartlife.data.TestNotesRepositoryImpl
import com.example.smartlife.domain.DeleteNoteUseCase
import com.example.smartlife.domain.EditNoteUseCase
import com.example.smartlife.domain.GetNoteUseCase
import com.example.smartlife.domain.Note
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class EditNoteViewModel(
    val id : Int,
    context: Context
) : ViewModel() {
    val repository = NoteRepositoryImpl.getInstance(context)

    val editNoteUseCase = EditNoteUseCase(repository)
    val getNotesUseCase = GetNoteUseCase(repository)
    val deleteNoteUseCase = DeleteNoteUseCase(repository)

    private val _state = MutableStateFlow<EditNoteState>(EditNoteState.Init)
    val state = _state.asStateFlow()
    init {
        viewModelScope.launch {
            _state.update {
                val newNote  = getNotesUseCase(id)
                EditNoteState.Editing(newNote)
            }
        }
    }

    fun processCommand(command: EditNoteCommand) {
        viewModelScope.launch {
            when (command) {
                EditNoteCommand.Back -> {
                    _state.update {
                        EditNoteState.Finished
                    }
                }
                is EditNoteCommand.InputDescription -> {
                    _state.update { prev->
                        if (prev is EditNoteState.Editing){
                            val editedNote = prev.note.copy(content = command.content)
                            prev.copy(note = editedNote)
                        }else{
                            prev
                        }
                    }
                }
                is EditNoteCommand.InputTitle -> {
                    _state.update { prev->
                        if (prev is EditNoteState.Editing){
                            val editedNote = prev.note.copy(title = command.title)
                            prev.copy(note = editedNote)
                        }else{
                            prev
                        }
                    }
                }
                EditNoteCommand.Save -> {
                    _state.update { previous->
                        if (previous is EditNoteState.Editing) {
                            editNoteUseCase(note = previous.note )
                            EditNoteState.Finished
                        }else{
                            previous
                        }
                    }
                }

                is EditNoteCommand.Delete ->{
                    _state.update { previous->
                        if (previous is EditNoteState.Editing) {
                            deleteNoteUseCase(previous.note.id)
                            EditNoteState.Finished
                        }else{
                            previous
                        }
                    }
                }
            }
        }
    }

}

sealed interface EditNoteCommand{
    data class InputTitle(val title: String) : EditNoteCommand

    data class InputDescription(val content: String) : EditNoteCommand

    data object Save : EditNoteCommand

    data object Back : EditNoteCommand

    data class Delete(val id: Int): EditNoteCommand

}

sealed interface EditNoteState {
    data class Editing(
        val note : Note
    ) : EditNoteState{
        val isEnabled : Boolean
            get() = note.title.isNotBlank() && note.content.isNotBlank()
    }
    object Init: EditNoteState

    data object Finished : EditNoteState
}