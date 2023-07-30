package com.example.vocabularyflashcards.ui.main

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.vocabularyflashcards.adapters.AllFlashcardsAdapter
import com.example.vocabularyflashcards.adapters.DeckAdapter
import com.example.vocabularyflashcards.adapters.LastQuizResultsAdapter
import com.example.vocabularyflashcards.adapters.MarkedFlashcardsAdapter
import com.example.vocabularyflashcards.data.db.UserDatabase
import com.example.vocabularyflashcards.data.db.entities.Deck
import com.example.vocabularyflashcards.data.db.entities.Flashcard
import com.example.vocabularyflashcards.data.db.entities.Quiz
import com.example.vocabularyflashcards.databinding.FragmentHomeBinding
import com.example.vocabularyflashcards.ui.UserViewModel
import com.example.vocabularyflashcards.ui.UserViewModelFactory
import com.example.vocabularyflashcards.ui.main.listeners.DeckOnItemClickListener
import com.example.vocabularyflashcards.ui.main.listeners.QuizOnItemClickListener
import com.example.vocabularyflashcards.utils.AppUtils

class HomeFragment : Fragment(), DeckOnItemClickListener, QuizOnItemClickListener {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var userViewModel: UserViewModel
    private lateinit var userDatabase: UserDatabase
    private lateinit var deckAdapter: DeckAdapter
    private lateinit var allFlashcardsAdapter: AllFlashcardsAdapter
    private lateinit var markedFlashcardsAdapter: MarkedFlashcardsAdapter
    private lateinit var lastQuizResultsAdapter: LastQuizResultsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(layoutInflater)
        userDatabase = UserDatabase (requireContext())
        userViewModel = ViewModelProvider (requireActivity(), UserViewModelFactory(userDatabase)).get(UserViewModel::class.java)
        deckAdapter = DeckAdapter(userViewModel)
        allFlashcardsAdapter = AllFlashcardsAdapter()
        markedFlashcardsAdapter = MarkedFlashcardsAdapter()
        lastQuizResultsAdapter = LastQuizResultsAdapter()

        binding.rvMyDecksHomeFrag.adapter = deckAdapter
        binding.rvMyDecksHomeFrag.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.rvAllFlashcardsHomeFrag.adapter = allFlashcardsAdapter
        binding.rvAllFlashcardsHomeFrag.layoutManager = LinearLayoutManager(requireContext())
        binding.rvMarkedFlashcardsHomeFrag.adapter = markedFlashcardsAdapter
        binding.rvMarkedFlashcardsHomeFrag.layoutManager = LinearLayoutManager(requireContext())
        binding.rvLastQuizResultsHomeFrag.adapter = lastQuizResultsAdapter
        binding.rvLastQuizResultsHomeFrag.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        deckAdapter.setListener(this)
        lastQuizResultsAdapter.setListener(this)

        binding.ivCreateNewDeckHomeFrag.setOnClickListener {
            findNavController().navigate(HomeFragmentDirections.actionGlobalCreateFragment())
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        userViewModel.getOnlineUser { user ->
            deckAdapter.setDeckList(user.deckList)

            binding.tvMyDecksHomeFrag.text = "My decks (${user.deckList.size})"
            AppUtils.getAllFlashcards(user.deckList) {
                val markedFlashcardsList = it.filter { flashcard -> flashcard.isMarked }
                markedFlashcardsAdapter.setMarkedFlashcardList(markedFlashcardsList.shuffled())
                binding.tvMarkedFlashcardsHomeFrag.text = "Marked Flashcards (${markedFlashcardsList.size})"

                allFlashcardsAdapter.setFlashcardList(it)
                binding.tvAllFlashcardsHomeFrag.text = "All flashcards (${it.size})"

                lastQuizResultsAdapter.setQuizList(user.quizList)
                binding.tvLastQuizResultsHomeFrag.text = "Last quiz results (${user.quizList.size})"
            }

            if (user.deckList.isEmpty()) {
                binding.rvMyDecksHomeFrag.visibility = View.GONE
                binding.rvAllFlashcardsHomeFrag.visibility = View.GONE
                binding.rvMarkedFlashcardsHomeFrag.visibility = View.GONE
                binding.rvLastQuizResultsHomeFrag.visibility = View.GONE
            } else {
                binding.rvMyDecksHomeFrag.visibility = View.VISIBLE
                binding.rvAllFlashcardsHomeFrag.visibility = View.VISIBLE
                binding.rvMarkedFlashcardsHomeFrag.visibility = View.VISIBLE
                binding.rvLastQuizResultsHomeFrag.visibility = View.VISIBLE
            }
        }
    }

    override fun onItemClick(deck: Deck, navLocation: String, position: Int) {
        when (navLocation) {
            "edit" -> {
                findNavController().navigate(HomeFragmentDirections.actionGlobalEditFragment(deck.title))
            }
            "delete" -> {
                val alertDialog = AlertDialog.Builder(requireContext())
                alertDialog.setMessage("Are you sure you want to delete '${deck.title}'?")
                    .setCancelable(true)
                    .setPositiveButton("Yes") { _, _ ->
                        userViewModel.getOnlineUser { user ->
                            userViewModel.removeDeck(deck)
                            deckAdapter.setDeckList(user.deckList)
                            AppUtils.getAllFlashcards(user.deckList) {
                                allFlashcardsAdapter.setFlashcardList(it)
                                markedFlashcardsAdapter.setMarkedFlashcardList(it.filter { flashcard -> flashcard.isMarked })
                            }
                        }
                        Toast.makeText(requireContext(), "'${deck.title}' deleted", Toast.LENGTH_SHORT).show()
                    }
                    .setNegativeButton("No") { dialogInterface, _ ->
                        dialogInterface.cancel()
                    }
                    .show()
            }
            "details" -> {
                findNavController().navigate(HomeFragmentDirections.actionGlobalDetailsFragment(deck.title))
            }
        }
    }

    override fun onItemClick(quiz: Quiz) {
        userViewModel.setActiveQuiz(quiz)
        findNavController().navigate(HomeFragmentDirections.actionGlobalQuizResultDetailsFragment())
    }
}