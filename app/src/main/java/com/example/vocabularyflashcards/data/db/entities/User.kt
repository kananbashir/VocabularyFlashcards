package com.example.vocabularyflashcards.data.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity (tableName = "user_table")
data class User (

    var username: String,
    var password: String,
    var profilePhoto: String,
    var deckList: MutableList<Deck>,
    var quizList: MutableList<Quiz>,
    var markedFlashcardList: MutableList<Flashcard>,
    var isOnline: Boolean
    ) {

    @PrimaryKey (autoGenerate = true)
    var id: Int? = null
}

