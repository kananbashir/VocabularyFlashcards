package com.example.vocabularyflashcards.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.vocabularyflashcards.R
import com.example.vocabularyflashcards.data.db.entities.Flashcard

class MarkedFlashcardsAdapter: RecyclerView.Adapter<MarkedFlashcardsAdapter.MarkedFlashcardsViewHolder>() {

    private var markedFlashcardsList: List<Flashcard> = listOf()

    inner class MarkedFlashcardsViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val itemFlashcardWord: TextView = itemView.findViewById(R.id.tvFlashcardWordMarkedFlashcardsItem)
        val itemDeckTitle: TextView = itemView.findViewById(R.id.tvDeckTitleMarkedFlashcardsItem)
        val itemBackground: CardView = itemView.findViewById(R.id.cvBackgroundMarkedFlashcardsItem)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MarkedFlashcardsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.marked_flashcards_item_layout, parent, false)
        return MarkedFlashcardsViewHolder(view)
    }

    override fun getItemCount(): Int {
        return markedFlashcardsList.size
    }

    override fun onBindViewHolder(holder: MarkedFlashcardsViewHolder, position: Int) {
        val currentItem = markedFlashcardsList[position]

        holder.itemFlashcardWord.text = currentItem.word
        holder.itemDeckTitle.text = currentItem.deckTitle
        holder.itemBackground.setCardBackgroundColor(currentItem.color)
    }

    fun setMarkedFlashcardList (markedFlashcardsList: List< Flashcard>) {
        this.markedFlashcardsList = markedFlashcardsList
        notifyDataSetChanged()
    }
}