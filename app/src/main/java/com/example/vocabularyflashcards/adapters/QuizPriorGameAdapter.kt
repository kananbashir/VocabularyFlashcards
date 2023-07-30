package com.example.vocabularyflashcards.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.vocabularyflashcards.R
import com.example.vocabularyflashcards.data.db.entities.Flashcard

class QuizPriorGameAdapter: RecyclerView.Adapter<QuizPriorGameAdapter.QuizPriorGameViewHolder>() {

    private var flashcardList: List<Flashcard> = listOf()

    inner class QuizPriorGameViewHolder (itemView: View): RecyclerView.ViewHolder(itemView) {
        val itemNumber: TextView = itemView.findViewById(R.id.tvNumberQuizPriorGameItem)
        val itemNumberLayout: LinearLayout = itemView.findViewById(R.id.lyNumberQuizPriorGameItem)
        val itemFlashcardWord: TextView = itemView.findViewById(R.id.tvFlashcardQuizPriorGameItem)
        val itemFlashcardWordLayout: LinearLayout = itemView.findViewById(R.id.lyFlashcardWordQuizPriorGameItem)
        val itemFlashcardDeck: TextView = itemView.findViewById(R.id.tvDeckTitleQuizPriorGameItem)
        val itemFlashcardDeckLayout: LinearLayout = itemView.findViewById(R.id.lyDeckTitleQuizPriorGameItem)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuizPriorGameViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.quiz_prior_game_item_layout, parent, false)
        return QuizPriorGameViewHolder(view)
    }

    override fun getItemCount(): Int {
        return flashcardList.size
    }

    override fun onBindViewHolder(holder: QuizPriorGameViewHolder, position: Int) {
        val currentItem = flashcardList[position]

        holder.itemNumber.text = "${position+1}."
        holder.itemNumberLayout.setBackgroundColor(currentItem.color)
        holder.itemFlashcardWord.text = currentItem.word
        holder.itemFlashcardWordLayout.setBackgroundColor(currentItem.color)
        holder.itemFlashcardDeck.text = currentItem.deckTitle
        holder.itemFlashcardDeckLayout.setBackgroundColor(currentItem.color)
    }

    fun setFlashcardList (flashcardList: List<Flashcard>) {
        this.flashcardList = flashcardList
        notifyDataSetChanged()
    }
}