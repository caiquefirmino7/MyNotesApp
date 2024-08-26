package com.caique.mynotes.ui

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.caique.mynotes.R
import com.caique.mynotes.databinding.ActivityUserRegisterBinding
import com.caique.mynotes.extensions.toast
import com.caique.mynotes.model.UserEntity
import com.caique.mynotes.repository.UserRepository
import com.caique.mynotes.repository.UserRepositorySingleton
import kotlinx.coroutines.launch

class UserRegister : AppCompatActivity() {

    private lateinit var binding: ActivityUserRegisterBinding
    private lateinit var userRepository: UserRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initializeBinding()
        configureWindowInsets()
        initializeRepository()
        setupUI()
    }

    private fun initializeBinding() {
        binding = ActivityUserRegisterBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
    }

    private fun configureWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBarsInsets = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(
                systemBarsInsets.left,
                systemBarsInsets.top,
                systemBarsInsets.right,
                systemBarsInsets.bottom
            )
            insets
        }
    }

    private fun initializeRepository() {
        userRepository = UserRepositorySingleton.getRepository(this)
    }

    private fun setupUI() {
        configureRegisterButton()
    }

    private fun configureRegisterButton() {
        binding.activityRegistrationFormButtonRegister.setOnClickListener {
            val newUser = createNewUser()
            Log.i("UserRegister", "New User: $newUser")
            registerUser(newUser)
        }
    }

    private fun createNewUser(): UserEntity {
        with(binding) {
            val user = activityRegistrationFormUser.text.toString()
            val name = activityRegistrationFormName.text.toString()
            val password = activityRegistrationFormPassword.text.toString()
            return UserEntity(
                user = user,
                name = name,
                password = password
            )
        }
    }

    private fun registerUser(newUser: UserEntity) {
        lifecycleScope.launch {
            try {
                userRepository.insertUser(newUser)
                finish()
            } catch (e: Exception) {
                toast("Falha ao cadastrar usuário!")
            }
        }
    }
}
