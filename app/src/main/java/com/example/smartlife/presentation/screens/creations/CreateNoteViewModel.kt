package com.example.smartlife.presentation.screens.creations

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartlife.domain.AddNoteUseCase
import com.example.smartlife.domain.ContentItem
import com.example.smartlife.domain.ContentItem.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateNoteViewModel @Inject constructor(private val addNoteUseCase: AddNoteUseCase) :
    ViewModel() {


    private val _state = MutableStateFlow<CreateNoteState>(CreateNoteState.Creation())
    val state = _state.asStateFlow()

    fun processCommand(command: CreateNoteCommand) {
        viewModelScope.launch {
            when (command) {
                is CreateNoteCommand.Back -> {
                    _state.update { CreateNoteState.Finished }
                }

                is CreateNoteCommand.InputContent -> {
                    _state.update { prev ->
                        if (prev is CreateNoteState.Creation) {
                            val newContent = prev.content
                                .mapIndexed { index, contentItem ->
                                    if (index == command.index && contentItem is ContentItem.Text) {
                                        contentItem.copy(content = command.content)
                                    } else {
                                        contentItem
                                    }
                                }
                            prev.copy(content = newContent)
                        } else {
                            prev
                        }
                    }
                }

                is CreateNoteCommand.InputTitle -> {
                    _state.update { prev ->
                        if (prev is CreateNoteState.Creation) {
                            prev.copy(title = command.title)
                        } else {
                            prev
                        }
                    }
                }

                is CreateNoteCommand.Save -> {
                    _state.update { previous ->
                        if (previous is CreateNoteState.Creation) {
                            val content = previous.content.filter { contentItem ->
                                contentItem !is ContentItem.Text || contentItem.content.isNotBlank()
                            }
                            addNoteUseCase(title = previous.title, content = content)
                            CreateNoteState.Finished
                        } else {
                            previous
                        }
                    }
                }

                is CreateNoteCommand.AddImage -> {
                    _state.update { prev ->
                        if (prev is CreateNoteState.Creation) {
                            val newItems = prev.content.toMutableList()
                            val lastItem = newItems.last()
                            if (lastItem is ContentItem.Text && lastItem.content.isBlank()) {
                                newItems.removeAt(newItems.lastIndex)
                            }
                            newItems.add(Image(command.uri.toString()))
                            newItems.add(Text(""))
                            prev.copy(content = newItems)
                        } else {
                            prev
                        }
                    }
                }

                is CreateNoteCommand.DeleteImage -> {
                    _state.update { prev ->
                        if (prev is CreateNoteState.Creation) {
                            prev.content.toMutableList().apply {
                                removeAt(command.index)
                            }.let {
                                prev.copy(content = it)
                            }
                        } else {
                            prev
                        }
                    }
                }
            }
        }
    }

}

sealed interface CreateNoteCommand {
    data class InputTitle(val title: String) : CreateNoteCommand

    data class InputContent(val content: String, val index: Int) : CreateNoteCommand

    data class AddImage(val uri: Uri) : CreateNoteCommand

    data class DeleteImage(val index: Int) : CreateNoteCommand

    data object Save : CreateNoteCommand

    data object Back : CreateNoteCommand
}

sealed interface CreateNoteState {
    data class Creation(
        val title: String = "",
        val content: List<ContentItem> = listOf(ContentItem.Text(""))
    ) : CreateNoteState {
        val isSaveEnable: Boolean
            get() {
                return when {
                    title.isBlank() -> false
                    content.isEmpty() -> false
                    else -> {
                        return content.any {
                            it !is ContentItem.Text || it.content.isNotBlank()
                        }
                    }
                }
            }
    }

    data object Finished : CreateNoteState
}