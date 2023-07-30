package com.example.vocabularyflashcards.ui.game.quiz

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.vocabularyflashcards.data.db.UserDatabase
import com.example.vocabularyflashcards.data.db.entities.Question
import com.example.vocabularyflashcards.data.db.entities.Quiz
import com.example.vocabularyflashcards.databinding.FragmentQuizGameBinding
import com.example.vocabularyflashcards.ui.UserViewModel
import com.example.vocabularyflashcards.ui.UserViewModelFactory
import com.example.vocabularyflashcards.utils.CalendarUtil

class QuizGameFragment : Fragment() {
    private lateinit var binding: FragmentQuizGameBinding
    private lateinit var userDatabase: UserDatabase
    private lateinit var userViewModel: UserViewModel
    private var currentPosition: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentQuizGameBinding.inflate(layoutInflater)
        userDatabase = UserDatabase(requireContext())
        userViewModel = ViewModelProvider(requireActivity(), UserViewModelFactory(userDatabase)).get(UserViewModel::class.java)

        binding.cmTimerQuizGameFrag.start()

        userViewModel.getActiveQuiz { quiz ->
            binding.tvExplanationFlashcardQuizGameFrag.text = quiz.questionList[currentPosition].question
            binding.rbAnswer1QuizGameFrag.text = quiz.questionList[currentPosition].answer1.lowercase()
            binding.rbAnswer2QuizGameFrag.text = quiz.questionList[currentPosition].answer2.lowercase()
            binding.rbAnswer3QuizGameFrag.text = quiz.questionList[currentPosition].answer3.lowercase()
            binding.rbAnswer4QuizGameFrag.text = quiz.questionList[currentPosition].answer4.lowercase()
            binding.tvProgressQuizGameFrag.text = "${currentPosition+1}/${quiz.questionList.size}"
            binding.lvNextQuizGameFrag.setOnClickListener { toNextQuestion(quiz) }
            binding.lvPreviousQuizGameFrag.setOnClickListener { toPreviousQuestion(quiz) }
            binding.btFinishQuizGameFrag.setOnClickListener { finishQuiz(quiz) }
        }

        return binding.root
    }

    private fun toNextQuestion (quiz: Quiz) {
            binding.lvPreviousQuizGameFrag.visibility = View.VISIBLE

            updateQuizResult(quiz)

            currentPosition++
            binding.tvProgressQuizGameFrag.text = "${currentPosition+1}/${quiz.questionList.size}"

            if (currentPosition == quiz.questionList.size-1) {
                binding.lvNextQuizGameFrag.visibility = View.INVISIBLE
            } else {
                binding.lvNextQuizGameFrag.visibility = View.VISIBLE
            }

            updateAnswerTexts(quiz)
            updateAnswerCheckStatus(quiz)
    }

    private fun toPreviousQuestion (quiz: Quiz) {
            binding.lvNextQuizGameFrag.visibility = View.VISIBLE
            updateQuizResult(quiz)

            currentPosition--
            binding.tvProgressQuizGameFrag.text = "${currentPosition+1}/${quiz.questionList.size}"

            if (currentPosition == 0) {
                binding.lvPreviousQuizGameFrag.visibility = View.INVISIBLE
            } else {
                binding.lvPreviousQuizGameFrag.visibility = View.VISIBLE
            }

            updateAnswerTexts(quiz)
            updateAnswerCheckStatus(quiz)
    }

    private fun updateQuizResult (quiz: Quiz) {
            val currentQuestion = quiz.questionList[currentPosition]
            if (binding.rbAnswer1QuizGameFrag.isChecked) {
                currentQuestion.userAnswer = currentQuestion.answer1
            } else if (binding.rbAnswer2QuizGameFrag.isChecked) {
                currentQuestion.userAnswer = currentQuestion.answer2
            }  else if (binding.rbAnswer3QuizGameFrag.isChecked) {
                currentQuestion.userAnswer = currentQuestion.answer3
            } else if (binding.rbAnswer4QuizGameFrag.isChecked) {
                currentQuestion.userAnswer = currentQuestion.answer4
            }
        updateQuestionResult(currentQuestion)
    }

    private fun updateQuestionResult (question: Question) {
        if (question.userAnswer.isNotEmpty()) {
            if (question.userAnswer.equals(question.correctAnswer, true)) {
                question.result = "Correct"
            } else {
                question.result = "Wrong"
            }
        } else {
            question.result = "-"
        }
    }

    private fun updateAnswerTexts (quiz: Quiz) {
        val currentQuestion = quiz.questionList[currentPosition]

        binding.tvExplanationFlashcardQuizGameFrag.text = currentQuestion.question
        binding.rbAnswer1QuizGameFrag.text = currentQuestion.answer1.lowercase()
        binding.rbAnswer2QuizGameFrag.text = currentQuestion.answer2.lowercase()
        binding.rbAnswer3QuizGameFrag.text = currentQuestion.answer3.lowercase()
        binding.rbAnswer4QuizGameFrag.text = currentQuestion.answer4.lowercase()
    }

    private fun updateAnswerCheckStatus (quiz: Quiz) {
        val currentQuestion = quiz.questionList[currentPosition]

        if (currentQuestion.answer1 == currentQuestion.userAnswer) {
            binding.rbAnswer1QuizGameFrag.isChecked = true
        } else if (currentQuestion.answer2 == currentQuestion.userAnswer) {
            binding.rbAnswer2QuizGameFrag.isChecked = true
        } else if (currentQuestion.answer3 == currentQuestion.userAnswer) {
            binding.rbAnswer3QuizGameFrag.isChecked = true
        } else if (currentQuestion.answer4 == currentQuestion.userAnswer) {
            binding.rbAnswer4QuizGameFrag.isChecked = true
        } else {
            binding.rgQuizGameFrag.clearCheck()
        }
    }

    private fun finishQuiz (quiz: Quiz) {
        updateQuizResult(quiz)
        val alertDialog = AlertDialog.Builder(requireContext())
        alertDialog.setMessage("Are you sure you want to finish the quiz?")
            .setCancelable(true)
            .setPositiveButton("Yes") { _, _, ->
                quiz.date = CalendarUtil.getCurrentDate()
                quiz.timer = CalendarUtil.getStudyTime(binding.cmTimerQuizGameFrag)
                userViewModel.addNewUserQuiz(quiz)
                findNavController().navigate(QuizGameFragmentDirections.actionQuizGameFragmentToQuizResultFragment())
            }
            .setNegativeButton("No") { dialogInterface, _ ->
                dialogInterface.cancel()
            }
            .show()
    }
}