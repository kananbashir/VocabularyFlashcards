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

class StudyModeAdapter: RecyclerView.Adapter<StudyModeAdapter.StudyModeViewHolder>() {

    private var deckList: List<Deck> = listOf()
    private var checkedDecksList: MutableList<Deck> = mutableListOf()

    inner class StudyModeViewHolder (itemView: View): RecyclerView.ViewHolder(itemView) {
        val itemDeckAndCards: TextView = itemView.findViewById(R.id.tvDeckAndCardsSearchModeItem)
        val itemFavoutiteIcon: ImageView = itemView.findViewById(R.id.ivFavoriteIconSearchModeItem)
        val itemUncheckedIcon: ImageView = itemView.findViewById(R.id.ivUncheckedSearchModeItem)
        val itemCheckedIcon: ImageView = itemView.findViewById(R.id.ivCheckedSearchModeItem)
        val itemBackground: CardView = itemView.findViewById(R.id.cvBackgroundStudyModeItem)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudyModeViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.mode_item_layout, parent, false)
        return StudyModeViewHolder(view)
    }

    override fun getItemCount(): Int {
        return deckList.size
    }

    override fun onBindViewHolder(holder: StudyModeViewHolder, position: Int) {
        val currentItem = deckList[position]

        holder.itemDeckAndCards.text = "${currentItem.title} - ${currentItem.flashcardList.size} cards"
        holder.itemBackground.setCardBackgroundColor(currentItem.color)

        if (currentItem.isFavourite) {
            holder.itemFavoutiteIcon.visibility = View.VISIBLE
        } else {
            holder.itemFavoutiteIcon.visibility = View.GONE
        }

        holder.itemView.setOnClickListener {
            if (holder.itemUncheckedIcon.visibility == View.VISIBLE) {
                holder.itemUncheckedIcon.visibility = View.INVISIBLE
                holder.itemCheckedIcon.visibility = View.VISIBLE
                checkedDecksList.add(currentItem)
            } else {
                holder.itemUncheckedIcon.visibility = View.VISIBLE
                holder.itemCheckedIcon.visibility = View.INVISIBLE
                checkedDecksList.remove(currentItem)
            }
        }
    }

    fun setDeckList (deckList: List<Deck>) {
        this.deckList = deckList
        notifyDataSetChanged()
    }

    fun getCheckedDecksList (): List<Deck> {
        return checkedDecksList
    }
}