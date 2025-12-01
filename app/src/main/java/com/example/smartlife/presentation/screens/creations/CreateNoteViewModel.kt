package com.example.smartlife.presentation.screens.creations

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartlife.data.NoteRepositoryImpl
import com.example.smartlife.data.TestNotesRepositoryImpl
import com.example.smartlife.domain.AddNoteUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CreateNoteViewModel(context: Context) : ViewModel() {
    val repository = NoteRepositoryImpl.getInstance(context = context)


    private val addNoteUseCase = AddNoteUseCase(repository)

    private val _state = MutableStateFlow<CreateNoteState>(CreateNoteState.Creation())
    val state = _state.asStateFlow()

    fun processCommand(command: CreateNoteCommand) {
        viewModelScope.launch {
            when (command) {
                CreateNoteCommand.Back -> {
                    _state.update { CreateNoteState.Finished }
                }
                is CreateNoteCommand.InputDescription -> {
                    _state.update { prev->
                        if (prev is CreateNoteState.Creation){
                            prev.copy(
                                description = command.description,
                                isSaveEnable = (command.description.isNotBlank() && prev.title.isNotBlank())
                            )
                        }else{
                            prev
                        }
                    }
                }
                is CreateNoteCommand.InputTitle -> {
                    _state.update { prev->
                        if (prev is CreateNoteState.Creation){
                            prev.copy(title = command.title,
                                isSaveEnable = (prev.description.isNotBlank() && command.title.isNotBlank()))
                        }else{
                            prev
                        }
                    }
                }
                CreateNoteCommand.Save -> {
                    _state.update { previous->
                        if (previous is CreateNoteState.Creation) {
                            addNoteUseCase(title = previous.title, content = previous.description)
                            CreateNoteState.Finished
                        }else{
                            previous
                        }
                    }
                }
            }
        }
    }

}

sealed interface CreateNoteCommand{
    data class InputTitle(val title: String) : CreateNoteCommand

    data class InputDescription(val description: String) : CreateNoteCommand

    data object Save : CreateNoteCommand

    data object Back : CreateNoteCommand
}

sealed interface CreateNoteState {
    data class Creation(
        val title: String = "",
        val description: String = "",
        val isSaveEnable: Boolean = false
    ) : CreateNoteState

    data object Finished : CreateNoteState
}