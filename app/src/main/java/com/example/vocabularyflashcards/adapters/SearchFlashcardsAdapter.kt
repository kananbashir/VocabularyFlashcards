package com.example.vocabularyflashcards.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.vocabularyflashcards.R
import com.example.vocabularyflashcards.data.db.entities.Flashcard
import com.example.vocabularyflashcards.ui.UserViewModel
import com.example.vocabularyflashcards.ui.main.listeners.SearchOnItemClickListener

class SearchFlashcardsAdapter (private val userViewModel: UserViewModel): RecyclerView.Adapter<SearchFlashcardsAdapter.SearchFlashcardsViewHolder>() {

    private var flashcardsList: List<Flashcard> = listOf()
    private var searchOnItemClickListener: SearchOnItemClickListener? = null

    inner class SearchFlashcardsViewHolder (itemView: View): RecyclerView.ViewHolder(itemView) {
        val itemFlashcardWord: TextView = itemView.findViewById(R.id.tvFlashcardWordSearchFlashcardItem)
        val itemDeckTitle: TextView = itemView.findViewById(R.id.tvDeckTitleSearchFlashcardItem)
        val itemMarkedStatus: ImageView = itemView.findViewById(R.id.ivMarkedIconSearchFavouritesItem)
        val itemDetailsButton: ImageView = itemView.findViewById(R.id.ivGoDetailsSearchFlashcardsItem)
        val itemBackground: CardView = itemView.findViewById(R.id.cdSearchFlashcardsItemBackground)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchFlashcardsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.search_flashcards_item_layout, parent, false)
        return SearchFlashcardsViewHolder(view)
    }

    override fun getItemCount(): Int {
        return flashcardsList.size
    }

    override fun onBindViewHolder(holder: SearchFlashcardsViewHolder, position: Int) {
        val currentItem = flashcardsList[position]

        holder.itemFlashcardWord.text = currentItem.word
        holder.itemDeckTitle.text = currentItem.deckTitle
        holder.itemBackground.setCardBackgroundColor(currentItem.color)
        holder.itemDetailsButton.setOnClickListener { searchOnItemClickListener?.onItemClick(currentItem) }
        if (currentItem.isMarked) {
            holder.itemMarkedStatus.visibility = View.VISIBLE
        } else {
            holder.itemMarkedStatus.visibility = View.GONE
        }
    }

    fun setFlashcardList (flashcardList: List<Flashcard>) {
        this.flashcardsList = flashcardList
        notifyDataSetChanged()
    }

    fun setListener (listener: SearchOnItemClickListener) {
        searchOnItemClickListener = listener
    }

//    private fun findDeckBasedOnFlashcard (flashcard: Flashcard, callback: (Deck) -> Unit) {
//        userViewModel.getOnlineUser { user ->
//            for (deck in user.deckList) {
//                if (flashcard in deck.flashcardList) {
//                    callback (deck)
//                }
//            }
//        }
//    }
}