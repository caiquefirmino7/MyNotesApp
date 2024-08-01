package com.caique.mynotes.ui

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.caique.mynotes.R
import com.caique.mynotes.databinding.ActivityNoteCreationBinding
import com.caique.mynotes.model.NoteEntity
import com.caique.mynotes.repository.NoteRepository
import com.caique.mynotes.repository.NoteRepositorySingleton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Suppress("DEPRECATION")
class NoteCreation : AppCompatActivity() {

    private lateinit var binding: ActivityNoteCreationBinding
    private lateinit var noteRepository: NoteRepository
    private lateinit var note: NoteEntity
    private var noteId = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityNoteCreationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        configureWindowInsets()
        window.statusBarColor = ContextCompat.getColor(this, R.color.forestGreen)
        val insetsController = ViewCompat.getWindowInsetsController(window.decorView)
        insetsController?.isAppearanceLightStatusBars = false

        noteRepository = NoteRepositorySingleton.getRepository(this)

        binding.bottomNavigation.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.menu_create_save_note -> {
                    saveNote()
                    true
                }
                else -> false
            }
        }

        intent.getParcelableExtra<NoteEntity>("note")?.let { loadedNote ->
            noteId = loadedNote.id
            note = loadedNote
            fillFields(loadedNote)
        }
    }

    private fun configureWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun fillFields(loadedNote: NoteEntity) {
        title = "Edite Nota"

        with(binding) {
            createNoteTitle.setText(loadedNote.title)
            createNoteDesc.setText(loadedNote.description)
        }
    }

    private fun saveNote() {
        val title = binding.createNoteTitle.text.toString()
        val description = binding.createNoteDesc.text.toString()

        if (title.isBlank() || description.isBlank()) {
            Toast.makeText(this, "Os campos devem conter um Título e uma Nota!", Toast.LENGTH_SHORT)
                .show()
            return
        }
        val currentTime = System.currentTimeMillis()
        val newNote = NoteEntity(
            id = noteId,
            title = title,
            description = description,
            timeStamp = if (noteId == 0L) currentTime else note.timeStamp,
            lastModified = currentTime
        )
        CoroutineScope(Dispatchers.IO).launch {
            if (noteId != 0L) {
                noteRepository.update(newNote)
            } else {
                noteRepository.insert(newNote)
            }
            withContext(Dispatchers.Main) {
                binding.createNoteTitle.text.clear()
                binding.createNoteDesc.text.clear()
                Toast.makeText(this@NoteCreation, "Nota Salva com sucesso!", Toast.LENGTH_SHORT)
                    .show()
                finish()
            }
        }
    }
}
