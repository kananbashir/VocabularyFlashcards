package com.example.vocabularyflashcards.ui.game.study

import android.animation.Animator
import android.app.AlertDialog
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.vocabularyflashcards.R
import com.example.vocabularyflashcards.data.db.UserDatabase
import com.example.vocabularyflashcards.data.db.entities.Deck
import com.example.vocabularyflashcards.data.db.entities.Flashcard
import com.example.vocabularyflashcards.databinding.FragmentStudyGameBinding
import com.example.vocabularyflashcards.ui.UserViewModel
import com.example.vocabularyflashcards.ui.UserViewModelFactory
import com.example.vocabularyflashcards.utils.CalendarUtil
import com.example.vocabularyflashcards.utils.AppUtils

class StudyGameFragment : Fragment() {
    private lateinit var binding: FragmentStudyGameBinding
    private lateinit var userViewModel: UserViewModel
    private lateinit var userDatabase: UserDatabase
    private var isFlipped: Boolean = false
    private var handler = Handler(Looper.getMainLooper())
    private var currentPosition: Int = 0
    private var flashcardList: MutableList<Flashcard> = mutableListOf()
    private var oldDeckList: List<Deck> = listOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentStudyGameBinding.inflate(layoutInflater)
        userDatabase = UserDatabase(requireContext())
        userViewModel = ViewModelProvider(requireActivity(), UserViewModelFactory(userDatabase)).get(UserViewModel::class.java)

        userViewModel.getStudyModeCheckedDecksList {deckList ->
            oldDeckList = deckList
            flashcardList = AppUtils.getAllFlashcards(deckList).toMutableList()
            flashcardList.shuffle()
            binding.tvFlashcardWordStudyGameFrag.text = flashcardList[currentPosition].word.uppercase()
            binding.flashcardStudyGameFrag.setBackgroundColor(flashcardList[currentPosition].color)
            binding.tvProgressStudyGameFrag.text = "1/${flashcardList.size}"
            setMarkStatus()
        }

        binding.cmTimerStudyGameFrag.start()

        binding.ivMarkStatusStudyGameFrag.setOnClickListener { changeMarkStatus() }

        binding.ivFlipIconStudyGameFrag.setOnClickListener { flipFlashcard() }
        binding.lyNextStudyGameFrag.setOnClickListener { toNextFlashcard() }
        binding.lyPreviousStudyGameFrag.setOnClickListener { toPreviousFlashcard() }
        binding.btFinishStudyGameFrag.setOnClickListener { finishGame() }

