package com.example.vocabularyflashcards.data.db.entities

data class Question(

    var question: String,
    var answer1: String,
    var answer2: String,
    var answer3: String,
    var answer4: String,
    var correctAnswer: String,
    var userAnswer: String,
    var result: String

)
