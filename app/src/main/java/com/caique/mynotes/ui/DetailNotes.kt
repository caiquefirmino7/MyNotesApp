package com.caique.mynotes.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.caique.mynotes.R
import com.caique.mynotes.databinding.ActivityDetailNotesBinding
import com.caique.mynotes.extensions.toast
import com.caique.mynotes.model.NoteEntity
import com.caique.mynotes.repository.NoteRepository
import com.caique.mynotes.repository.NoteRepositorySingleton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.lang.reflect.Method

@Suppress("DEPRECATION")
class DetailNotes : AppCompatActivity() {

    private lateinit var binding: ActivityDetailNotesBinding
    private lateinit var note: NoteEntity
    private lateinit var noteRepository: NoteRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initializeBinding()
        setupUI()
        initializeRepository()
        handleIntent()
        configureWindowInsets()
    }

    override fun onResume() {
        super.onResume()
        updateNote()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        Log.d("DetailNotes", "onCreateOptionsMenu: Inflating menu")
        menuInflater.inflate(R.menu.menu_details_note, menu)
        menu?.let { enableIconsInOverflowMenu(it) }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_detail_edit -> {
                editNote()
                true
            }
            R.id.menu_detail_delete -> {
                showDeleteConfirmationDialog()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun initializeBinding() {
        binding = ActivityDetailNotesBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
    }

    private fun setupUI() {
        window.statusBarColor = ContextCompat.getColor(this, R.color.lightBlue)
        val insetsController = ViewCompat.getWindowInsetsController(window.decorView)
        insetsController?.isAppearanceLightStatusBars = false
    }

    private fun initializeRepository() {
        noteRepository = NoteRepositorySingleton.getRepository(this)
    }

    private fun handleIntent() {
        intent.getParcelableExtra<NoteEntity>("note")?.let {
            note = it
            updateNoteDetails(it)
        }
    }

    private fun configureWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.detailRoot) { v, insets ->
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

    private fun updateNote() {
        lifecycleScope.launch {
            noteRepository.getNoteById(note.id)?.let { updatedNote ->
                withContext(Dispatchers.Main) {
                    note = updatedNote
                    updateNoteDetails(note)
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun updateNoteDetails(note: NoteEntity) {
        with(binding) {
            detailTitle.text = note.title
            detailDescription.text = note.description

            val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

            val createdAtDate = dateFormat.format(Date(note.timeStamp))
            val createdAtTime = timeFormat.format(Date(note.timeStamp))
            val lastModified = if (note.lastModified > note.timeStamp) {
                "Última modificação: ${dateFormat.format(Date(note.lastModified))} às ${timeFormat.format(Date(note.lastModified))}"
            } else {
                ""
            }
            detailData.text = "Criado em: $createdAtDate às $createdAtTime"
            detailLastModified.text = lastModified
        }
    }

    private fun editNote() {
        Intent(this, NoteCreation::class.java).apply {
            putExtra("note", note)
            startActivity(this)
        }
    }

    private fun showDeleteConfirmationDialog() {
        AlertDialog.Builder(this)
            .setTitle("Confirmar exclusão")
            .setMessage("Tem certeza que deseja excluir esta nota?")
            .setPositiveButton("Excluir") { dialog, _ ->
                deleteNote()
                dialog.dismiss()
            }
            .setNegativeButton("Cancelar") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun deleteNote() {
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                noteRepository.delete(note)
            }
            finish()
            toast("Nota removida com sucesso!")
        }
    }

    private fun enableIconsInOverflowMenu(menu: Menu) {
        try {
            val menuBuilder = menu::class.java
            val method: Method = menuBuilder.getDeclaredMethod("setOptionalIconsVisible", Boolean::class.java)
            method.isAccessible = true
            method.invoke(menu, true)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
