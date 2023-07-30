package com.example.vocabularyflashcards.ui.main

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupWindow
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.vocabularyflashcards.R
import com.example.vocabularyflashcards.adapters.StudyModeAdapter
import com.example.vocabularyflashcards.data.db.UserDatabase
import com.example.vocabularyflashcards.data.db.entities.Deck
import com.example.vocabularyflashcards.databinding.FragmentStudyModeBinding
import com.example.vocabularyflashcards.ui.UserViewModel
import com.example.vocabularyflashcards.ui.UserViewModelFactory
import com.example.vocabularyflashcards.utils.AppUtils

class StudyModeFragment : Fragment() {
    private lateinit var binding: FragmentStudyModeBinding
    private lateinit var userViewModel: UserViewModel
    private lateinit var userDatabase: UserDatabase
    private lateinit var studyModeAdapter: StudyModeAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentStudyModeBinding.inflate(layoutInflater)
        userDatabase = UserDatabase (requireContext())
        userViewModel = ViewModelProvider(requireActivity(), UserViewModelFactory(userDatabase)).get(UserViewModel::class.java)
        studyModeAdapter = StudyModeAdapter()
        binding.rvStudyModeFrag.adapter = studyModeAdapter
        binding.rvStudyModeFrag.layoutManager = LinearLayoutManager(requireContext())
        binding.ivFilterIconStudyModeFrag.setOnClickListener { showDropdown(binding.ivFilterIconStudyModeFrag) }
        binding.btGoStudyStudyModeFrag.setOnClickListener { startStudySession() }

        binding.rgStudyModeFrag.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.rbCustomSelectionStudyModeFrag -> {
                    binding.tvFilterOptionStudyModeFrag.visibility = View.VISIBLE
                    binding.ivFilterIconStudyModeFrag.visibility = View.VISIBLE
                    userViewModel.getOnlineUser { user ->
                        studyModeAdapter.setDeckList(user.deckList)
                    }
                    binding.tvRandomSelectionStudyModeFrag.visibility = View.INVISIBLE
                    binding.tvFilterOptionStudyModeFrag.setText(R.string.text_filter_newest_first)
                }
                R.id.rbRandomSelectionStudyModeFrag -> {
                    binding.tvFilterOptionStudyModeFrag.visibility = View.INVISIBLE
                    binding.ivFilterIconStudyModeFrag.visibility = View.INVISIBLE
                    binding.tvRandomSelectionStudyModeFrag.visibility = View.VISIBLE
                    studyModeAdapter.setDeckList(listOf())
                }
            }
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        userViewModel.getOnlineUser { user ->
            studyModeAdapter.setDeckList(user.deckList)
        }
    }

    private fun showDropdown (anchorView: ImageView) {
        userViewModel.getOnlineUser { user ->

            val inflater = LayoutInflater.from(anchorView.context)
            val popupView = inflater.inflate(R.layout.filter_dropdown_layout, null)

            val radioButtonNewest = popupView.findViewById<RadioButton>(R.id.rbFilterNewestDropdownItem)
            val radioButtonOldest = popupView.findViewById<RadioButton>(R.id.rbFilterOldestDropdownItem)
            val radioButtonFavorites = popupView.findViewById<RadioButton>(R.id.rbFilterFavouritesDropdownItem)

            val popupWindow = PopupWindow(
                popupView,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                true
            )
            popupWindow.elevation = 20f

            radioButtonNewest.setOnClickListener {
                binding.tvFilterOptionStudyModeFrag.setText(R.string.text_filter_newest_first)
                studyModeAdapter.setDeckList(user.deckList)
                popupWindow.dismiss()
            }

            radioButtonOldest.setOnClickListener {
                binding.tvFilterOptionStudyModeFrag.setText(R.string.text_filter_oldest_first)
                studyModeAdapter.setDeckList(user.deckList.reversed())
                popupWindow.dismiss()
            }

            radioButtonFavorites.setOnClickListener {
                binding.tvFilterOptionStudyModeFrag.setText(R.string.text_filter_favourites_first)
                studyModeAdapter.setDeckList(filterDeckList(user.deckList))
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

    private fun startStudySession () {
        when (binding.rgStudyModeFrag.checkedRadioButtonId) {
            R.id.rbCustomSelectionStudyModeFrag -> {
                val checkedDeckList = studyModeAdapter.getCheckedDecksList()
                if (checkedDeckList.isNotEmpty()) {
                    userViewModel.setStudyModeCheckedDecksList(checkedDeckList)
                    findNavController().navigate(StudyModeFragmentDirections.actionGlobalStudyGameSession())
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Please, select at least one deck",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            R.id.rbRandomSelectionStudyModeFrag -> {
                userViewModel.getOnlineUser { user ->
                    AppUtils.getRandomSelectedDecksList(user.deckList) {
                        userViewModel.setStudyModeCheckedDecksList(it)
                        findNavController().navigate(StudyModeFragmentDirections.actionGlobalStudyGameSession())
                    }
                }
            }
        }
    }
}