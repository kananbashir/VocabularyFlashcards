package com.example.vocabularyflashcards.data.db.entities


data class Deck(

    var title: String,
    var flashcardList: MutableList<Flashcard>,
    var color: Int,
    var isFavourite: Boolean

)