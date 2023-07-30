package com.example.vocabularyflashcards.ui

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.vocabularyflashcards.data.db.entities.Deck
import com.example.vocabularyflashcards.data.db.entities.Flashcard
import com.example.vocabularyflashcards.data.db.entities.Question
import com.example.vocabularyflashcards.data.db.entities.Quiz
import com.example.vocabularyflashcards.data.db.entities.User
import com.example.vocabularyflashcards.data.repositories.UserRepository
import com.example.vocabularyflashcards.utils.AppUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class UserViewModel (private var repository: UserRepository) : ViewModel() {

    val userList: LiveData<List<User>> = repository.getAllUser()
    private var studyModeCheckedDecks: List<Deck> = listOf()
    private var quizModeQuiz: Quiz = Quiz(mutableListOf(), mutableListOf(), "", "")

    fun upsert (user: User) = CoroutineScope(Dispatchers.Main).launch {
        repository.upsert(user)
    }

    fun delete (user: User) = CoroutineScope(Dispatchers.Main).launch {
        repository.delete(user)
    }

    fun makeUserOnline(username: String) {
        userList.value?.let {
            for (user in it) {
                if (user.username == username) {
                    user.isOnline = true
                    upsert(user)
                    break
                }
            }
        }
    }

    fun makeUserOffline() {
        userList.value?.let {
            for (user in it) {
                if (user.isOnline) {
                    user.isOnline = false
                    upsert(user)
                    break
                }
            }
        }
    }

    fun addNewDeck (deck: Deck) {
        Log.i("TCP", "New deck - Title(${deck.title}, Color(${deck.color}")
        getOnlineUser { onlineUser ->
            onlineUser.deckList.add(deck)
            upsert(onlineUser)
        }
    }

    fun getOnlineUser (callback: (User) -> Unit) {
        userList.value?.let {
            for (user in it) {
                if (user.isOnline) {
                    callback(user)
                }
            }
        }
    }

    fun changeDeckFavouriteStatus (deck: Deck) {
        getOnlineUser { user ->
            val indexDeck = user.deckList.indexOf(deck)
            deck.isFavourite = !deck.isFavourite

            user.deckList.set(indexDeck, deck)
            upsert(user)
        }
    }

    fun updateDeck (newDeck: Deck, oldDeck: Deck) {
        getOnlineUser { user ->
            val indexOldDeck = user.deckList.indexOf(oldDeck)
            user.deckList.set(indexOldDeck, newDeck)
            upsert(user)
        }
    }

    fun updateDeck (newDeckList: List<Deck>, oldDeckList: List<Deck>) {
        for (i in oldDeckList.indices) {
            updateDeck(studyModeCheckedDecks[i], oldDeckList[i])
        }
    }

    fun removeDeck (deck: Deck) {
        getOnlineUser { user ->
            user.deckList.remove(deck)
            upsert(user)
        }
    }

    fun setStudyModeCheckedDecksList (checkedDecksList: List<Deck>) {
        studyModeCheckedDecks = checkedDecksList
    }

    fun getStudyModeCheckedDecksList (callback: (List<Deck>) -> Unit) {
        callback (studyModeCheckedDecks)
    }

    fun updateMarkStatus (flashcard2: Flashcard) {
            for (deck in studyModeCheckedDecks) {
                for (flashcard in deck.flashcardList) {
                    if (flashcard == flashcard2) {
                        flashcard.isMarked = !flashcard.isMarked
                    }
                }
            }
    }

    fun setNewQuiz (deckList: List<Deck>) {
        val questionsList = createNewQuestionList(deckList)
        quizModeQuiz = Quiz (questionsList, deckList.toMutableList(), "", "")
    }

    fun setActiveQuiz (quiz: Quiz) {
        quizModeQuiz = quiz
    }

    fun getActiveQuiz (callback: (Quiz) -> Unit) {
        callback (quizModeQuiz)
    }

    private fun createNewQuestionList (deckList: List<Deck>): MutableList<Question> {
        val questionList = mutableListOf<Question>()
        val usedQuestions = mutableListOf<Flashcard>()

        AppUtils.getAllFlashcards(deckList) { allFlashcardsList ->
            for (i in allFlashcardsList.indices) {
                var randomQuestionIndex = allFlashcardsList.indices.random()
                if (usedQuestions.isNotEmpty()) {
                    while (allFlashcardsList[randomQuestionIndex] in usedQuestions) {
                        randomQuestionIndex = allFlashcardsList.indices.random()
                    }
                }
                usedQuestions.add(allFlashcardsList[randomQuestionIndex])

                val question = allFlashcardsList[randomQuestionIndex] // random question (which is not in usedQuestion)
                val answers = mutableListOf<Flashcard>()

                for (j in 0..2) {
                    var randomAnswer = allFlashcardsList.indices.random()

                    while (allFlashcardsList[randomAnswer] in answers || allFlashcardsList[randomAnswer] == question) {
                        if (answers.isEmpty() && allFlashcardsList[randomAnswer] != question) {
                            break
                        }
                        randomAnswer = allFlashcardsList.indices.random()
                    }

                    answers.add(allFlashcardsList[randomAnswer])
                }

                answers.add(question)
                val shuffledAnswerList = answers.shuffled()

                questionList.add(
                    Question(
                        question.meaning,
                        shuffledAnswerList[0].word,
                        shuffledAnswerList[1].word,
                        shuffledAnswerList[2].word,
                        shuffledAnswerList[3].word,
                        question.word,
                        "",
                        ""
                    )
                )
            }
        }

        return questionList
    }

    fun addNewUserQuiz (quiz: Quiz) {
        getOnlineUser { user ->
            user.quizList.add(quiz)
            upsert(user)
        }
    }
}