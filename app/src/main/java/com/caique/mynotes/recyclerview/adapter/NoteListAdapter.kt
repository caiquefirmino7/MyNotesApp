package com.caique.mynotes.recyclerview.adapter

import android.content.Context
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
                noteDateIcon.setImageResource(R.drawable.ic_date)

                noteDataCreated.text = formatDate(currentItem.timeStamp)
                noteDataModified.text = formatLastModified(currentItem)

                cardViewNoteItem.setOnClickListener {
                    navigateToDetail(it.context, currentItem)
                }

                cardViewNoteItem.setOnLongClickListener {
                    showContextMenu(it.context, currentItem, clickToEdit, clickToRemove)
                    true
                }
            }
        }

        private fun formatDate(timestamp: Long): String {
            return dateFormat.format(timestamp)
        }

        private fun formatLastModified(note: NoteEntity): String {
            return if (note.lastModified > note.timeStamp) {
                "Última Modificação: ${dateFormat.format(note.lastModified)}"
            } else {
                ""
            }
        }

        private fun navigateToDetail(context: Context, note: NoteEntity) {
            val intent = Intent(context, DetailNotes::class.java).apply {
                putExtra("note", note)
            }
            context.startActivity(intent)
        }

        private fun showContextMenu(
            context: Context,
            note: NoteEntity,
            clickToEdit: (NoteEntity) -> Unit,
            clickToRemove: (NoteEntity) -> Unit
        ) {
            val menuItems = arrayOf("Editar", "Excluir")
            android.app.AlertDialog.Builder(context)
                .setItems(menuItems) { _, which ->
                    when (which) {
                        0 -> clickToEdit(note)
                        1 -> clickToRemove(note)
                    }
                }
                .show()
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
