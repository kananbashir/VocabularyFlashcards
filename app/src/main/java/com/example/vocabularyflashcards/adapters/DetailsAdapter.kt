package com.example.vocabularyflashcards.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.vocabularyflashcards.R
import com.example.vocabularyflashcards.data.db.entities.Flashcard

class DetailsAdapter: RecyclerView.Adapter<DetailsAdapter.DetailsViewHolder>() {

    private var flashcardList: List<Flashcard> = listOf()

    inner class DetailsViewHolder (itemView: View): RecyclerView.ViewHolder (itemView) {
        val itemFlashcardWord: TextView = itemView.findViewById(R.id.tvFlashcardWordDetailsItem)
        val itemFlashcardMeaning: TextView = itemView.findViewById(R.id.tvMeaningDetailsItem)
        val itemNumber: TextView = itemView.findViewById(R.id.tvNumberDetailsItem)
        val itemMarkIcon: ImageView = itemView.findViewById(R.id.ivMarkStatusDetailsItem)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DetailsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.details_item_layout, parent, false)
        view.setBackgroundColor(flashcardList[0].color)
        return DetailsViewHolder(view)
    }

    override fun getItemCount(): Int {
        return flashcardList.size
    }

    override fun onBindViewHolder(holder: DetailsViewHolder, position: Int) {
        val currentItem = flashcardList[position]

        holder.itemFlashcardWord.text = currentItem.word
        holder.itemFlashcardMeaning.text = currentItem.meaning
        holder.itemNumber.text = "${position+1}"
        if (currentItem.isMarked) {
            holder.itemMarkIcon.visibility = View.VISIBLE
        } else {
            holder.itemMarkIcon.visibility = View.GONE
        }
    }

    fun setFlashcardList (flashcardList: List<Flashcard>) {
        this.flashcardList = flashcardList
        notifyDataSetChanged()
    }
}