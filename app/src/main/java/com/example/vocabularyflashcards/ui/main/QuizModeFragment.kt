package com.example.vocabularyflashcards.ui.main

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout.LayoutParams
import android.widget.PopupWindow
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.vocabularyflashcards.R
import com.example.vocabularyflashcards.adapters.QuizModeAdapter
import com.example.vocabularyflashcards.data.db.UserDatabase
import com.example.vocabularyflashcards.data.db.entities.Deck
import com.example.vocabularyflashcards.databinding.FragmentQuizModeBinding
import com.example.vocabularyflashcards.ui.UserViewModel
import com.example.vocabularyflashcards.ui.UserViewModelFactory
import com.example.vocabularyflashcards.utils.AppUtils

class QuizModeFragment : Fragment() {
    private lateinit var binding: FragmentQuizModeBinding
    private lateinit var userViewModel: UserViewModel
    private lateinit var userDatabase: UserDatabase
    private lateinit var quizModeAdapter: QuizModeAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentQuizModeBinding.inflate(layoutInflater)
        userDatabase = UserDatabase (requireContext())
        userViewModel = ViewModelProvider(requireActivity(), UserViewModelFactory(userDatabase)).get(UserViewModel::class.java)
        quizModeAdapter = QuizModeAdapter()
        binding.rvQuizFrag.adapter = quizModeAdapter
        binding.rvQuizFrag.layoutManager = LinearLayoutManager(requireContext())

        binding.ivFilterIconQuizModeFrag.setOnClickListener { showDropDown(binding.ivFilterIconQuizModeFrag) }
        binding.btGoQuizQuizModeFrag.setOnClickListener { startQuizSession() }

        binding.rgQuizModeFrag.setOnCheckedChangeListener{ _, checkedId ->
            when (checkedId) {
                R.id.rbCustomSelectionQuizModeFrag -> {
                    binding.tvFilterOptionQuizModeFrag.visibility = View.VISIBLE
                    binding.ivFilterIconQuizModeFrag.visibility = View.VISIBLE
                    binding.tvRandomSelectionStudyModeFrag.visibility = View.GONE
                    userViewModel.getOnlineUser { user ->
                        quizModeAdapter.setDeckList(user.deckList)
                    }
                }
                R.id.rbRandomSelectionQuizModeFrag -> {
                    binding.tvFilterOptionQuizModeFrag.visibility = View.INVISIBLE
                    binding.ivFilterIconQuizModeFrag.visibility = View.INVISIBLE
                    binding.tvRandomSelectionStudyModeFrag.visibility = View.VISIBLE
                    quizModeAdapter.setDeckList(listOf())
                }
            }
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        userViewModel.getOnlineUser { user ->
            quizModeAdapter.setDeckList(user.deckList)
        }
    }

    private fun showDropDown (anchorView: View) {
        userViewModel.getOnlineUser { user ->
            val inflater = LayoutInflater.from(anchorView.context)
            val popupView = inflater.inflate(R.layout.filter_dropdown_layout, null)

            val rbNewestButton: RadioButton =
                popupView.findViewById(R.id.rbFilterNewestDropdownItem)
            val rbOldestButton: RadioButton =
                popupView.findViewById(R.id.rbFilterOldestDropdownItem)
            val rbFavouritesButton: RadioButton =
                popupView.findViewById(R.id.rbFilterFavouritesDropdownItem)

            val popupWindow = PopupWindow(
                popupView,
                LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT,
                true
            )

            rbNewestButton.setOnClickListener {
                binding.tvFilterOptionQuizModeFrag.setText(R.string.text_filter_newest_first)
                quizModeAdapter.setDeckList(user.deckList)
                popupWindow.dismiss()
            }

            rbOldestButton.setOnClickListener {
                binding.tvFilterOptionQuizModeFrag.setText(R.string.text_filter_oldest_first)
                quizModeAdapter.setDeckList(user.deckList.reversed())
                popupWindow.dismiss()
            }

            rbFavouritesButton.setOnClickListener {
                binding.tvFilterOptionQuizModeFrag.setText(R.string.text_filter_favourites_first)
                quizModeAdapter.setDeckList(filterDeckList(user.deckList))
                popupWindow.dismiss()
            }

            popupWindow.showAsDropDown(anchorView)
        }
    }

    private fun filterDeckList (deckList: List<Deck>): List<Deck> {
        val filteredDeckList = mutableListOf<Deck>()

        val favouriteDecksList = deckList.filter { deck ->
            deck.isFavourite
        }

        for (favouriteDeck in favouriteDecksList) {
            filteredDeckList.add(favouriteDeck)
        }

        val nonFavouriteDecksList = deckList.filter { deck ->
            !deck.isFavourite
        }

        for (nonFavouriteDeck in nonFavouriteDecksList) {
            filteredDeckList.add(nonFavouriteDeck)
        }
        return filteredDeckList
    }

    private fun startQuizSession () {
        when (binding.rgQuizModeFrag.checkedRadioButtonId) {
            R.id.rbCustomSelectionQuizModeFrag -> {
                val checkedDeckList = quizModeAdapter.getCheckedDeckList()
                if (checkedDeckList.isNotEmpty()) {
                    userViewModel.setNewQuiz(checkedDeckList)
                    findNavController().navigate(QuizModeFragmentDirections.actionGlobalQuizGameSession())
                } else {
                    Toast.makeText(requireContext(), "Please, select at least one deck", Toast.LENGTH_SHORT).show()
                }
            }
            R.id.rbRandomSelectionQuizModeFrag -> {
                userViewModel.getOnlineUser { user ->
                    AppUtils.getRandomSelectedDecksList(user.deckList) { deckList ->
                        userViewModel.setNewQuiz(deckList)
                    }
                }
                findNavController().navigate(QuizModeFragmentDirections.actionGlobalQuizGameSession())
            }
        }
    }


}