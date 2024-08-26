package com.caique.mynotes.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.caique.mynotes.databinding.ActivitySplashScreenBinding
import com.caique.mynotes.extensions.goTo

@SuppressLint("CustomSplashScreen")
class SplashScreen : AppCompatActivity() {

    private lateinit var binding: ActivitySplashScreenBinding
    private val splashTimeout: Long = 3000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initializeBinding()
        configureWindowInsets()
        startMainActivityAfterDelay()
    }

    private fun initializeBinding() {
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
    }

    private fun startMainActivityAfterDelay() {
        Handler(Looper.getMainLooper()).postDelayed({
            if (!isFinishing) {
                navigateToNoteList()
            }
        }, splashTimeout)
    }

    private fun navigateToNoteList() {
        goTo(NoteList::class.java)
        finish()
    }

    private fun configureWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBarsInsets = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBarsInsets.left, systemBarsInsets.top, systemBarsInsets.right, systemBarsInsets.bottom)
            insets
        }
    }
}
