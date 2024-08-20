package com.caique.mynotes.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.caique.mynotes.R
import com.caique.mynotes.databinding.ActivityUserLoginBinding
import com.caique.mynotes.extensions.goTo
import com.caique.mynotes.repository.UserRepository
import com.caique.mynotes.repository.UserRepositorySingleton
import kotlinx.coroutines.launch

class UserLogin : AppCompatActivity() {
    private lateinit var binding: ActivityUserLoginBinding
    private lateinit var userRepository: UserRepository


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityUserLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        userRepository = UserRepositorySingleton.getRepository(this)
        configLoginButton()
        configRegisterButton()
    }


    private fun configLoginButton() {
        with(binding) {
            activityLoginButtonLogin.setOnClickListener {
                val user = activityLoginUser.text.toString()
                val password = activityLoginPassword.text.toString()
                lifecycleScope.launch {
                    userRepository.authenticateUser(user, password)?.let { user ->
                      goTo(NoteList::class.java){
                          putExtra("USER_ID_KEY",user.user )
                      }
                    } ?: Toast.makeText(
                        this@UserLogin,
                        "Falha na autenticação!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun configRegisterButton() {
        binding.activityLoginButtonRegister.setOnClickListener {
            val intent = Intent(this@UserLogin, UserRegister::class.java)
            startActivity(intent)
        }
    }
}