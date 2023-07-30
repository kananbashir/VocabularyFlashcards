package com.example.vocabularyflashcards.ui.main

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.vocabularyflashcards.adapters.FavouritesAdapter
import com.example.vocabularyflashcards.data.db.UserDatabase
import com.example.vocabularyflashcards.data.db.entities.Deck
import com.example.vocabularyflashcards.databinding.FragmentFavoritesBinding
import com.example.vocabularyflashcards.ui.UserViewModel
import com.example.vocabularyflashcards.ui.UserViewModelFactory
import com.example.vocabularyflashcards.ui.main.listeners.DeckOnItemClickListener

class FavoritesFragment : Fragment(), DeckOnItemClickListener {
    private lateinit var binding: FragmentFavoritesBinding
    private lateinit var userViewModel: UserViewModel
    private lateinit var userDatabase: UserDatabase
    private lateinit var favouritesAdapter: FavouritesAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFavoritesBinding.inflate(layoutInflater)
        userDatabase = UserDatabase(requireContext())
        userViewModel = ViewModelProvider(requireActivity(), UserViewModelFactory(userDatabase)).get(UserViewModel::class.java)
        favouritesAdapter = FavouritesAdapter(userViewModel)

        binding.rvAllFavsFavoritesFrag.adapter = favouritesAdapter
        binding.rvAllFavsFavoritesFrag.layoutManager = LinearLayoutManager(requireContext())
        favouritesAdapter.setListener(this)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        userViewModel.getOnlineUser { user ->
            val favouriteDeckList = user.deckList.filter { deck -> deck.isFavourite }
            favouritesAdapter.setFavouriteDeckList(favouriteDeckList)
            binding.tvAllFavsFavoritesFrag.text = "All favourite decks (${favouriteDeckList.size})"
        }
    }

    override fun onItemClick(deck: Deck, navLocation: String, position: Int) {
        when (navLocation) {
            "edit" -> {
                findNavController().navigate(FavoritesFragmentDirections.actionGlobalEditFragment(deck.title))
            }
            "delete" -> {
                val alertDialog = AlertDialog.Builder(requireContext())
                alertDialog.setMessage("Are you sure you want to delete '${deck.title}'")
                    .setCancelable(true)
                    .setPositiveButton("Yes") { _, _, ->
                        userViewModel.getOnlineUser { user ->
                            userViewModel.removeDeck(deck)
                            favouritesAdapter.setFavouriteDeckList(user.deckList.filter { deck -> deck.isFavourite })
                        }
                    }
                    .setNegativeButton("No") { dialogInterface, _ ->
                        dialogInterface.cancel()
                    }
                    .show()
            }
            "details" -> {
                findNavController().navigate(FavoritesFragmentDirections.actionGlobalDetailsFragment(deck.title))
            }
        }
    }

}