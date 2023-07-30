package com.example.vocabularyflashcards.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.vocabularyflashcards.R
import com.example.vocabularyflashcards.data.db.entities.Deck
import com.example.vocabularyflashcards.data.db.entities.Flashcard
import com.example.vocabularyflashcards.data.db.entities.Question
import com.example.vocabularyflashcards.data.db.entities.Quiz
import com.example.vocabularyflashcards.ui.main.listeners.QuizOnItemClickListener

class LastQuizResultsAdapter: RecyclerView.Adapter<LastQuizResultsAdapter.LastQuizResultsViewHolder> () {

    private var quizList: List<Quiz> = listOf()
    private var quizOnItemClickListener: QuizOnItemClickListener? = null

    inner class LastQuizResultsViewHolder (itemView: View): RecyclerView.ViewHolder(itemView) {
        val itemDecksAndFlashcards: TextView = itemView.findViewById(R.id.tvDecksAndFlashcardsQuizResultItem)
        val itemDate: TextView = itemView.findViewById(R.id.tvDateQuizResultItem)
        val itemRightAnswers: TextView = itemView.findViewById(R.id.tvRightAnswersQuizResultItem)
        val itemWrongAnswers: TextView = itemView.findViewById(R.id.tvWrongAnswersQuizResultItem)
        val itemNotAnswered: TextView = itemView.findViewById(R.id.tvNotAnsweredQuizResultItem)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LastQuizResultsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.quiz_result_item_layout, parent, false)
        return LastQuizResultsViewHolder(view)
    }

    override fun getItemCount(): Int {
        return quizList.size
    }

    override fun onBindViewHolder(holder: LastQuizResultsViewHolder, position: Int) {
        val currentItem = quizList[position]

        holder.itemDecksAndFlashcards.text = "${currentItem.deckList.size} decks / ${getAllFlashcards(currentItem.deckList).size} flashcards"
        holder.itemDate.text = currentItem.date
        holder.itemView.setOnClickListener { quizOnItemClickListener?.onItemClick(currentItem) }
        getQuizResults (currentItem.questionList) { rightAnswers, wrongAnswers, notAnswered ->
            holder.itemRightAnswers.text = "$rightAnswers right ans."
            holder.itemWrongAnswers.text = "$wrongAnswers wrong ans."
            holder.itemNotAnswered.text = "$notAnswered not answered"
        }
    }

    private fun getAllFlashcards (deckList: List<Deck>): List<Flashcard> {
        var allFlashcardsList = mutableListOf<Flashcard>()

        for (deck in deckList) {
            for (flashcard in deck.flashcardList) {
                allFlashcardsList.add(flashcard)
            }
        }

        return allFlashcardsList
    }

    private fun getQuizResults (questionList: List<Question>, callback: (Int, Int, Int) -> Unit) {
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

    fun setListener (listener: QuizOnItemClickListener) {
        quizOnItemClickListener = listener
    }

    fun setQuizList (quizList: List<Quiz>) {
        this.quizList = quizList
    }
}