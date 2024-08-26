package com.caique.mynotes.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.caique.mynotes.R
import com.caique.mynotes.databinding.ActivityNoteListBinding
import com.caique.mynotes.extensions.goTo
import com.caique.mynotes.model.NoteEntity
import com.caique.mynotes.recyclerview.adapter.NoteListAdapter
import com.caique.mynotes.repository.NoteRepository
import com.caique.mynotes.repository.NoteRepositorySingleton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.Normalizer
import java.util.regex.Pattern

@Suppress("DEPRECATION")
class NoteList : UserBaseActivity() {

    private lateinit var binding: ActivityNoteListBinding
    private lateinit var noteAdapter: NoteListAdapter
    private lateinit var noteRepository: NoteRepository
    private var allNotes: List<NoteEntity> = listOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initializeBinding()
        configureNoteAdapter()
        configureUI()
        configureWindowInsets()
        configureStatusBar()
        noteRepository = NoteRepositorySingleton.getRepository(this)
    }

    override fun onResume() {
        super.onResume()
        loadNotes()
        clearSearchViewFocus()
    }

    @Suppress("OVERRIDE_DEPRECATION")
    override fun onBackPressed() {
        super.onBackPressed()
        finishAffinity()
    }

    private fun initializeBinding() {
        binding = ActivityNoteListBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
    }

    private fun configureNoteAdapter() {
        noteAdapter = NoteListAdapter(
            mutableListOf(),
            clickToRemove = { note -> confirmToRemove(note) },
            clickToEdit = { note -> editNoteDetails(note) }
        )
    }

    private fun configureUI() {
        setupRecyclerView()
        setupExtendedFab()
        setupSearchView()
        setupBottomNavigationView()
    }

    private fun setupRecyclerView() {
        binding.recyclerView.adapter = noteAdapter
        binding.recyclerView.layoutManager =
            StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
    }

    private fun setupExtendedFab() {
        binding.extendedFab.setOnClickListener { goTo(NoteCreation::class.java) }
    }

    private fun setupSearchView() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = false

            override fun onQueryTextChange(newText: String?): Boolean {
                val filteredNotes = filterNotes(newText)
                noteAdapter.updateNotes(filteredNotes)
                return true
            }
        })
    }

    private fun setupBottomNavigationView() {
        binding.bottomNavigation.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.menu_user_logout -> {
                    confirmLogout()
                    true
                }

                R.id.menu_user_profile -> {
                    goTo(UserProfile::class.java)
                    true
                }
                else -> false
            }
        }
    }

    private fun configureWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }




    private fun loadNotes() {
        lifecycleScope.launch {
            user.filterNotNull().collect { user ->
                fetchUserNotes(user.user)
            }
        }
    }

    private suspend fun fetchUserNotes(userId: String) {
        noteRepository.getNotesByUser(userId).collect { notes ->
            allNotes = notes
            updateUI()
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun updateUI() {
        noteAdapter.updateNotes(allNotes)
        if (allNotes.isEmpty()) {
            binding.emptyImage.visibility = View.VISIBLE
            binding.recyclerView.visibility = View.GONE
        } else {
            binding.emptyImage.visibility = View.GONE
            binding.recyclerView.visibility = View.VISIBLE
        }
    }

    private fun filterNotes(query: String?): List<NoteEntity> {
        return if (query.isNullOrEmpty()) {
            allNotes
        } else {
            val normalizedQuery = normalizeString(query)
            allNotes.filter {
                normalizeString(it.title).contains(
                    normalizedQuery,
                    ignoreCase = true
                )
            }
        }
    }

    private fun normalizeString(text: String): String {
        val temp = Normalizer.normalize(text, Normalizer.Form.NFD)
        return Pattern.compile("\\p{InCombiningDiacriticalMarks}+").matcher(temp).replaceAll("")
    }

    private fun clearSearchViewFocus() {
        with(binding.searchView) {
            clearFocus()
            setQuery("", false)
        }
    }

    private fun editNoteDetails(note: NoteEntity) {
        goTo(NoteCreation::class.java) {
            putExtra("note", note)
        }
    }

    private fun confirmToRemove(note: NoteEntity) {
        AlertDialog.Builder(this).apply {
            setTitle("Escolha uma ação")
            setMessage("Tem certeza que deseja excluir essa nota?")
            setPositiveButton("Sim") { _, _ -> removeNoteFromList(note) }
            setNegativeButton("Não") { dialog, _ -> dialog.dismiss() }
            create().show()
        }
    }

    private fun removeNoteFromList(note: NoteEntity) {
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                noteRepository.delete(note)
            }
            loadNotes()
            Toast.makeText(this@NoteList, "Nota removida com sucesso!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun confirmLogout() {
        AlertDialog.Builder(this).apply {
            setTitle("Confirmação")
            setMessage("Tem certeza de que deseja sair?")
            setPositiveButton("Sim") { _, _ -> performLogout() }
            setNegativeButton("Não") { dialog, _ -> dialog.dismiss() }
            create().show()
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
        noteAdapter.updateNotes(allNotes.sortedByDescending { it.timeStamp })
    }

    private fun sortNotesByOldest() {
        noteAdapter.updateNotes(allNotes.sortedBy { it.timeStamp })
    }

    private fun sortNotesAlphabetically() {
        noteAdapter.updateNotes(allNotes.sortedBy { it.title })
    }

    private fun sortNotesReverseAlphabetically() {
        noteAdapter.updateNotes(allNotes.sortedByDescending { it.title })
    }
}
