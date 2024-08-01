package com.caique.mynotes.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.caique.mynotes.R
import com.caique.mynotes.databinding.ActivityMainBinding
import com.caique.mynotes.model.NoteEntity
import com.caique.mynotes.recyclerview.adapter.NoteListAdapter
import com.caique.mynotes.repository.NoteRepository
import com.caique.mynotes.repository.NoteRepositorySingleton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.Normalizer
import java.util.regex.Pattern

@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var noteAdapter: NoteListAdapter
    private lateinit var noteRepository: NoteRepository
    private var allNotes: List<NoteEntity> = listOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        configureWindowInsets()
        window.statusBarColor = ContextCompat.getColor(this, R.color.forestGreen)
        val insetsController = ViewCompat.getWindowInsetsController(window.decorView)
        insetsController?.isAppearanceLightStatusBars = false

        noteRepository = NoteRepositorySingleton.getRepository(this)

        noteAdapter = NoteListAdapter(
            mutableListOf(),
            clickToRemove = { note -> confirmToRemove(note) },
            clickToEdit = { note -> editNote(note) }
        )

        setupUI()
    }

    override fun onResume() {
        super.onResume()
        loadNotes()
        clearSearchViewFocus()
    }

    private fun setupUI() {
        configureRecyclerView()
        configureExtendedFab()
        setupSearchView()
    }

    private fun configureRecyclerView() {
        binding.recyclerView.adapter = noteAdapter
        binding.recyclerView.layoutManager =
            StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
    }

    private fun configureExtendedFab() {
        binding.extendedFab.setOnClickListener {
            navigateToNoteEditor()
        }
    }

    private fun navigateToNoteEditor() {
        val intent = Intent(this, NoteCreation::class.java)
        startActivity(intent)
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun updateUI() {
        noteAdapter.notifyDataSetChanged()
        if (allNotes.isEmpty()) {
            binding.emptyImage.visibility = View.VISIBLE
            binding.recyclerView.visibility = View.GONE
        } else {
            binding.emptyImage.visibility = View.GONE
            binding.recyclerView.visibility = View.VISIBLE
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_sort_recent -> {
                sortNotesByRecent()
                true
            }
            R.id.action_sort_oldest -> {
                sortNotesByOldest()
                true
            }
            R.id.action_sort_az -> {
                sortNotesAlphabetically()
                true
            }
            R.id.action_sort_za -> {
                sortNotesReverseAlphabetically()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun sortNotesByRecent() {
        val sortedNotes = allNotes.sortedByDescending { it.timeStamp }
        noteAdapter.updateNotes(sortedNotes)
    }

    private fun sortNotesByOldest() {
        val sortedNotes = allNotes.sortedBy { it.timeStamp }
        noteAdapter.updateNotes(sortedNotes)
    }

    private fun sortNotesAlphabetically() {
        val sortedNotes = allNotes.sortedBy { it.title }
        noteAdapter.updateNotes(sortedNotes)
    }

    private fun sortNotesReverseAlphabetically() {
        val sortedNotes = allNotes.sortedByDescending { it.title }
        noteAdapter.updateNotes(sortedNotes)
    }

    private fun setupSearchView() {
        binding.searchView.setOnQueryTextListener(object : android.widget.SearchView.OnQueryTextListener,
            SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                val filteredNotes = if (newText.isNullOrEmpty()) {
                    allNotes
                } else {
                    val normalizedQuery = normalizeString(newText)
                    allNotes.filter {
                        normalizeString(it.title).contains(normalizedQuery, ignoreCase = true)
                    }
                }
                noteAdapter.updateNotes(filteredNotes)
                return true
            }
        })
    }

    private fun normalizeString(text: String): String {
        val temp = Normalizer.normalize(text, Normalizer.Form.NFD)
        return Pattern.compile("\\p{InCombiningDiacriticalMarks}+").matcher(temp).replaceAll("")
    }

    private fun clearSearchViewFocus() {
        with(binding) {
            searchView.clearFocus()
            searchView.setQuery("", false)
        }
    }

    private fun loadNotes() {
        lifecycleScope.launch {
            allNotes = noteRepository.getAllNotes()
            noteAdapter.updateNotes(allNotes)
            updateUI()
        }
    }

    private fun editNote(note: NoteEntity) {
        val intent = Intent(this, NoteCreation::class.java).apply {
            putExtra("note", note)
        }
        startActivity(intent)
    }

    private fun confirmToRemove(note: NoteEntity) {
        AlertDialog.Builder(this).apply {
            setTitle("Escolha uma ação")
            setMessage("Tem certeza que deseja excluir essa nota?")
            setPositiveButton("Sim") { _, _ ->
                removeNote(note)
            }
            setNegativeButton("Não") { dialog, _ ->
                dialog.dismiss()
            }
            create().show()
        }
    }

    private fun removeNote(note: NoteEntity) {
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                noteRepository.delete(note)
            }
            loadNotes()
            Toast.makeText(this@MainActivity, "Nota removida com sucesso!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun configureWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}
