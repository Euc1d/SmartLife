package com.example.smartlife.presentation.navigation

import android.os.Bundle
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.smartlife.presentation.screens.creations.CreateNoteScreen
import com.example.smartlife.presentation.screens.edits.EditNoteScreen
import com.example.smartlife.presentation.screens.notes.NotesScreen

@Composable
fun NavGraph(){
    val navController = rememberNavController()

    NavHost(
        navController=navController,
        startDestination = Screen.Notes.route
    ){
        composable(Screen.Notes.route) {
            NotesScreen(
                onNoteClick = { note ->
                    navController.navigate(Screen.EditScreen.createRoute(note.id))
                },
                onFABClick = {
                    navController.navigate(Screen.AddNote.route)
                }
            )
        }
        composable(Screen.AddNote.route) {
            CreateNoteScreen(
                onFinished = {
                    navController.popBackStack()
                }
            )
        }
        composable(Screen.EditScreen.route) {
            val id = Screen.EditScreen.getIdFromBundle(it.arguments)
            EditNoteScreen(
                noteId = id,
                onFinished = {
                    navController.popBackStack()
                }
            )
        }
    }
}
sealed class Screen(val route: String){
    data object Notes: Screen("Notes")
    data object AddNote: Screen("AddNote")

    data object EditScreen: Screen("EditNote/{note_id}"){
        fun createRoute(noteId: Int): String{
            return "EditNote/$noteId"
        }
        fun getIdFromBundle(arguments: Bundle?): Int{
            return arguments?.getString("note_id")?.toInt() ?:0
        }
    }
}