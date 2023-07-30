package com.example.vocabularyflashcards.data.db.entities

data class Quiz(

    var questionList: MutableList<Question>,
    var deckList: MutableList<Deck>,
    var date: String,
    var timer: String

)
