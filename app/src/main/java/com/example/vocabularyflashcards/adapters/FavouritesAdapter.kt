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

class FavouritesAdapter (private val userViewModel: UserViewModel): RecyclerView.Adapter<FavouritesAdapter.FavouritesViewHolder>() {

    private var favouriteDecksList = listOf<Deck>()
    private var deckOnItemClickListener: DeckOnItemClickListener? = null

    inner class FavouritesViewHolder (itemView: View): RecyclerView.ViewHolder(itemView) {
        val itemTitle: TextView = itemView.findViewById(R.id.tvDeckTitleFavouriteDecksItem)
        val itemWord1: TextView = itemView.findViewById(R.id.tvRandFlashcard1FavouriteDecksItem)
        val itemWord2: TextView = itemView.findViewById(R.id.tvRandFlashcard2FavouriteDecksItem)
        val itemWord3: TextView = itemView.findViewById(R.id.tvRandFlashcard3FavouriteDecksItem)
        val itemWord4: TextView = itemView.findViewById(R.id.tvRandFlashcard4FavouriteDecksItem)
        val itemWord5: TextView = itemView.findViewById(R.id.tvRandFlashcard5FavouriteDecksItem)
        val itemTotalCards: TextView = itemView.findViewById(R.id.tvTotalCardsFavouriteDecksItem)
        val itemCover: ConstraintLayout = itemView.findViewById(R.id.constraintLayoutCoverFavouriteDecksItem)
        val itemEditButton: ImageView = itemView.findViewById(R.id.ivGoEditFavouriteDecksItem)
        val itemDeleteButton: ImageView = itemView.findViewById(R.id.ivGoDeleteFavouriteDecksItem)
        val itemDetailsButton: ImageView = itemView.findViewById(R.id.ivGoDetailsFavouriteDecksItem)
        val itemFavouriteImageButton: ImageView = itemView.findViewById(R.id.ivNonFavoriteIconFavouriteDecksItem)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavouritesViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.favourite_decks_item_layout, parent, false)
        return FavouritesViewHolder(view)
    }

    override fun getItemCount(): Int {
        return favouriteDecksList.size
    }

    override fun onBindViewHolder(holder: FavouritesViewHolder, position: Int) {
        val currentItem = favouriteDecksList[position]

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

        holder.itemFavouriteImageButton.setOnClickListener { changeFavouriteStatus(currentItem, position) }
        holder.itemEditButton.setOnClickListener { deckOnItemClickListener?.onItemClick(currentItem, "edit", 0) }
        holder.itemDeleteButton.setOnClickListener { deckOnItemClickListener?.onItemClick(currentItem, "delete", position) }
        holder.itemDetailsButton.setOnClickListener { deckOnItemClickListener?.onItemClick(currentItem, "details", 0) }
    }

    private fun changeFavouriteStatus (deck: Deck, position: Int) {
        userViewModel.changeDeckFavouriteStatus(deck)
        notifyItemChanged(position)
    }

    fun setFavouriteDeckList (deckList: List<Deck>) {
        val tempFavouriteDeckList = mutableListOf<Deck>()

        for (deck in deckList) {
            if (deck.isFavourite) {
                tempFavouriteDeckList.add(deck)
            }
        }

        favouriteDecksList = tempFavouriteDeckList
        notifyDataSetChanged()
    }

    fun setListener (listener: DeckOnItemClickListener) {
        deckOnItemClickListener = listener
    }


}