package com.example.vocabularyflashcards.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.vocabularyflashcards.R
import com.example.vocabularyflashcards.data.db.entities.Deck
import com.example.vocabularyflashcards.ui.UserViewModel
import com.example.vocabularyflashcards.ui.main.listeners.DeckOnItemClickListener

class DeckAdapter(private val userViewModel: UserViewModel): RecyclerView.Adapter<DeckAdapter.DeckViewHolder>() {

    private var deckList: List<Deck> = listOf()
    private var deckOnItemClickListener: DeckOnItemClickListener? = null

    inner class DeckViewHolder (itemView: View): RecyclerView.ViewHolder(itemView) {
        val itemTitle: TextView = itemView.findViewById(R.id.tvDeckTitleMyDecksItem)
        val itemWord1: TextView = itemView.findViewById(R.id.tvRandFlashcard1MyDecksItem)
        val itemWord2: TextView = itemView.findViewById(R.id.tvRandFlashcard2MyDecksItem)
        val itemWord3: TextView = itemView.findViewById(R.id.tvRandFlashcard3MyDecksItem)
        val itemWord4: TextView = itemView.findViewById(R.id.tvRandFlashcard4MyDecksItem)
        val itemWord5: TextView = itemView.findViewById(R.id.tvRandFlashcard5MyDecksItem)
        val itemTotalCards: TextView = itemView.findViewById(R.id.tvTotalCardsMyDecksItem)
        val itemCover: ConstraintLayout = itemView.findViewById(R.id.constraintLayoutCover)
        val itemEditButton: ImageView = itemView.findViewById(R.id.ivGoEditMyDecksItem)
        val itemDeleteButton: ImageView = itemView.findViewById(R.id.ivGoDeleteMyDecksItem)
        val itemDetailsButton: ImageView = itemView.findViewById(R.id.ivGoDetailsMyDecksItem)
        val itemFavouriteImageButton: ImageView = itemView.findViewById(R.id.ivNonFavoriteIconMyDecksItem)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeckViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.my_decks_item_layout, parent, false)
        return DeckViewHolder(view)
    }

    override fun getItemCount(): Int {
        return deckList.size
    }

    override fun onBindViewHolder(holder: DeckViewHolder, position: Int) {
        val currentItem = deckList[position]

        holder.itemTitle.text = currentItem.title
        holder.itemWord1.text = currentItem.flashcardList[0].word
        holder.itemWord2.text = currentItem.flashcardList[1].word
        holder.itemWord3.text = currentItem.flashcardList[2].word
        holder.itemWord4.text = currentItem.flashcardList[3].word
        holder.itemWord5.text = currentItem.flashcardList[4].word
        holder.itemTotalCards.text = "${currentItem.flashcardList.size} cards"
        holder.itemCover.setBackgroundColor(currentItem.color)
        holder.itemEditButton.setColorFilter(currentItem.color)
        holder.itemDeleteButton.setColorFilter(currentItem.color)
        holder.itemDetailsButton.setColorFilter(currentItem.color)

        holder.itemFavouriteImageButton.setImageResource(setFavIcon(currentItem))
        holder.itemView.setOnClickListener { updateFavouriteStatus(currentItem, position) }
        holder.itemEditButton.setOnClickListener { deckOnItemClickListener?.onItemClick(currentItem, "edit", 0) }
        holder.itemDeleteButton.setOnClickListener { deckOnItemClickListener?.onItemClick(currentItem, "delete", position) }
        holder.itemDetailsButton.setOnClickListener { deckOnItemClickListener?.onItemClick(currentItem, "details", 0) }
    }

    private fun setFavIcon (deck: Deck): Int {
        if (deck.isFavourite) {
             return R.drawable.ic_favorite_white
        }
        return R.drawable.ic_favorite_white_no_fill
    }

    private fun updateFavouriteStatus (deck: Deck, position: Int) {
        userViewModel.changeDeckFavouriteStatus(deck)
        setFavIcon(deck)
        notifyItemChanged(position)
    }

    fun setDeckList (deckList: List<Deck>) {
        this.deckList = deckList
        notifyDataSetChanged()
    }

    fun setListener (listener: DeckOnItemClickListener) {
        deckOnItemClickListener = listener
    }

}