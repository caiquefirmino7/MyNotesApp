package com.caique.mynotes.ui

import android.os.Bundle
import android.text.InputType
import android.widget.EditText
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.lifecycleScope
import com.caique.mynotes.databinding.ActivityUserProfileBinding
import com.caique.mynotes.extensions.toast
import com.caique.mynotes.repository.UserRepository
import com.caique.mynotes.repository.UserRepositorySingleton
import kotlinx.coroutines.launch

class UserProfile : UserBaseActivity() {

    private lateinit var binding: ActivityUserProfileBinding
    private lateinit var userRepository: UserRepository


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        userRepository = UserRepositorySingleton.getRepository(this)
        setupUI()
    }

    private fun setupUI() {
        configureStatusBar()
        lifecycleScope.launch {
            user.value?.let {
                binding.profileUserName.text = it.name
            }
        }

        binding.profileBtnChangeName.setOnClickListener {
            changeUserName()
        }

        binding.profileBtnChangePassword.setOnClickListener {
            changeUserPassword()
        }

        binding.profileBtnDeleteProfile.setOnClickListener {
            confirmDeleteProfile()
        }
    }

    private fun changeUserName() {
        val currentName = user.value?.name ?: ""

        AlertDialog.Builder(this).apply {
            setTitle("Alterar Nome")
            val userName = EditText(this@UserProfile).apply {
                setText(currentName)
                hint = "Novo Nome"
            }
            setView(userName)
            setPositiveButton("Alterar") { _, _ ->
                val newName = userName.text.toString().trim()
                if (newName.isNotEmpty()) {
                    lifecycleScope.launch {
                        user.value?.let {
                            userRepository.updateUserName(it.user, newName)
                            binding.profileUserName.text = newName
                            toast("Nome alterado com sucesso!")
                        }
                    }
                } else {
                    toast("Nome não pode ser vazio.")
                }
            }
            setNegativeButton("Cancelar") { dialog, _ ->
                dialog.dismiss()
            }
            create().show()
        }
    }

    private fun changeUserPassword() {
        val oldPasswordEditText = EditText(this).apply {
            hint = "Senha Antiga"
            inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        }

        val newPasswordEditText = EditText(this).apply {
            hint = "Nova Senha"
            inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        }

        val linearLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            addView(oldPasswordEditText)
            addView(newPasswordEditText)
        }

        AlertDialog.Builder(this).apply {
            setTitle("Alterar Senha")
            setView(linearLayout)
            setPositiveButton("Alterar") { _, _ ->
                val oldPassword = oldPasswordEditText.text.toString().trim()
                val newPassword = newPasswordEditText.text.toString().trim()
                lifecycleScope.launch {
                    user.value?.let { userEntity ->
                        val currentUser =
                            userRepository.authenticateUser(userEntity.user, oldPassword)
                        if (currentUser != null && newPassword.isNotEmpty() && newPassword != oldPassword) {
                            userRepository.updateUserPassword(userEntity.user, newPassword)
                            toast("Senha alterada com sucesso!")
                        } else {
                            toast("Senha antiga incorreta ou nova senha inválida.")
                        }
                    }
                }
            }
            setNegativeButton("Cancelar") { dialog, _ ->
                dialog.dismiss()
            }
            create().show()
        }
    }

    private fun confirmDeleteProfile() {
        AlertDialog.Builder(this).apply {
            setTitle("Confirmação")
            setMessage("Tem certeza de que deseja excluir seu perfil?")
            setPositiveButton("Sim") { _, _ ->
                deleteUserProfile()
            }
            setNegativeButton("Não") { dialog, _ ->
                dialog.dismiss()
            }
            create().show()
        }
    }

    private fun deleteUserProfile() {
        lifecycleScope.launch {
            user.value?.let {
                userRepository.deleteUser(it)
                toast("Perfil excluído.")
                goToLogin()
            }
        }
    }
}

