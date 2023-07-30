package com.example.vocabularyflashcards.ui.game.study

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.vocabularyflashcards.R
import com.example.vocabularyflashcards.data.db.UserDatabase
import com.example.vocabularyflashcards.data.db.entities.Deck
import com.example.vocabularyflashcards.databinding.FragmentStudyResultBinding
import com.example.vocabularyflashcards.ui.UserViewModel
import com.example.vocabularyflashcards.ui.UserViewModelFactory
import com.example.vocabularyflashcards.utils.AppUtils

class StudyResultFragment : Fragment() {
    private lateinit var binding: FragmentStudyResultBinding
    private lateinit var userViewModel: UserViewModel
    private lateinit var userDatabase: UserDatabase
    private lateinit var studyResultArgs: StudyResultFragmentArgs

   override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
       binding = FragmentStudyResultBinding.inflate(layoutInflater)
       userDatabase = UserDatabase(requireContext())
       userViewModel = ViewModelProvider (requireActivity(), UserViewModelFactory(userDatabase)).get(UserViewModel::class.java)
       studyResultArgs = StudyResultFragmentArgs.fromBundle(requireArguments())

       userViewModel.getStudyModeCheckedDecksList { deckList ->
           AppUtils.getUsedDecksAndMarkedFlashcards (deckList) { usedDecks, markedFlashcards ->
               binding.tvUsedDecksStudyResultFrag.text = usedDecks
               binding.tvMarkedFlashcardsStudyResultFrag.text = markedFlashcards
               binding.tvDateStudyResultFrag.text = studyResultArgs.studyDate
               binding.tvSpentTimeStudyResultFrag.text = "${studyResultArgs.studyTime} minutes"
               binding.tvUsedFlashcardsStudyResultFrag.text = "Total ${studyResultArgs.flashcardSize} flashcards"
           }
       }


       binding.btNavtoHomeStudyResultFrag.setOnClickListener {
           findNavController().navigate(StudyResultFragmentDirections.actionGlobalMainSession2())
       }

       return binding.root
    }
}