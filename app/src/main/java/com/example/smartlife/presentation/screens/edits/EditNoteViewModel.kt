package com.example.smartlife.presentation.screens.edits

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartlife.domain.ContentItem
import com.example.smartlife.domain.ContentItem.Image
import com.example.smartlife.domain.ContentItem.Text
import com.example.smartlife.domain.DeleteNoteUseCase
import com.example.smartlife.domain.EditNoteUseCase
import com.example.smartlife.domain.GetNoteUseCase
import com.example.smartlife.domain.Note
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel(assistedFactory = EditNoteViewModel.Factory::class)
class EditNoteViewModel @AssistedInject constructor(
    @Assisted("id") val id : Int,
    val editNoteUseCase : EditNoteUseCase,
    val getNotesUseCase : GetNoteUseCase,
    val deleteNoteUseCase: DeleteNoteUseCase
    ) : ViewModel() {

    private val _state = MutableStateFlow<EditNoteState>(EditNoteState.Init)
    val state = _state.asStateFlow()
    init {
        viewModelScope.launch {
            _state.update {
                val note: Note = getNotesUseCase(id)
                val content: List<ContentItem> = if (note.content.lastOrNull() !is ContentItem.Text) {
                    note.content + ContentItem.Text("")
                } else {
                    note.content
                }
                EditNoteState.Editing(note.copy(content = content))
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
                        val newContent = prev.note.content
                            .mapIndexed { index, contentItem ->
                                if (index == command.index && contentItem is ContentItem.Text) {
                                    contentItem.copy(content = command.content)
                                } else {
                                    contentItem
                                }
                            }
                            val editedNote = prev.note.copy(content = newContent)
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

                is EditNoteCommand.AddImage -> {
                    _state.update { prev ->
                        if (prev is EditNoteState.Editing) {
                            val newItems = prev.note.content.toMutableList()
                            val lastItem = newItems.last()
                            if (lastItem is ContentItem.Text && lastItem.content.isBlank()) {
                                newItems.removeAt(newItems.lastIndex)
                            }
                            newItems.add(Image(command.uri.toString()))
                            newItems.add(Text(""))
                            newItems.toList()
                            prev.copy(note = prev.note.copy(content = newItems))
                        } else {
                            prev
                        }
                    }
                }

                is EditNoteCommand.DeleteImage -> {
                    _state.update { prev ->
                        if (prev is EditNoteState.Editing) {
                            prev.note.content.toMutableList().apply {
                                removeAt(command.index)
                            }.let {
                                prev.copy(note = prev.note.copy(content = it))
                            }
                        } else {
                            prev
                        }
                    }
                }
            }
        }
    }
    @AssistedFactory
    interface Factory{
        fun create(@Assisted("id") id: Int): EditNoteViewModel
    }

}

sealed interface EditNoteCommand{
    data class InputTitle(val title: String) : EditNoteCommand

    data class InputDescription(val content: String, val index: Int) : EditNoteCommand

    data class AddImage(val uri: Uri): EditNoteCommand
    data class DeleteImage(val index: Int): EditNoteCommand
    data object Save : EditNoteCommand

    data object Back : EditNoteCommand

    data class Delete(val id: Int): EditNoteCommand

}

sealed interface EditNoteState {
    data class Editing(
        val note : Note
    ) : EditNoteState{
        val isEnabled : Boolean
            get(){
                return when{
                    note.title.isBlank() -> false
                    note.content.isEmpty() -> false
                    else -> {
                        return note.content.any{
                            it !is ContentItem.Text || it.content.isNotBlank()
                        }
                    }
                }
            }
    }
    object Init: EditNoteState

    data object Finished : EditNoteState
}

