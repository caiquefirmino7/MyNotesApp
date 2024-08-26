package com.caique.mynotes.ui

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.caique.mynotes.R
import com.caique.mynotes.databinding.ActivityNoteCreationBinding
import com.caique.mynotes.extensions.toast
import com.caique.mynotes.model.NoteEntity
import com.caique.mynotes.repository.NoteRepository
import com.caique.mynotes.repository.NoteRepositorySingleton
import com.caique.mynotes.repository.UserRepository
import com.caique.mynotes.repository.UserRepositorySingleton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Suppress("DEPRECATION")
class NoteCreation : UserBaseActivity() {

    private lateinit var binding: ActivityNoteCreationBinding
    private lateinit var noteRepository: NoteRepository
    private lateinit var userRepository: UserRepository
    private var note: NoteEntity? = null
    private var noteId = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityNoteCreationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initializeRepositories()
        configureWindowInsets()
        setupUI()
        handleIntent()
        checkUserAuthentication()
    }

    private fun setupUI() {
        configureStatusBar()
        configureBottomNavigation()
    }
    
    private fun configureBottomNavigation() {
        binding.bottomNavigation.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.menu_create_save_note -> {
                    saveNote()
                    true
                }
                else -> false
            }
        }
    }

    private fun initializeRepositories() {
        noteRepository = NoteRepositorySingleton.getRepository(this)
        userRepository = UserRepositorySingleton.getRepository(this)
    }

    private fun handleIntent() {
        intent.getParcelableExtra<NoteEntity>("note")?.let { loadedNote ->
            noteId = loadedNote.id
            note = loadedNote
            fillFields(loadedNote)
        }
    }

    private fun fillFields(loadedNote: NoteEntity) {
        title = "Edite Nota"
        with(binding) {
            createNoteTitle.setText(loadedNote.title)
            createNoteDesc.setText(loadedNote.description)
        }
    }

    private fun configureWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun checkUserAuthentication() {
        lifecycleScope.launch {
            user.filterNotNull().collect { loggedInUser ->
                Log.i("NoteCreation", "Usuário logado: $loggedInUser")
            }
        }
    }

    private fun saveNote() {
        val title = binding.createNoteTitle.text.toString().trim()
        val description = binding.createNoteDesc.text.toString().trim()

        if (title.isEmpty() || description.isEmpty()) {
            toast("Os campos devem conter um Título e uma Nota!")
            return
        }

        lifecycleScope.launch {
            val loggedUserId = user.value?.user
            if (loggedUserId != null) {
                val newNote = createNoteEntity(title, description, loggedUserId)
                saveOrUpdateNoteInDatabase(newNote)
            } else {
                toast("Usuário não encontrado. Não é possível salvar a nota.")
            }
        }
    }

    private fun createNoteEntity(title: String, description: String, userId: String): NoteEntity {
        val currentTime = System.currentTimeMillis()
        return NoteEntity(
            id = noteId,
            title = title,
            description = description,
            timeStamp = if (noteId == 0L) currentTime else note?.timeStamp ?: currentTime,
            lastModified = currentTime,
            userId = userId
        )
    }

    private suspend fun saveOrUpdateNoteInDatabase(note: NoteEntity) {
        withContext(Dispatchers.IO) {
            if (noteId != 0L) {
                noteRepository.update(note)
            } else {
                noteRepository.insert(note)
            }
        }
        withContext(Dispatchers.Main) {
            clearFields()
            toast("Nota Salva com sucesso!")
            finish()
        }
    }

    private fun clearFields() {
        with(binding) {
            createNoteTitle.text.clear()
            createNoteDesc.text.clear()
        }
    }

}
