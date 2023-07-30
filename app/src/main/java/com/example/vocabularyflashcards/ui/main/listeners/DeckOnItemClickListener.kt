package com.example.vocabularyflashcards.ui.main.listeners

import com.example.vocabularyflashcards.data.db.entities.Deck

interface DeckOnItemClickListener {

    fun onItemClick (deck: Deck, navLocation: String, position: Int)

}