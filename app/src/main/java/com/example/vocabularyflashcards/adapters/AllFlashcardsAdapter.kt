package com.example.vocabularyflashcards.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.vocabularyflashcards.R
import com.example.vocabularyflashcards.data.db.entities.Deck
import com.example.vocabularyflashcards.data.db.entities.Flashcard

class AllFlashcardsAdapter: RecyclerView.Adapter<AllFlashcardsAdapter.AllFlashcardsViewHolder>() {

    private var flashcardList: List<Flashcard> = listOf()

    inner class AllFlashcardsViewHolder (itemView: View): RecyclerView.ViewHolder(itemView) {
        val itemFlashcard: TextView = itemView.findViewById(R.id.tvFlashcardAllFlashcardsItemLayout)
        val itemBackground: CardView = itemView.findViewById(R.id.cvAllFlashcardsItem)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AllFlashcardsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.all_flashcards_item_layout, parent, false)
        return AllFlashcardsViewHolder(view)
    }

    override fun getItemCount(): Int {
        return flashcardList.size
    }

    override fun onBindViewHolder(holder: AllFlashcardsViewHolder, position: Int) {
        val currentItem = flashcardList[position]

        holder.itemFlashcard.text = currentItem.word
        holder.itemBackground.setCardBackgroundColor(currentItem.color)
    }

    fun setFlashcardList (flashcardList: List<Flashcard>) {
        this.flashcardList = flashcardList.shuffled()
        notifyDataSetChanged()
    }
}