package com.example.vocabularyflashcards.ui.game.quiz

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.vocabularyflashcards.adapters.QuizResultDetailsAdapter
import com.example.vocabularyflashcards.data.db.UserDatabase
import com.example.vocabularyflashcards.databinding.FragmentQuizResultDetailsBinding
import com.example.vocabularyflashcards.ui.UserViewModel
import com.example.vocabularyflashcards.ui.UserViewModelFactory
import com.example.vocabularyflashcards.utils.AppUtils

class QuizResultDetailsFragment : Fragment() {
    private lateinit var binding: FragmentQuizResultDetailsBinding
    private lateinit var userViewModel: UserViewModel
    private lateinit var userDatabase: UserDatabase
    private lateinit var quizResultDetailsAdapter: QuizResultDetailsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentQuizResultDetailsBinding.inflate(layoutInflater)
        userDatabase = UserDatabase(requireContext())
        userViewModel = ViewModelProvider(requireActivity(), UserViewModelFactory(userDatabase)).get(UserViewModel::class.java)
        quizResultDetailsAdapter = QuizResultDetailsAdapter(requireContext())
        binding.rvQuizResultDetailsFrag.adapter = quizResultDetailsAdapter
        binding.rvQuizResultDetailsFrag.layoutManager = LinearLayoutManager(requireContext())

        userViewModel.getActiveQuiz { quiz ->
            quizResultDetailsAdapter.setQuestionList(quiz.questionList)
            binding.tvDateQuizResultDetailsFrag.text = quiz.date
            AppUtils.getQuizResults (quiz.questionList) { rightAnswers, wrongAnswers, notAnswered ->
                binding.tvRightAnswersQuizResultDetailsFrag.text = "$rightAnswers right answers"
                binding.tvWrongAnswersQuizResultDetailsFrag.text = "$wrongAnswers wrong answers"
                binding.tvNotAnsweredQuizResultDetailsFrag.text = "$notAnswered not answered"
            }
        }

        binding.lyNavToHomeQuizResultDetailsFrag.setOnClickListener {
            findNavController().navigate(QuizResultDetailsFragmentDirections.actionGlobalMainSession())
        }

        return binding.root
    }
}