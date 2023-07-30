package com.example.vocabularyflashcards.ui.login

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.vocabularyflashcards.R
import com.example.vocabularyflashcards.data.db.UserDatabase
import com.example.vocabularyflashcards.data.db.entities.User
import com.example.vocabularyflashcards.databinding.FragmentRegisterBinding
import com.example.vocabularyflashcards.ui.UserViewModel
import com.example.vocabularyflashcards.ui.UserViewModelFactory
import java.io.File
import java.io.FileOutputStream
import java.lang.Exception

class RegisterFragment : Fragment() {
    private lateinit var binding: FragmentRegisterBinding
    private lateinit var imagePickerLauncher: ActivityResultLauncher<Intent>
    private lateinit var userViewModel: UserViewModel
    private lateinit var userDatabase: UserDatabase
    private var imageUri: Uri? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRegisterBinding.inflate(layoutInflater)
        userDatabase = UserDatabase (requireContext())
        userViewModel = ViewModelProvider (requireActivity(), UserViewModelFactory(userDatabase)).get(UserViewModel::class.java)

        binding.btSignupRegisterFrag.setOnClickListener { performRegister() }
        binding.inputPasswordAgainRegisterFrag.doOnTextChanged { text, _, _, _ ->
            checkForPasswordCompatibility(text)
        }

        binding.ivHintIsOnRegisterFrag.setOnClickListener { changePasswordVisibility("on") }
        binding.ivHintIsOffRegisterFrag.setOnClickListener { changePasswordVisibility("off") }
        binding.btAddPhotoRegisterFrag.setOnClickListener {
            val intent = Intent (Intent.ACTION_OPEN_DOCUMENT, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            imagePickerLauncher.launch(intent)
        }

        binding.lyNavToRLoginRegisterFrag.setOnClickListener {
            findNavController().navigate(RegisterFragmentDirections.actionRegisterFragmentToLoginFragment())
        }

        imagePickerLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                imageUri = data?.data
            }

            Glide.with(this).load(imageUri).into(binding.profilePhotoRegisterFrag)
        }

        return binding.root
    }

    private fun performRegister () {
        val username = binding.inputUsernameRegisterFrag.text.toString()
        val password = binding.inputPasswordRegisterFrag.text.toString()
        var profilePhotoBitmap: Bitmap? = null
        if (imageUri != null) {
            profilePhotoBitmap = getBitmap(requireContext(), imageUri!!) //Uri.parse(imageUri)
        }

        if (checkAllInputColumns()) {
            checkUsername(username) { isUnique ->
                if (!isUnique) {
                    Toast.makeText(
                        requireContext(),
                        "This username has already taken.",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    val userProfilePhotoFilePath = saveProfilePhotoToInternalStorage(profilePhotoBitmap!!, requireContext(), username)
                    userViewModel.upsert(
                        User(
                            username,
                            password,
                            userProfilePhotoFilePath!!,
                            mutableListOf(),
                            mutableListOf(),
                            mutableListOf(),
                            false
                        )
                    )
                    findNavController().navigate(RegisterFragmentDirections.actionRegisterFragmentToLoginFragment())
                }
            }
        }
    }

    private fun getBitmap (context: Context, imageUri: Uri): Bitmap? {
            return try {
                val inputStream = context.contentResolver.openInputStream(imageUri)
                val bitmap = BitmapFactory.decodeStream(inputStream)
                inputStream?.close()
                bitmap
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
    }

    private fun saveProfilePhotoToInternalStorage(bitmap: Bitmap, context: Context, username: String): String? {
        val fileName = "profile_photo_$username.jpg" // unique identifier for photo
        val file = File(context.filesDir, fileName)

        return try {
            val outputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            outputStream.flush()
            outputStream.close()
            file.absolutePath // Return the file path of the saved image
        } catch (e: Exception) {
            e.printStackTrace()
            null // Return null if there's an error saving the image
        }
    }



    private fun checkAllInputColumns (): Boolean {
        val username = binding.inputUsernameRegisterFrag.text.toString()
        val password = binding.inputPasswordRegisterFrag.text.toString()
        val passwordAgain = binding.inputPasswordAgainRegisterFrag.text.toString()

        if (username.isEmpty() || password.isEmpty() || passwordAgain.isEmpty() || imageUri == null) {
            Toast.makeText(requireContext(), "Please fill all columns!", Toast.LENGTH_SHORT).show()
            return false
        } else {
            return true
        }
    }

    private fun checkForPasswordCompatibility (text: CharSequence?) {
        val password = binding.inputPasswordRegisterFrag.text.toString()

        if (password.isNotEmpty()) {
            if (text.toString() != password) {
                binding.inputPasswordRegisterFrag.setBackgroundResource(R.drawable.text_input_bkg_error)
                binding.inputPasswordAgainRegisterFrag.setBackgroundResource(R.drawable.text_input_bkg_error)
                binding.btSignupRegisterFrag.isClickable = false
                binding.btSignupRegisterFrag.setBackgroundColor(resources.getColor(R.color.gray_non_clickable_button, null))
            } else {
                binding.inputPasswordRegisterFrag.setBackgroundResource(R.drawable.text_input_bkg)
                binding.inputPasswordAgainRegisterFrag.setBackgroundResource(R.drawable.text_input_bkg)
                binding.btSignupRegisterFrag.isClickable = true
                binding.btSignupRegisterFrag.setBackgroundColor(resources.getColor(R.color.davys_gray, null))
            }
        }
    }

    private fun changePasswordVisibility(status: String) {
        when (status) {
            "on" -> {
                binding.inputPasswordRegisterFrag.transformationMethod = HideReturnsTransformationMethod.getInstance()
                binding.inputPasswordAgainRegisterFrag.transformationMethod = HideReturnsTransformationMethod.getInstance()
                binding.ivHintIsOnRegisterFrag.visibility = View.GONE
                binding.ivHintIsOffRegisterFrag.visibility = View.VISIBLE
            }

            "off" -> {
                binding.inputPasswordRegisterFrag.transformationMethod = PasswordTransformationMethod.getInstance()
                binding.inputPasswordAgainRegisterFrag.transformationMethod = PasswordTransformationMethod.getInstance()
                binding.ivHintIsOnRegisterFrag.visibility = View.VISIBLE
                binding.ivHintIsOffRegisterFrag.visibility = View.GONE
            }
        }
    }

    private fun checkUsername (userInput: String, callback: (Boolean) -> Unit) {
        var isUnique = true

        userViewModel.userList.observe(viewLifecycleOwner) {userList ->
            for (item in userList) {
                if (item.username == userInput) {
                    isUnique = false
                    break
                }
            }

            callback(isUnique)
        }

    }
}