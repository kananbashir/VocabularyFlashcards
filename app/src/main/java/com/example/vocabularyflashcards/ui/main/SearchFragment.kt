package com.example.vocabularyflashcards.ui.main

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.vocabularyflashcards.R
import com.example.vocabularyflashcards.adapters.SearchDecksAdapter
import com.example.vocabularyflashcards.adapters.SearchFavouritesAdapter
import com.example.vocabularyflashcards.adapters.SearchFlashcardsAdapter
import com.example.vocabularyflashcards.data.db.UserDatabase
import com.example.vocabularyflashcards.data.db.entities.Deck
import com.example.vocabularyflashcards.data.db.entities.Flashcard
import com.example.vocabularyflashcards.databinding.FragmentSearchBinding
import com.example.vocabularyflashcards.ui.UserViewModel
import com.example.vocabularyflashcards.ui.UserViewModelFactory
import com.example.vocabularyflashcards.ui.main.listeners.SearchOnItemClickListener
import com.example.vocabularyflashcards.utils.AppUtils

class SearchFragment : Fragment(), SearchOnItemClickListener {
    private lateinit var binding: FragmentSearchBinding
    private lateinit var userViewModel: UserViewModel
    private lateinit var userDatabase: UserDatabase
    private lateinit var searchDecksAdapter: SearchDecksAdapter
    private lateinit var searchFavouritesAdapter: SearchFavouritesAdapter
    private lateinit var searchFlashcardsAdapter: SearchFlashcardsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSearchBinding.inflate(layoutInflater)
        userDatabase = UserDatabase(requireContext())
        userViewModel = ViewModelProvider(requireActivity(), UserViewModelFactory(userDatabase)).get(UserViewModel::class.java)
        searchDecksAdapter = SearchDecksAdapter()
        searchDecksAdapter.setListener(this)
        searchFavouritesAdapter = SearchFavouritesAdapter()
        searchFavouritesAdapter.setListener(this)
        searchFlashcardsAdapter = SearchFlashcardsAdapter(userViewModel)
        searchFlashcardsAdapter.setListener(this)

        binding.rgSearchFrag.setOnCheckedChangeListener { _, checkedId ->
            userViewModel.getOnlineUser { user ->
                when (checkedId) {
                    R.id.rbSearchDecksSearchFrag -> {
                        binding.rvDecksSearchFrag.adapter = searchDecksAdapter
                        binding.rvDecksSearchFrag.layoutManager = LinearLayoutManager(requireContext())
                        binding.rvDecksSearchFrag.visibility = View.VISIBLE
                        binding.rvFavoritesSearchFrag.visibility = View.INVISIBLE
                        binding.rvFlashcardsSearchFrag.visibility = View.INVISIBLE
                        searchDecksAdapter.setDeckList(user.deckList)
                        binding.tvTotalResultsSearchFrag.text = "Total ${user.deckList.size} results found"
                    }
                    R.id.rbSearchFavoritesSearchFrag -> {
                        binding.rvFavoritesSearchFrag.adapter = searchFavouritesAdapter
                        binding.rvFavoritesSearchFrag.layoutManager = LinearLayoutManager(requireContext())
                        binding.rvFavoritesSearchFrag.visibility = View.VISIBLE
                        binding.rvDecksSearchFrag.visibility = View.INVISIBLE
                        binding.rvFlashcardsSearchFrag.visibility = View.INVISIBLE

                        val favouriteDecksList = AppUtils.getFavouriteDecks(user.deckList)
                        searchFavouritesAdapter.setFavouriteDeckList(favouriteDecksList)
                        binding.tvTotalResultsSearchFrag.text = "Total ${favouriteDecksList.size} results found"
                    }
                    R.id.rbSearchFlashcardsSearchFrag -> {
                        binding.rvFlashcardsSearchFrag.adapter = searchFlashcardsAdapter
                        binding.rvFlashcardsSearchFrag.layoutManager = LinearLayoutManager(requireContext())
                        binding.rvFlashcardsSearchFrag.visibility = View.VISIBLE
                        binding.rvDecksSearchFrag.visibility = View.INVISIBLE
                        binding.rvFavoritesSearchFrag.visibility = View.INVISIBLE

                        val allFlashcardsList = AppUtils.getAllFlashcards(user.deckList)
                        searchFlashcardsAdapter.setFlashcardList(allFlashcardsList)
                        binding.tvTotalResultsSearchFrag.text = "Total ${allFlashcardsList.size} results found"
                    }
                }
            }
        }

        binding.searchViewSearchFrag.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filterList(newText!!, binding.rgSearchFrag.checkedRadioButtonId)
                return true
            }

        })

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        userViewModel.getOnlineUser { user ->
            binding.rvDecksSearchFrag.adapter = searchDecksAdapter
            binding.rvDecksSearchFrag.layoutManager = LinearLayoutManager(requireContext())
            searchDecksAdapter.setDeckList(user.deckList)
            binding.tvTotalResultsSearchFrag.text = "Total ${user.deckList.size} results found"
        }
    }

    private fun filterList (key: String, checkedRadioButtonId: Int) {
        userViewModel.getOnlineUser { user ->
            when (checkedRadioButtonId) {
                R.id.rbSearchDecksSearchFrag -> {
                    var filteredList = listOf<Deck>()
                    filteredList = user.deckList.filter { deck -> deck.title.contains(key, true) }

                    searchDecksAdapter.setDeckList(filteredList)
                    binding.tvTotalResultsSearchFrag.text = "Total ${filteredList.size} results found"
                }
                R.id.rbSearchFavoritesSearchFrag -> {
                    var filteredList = listOf<Deck>()
                    filteredList = user.deckList.filter { deck -> deck.title.contains(key, true) }

                    searchFavouritesAdapter.setFavouriteDeckList(filteredList)
                    binding.tvTotalResultsSearchFrag.text = "Total ${filteredList.size} results found"
                }
                R.id.rbSearchFlashcardsSearchFrag -> {
                    var filteredList = listOf<Flashcard>()
                    val allFlashcardsList = AppUtils.getAllFlashcards(user.deckList)
                    filteredList = allFlashcardsList.filter { flashcard -> flashcard.word.contains(key, true) }

                    searchFlashcardsAdapter.setFlashcardList(filteredList)
                    binding.tvTotalResultsSearchFrag.text = "Total ${filteredList.size} results found"
                }
            }
        }
    }

    override fun onItemClick(deck: Deck, navLocation: String, position: Int) {
        when (navLocation) {
            "edit" -> {
                findNavController().navigate(SearchFragmentDirections.actionGlobalEditFragment(deck.title))
            }
            "delete" -> {
                val alertDialog = AlertDialog.Builder(requireContext())
                alertDialog.setMessage("Are you sure you want to delete '${deck.title}'")
                    .setCancelable(true)
                    .setPositiveButton("YES") { _, _ ->
                        userViewModel.removeDeck(deck)
                        Toast.makeText(requireContext(), "'${deck.title}' deleted", Toast.LENGTH_SHORT).show()
                        searchDecksAdapter.notifyItemRemoved(position)
                        searchFavouritesAdapter.notifyItemRemoved(position)
                    }
                    .setNegativeButton("NO") { dialogInterface, _ ->
                        dialogInterface.cancel()
                    }
                    .show()
            }
            "details" -> {
                findNavController().navigate(SearchFragmentDirections.actionGlobalDetailsFragment(deck.title))
            }
        }
    }

    override fun onItemClick(flashcard: Flashcard) {
            findNavController().navigate(SearchFragmentDirections.actionGlobalDetailsFragment(flashcard.deckTitle))
    }
}