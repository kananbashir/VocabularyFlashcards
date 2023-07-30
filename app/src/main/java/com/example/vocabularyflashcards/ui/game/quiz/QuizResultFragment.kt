package com.example.vocabularyflashcards.ui.game.quiz

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.vocabularyflashcards.data.db.UserDatabase
import com.example.vocabularyflashcards.data.db.entities.Quiz
import com.example.vocabularyflashcards.databinding.FragmentQuizResultBinding
import com.example.vocabularyflashcards.ui.UserViewModel
import com.example.vocabularyflashcards.ui.UserViewModelFactory
import com.example.vocabularyflashcards.utils.AppUtils

class QuizResultFragment : Fragment() {
    private lateinit var binding: FragmentQuizResultBinding
    private lateinit var userViewModel: UserViewModel
    private lateinit var userDatabase: UserDatabase

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentQuizResultBinding.inflate(layoutInflater)
        userDatabase = UserDatabase (requireContext())
        userViewModel = ViewModelProvider(requireActivity(), UserViewModelFactory(userDatabase)).get(UserViewModel::class.java)

        userViewModel.getOnlineUser { user ->
            val lastQuiz = user.quizList.last()
            binding.tvDateQuizResultFrag.text = lastQuiz.date
            binding.tvSpentTimeQuizResultFrag.text = lastQuiz.timer
            binding.tvUsedFlashcardsQuizResultFrag.text = "Total ${AppUtils.getAllFlashcards (lastQuiz.deckList).size} flashcards"
            AppUtils.getUsedDecksAndMarkedFlashcards(lastQuiz.deckList) { usedDecks, _ ->
                binding.tvUsedDecksQuizResultFrag.text = usedDecks
            }

            AppUtils.getQuizResults (lastQuiz.questionList) { rightAnswers, wrongAnswers, notAnswered ->
                binding.tvRightAnswersQuizResultFrag.text = "$rightAnswers right answers"
                binding.tvWrongAnswersQuizResultFrag.text = "$wrongAnswers wrong answers"
                binding.tvNotAnsweredQuizResultFrag.text = "$notAnswered not answered"
            }
        }

        binding.lyNavtoHomeQuizResultFrag.setOnClickListener {
            findNavController().navigate(QuizResultFragmentDirections.actionGlobalMainSession())
        }

        binding.btGoDetailsQuizResultFrag.setOnClickListener {
            findNavController().navigate(QuizResultFragmentDirections.actionQuizResultFragmentToQuizResultDetailsFragment())
        }

        return binding.root
    }
}