package com.example.vocabularyflashcards.ui.game.quiz

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.vocabularyflashcards.adapters.QuizPriorGameAdapter
import com.example.vocabularyflashcards.data.db.UserDatabase
import com.example.vocabularyflashcards.databinding.FragmentQuizPriorGameBinding
import com.example.vocabularyflashcards.ui.UserViewModel
import com.example.vocabularyflashcards.ui.UserViewModelFactory
import com.example.vocabularyflashcards.utils.AppUtils

class QuizPriorGameFragment : Fragment() {
    private lateinit var binding: FragmentQuizPriorGameBinding
    private lateinit var userViewModel: UserViewModel
    private lateinit var userDatabase: UserDatabase
    private lateinit var quizPriorGameAdapter: QuizPriorGameAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentQuizPriorGameBinding.inflate(layoutInflater)
        userDatabase = UserDatabase(requireContext())
        userViewModel = ViewModelProvider(requireActivity(), UserViewModelFactory(userDatabase)).get(UserViewModel::class.java)
        quizPriorGameAdapter = QuizPriorGameAdapter()
        binding.rvSelectedFlashcardsQuizPriorGameFrag.adapter = quizPriorGameAdapter
        binding.rvSelectedFlashcardsQuizPriorGameFrag.layoutManager = LinearLayoutManager(requireContext())

        userViewModel.getActiveQuiz { quiz ->
            quizPriorGameAdapter.setFlashcardList(AppUtils.getAllFlashcards(quiz.deckList))
        }

        binding.btStartQuizPriorGameFrag.setOnClickListener {
            findNavController().navigate(QuizPriorGameFragmentDirections.actionQuizPriorGameFragmentToQuizGameFragment())
        }

        binding.btCancelQuizPriorGameFrag.setOnClickListener {
            findNavController().popBackStack()
        }

        return binding.root
    }
}