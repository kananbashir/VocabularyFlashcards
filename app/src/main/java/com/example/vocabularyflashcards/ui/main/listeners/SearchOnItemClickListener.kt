package com.example.vocabularyflashcards.ui.main.listeners

import com.example.vocabularyflashcards.data.db.entities.Deck
import com.example.vocabularyflashcards.data.db.entities.Flashcard

interface SearchOnItemClickListener {

    fun onItemClick (deck: Deck, navLocation: String, position: Int)
    fun onItemClick (flashcard: Flashcard)

}