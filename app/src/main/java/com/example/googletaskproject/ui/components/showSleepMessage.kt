package com.example.googletaskproject.ui.components

import android.content.Context
import com.example.googletaskproject.core.SessionManager
import com.example.googletaskproject.databinding.DialogSleepMessageBinding
import com.example.googletaskproject.utils.Const
import com.example.googletaskproject.utils.extensions.context.createCustomDialog
import com.example.googletaskproject.utils.extensions.context.showToast

fun Context.showSleepMessage() {
    createCustomDialog(
        DialogSleepMessageBinding::inflate,
        isCancelable = false
    ) { dialog, binding ->
        binding.etUserID.setText(SessionManager.getString(Const.SLEEP_MODE_MESSAGE))
        binding.btnNegative.setOnClickListener {
            dialog.dismiss()
        }

        binding.btnPositive.setOnClickListener {
            if (binding.etUserID.text.toString().trim().isEmpty()) {
                showToast("Please enter message.")
                return@setOnClickListener
            }
            SessionManager.putString(
                Const.SLEEP_MODE_MESSAGE,
                binding.etUserID.text.toString().trim()
            )
            showToast("Sleep mode message updated.")
            dialog.dismiss()
        }
    }.show()


}