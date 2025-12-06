package com.example.smartlife.presentation.screens.notes

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.smartlife.R
import com.example.smartlife.domain.Note
import com.example.smartlife.presentation.ui.theme.OtherNotesColors
import com.example.smartlife.presentation.ui.theme.PinnedNotesColors
import com.example.smartlife.presentation.units.DateFormatter


@Composable
fun NotesScreen(
    modifier: Modifier = Modifier,
    viewModel: NoteViewModel = hiltViewModel(),
    onNoteClick: (Note) -> Unit,
    onFABClick: () -> Unit,
) {


    val state by viewModel.state.collectAsState()
    val currentState = state
    Scaffold(
        modifier = modifier,
        floatingActionButton = {
            FloatingActionButton(
                onClick = onFABClick,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                shape = CircleShape
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_add_note),
                    contentDescription = "Add Note"
                )
            }
        }
    )
    { innerPadding ->
        LazyColumn(
            contentPadding = innerPadding
        ) {
            item {
                Title(
                    modifier = Modifier.padding(horizontal = 24.dp),
                    str = "ALL Notes"
                )
            }
            item {
                Spacer(Modifier.height(16.dp))
            }
            item {
                SearchBar(
                    modifier = Modifier.padding(horizontal = 24.dp),
                    query = currentState.query,
                    onQueryChange = {
                        viewModel.processCommand(NotesCommand.InputSearchQuery(it))
                    }
                )
            }
            item {
                Spacer(Modifier.height(24.dp))
            }
            item {
                SubTitle(
                    modifier = Modifier.padding(horizontal = 24.dp),
                    str = if (currentState.pinnedList.size <1){
                        "No one Note is Pinned"
                    }else{
                        "Pinned"
                    }
                )
            }
            item {
                Spacer(Modifier.height(16.dp))
            }
            item {
                LazyRow(
                    modifier = Modifier,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(horizontal = 24.dp)
                ) {
                    itemsIndexed(
                        items = currentState.pinnedList,
                        key = { _, note -> note.id }
                    ) { index, note ->
                        NoteCard(
                            modifier = Modifier.widthIn(max = 160.dp).heightIn(max = 250.dp),
                            note = note,
                            noteClicked ={onNoteClick(note)},
                            backGroundColor = PinnedNotesColors[(index % PinnedNotesColors.size)],
                            onLongClick = {
                                viewModel.processCommand(NotesCommand.SwitchPinnedStatus(it.id))
                            }
                        )
                    }
                }
            }
            item {
                Spacer(Modifier.height(24.dp))
            }
            item {
                SubTitle(
                    modifier = Modifier.padding(horizontal = 24.dp),
                    str = "Others"
                )
            }
            item {
                Spacer(Modifier.height(16.dp))
            }
            itemsIndexed(
                items = currentState.otherList,
                key = { _, note -> note.id }
            ) { index, note ->

                NoteCard(
                    modifier = modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    note = note,
                    noteClicked = {onNoteClick(note)},
                    backGroundColor = OtherNotesColors[(index % OtherNotesColors.size)],
                    onLongClick = {
                        viewModel.processCommand(NotesCommand.SwitchPinnedStatus(it.id))
                    }
                )
                Spacer(Modifier.height(8.dp))

            }
        }
    }

}
fun cheker(id: Int): Int{
    if (id % 2 ==0 ) {
        return R.drawable.back1
    }
    else{
        return R.drawable.back2
    }
}

@Composable
fun NoteCard(
    modifier: Modifier = Modifier,
    note: Note,
    backGroundColor: Color,
    noteClicked: (Note) -> Unit,
    onLongClick: (Note) -> Unit) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(backGroundColor)
            .combinedClickable(
                onClick = { noteClicked(note) },
                onLongClick = { onLongClick(note) })
    ) {
            Box {
                Image(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .heightIn(max = 110.dp)
                    ,
                    contentDescription = "Note $note",
                    contentScale = ContentScale.FillWidth,
                    painter = painterResource(cheker(note.id)),
                )
                Column(modifier.padding(16.dp)) {
                    Spacer(Modifier.height(10.dp))
                    Text(
                        text = note.title,
                        fontSize = 14.sp,
                        color =
                            if (cheker(note.id)==R.drawable.back1){
                                Color.White
                        }else{
                                MaterialTheme.colorScheme.onSurface
                        },
                        maxLines = 1
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = DateFormatter.formatDateTimeToString(note.updatedAt),
                        fontSize = 12.sp,
                        color = Color.White
                            //MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(Modifier.height(16.dp))
                    Text(
                        text = note.content,
                        maxLines = 4,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
    }
}

@Composable
private fun Title(
    modifier: Modifier = Modifier,
    str: String
) {
    Text(
        modifier = modifier,
        text = str,
        fontSize = 24.sp,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onBackground
    )
}

@Composable
private fun SearchBar(
    modifier: Modifier = Modifier,
    query: String,
    onQueryChange: (String) -> Unit
) {
    TextField(
        modifier = modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                shape = RoundedCornerShape(10.dp)
            ),
        value = query,
        onValueChange = onQueryChange,
        placeholder = {
            Text(
                text = "Search...",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        colors = TextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.surface,
            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent
        ),
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "SearchNotes",
                tint = MaterialTheme.colorScheme.onSurface
            )
        },
        shape = RoundedCornerShape(10.dp),
    )
}

@Composable
private fun SubTitle(
    modifier: Modifier = Modifier,
    str: String
) {
    Text(
        modifier = modifier,
        text = str,
        fontSize = 14.sp,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
}