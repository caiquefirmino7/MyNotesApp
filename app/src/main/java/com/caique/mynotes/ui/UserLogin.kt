package com.caique.mynotes.ui

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.lifecycleScope
import com.caique.mynotes.R
import com.caique.mynotes.databinding.ActivityUserLoginBinding
import com.caique.mynotes.extensions.goTo
import com.caique.mynotes.extensions.toast
import com.caique.mynotes.preferences.dataStore
import com.caique.mynotes.preferences.loggedUserPreferences
import com.caique.mynotes.repository.UserRepository
import com.caique.mynotes.repository.UserRepositorySingleton
import kotlinx.coroutines.launch

@Suppress("DEPRECATION")
class UserLogin : AppCompatActivity() {
    private lateinit var binding: ActivityUserLoginBinding
    private lateinit var userRepository: UserRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initializeBinding()
        configureWindowInsets()
        initializeRepositories()
        setupUI()
    }

    private fun initializeBinding() {
        binding = ActivityUserLoginBinding.inflate(layoutInflater)
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

    private fun initializeRepositories() {
        userRepository = UserRepositorySingleton.getRepository(this)
    }

    private fun setupUI() {
        configureLoginButton()
        configureStatusBar()
        configureRegisterButton()
    }

    private fun configureLoginButton() {
        binding.activityLoginButtonLogin.setOnClickListener {
            val user = binding.activityLoginUser.text.toString()
            val password = binding.activityLoginPassword.text.toString()
            performAuthentication(user, password)
        }
    }

    private fun performAuthentication(user: String, password: String) {
        lifecycleScope.launch {
            userRepository.authenticateUser(user, password)?.let { authenticatedUser ->
                saveUserToPreferences(authenticatedUser.user)
                navigateToNoteList()
            } ?: showAuthenticationError()
        }
    }

    private suspend fun saveUserToPreferences(user: String) {
        dataStore.edit { preferences ->
            preferences[loggedUserPreferences] = user
        }
    }

    private fun navigateToNoteList() {
        goTo(NoteList::class.java)
        finish()
    }

    private fun showAuthenticationError() {
        toast("Falha na autenticação!")
    }

    private fun configureRegisterButton() {
        binding.activityLoginButtonRegister.setOnClickListener {
            goTo(UserRegister::class.java)
        }
    }

    protected fun configureStatusBar() {
        window.statusBarColor = ContextCompat.getColor(this, R.color.blue)
        ViewCompat.getWindowInsetsController(window.decorView)?.isAppearanceLightStatusBars = false
    }
}
