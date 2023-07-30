package com.example.vocabularyflashcards.ui.login

import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.vocabularyflashcards.data.db.UserDatabase
import com.example.vocabularyflashcards.data.db.entities.User
import com.example.vocabularyflashcards.databinding.FragmentLoginBinding
import com.example.vocabularyflashcards.ui.UserViewModel
import com.example.vocabularyflashcards.ui.UserViewModelFactory

class LoginFragment : Fragment() {
    private lateinit var binding: FragmentLoginBinding
    private lateinit var userViewModel: UserViewModel
    private lateinit var userDatabase: UserDatabase

   override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

       userDatabase = UserDatabase(requireContext())
       userViewModel = ViewModelProvider (requireActivity(), UserViewModelFactory(userDatabase)).get(UserViewModel::class.java)

       checkForOnlineUser { isOnline ->
           if (isOnline) {
               findNavController().navigate(LoginFragmentDirections.actionGlobalNavigation2())
           }
       }

       binding = FragmentLoginBinding.inflate(layoutInflater)
       binding.btSigninLoginFrag.setOnClickListener { performLogin() }
       binding.lyNavToRegisterLoginFrag.setOnClickListener {
           findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToRegisterFragment())
       }

       binding.ivHintIsOnLoginFrag.setOnClickListener { changePasswordVisibility ("on") }
       binding.ivHintIsOffLoginFrag.setOnClickListener { changePasswordVisibility("off") }

       return binding.root
    }



    private fun performLogin () {
        val username = binding.inputUsernameLoginFrag.text.toString()
        val password = binding.inputPasswordLoginFrag.text.toString()

        checkUsernameAndPassword(username, password) {isMatching ->
            if (isMatching) {
                userViewModel.makeUserOnline(username)
                findNavController().navigate(LoginFragmentDirections.actionGlobalNavigation2())
            } else {
                Toast.makeText(requireContext(), "Username or Password is incorrect", Toast.LENGTH_SHORT).show()
            }

        }
    }

    private fun checkUsernameAndPassword (username: String, password: String, callback: (Boolean) -> Unit) {
        var isMatching = false

        userViewModel.userList.observe(viewLifecycleOwner) {userList ->
            for (user in userList) {
                if (username == user.username && password == user.password) {
                    isMatching = true
                    break
                }
            }

            callback (isMatching)
        }

    }

    private fun changePasswordVisibility (status: String) {
        when (status) {
            "on" -> {
                binding.inputPasswordLoginFrag.transformationMethod = HideReturnsTransformationMethod.getInstance()
                binding.ivHintIsOnLoginFrag.visibility = View.GONE
                binding.ivHintIsOffLoginFrag.visibility = View.VISIBLE
            }
            "off" -> {
                binding.inputPasswordLoginFrag.transformationMethod = PasswordTransformationMethod.getInstance()
                binding.ivHintIsOnLoginFrag.visibility = View.VISIBLE
                binding.ivHintIsOffLoginFrag.visibility = View.GONE
            }
        }
    }

    private fun checkForOnlineUser (callback: (Boolean) -> Unit) {
        var isOnline = false

        userViewModel.userList.observe(viewLifecycleOwner) {userList ->
            for (user in userList) {
                if (user.isOnline) {
                    isOnline = true
                    break
                } else {
                    Log.i("TCP", "No online user - ${user.username} is ${user.isOnline}")
                }
            }

            callback (isOnline)
        }
    }
}