package com.caique.mynotes.ui

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.caique.mynotes.R
import com.caique.mynotes.databinding.ActivityUserRegisterBinding
import com.caique.mynotes.model.UserEntity
import com.caique.mynotes.repository.UserRepository
import com.caique.mynotes.repository.UserRepositorySingleton
import kotlinx.coroutines.launch

class UserRegister : AppCompatActivity() {

    private lateinit var binding: ActivityUserRegisterBinding
   private lateinit var userRepository: UserRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserRegisterBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)

        userRepository = UserRepositorySingleton.getRepository(this@UserRegister)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        configButtonRegister()
    }

    private fun configButtonRegister() {

        binding.activityRegistrationFormButtonRegister.setOnClickListener {
            val newUser = createNewUser()
            Log.i("cadastroUsuario", "onCreate: $newUser")
            lifecycleScope.launch {
                try {
                    userRepository.insertUser(newUser)
                    finish()
                } catch (e: Exception) {
                    Toast.makeText(
                        this@UserRegister,
                        "Falha ao cadastrar usuário!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
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
}