        return binding.root
    }

    private fun flipFlashcard() {
        if (isFlipped) {

            binding.flashcardStudyGameFrag.animate().rotationY(-90f).setListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animation: Animator) {}
                override fun onAnimationEnd(animation: Animator) {}
                override fun onAnimationCancel(animation: Animator) {}
                override fun onAnimationRepeat(animation: Animator) {}
            }).start()

            binding.flashcardStudyGameFrag.animate().rotationY(0f).setListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animation: Animator) {
                    handleFlashcardState(0f, 29f, "front")
                }
                override fun onAnimationEnd(animation: Animator) {
                    binding.ivFlipIconStudyGameFrag.setImageResource(R.drawable.ic_flip_right)
                }
                override fun onAnimationCancel(animation: Animator) {}
                override fun onAnimationRepeat(animation: Animator) {}
            }).start()
            if (currentPosition < flashcardList.size-1 && currentPosition != 0) {
                binding.lyNextStudyGameFrag.visibility = View.VISIBLE
                binding.lyPreviousStudyGameFrag.visibility = View.VISIBLE
            } else if (currentPosition == flashcardList.size-1) {
                binding.lyPreviousStudyGameFrag.visibility = View.VISIBLE
            } else if (currentPosition == 0) {
                binding.lyNextStudyGameFrag.visibility = View.VISIBLE
            }


        } else {
            binding.flashcardStudyGameFrag.animate().rotationY(90f).setListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animation: Animator) {}
                override fun onAnimationEnd(animation: Animator) {}
                override fun onAnimationCancel(animation: Animator) {}
                override fun onAnimationRepeat(animation: Animator) {}
            }).start()

            binding.flashcardStudyGameFrag.animate().rotationY(180f).setListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animation: Animator) {
                    handleFlashcardState(180f, 12f, "back")
                }
                override fun onAnimationEnd(animation: Animator) {
                    binding.ivFlipIconStudyGameFrag.setImageResource(R.drawable.ic_flip_left)
                }
                override fun onAnimationCancel(animation: Animator) {}
                override fun onAnimationRepeat(animation: Animator) {}
            }).start()
            binding.lyNextStudyGameFrag.visibility = View.INVISIBLE
            binding.lyPreviousStudyGameFrag.visibility = View.INVISIBLE
        }

        isFlipped = !isFlipped
    }

    private fun handleFlashcardState (rotationValue: Float, textSize: Float, flashcardSide: String) {
        when (flashcardSide) {
            "front" -> {handler.postDelayed({binding.tvFlashcardWordStudyGameFrag.text = flashcardList[currentPosition].word.uppercase()}, 180)}
            "back" -> {handler.postDelayed({binding.tvFlashcardWordStudyGameFrag.text = flashcardList[currentPosition].meaning}, 180)}
        }

        handler.postDelayed({binding.tvFlashcardWordStudyGameFrag.rotationY = rotationValue}, 190)
        handler.postDelayed({binding.tvFlashcardWordStudyGameFrag.textSize = textSize}, 190)
        handler.postDelayed({binding.flashcardStudyGameFrag.setBackgroundColor(flashcardList[currentPosition].color)}, 190)
        handler.postDelayed({setMarkStatus()}, 190)
    }

    private fun toNextFlashcard () {
        currentPosition++
        binding.tvProgressStudyGameFrag.text = "${currentPosition+1}/${flashcardList.size}"
        binding.lyPreviousStudyGameFrag.visibility = View.VISIBLE

        if (currentPosition == flashcardList.size-1) {
            binding.lyNextStudyGameFrag.visibility = View.INVISIBLE
        } else {
            binding.ivNextStudyGameFrag.visibility = View.VISIBLE
        }

        binding.tvFlashcardWordStudyGameFrag.text = flashcardList[currentPosition].word.uppercase()
        binding.flashcardStudyGameFrag.setBackgroundColor(flashcardList[currentPosition].color)
        setMarkStatus()
    }

    private fun toPreviousFlashcard () {
        currentPosition--
        binding.tvProgressStudyGameFrag.text = "${currentPosition+1}/${flashcardList.size}"
        binding.lyNextStudyGameFrag.visibility = View.VISIBLE

        if (currentPosition == 0) {
            binding.lyPreviousStudyGameFrag.visibility = View.INVISIBLE
        } else {
            binding.ivPreviousStudyGameFrag.visibility = View.VISIBLE
        }

        binding.tvFlashcardWordStudyGameFrag.text = flashcardList[currentPosition].word.uppercase()
        binding.flashcardStudyGameFrag.setBackgroundColor(flashcardList[currentPosition].color)
        setMarkStatus()
    }

    private fun setMarkStatus () {
        if (flashcardList[currentPosition].isMarked) {
            binding.ivMarkStatusStudyGameFrag.setImageResource(R.drawable.ic_marked)
        } else {
            binding.ivMarkStatusStudyGameFrag.setImageResource(R.drawable.ic_non_marked)
        }
    }

    private fun changeMarkStatus () {
        userViewModel.updateMarkStatus(flashcardList[currentPosition])
        setMarkStatus()
    }

    private fun finishGame () {
        val alertDialog = AlertDialog.Builder(requireContext())
        alertDialog.setMessage("Are you sure you want to finish study session?")
            .setCancelable(true)
            .setPositiveButton("Yes") { _, _ ->
                val studyTime = CalendarUtil.getStudyTime(binding.cmTimerStudyGameFrag)
                val studyDate = CalendarUtil.getCurrentDate()
                userViewModel.getStudyModeCheckedDecksList { newDeckList ->
                    userViewModel.updateDeck(newDeckList, oldDeckList)
                }
                findNavController().navigate(StudyGameFragmentDirections.actionStudyGameFragmentToStudyResultFragment(studyDate, studyTime, flashcardList.size.toString()))
            }
            .setNegativeButton("No") { dialogInterface, _ ->
                dialogInterface.cancel()
            }
            .show()
    }
}