package com.example.vocabularyflashcards.ui.other

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.vocabularyflashcards.adapters.DetailsAdapter
import com.example.vocabularyflashcards.data.db.UserDatabase
import com.example.vocabularyflashcards.data.db.entities.Deck
import com.example.vocabularyflashcards.data.db.entities.Flashcard
import com.example.vocabularyflashcards.databinding.FragmentDetailsBinding
import com.example.vocabularyflashcards.ui.UserViewModel
import com.example.vocabularyflashcards.ui.UserViewModelFactory

class DetailsFragment : Fragment() {
    private lateinit var binding: FragmentDetailsBinding
    private lateinit var userViewModel: UserViewModel
    private lateinit var userDatabase: UserDatabase
    private lateinit var detailsFragmentArgs: DetailsFragmentArgs
    private lateinit var detailsAdapter: DetailsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDetailsBinding.inflate(layoutInflater)
        userDatabase = UserDatabase(requireContext())
        userViewModel = ViewModelProvider(requireActivity(), UserViewModelFactory(userDatabase)).get(UserViewModel::class.java)
        detailsFragmentArgs = DetailsFragmentArgs.fromBundle(requireArguments())
        detailsAdapter = DetailsAdapter()
        binding.rvFlashcardsDetailsFrag.adapter = detailsAdapter
        binding.rvFlashcardsDetailsFrag.layoutManager = LinearLayoutManager(requireContext())

        binding.tvDeckTitleDetailsFrag.text = detailsFragmentArgs.deckTitle

        binding.ivFabEditDetailsFrag.setOnClickListener {
            findNavController().navigate(DetailsFragmentDirections.actionDetailsFragmentToEditFragment(detailsFragmentArgs.deckTitle))
        }

        binding.searchViewDetailsFrag.setOnQueryTextListener(object: SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filterList (newText!!)
                return true
            }

        })

        getDeck (detailsFragmentArgs.deckTitle) {
            detailsAdapter.setFlashcardList(it.flashcardList)
            binding.tvTotalFlashcardsDetailsFrag.text = "Total number of flashcards (${it.flashcardList.size})"
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object: OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                findNavController().popBackStack()
            }
        })

        return binding.root
    }

    private fun getDeck (deckTitle: String, callback: (Deck) -> Unit) {
        userViewModel.getOnlineUser { user ->
            for (deck in user.deckList) {
                if (deck.title == deckTitle) {
                    callback (deck)
                }
            }
        }
    }

    private fun filterList (key: String) {
        var tempFilteredFlashcardList = mutableListOf<Flashcard>()
        getDeck(detailsFragmentArgs.deckTitle) {
            for (flashcard in it.flashcardList) {
                if (flashcard.word.contains(key, true)){
                    tempFilteredFlashcardList.add(flashcard)
                }
            }
        }

        if (key.isNotEmpty()) {
            detailsAdapter.setFlashcardList(tempFilteredFlashcardList)
            binding.tvTotalFlashcardsDetailsFrag.text = "${tempFilteredFlashcardList.size} results found"
        } else {
            getDeck(detailsFragmentArgs.deckTitle) {
                detailsAdapter.setFlashcardList(it.flashcardList)
                binding.tvTotalFlashcardsDetailsFrag.text = "Total number of flashcards (${it.flashcardList.size})"
            }
        }
    }
}