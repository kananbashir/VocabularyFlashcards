package com.example.vocabularyflashcards.data.db.entities

data class Flashcard(

    var word: String,
    var meaning: String,
    var deckTitle: String,
    var color: Int,
    var isMarked: Boolean

)
