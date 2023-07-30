package com.example.vocabularyflashcards.ui.other

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.vocabularyflashcards.R
import com.example.vocabularyflashcards.data.db.UserDatabase
import com.example.vocabularyflashcards.data.db.entities.Deck
import com.example.vocabularyflashcards.data.db.entities.Flashcard
import com.example.vocabularyflashcards.databinding.FragmentEditBinding
import com.example.vocabularyflashcards.ui.UserViewModel
import com.example.vocabularyflashcards.ui.UserViewModelFactory

class EditFragment : Fragment() {
    private lateinit var binding: FragmentEditBinding
    private lateinit var userViewModel: UserViewModel
    private lateinit var userDatabase: UserDatabase
    private lateinit var editFragmentArgs: EditFragmentArgs

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEditBinding.inflate(layoutInflater)
        editFragmentArgs = EditFragmentArgs.fromBundle(requireArguments())
        userDatabase = UserDatabase(requireContext())
        userViewModel = ViewModelProvider(requireActivity(), UserViewModelFactory(userDatabase)).get(UserViewModel::class.java)

        binding.tvEditDeckEditFrag.text = "Edit '${editFragmentArgs.deckTitle}'"
        binding.inputDeckTitleEditFrag.setText(editFragmentArgs.deckTitle)
        binding.ivAddNewEditFrag.setOnClickListener { addNewItemView() }
        binding.btDiscardEditFrag.setOnClickListener { discardAllChanges() }
        binding.btSaveEditFrag.setOnClickListener { saveAllChanges() }
        inflateAllItems()

