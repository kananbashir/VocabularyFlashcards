package com.example.vocabularyflashcards.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.vocabularyflashcards.R
import com.example.vocabularyflashcards.data.db.entities.Question

class QuizResultDetailsAdapter (private val context: Context): RecyclerView.Adapter<QuizResultDetailsAdapter.QuizResultDetailsViewHolder>() {

    private var questionList: List<Question> = listOf()

    inner class QuizResultDetailsViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val itemNumber: TextView = itemView.findViewById(R.id.tvNumberQuizDetailsItem)
        val itemNumberLayout: LinearLayout = itemView.findViewById(R.id.lyNumberQuizDetailsItem)
        val itemFlashcardWord: TextView = itemView.findViewById(R.id.tvFlashcardQuizDetailsItem)
        val itemFlashcardWordLayout: LinearLayout = itemView.findViewById(R.id.lyFlashcardQuizDetailsItem)
        val itemAnswer: TextView = itemView.findViewById(R.id.tvAnswerQuizDetailsItem)
        val itemAnswerLayout: LinearLayout = itemView.findViewById(R.id.lyAnswerQuizDetailsItem)
        val itemResult: TextView = itemView.findViewById(R.id.tvResultQuizDetailsItem)
        val itemResultLayout: LinearLayout = itemView.findViewById(R.id.lyResultQuizDetailsItem)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuizResultDetailsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.quiz_details_item_layout, parent, false)
        return QuizResultDetailsViewHolder(view)
    }

    override fun getItemCount(): Int {
        return questionList.size
    }

    override fun onBindViewHolder(holder: QuizResultDetailsViewHolder, position: Int) {
        val currentItem = questionList[position]

        holder.itemResult.text = currentItem.result
        holder.itemNumber.text = "${position+1}."
        holder.itemFlashcardWord.text = currentItem.correctAnswer

        if (currentItem.userAnswer != "") {
            holder.itemAnswer.text = currentItem.userAnswer
        } else {
            holder.itemAnswer.text = "-"
        }

        getBackgroundColor(currentItem) {color ->
            holder.itemNumberLayout.setBackgroundColor(color)
            holder.itemFlashcardWordLayout.setBackgroundColor(color)
            holder.itemAnswerLayout.setBackgroundColor(color)
            holder.itemResultLayout.setBackgroundColor(color)
        }
    }

    private fun getBackgroundColor (question: Question, callback: (Int) -> Unit) {
        when (question.result) {
            "Correct" -> { callback (ContextCompat.getColor(context, R.color.tea_green_dt)) }
            "Wrong" -> { callback (ContextCompat.getColor(context, R.color.light_red_dt)) }
            "-" -> { callback (ContextCompat.getColor(context, R.color.seasalt_gray_dt)) }
            "" -> { callback (ContextCompat.getColor(context, R.color.seasalt_gray_dt)) }
        }
    }

    fun setQuestionList (questionList: List<Question>) {
        this.questionList = questionList
        notifyDataSetChanged()
    }
}