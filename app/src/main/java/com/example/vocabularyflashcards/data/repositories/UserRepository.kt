package com.example.vocabularyflashcards.data.repositories

import com.example.vocabularyflashcards.data.db.UserDatabase
import com.example.vocabularyflashcards.data.db.entities.User

class UserRepository (private var db: UserDatabase) {

    suspend fun upsert (user: User) = db.getUserDao().upsert(user)

    suspend fun delete (user: User) = db.getUserDao().delete(user)

    fun getAllUser () = db.getUserDao().getAllUser()

}