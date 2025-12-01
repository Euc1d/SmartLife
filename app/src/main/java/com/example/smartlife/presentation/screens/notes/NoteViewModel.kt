package com.example.smartlife.presentation.screens.notes

import android.content.Context
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartlife.data.NoteRepositoryImpl
import com.example.smartlife.data.TestNotesRepositoryImpl
import com.example.smartlife.domain.AddNoteUseCase
import com.example.smartlife.domain.GetAllNotesUseCase
import com.example.smartlife.domain.Note
import com.example.smartlife.domain.NotesRepository
import com.example.smartlife.domain.SearchNotesUseCase
import com.example.smartlife.domain.SwitchPinnedStatusUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class NoteViewModel(context: Context): ViewModel() {

    private val repository = NoteRepositoryImpl.getInstance(context)
    private val getAllNotesUseCase = GetAllNotesUseCase(repository)
    private val searchNotesUseCase = SearchNotesUseCase(repository)
    private val switchPinnedStatusUseCase = SwitchPinnedStatusUseCase(repository)


    private val _state = MutableStateFlow(NoteScreenState())
    val state = _state.asStateFlow()

    private val query = MutableStateFlow("")

    init {
        query
            .onEach { input ->
                _state.update { note ->
                    note.copy( query = input )
                }
            }
            .flatMapLatest { str ->
            if (str.isBlank()){
                getAllNotesUseCase()
            }else{
                searchNotesUseCase(str)
            }
        }.onEach { res ->
            val pinned = res.filter{
                it.isPinned
            }
            val others = res.filter{
                !it.isPinned
            }
            _state.update {
                it.copy(pinnedList = pinned, otherList = others)
            }
        }.launchIn(viewModelScope)

    }

    fun processCommand(command: NotesCommand){
        viewModelScope.launch {
            when(command){
                is NotesCommand.InputSearchQuery -> {
                    query.update {
                        command.query.trim()
                    }

                }
                is NotesCommand.SwitchPinnedStatus -> {
                    switchPinnedStatusUseCase(command.noteId)
                }
            }
        }
    }


}


sealed interface NotesCommand{
    data class InputSearchQuery(val query: String): NotesCommand
    data class SwitchPinnedStatus(val noteId: Int): NotesCommand
}


data class NoteScreenState(
    val query: String="",
    val pinnedList: List<Note> = listOf(),
    val otherList: List<Note> = listOf()
)

