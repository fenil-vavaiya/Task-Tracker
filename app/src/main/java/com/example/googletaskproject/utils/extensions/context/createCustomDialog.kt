package com.example.googletaskproject.utils.extensions.context

import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.view.Window
import android.view.WindowManager
import androidx.viewbinding.ViewBinding
import com.example.googletaskproject.R

fun <T : ViewBinding> Context.createCustomDialog(
    bindingInflater: (LayoutInflater) -> T,
    isCancelable: Boolean = true,
    widthPercentage: Float = 0.5f, // Default width is 90% of screen width
    setup: (Dialog, T) -> Unit
): Dialog {
    val binding = bindingInflater(LayoutInflater.from(this))
    val dialog = Dialog(this, R.style.MaterialAlertDialog_rounded).apply {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setCancelable(isCancelable)
        setCanceledOnTouchOutside(isCancelable)
        setContentView(binding.root)

        window?.apply {
            setLayout(
                WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT
            )
            decorView.setBackgroundResource(android.R.color.transparent)

            // Set width based on screen size
            val displayMetrics = resources.displayMetrics
            val dialogWidth = (displayMetrics.widthPixels * widthPercentage).toInt()
            attributes = attributes?.apply {
                width = dialogWidth
            }
        }
    }


    setup(dialog, binding)

    return dialog
}
