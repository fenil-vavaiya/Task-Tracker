package com.example.googletaskproject.ui.components

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.provider.Settings
import com.example.googletaskproject.databinding.DialogNoNetworkBinding
import com.example.googletaskproject.utils.extensions.context.createCustomDialog

fun Context.showNetworkDialog(): Dialog {
    return createCustomDialog(
        DialogNoNetworkBinding::inflate,
        isCancelable = false
    ) { dialog, binding ->

        binding.wifiBtn.setOnClickListener {
            val intent = Intent(Settings.ACTION_WIFI_SETTINGS)
            startActivity(intent)
            dialog.dismiss()
        }

        binding.internetBtn.setOnClickListener {
            val intent = Intent(Settings.ACTION_NETWORK_OPERATOR_SETTINGS)
            startActivity(intent)
            dialog.dismiss()
        }
    }
}
