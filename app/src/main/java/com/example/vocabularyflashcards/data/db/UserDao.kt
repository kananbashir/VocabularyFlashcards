package com.example.vocabularyflashcards.data.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.vocabularyflashcards.data.db.entities.User

@Dao
interface UserDao {

    @Insert (onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert (user: User)

    @Delete
    suspend fun delete (user: User)

    @Query ("SELECT * FROM user_table")
    fun getAllUser (): LiveData<List<User>>

}