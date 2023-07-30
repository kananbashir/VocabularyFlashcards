package com.example.vocabularyflashcards.data.db

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.room.TypeConverter
import com.example.vocabularyflashcards.data.db.entities.Deck
import com.example.vocabularyflashcards.data.db.entities.Flashcard
import com.example.vocabularyflashcards.data.db.entities.Quiz
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.ByteArrayOutputStream

class Converters {

    private var gson = Gson()

//    @TypeConverter
//    fun fromBitmap(bitmap: Bitmap): ByteArray {
//        val outputStream = ByteArrayOutputStream()
//        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
//        return outputStream.toByteArray()
//    }
//
//    @TypeConverter
//    fun toBitmap (byteArray: ByteArray): Bitmap {
//        return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
//    }

    @TypeConverter
    fun fromDeck (deckList: List<Deck>): String {
        return gson.toJson(deckList)
    }

    @TypeConverter
    fun toDeck (value: String): MutableList<Deck> {
        val listType = object : TypeToken<MutableList<Deck>>() {}.type
        return gson.fromJson(value, listType)
    }

    @TypeConverter
    fun fromQuiz (quizList: List<Quiz>): String {
        return gson.toJson(quizList)
    }

    @TypeConverter
    fun toQuiz (value: String): MutableList<Quiz> {
        val listType = object : TypeToken<MutableList<Quiz>>() {}.type
        return gson.fromJson(value, listType)
    }

    @TypeConverter
    fun fromFlashcard (flashcardList: List<Flashcard>): String {
        return gson.toJson(flashcardList)
    }

    @TypeConverter
    fun toFlashcard (value: String): MutableList<Flashcard> {
        val listType = object : TypeToken<MutableList<Flashcard>>() {}.type
        return gson.fromJson(value, listType)
    }
}