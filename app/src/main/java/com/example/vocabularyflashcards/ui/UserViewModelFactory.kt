package com.example.vocabularyflashcards.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.vocabularyflashcards.data.db.UserDatabase
import com.example.vocabularyflashcards.data.repositories.UserRepository
import java.lang.IllegalArgumentException

class UserViewModelFactory (private var db: UserDatabase) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        if (modelClass.isAssignableFrom(UserViewModel::class.java)) {
            return UserViewModel(UserRepository(db)) as T
        }
        throw IllegalArgumentException ("Unknown viewmodel class")
    }

}