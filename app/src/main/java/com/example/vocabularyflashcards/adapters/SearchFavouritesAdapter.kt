package com.example.vocabularyflashcards.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.vocabularyflashcards.R
import com.example.vocabularyflashcards.data.db.entities.Deck
import com.example.vocabularyflashcards.ui.main.listeners.SearchOnItemClickListener

class SearchFavouritesAdapter: RecyclerView.Adapter<SearchFavouritesAdapter.SearchFavouritesViewHolder>() {

    private var favouriteDecksList: List<Deck> = listOf()
    private var searchOnItemClickListener: SearchOnItemClickListener? = null

    inner class SearchFavouritesViewHolder (itemView: View): RecyclerView.ViewHolder(itemView) {
        val itemDeckInfo:TextView = itemView.findViewById(R.id.tvDeckAndCardsSearchItem)
        val itemFavouriteStatusIcon: ImageView = itemView.findViewById(R.id.ivFavoriteSearchItem)
        val itemEditButton: ImageView = itemView.findViewById(R.id.ivGoEditSearchItem)
        val itemDeleteButton: ImageView = itemView.findViewById(R.id.ivGoDeleteSearchItem)
        val itemDetailsButton: ImageView = itemView.findViewById(R.id.ivGoDetailsSearchItem)
        val itemBackground: CardView = itemView.findViewById(R.id.cdSearchDecksItemBackground)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchFavouritesViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.search_decks_item_layout, parent, false)
        return SearchFavouritesViewHolder(view)
    }

    override fun getItemCount(): Int {
        return favouriteDecksList.size
    }

    override fun onBindViewHolder(holder: SearchFavouritesViewHolder, position: Int) {
        val currentItem = favouriteDecksList[position]

        holder.itemDeckInfo.text = "${currentItem.title} - ${currentItem.flashcardList.size} cards"
        holder.itemFavouriteStatusIcon.visibility = View.VISIBLE
        holder.itemBackground.setCardBackgroundColor(currentItem.color)
        holder.itemEditButton.setOnClickListener { searchOnItemClickListener?.onItemClick(currentItem, "edit", 0) }
        holder.itemDeleteButton.setOnClickListener { searchOnItemClickListener?.onItemClick(currentItem, "delete", position) }
        holder.itemDetailsButton.setOnClickListener { searchOnItemClickListener?.onItemClick(currentItem, "details", 0) }
    }

    fun setFavouriteDeckList (favouriteDecksList: List<Deck>) {
        this.favouriteDecksList = favouriteDecksList
        notifyDataSetChanged()
    }

    fun setListener (listener: SearchOnItemClickListener) {
        searchOnItemClickListener = listener
    }
}