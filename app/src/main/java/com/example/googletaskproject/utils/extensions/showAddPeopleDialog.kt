package com.example.googletaskproject.utils.extensions

import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import com.example.googletaskproject.databinding.DialogLogoutBinding

fun Context.showLogoutDialog(
    onDismissListener: () -> Unit
) {
    val dialog = Dialog(this)
    val binding = DialogLogoutBinding.inflate(LayoutInflater.from(this))

    dialog.setContentView(binding.root)

    binding.btnYes.setOnClickListener {
        onDismissListener()
        dialog.dismiss()
    }

    binding.btnCancel.setOnClickListener { dialog.dismiss() }

    dialog.show()
}
