package com.example.vocabularyflashcards

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowInsetsController
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.bumptech.glide.Glide
import com.example.vocabularyflashcards.data.db.UserDatabase
import com.example.vocabularyflashcards.data.db.entities.User
import com.example.vocabularyflashcards.databinding.ActivityMainBinding
import com.example.vocabularyflashcards.ui.UserViewModel
import com.example.vocabularyflashcards.ui.UserViewModelFactory
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private lateinit var userViewModel: UserViewModel
    private lateinit var userDatabase: UserDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.setDecorFitsSystemWindows(false)
            val controller = window.insetsController
            controller?.setSystemBarsAppearance(WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS, WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS)
            window.statusBarColor = getColor(R.color.white)
        }

        userDatabase = UserDatabase (this)
        userViewModel = ViewModelProvider(this, UserViewModelFactory(userDatabase)).get(UserViewModel::class.java)

        binding.flLoadingApp.visibility = View.VISIBLE
        GlobalScope.launch {
            delay(400)
            binding.flLoadingApp.visibility = View.INVISIBLE
        }

        getOnlineUser { user ->
            Glide.with(this).load(getBitmap(this, "profile_photo_${user.username}.jpg")!!).into(binding.ivProfilePhotoHeader)
            binding.tvUsernameHeader.text = user.username
        }

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        navController = navHostFragment.navController

        val topLevelDestinations = setOf(
            R.id.homeFragment,
            R.id.searchFragment,
            R.id.favoritesFragment,
            R.id.studyModeFragment,
            R.id.quizModeFragment
        )

        navController.addOnDestinationChangedListener { _, destination, _ ->
            if (destination.id in topLevelDestinations) {
                binding.mainActivityHeader.visibility = View.VISIBLE
                binding.bottomNavMenu.visibility = View.VISIBLE
                binding.bottomNavMenu.setupWithNavController(navController)
            } else {
                binding.mainActivityHeader.visibility = View.GONE
                binding.bottomNavMenu.visibility = View.GONE
            }
        }

        binding.ivLogoutHeader.setOnClickListener { logOut() }

        val onBackPressedCallback = object: OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (binding.bottomNavMenu.isVisible) {
                    val alertDialog: AlertDialog.Builder = AlertDialog.Builder(this@MainActivity)
                    alertDialog.setMessage("Do you want to exit?")
                        .setCancelable(true)
                        .setPositiveButton("Yes") { _, _ ->
                            userViewModel.makeUserOffline()
                            val loginGraph = navController.navInflater.inflate(R.navigation.navigation)
                            navController.graph = loginGraph
                        }
                        .setNegativeButton("No") { dialogInterface, _ ->
                            dialogInterface.cancel()
                        }
                        .show()
                }
            }
        }

        onBackPressedDispatcher.addCallback(this, onBackPressedCallback)

    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    private fun getOnlineUser (callback: (User) -> Unit) {
        userViewModel.userList.observe(this) {userList ->
            for (user in userList) {
                if (user.isOnline) {
                    callback (user)
                }
            }
        }
    }

    private fun getBitmap (context: Context, fileName: String): Bitmap? {
        try {
            val inputStream = context.openFileInput(fileName)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            inputStream.close()

            return bitmap
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return null
    }

    private fun logOut () {
        userViewModel.makeUserOffline()
        val loginGraph = navController.navInflater.inflate(R.navigation.navigation)
        navController.graph = loginGraph
    }
}