        return binding.root
    }

    private fun inflateAllItems () {
        val parentLayout = binding.parentContainerLayout

        getDeck(editFragmentArgs.deckTitle) {deck ->
            for (i in 0 until deck.flashcardList.size) {
                val childView = LayoutInflater.from(requireContext()).inflate(R.layout.fragment_edit_new_item_layout, null)
                val childWord = childView.findViewById<EditText>(R.id.inputWordEditFragmentItemLayout)
                val childNumber = childView.findViewById<TextView>(R.id.tvNumberEditFragmentItemLayout)
                childView.findViewById<EditText>(R.id.inputMeaningEditFragmentItemLayout).setText(deck.flashcardList[i].meaning)
                childWord.setText(deck.flashcardList[i].word)
                childNumber.text = "${i+1}."

                val deleteButton = childView.findViewById<ImageView>(R.id.ivDeleteEditFragmentItemLayout)
                deleteButton.setOnClickListener {
                    deleteItemView(parentLayout, childView, childWord, childNumber)
                }

                parentLayout.addView(childView)
                updateItemViewAndNumber (parentLayout)
            }
        }

        binding.tvTotalAddedFlashcardsEditFrag.text = "Total ${parentLayout.childCount} flashcards added"
    }

    private fun addNewItemView () {
        val parentLayout = binding.parentContainerLayout
        val childView = LayoutInflater.from(requireContext()).inflate(R.layout.fragment_edit_new_item_layout, null)
        val childWord = childView.findViewById<EditText>(R.id.inputWordEditFragmentItemLayout)
        val childNumber = childView.findViewById<TextView>(R.id.tvNumberEditFragmentItemLayout)

        val deleteButton = childView.findViewById<ImageView>(R.id.ivDeleteEditFragmentItemLayout)
        deleteButton.setOnClickListener { deleteItemView(parentLayout, childView, childWord, childNumber) }

        parentLayout.addView(childView)
        updateItemViewAndNumber(parentLayout)
    }

    private fun getDeck(deckTitle: String, callback: (Deck) -> Unit) {
        userViewModel.getOnlineUser { user ->
            for (deck in user.deckList) {
                if (deck.title == deckTitle) {
                    callback (deck)
                }
            }
        }
    }

    private fun updateItemViewAndNumber (parentLayout: LinearLayout) {
        binding.tvTotalAddedFlashcardsEditFrag.text = "Total ${parentLayout.childCount} flashcards added"
        for (item in 0 until parentLayout.childCount) {
            val childView = parentLayout.getChildAt(item) as ConstraintLayout
            childView.findViewById<TextView>(R.id.tvNumberEditFragmentItemLayout).text = "${item+1}."
        }
    }

    private fun deleteItemView (parentLayout: LinearLayout, childView: View, childWord: EditText, childNumber: TextView) {
        parentLayout.removeView(childView)
        updateItemViewAndNumber (parentLayout)

        if (childWord.text.toString().isEmpty()) {
            Toast.makeText(requireContext(), "Line '${childNumber.text}' deleted", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(requireContext(), "'${childWord.text}' in the line '${childNumber.text}' deleted", Toast.LENGTH_SHORT).show()
        }
    }

    private fun discardAllChanges () {
        val alertDialog = AlertDialog.Builder(requireContext())
        alertDialog.setMessage("Do you want to cancel everything?")
            .setCancelable(true)
            .setPositiveButton("Yes") { _, _ ->
                findNavController().navigate(EditFragmentDirections.actionEditFragmentToMainSession())
            }
            .setNegativeButton("No") { dialogInterface, _ ->
                dialogInterface.cancel()
            }
            .show()
    }

    private fun saveAllChanges () {
        val deckTitle = binding.inputDeckTitleEditFrag.text.toString()
        checkDeckTitleCompatibility (deckTitle) { isUnique ->
            if (isUnique) {
                if (checkIfAllColumnsFilled()) {
                    if (checkItemCount()) {
                        getDeck(editFragmentArgs.deckTitle) { oldDeck ->
                            userViewModel.updateDeck(getUpdatedDeck(oldDeck), oldDeck)
                            Toast.makeText(requireContext(), "All changes have been saved for '${oldDeck.title}'", Toast.LENGTH_SHORT).show()
                            findNavController().navigate(EditFragmentDirections.actionEditFragmentToMainSession())
                        }
                    } else {
                        Toast.makeText(requireContext(), "At least 10 flashcard needed", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(requireContext(), "Please fill all columns", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(requireContext(), "Title '$deckTitle' has already been taken", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun checkDeckTitleCompatibility (deckTitle: String, callback: (Boolean) -> Unit) {
        var isUnique = true

        userViewModel.getOnlineUser { user ->
            if (deckTitle != editFragmentArgs.deckTitle) {
                for (deck in user.deckList) {
                    if (deck.title == deckTitle) {
                        isUnique = false
                    }
                }
            }
        }
        callback (isUnique)
    }

    private fun checkIfAllColumnsFilled (): Boolean {
        val parentLayout = binding.parentContainerLayout
        var isAllColumnsFilled = false
        var filledColumnCount = 0

        for (item in 0 until parentLayout.childCount) {
            val childView = parentLayout.getChildAt(item) as ConstraintLayout
            val childWord = childView.findViewById<EditText>(R.id.inputWordEditFragmentItemLayout).text.toString()
            val childMeaning = childView.findViewById<EditText>(R.id.inputMeaningEditFragmentItemLayout).text.toString()

            if (childWord.isNotEmpty() && childMeaning.isNotEmpty()) {
                filledColumnCount++
            }
        }

        if (filledColumnCount == parentLayout.childCount) {
            isAllColumnsFilled = true
        }

        return isAllColumnsFilled
    }

    private fun getUpdatedDeck (oldDeck: Deck): Deck {
        val parentLayout = binding.parentContainerLayout
        val deckTitle = binding.inputDeckTitleEditFrag.text.toString()
        var tempFlashcardList = mutableListOf<Flashcard>()

        for (item in 0 until parentLayout.childCount) {
            val childView = parentLayout.getChildAt(item) as ConstraintLayout
            val childWord = childView.findViewById<EditText>(R.id.inputWordEditFragmentItemLayout).text.toString()
            val childMeaning = childView.findViewById<EditText>(R.id.inputMeaningEditFragmentItemLayout).text.toString()
            tempFlashcardList.add(Flashcard(childWord, childMeaning, deckTitle, oldDeck.color, checkForMark(childWord, oldDeck)))
        }

        return Deck(deckTitle, tempFlashcardList, oldDeck.color, oldDeck.isFavourite)
    }

    private fun checkForMark (word: String, oldDeck: Deck): Boolean {
        var isMarked = false

        for (flashcard in oldDeck.flashcardList) {
            if (flashcard.word == word) {
                if (flashcard.isMarked) {
                    isMarked = true
                }
            }
        }

        return isMarked
    }

    private fun checkItemCount (): Boolean{
        val parentLayout = binding.parentContainerLayout
        if (parentLayout.childCount > 10) {
            return true
        }
        return false
    }
}