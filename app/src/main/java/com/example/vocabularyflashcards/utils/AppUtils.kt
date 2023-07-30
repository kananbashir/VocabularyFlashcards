package com.example.vocabularyflashcards.utils

import com.example.vocabularyflashcards.data.db.entities.Deck
import com.example.vocabularyflashcards.data.db.entities.Flashcard
import com.example.vocabularyflashcards.data.db.entities.Question

class AppUtils {

    companion object {

        fun getAllFlashcards (deckList: List<Deck>): List<Flashcard> {
            val allFlashcardsList = mutableListOf<Flashcard>()

            for (deck in deckList) {
                for (flashcard in deck.flashcardList) {
                    allFlashcardsList.add(flashcard)
                }
            }

            return allFlashcardsList
        }

        fun getAllFlashcards (deckList: List<Deck>, callback: (List<Flashcard>) -> Unit) {
            val allFlashcardsList = mutableListOf<Flashcard>()

            for (deck in deckList) {
                for (flashcard in deck.flashcardList) {
                    allFlashcardsList.add(flashcard)
                }
            }

            callback (allFlashcardsList)
        }

        fun getQuizResults (questionList: List<Question>, callback: (Int, Int, Int) -> Unit) {
            var rightAnswers = 0
            var wrongAnswers = 0
            var notAnswered = 0

            for (question in questionList) {
                if (question.result == "Correct") {
                    rightAnswers++
                } else if (question.result == "Wrong") {
                    wrongAnswers++
                } else {
                    notAnswered++
                }
            }

            callback (rightAnswers, wrongAnswers, notAnswered)
        }

        fun getUsedDecksAndMarkedFlashcards (deckList: List<Deck>, callback: (String, String) -> Unit) {
            var usedDecks = ""
            var markedFlashcards = ""
            for (deck in deckList) {
                usedDecks = if (usedDecks.isNotEmpty()) {
                    "$usedDecks\n―\n${deck.title} - ${deck.flashcardList.size} cards"
                } else {
                    "${deck.title} - ${deck.flashcardList.size} cards"
                }

                for (flashcard in deck.flashcardList) {
                    if (flashcard.isMarked) {
                        markedFlashcards = if (markedFlashcards.isNotEmpty()) {
                            "$markedFlashcards\n―\n${flashcard.word}"
                        } else {
                            flashcard.word
                        }
                    }
                }
            }
            callback (usedDecks, markedFlashcards)
        }

        fun getRandomSelectedDecksList(deckList: List<Deck>, callback: (List<Deck>) -> Unit) {
            var randomSelectedDecksList = mutableListOf<Deck>()

            if (deckList.size == 1) {
                randomSelectedDecksList = deckList.toMutableList()
            } else {
                val randomNumberOfDecks = (1 until deckList.size).random()

                for (i in 1..randomNumberOfDecks) {
                    var randomDeck = deckList.random()
                    while (randomDeck in randomSelectedDecksList) {
                        randomDeck = deckList.random()
                    }
                    randomSelectedDecksList.add(randomDeck)
                }
            }

                callback(randomSelectedDecksList)
        }


        fun getFavouriteDecks (deckList: List<Deck>): List<Deck> {
            val favoriteDecks = mutableListOf<Deck>()

            for (deck in deckList) {
                if (deck.isFavourite) {
                    favoriteDecks.add(deck)
                }
            }

            return favoriteDecks
        }
    }
}