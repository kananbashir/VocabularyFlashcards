package com.example.vocabularyflashcards.ui.other

import android.app.AlertDialog
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.vocabularyflashcards.R
import com.example.vocabularyflashcards.data.db.UserDatabase
import com.example.vocabularyflashcards.data.db.entities.Deck
import com.example.vocabularyflashcards.data.db.entities.Flashcard
import com.example.vocabularyflashcards.databinding.FragmentCreateBinding
import com.example.vocabularyflashcards.ui.UserViewModel
import com.example.vocabularyflashcards.ui.UserViewModelFactory

class CreateFragment : Fragment() {
    private lateinit var binding: FragmentCreateBinding
    private lateinit var userViewModel: UserViewModel
    private lateinit var userDatabase: UserDatabase
    private var randomColor: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCreateBinding.inflate(layoutInflater)
        userDatabase = UserDatabase(requireContext())
        userViewModel = ViewModelProvider (requireActivity(), UserViewModelFactory(userDatabase)).get(UserViewModel::class.java)

        binding.ivAddNewCreateFrag.setOnClickListener { addNewItemView() }
        binding.btCreateCreateFrag.setOnClickListener { createNewDeck() }
        binding.btDiscardCreateFrag.setOnClickListener { discardAllChanges() }

        return binding.root
    }

    private fun createNewDeck () {
        val deckTitle = binding.inputDeckTitleCreateFrag.text.toString()

        checkDeckTitleCompatibility(deckTitle) {isUnique ->
            if (isUnique) {
                if (checkIfAllColumnsFilled()) {
                    randomColor = getRandomColor()
                    userViewModel.addNewDeck(Deck(deckTitle, createNewFlashcardList(), randomColor, false))
                    findNavController().navigate(CreateFragmentDirections.actionCreateFragmentToMainSession())
                } else {
                    Toast.makeText(requireContext(), "Please, fill all columns", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(requireContext(), "Title '$deckTitle' has already been taken", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun addNewItemView () {
        val parentLayout = binding.parentContainerLayout
        val childView = LayoutInflater.from(requireContext()).inflate(R.layout.fragment_create_new_item_layout, null)

        val deleteButton = childView.findViewById<ImageView>(R.id.ivDeleteCreateFragmentItemLayout)
        deleteButton.setOnClickListener { deleteLayoutItem(parentLayout, childView) }

        parentLayout.addView(childView)

        updateItemViewNumber(parentLayout) {
            updateButtonAndText(it)
        }
    }

    private fun updateItemViewNumber(parentLayout: LinearLayout, callback: (Int) -> Unit) {
        var itemCount = 1

        for (item in 1 until parentLayout.childCount) {
            val child = parentLayout.getChildAt(item) as ConstraintLayout
            val childNumber = child.findViewById<TextView>(R.id.tvNumberCreateFragmentItemLayout)
            childNumber.text = "${item+1}."
            itemCount++
        }

        callback (itemCount)
    }

    private fun updateButtonAndText (number: Int) {
        binding.tvTotalAddedFlashcardsCreateFrag.text = "Total $number flashcards added"
        if (number > 9) {
            binding.btCreateCreateFrag.isClickable = true
            binding.btCreateCreateFrag.setBackgroundColor(resources.getColor(R.color.davys_gray, null))
        } else {
            binding.btCreateCreateFrag.isClickable = false
            binding.btCreateCreateFrag.setBackgroundColor(resources.getColor(R.color.gray_non_clickable_button, null))
        }
    }

    private fun deleteLayoutItem (parentLayout: LinearLayout, childView: View) {
        val childViewWord = childView.findViewById<TextView>(R.id.inputWordCreateFragmentItemLayout).text.toString()
        val childViewNumber = childView.findViewById<TextView>(R.id.tvNumberCreateFragmentItemLayout).text.toString()

        parentLayout.removeView(childView)

        updateItemViewNumber(parentLayout) {
            updateButtonAndText(it)
        }

        if (childViewWord.isEmpty()) {
            Toast.makeText(requireContext(), "Line '$childViewNumber' deleted", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(requireContext(), "'$childViewWord' in the line '$childViewNumber' deleted", Toast.LENGTH_SHORT).show()
        }
    }

    private fun checkIfAllColumnsFilled (): Boolean {
        var isAllColumnsFilled = false
        var filledColumnCount = 0
        val parentLayout = binding.parentContainerLayout
        val deckTitle = binding.inputWordEditFrag.text.toString()
        val initialWord = binding.inputMeaningEditFrag.text.toString()
        val initialMeaning = binding.inputDeckTitleCreateFrag.text.toString()

        if (deckTitle.isNotEmpty() && initialWord.isNotEmpty() && initialMeaning.isNotEmpty()) {
            filledColumnCount++
        }

        for (item in 1 until parentLayout.childCount) {
            val childView = parentLayout.getChildAt(item) as ConstraintLayout
            val childWord = childView.findViewById<EditText>(R.id.inputWordCreateFragmentItemLayout).text.toString()
            val childMeaning = childView.findViewById<EditText>(R.id.inputMeaningCreateFragmentItemLayout).text.toString()

            if (childWord.isNotEmpty() && childMeaning.isNotEmpty()) {
                filledColumnCount++
            }
        }

        if (filledColumnCount == parentLayout.childCount) {
            isAllColumnsFilled = true
        }

        return isAllColumnsFilled
    }

    private fun checkDeckTitleCompatibility (deckTitle: String, callback: (Boolean) -> Unit) {
        var isUnique = true

        userViewModel.getOnlineUser {onlineUser ->
            for (deck in onlineUser.deckList) {
                if (deck.title == deckTitle) {
                    isUnique = false
                }
            }
            callback (isUnique)
        }
    }

    private fun createNewFlashcardList (): MutableList<Flashcard> {
        var flashcardList = mutableListOf<Flashcard>()
        val parentLayout = binding.parentContainerLayout
        val initialWord = binding.inputWordEditFrag.text.toString()
        val initialMeaning = binding.inputMeaningEditFrag.text.toString()
        val deckTitle = binding.inputDeckTitleCreateFrag.text.toString()

        flashcardList.add(Flashcard(initialWord, initialMeaning, deckTitle, randomColor, false))

        for (item in 1 until parentLayout.childCount) {
            val child = parentLayout.getChildAt(item) as ConstraintLayout
            val childWord = child.findViewById<EditText>(R.id.inputWordCreateFragmentItemLayout).text.toString()
            val childMeaning = child.findViewById<EditText>(R.id.inputMeaningCreateFragmentItemLayout).text.toString()

            flashcardList.add(Flashcard(childWord, childMeaning, deckTitle, randomColor, false))
        }

        return flashcardList
    }

    private fun getRandomColor(): Int {
        val gradeList = listOf((55..210).random(), (165..210).random(), (55..85).random()).shuffled()
        return Color.argb(255, gradeList[0], gradeList[1], gradeList[2])
    }

    private fun discardAllChanges () {
        val alertDialog = AlertDialog.Builder(requireContext())
        alertDialog.setMessage("Are you sure you want to discard all changes?")
            .setCancelable(true)
            .setPositiveButton("YES") { _, _, ->
                findNavController().navigate(CreateFragmentDirections.actionCreateFragmentToMainSession())
            }
            .setNegativeButton("NO") { dialogInterface, _ ->
                dialogInterface.cancel()
            }
            .show()
    }
}