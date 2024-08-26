package com.caique.mynotes.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.lifecycleScope
import com.caique.mynotes.R
import com.caique.mynotes.extensions.goTo
import com.caique.mynotes.model.UserEntity
import com.caique.mynotes.preferences.dataStore
import com.caique.mynotes.preferences.loggedUserPreferences
import com.caique.mynotes.repository.UserRepository
import com.caique.mynotes.repository.UserRepositorySingleton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

abstract class UserBaseActivity : AppCompatActivity() {

    private lateinit var userRepository: UserRepository

    private val _user: MutableStateFlow<UserEntity?> = MutableStateFlow(null)
    protected val user: StateFlow<UserEntity?> = _user

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        userRepository = UserRepositorySingleton.getRepository(this)
        lifecycleScope.launch {
            checkLoggedUser()
        }

    }

    private suspend fun searchUser(userId: String): UserEntity? {
        return userRepository.getUserById(userId).firstOrNull().also {
            _user.value = it
        }
    }

    protected fun performLogout() {
        lifecycleScope.launch {
            dataStore.edit { preferences ->
                preferences.remove(loggedUserPreferences)
            }
            withContext(Dispatchers.Main) {
                goToLogin()
            }
        }
    }

    protected fun goToLogin() {
        goTo(UserLogin::class.java) {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        }
        finish()
    }

    private suspend fun checkLoggedUser() {
        dataStore.data.collect { preferences ->
            preferences[loggedUserPreferences]?.let { userId ->
                searchUser(userId)
            } ?: goToLogin()
        }
    }

    protected fun configureStatusBar() {
        window.statusBarColor = ContextCompat.getColor(this, R.color.blue)
        ViewCompat.getWindowInsetsController(window.decorView)?.isAppearanceLightStatusBars = false
    }


}