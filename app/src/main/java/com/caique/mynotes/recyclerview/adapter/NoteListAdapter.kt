package com.caique.mynotes.recyclerview.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.caique.mynotes.R
import com.caique.mynotes.databinding.NoteItemBinding
import com.caique.mynotes.model.NoteEntity
import com.caique.mynotes.ui.DetailNotes
import java.text.SimpleDateFormat
import java.util.Locale

class NoteListAdapter(
    private val noteList: MutableList<NoteEntity>,
    private val clickToEdit: (NoteEntity) -> Unit,
    private val clickToRemove: (NoteEntity) -> Unit
) : RecyclerView.Adapter<NoteListAdapter.NoteViewHolder>() {

    private val dateFormat = SimpleDateFormat("dd/MM/yyyy 'às' HH:mm", Locale.getDefault())

    fun updateNotes(newNotes: List<NoteEntity>) {
        val diffResult = DiffUtil.calculateDiff(NoteDiffCallback(noteList, newNotes))
        noteList.clear()
        noteList.addAll(newNotes)
        diffResult.dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val binding = NoteItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NoteViewHolder(binding)
    }

    override fun getItemCount(): Int = noteList.size

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        val currentItem = noteList[position]
        holder.bind(currentItem, clickToEdit, clickToRemove)
    }

    inner class NoteViewHolder(private val binding: NoteItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(
            currentItem: NoteEntity,
            clickToEdit: (NoteEntity) -> Unit,
            clickToRemove: (NoteEntity) -> Unit
        ) {
            with(binding) {
                noteTitle.text = currentItem.title
                noteDesc.text = currentItem.description

                val creationDateString = dateFormat.format(currentItem.timeStamp)
                noteDateIcon.setImageResource(R.drawable.ic_date)
                noteDataCreated.text = creationDateString

                val modificationDateString = if (currentItem.lastModified > currentItem.timeStamp) {
                    "Última Modificação: ${dateFormat.format(currentItem.lastModified)}"
                } else {
                    ""
                }
                noteDataModified.text = modificationDateString

                cardViewNoteItem.setOnClickListener {
                    val context = it.context
                    val intent = Intent(context, DetailNotes::class.java).apply {
                        putExtra("note", currentItem)
                    }
                    context.startActivity(intent)
                }

                cardViewNoteItem.setOnLongClickListener {
                    val menuItems = arrayOf("Editar", "Excluir")
                    android.app.AlertDialog.Builder(it.context)
                        .setItems(menuItems) { _, which ->
                            when (which) {
                                0 -> clickToEdit(currentItem)
                                1 -> clickToRemove(currentItem)
                            }
                        }
                        .show()
                    true
                }
            }
        }
    }

    private class NoteDiffCallback(
        private val oldList: List<NoteEntity>,
        private val newList: List<NoteEntity>
    ) : DiffUtil.Callback() {

        override fun getOldListSize(): Int = oldList.size

        override fun getNewListSize(): Int = newList.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition].id == newList[newItemPosition].id
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition] == newList[newItemPosition]
        }
    }
}
