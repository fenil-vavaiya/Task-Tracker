package com.example.googletaskproject.ui.components

import android.content.Context
import android.view.View
import com.example.googletaskproject.data.model.UserModel
import com.example.googletaskproject.databinding.DialogYourDetailsBinding
import com.example.googletaskproject.utils.extensions.context.createCustomDialog
import com.example.googletaskproject.utils.extensions.context.showToast

fun Context.showUserDetails(userModel: UserModel, callback: (UserModel) -> Unit) {
    createCustomDialog(
        DialogYourDetailsBinding::inflate,
        isCancelable = false
    ) { dialog, binding ->

        binding.switchChoice.setOnCheckedChangeListener { _, b ->
            binding.tvCheckBox.text = if (b) "Parent" else "Child"
            binding.etParentId.visibility = if (b) View.GONE else View.VISIBLE
        }

        binding.btnPositive.setOnClickListener {
            if (binding.etUserID.text.toString().trim().isEmpty()) {
                showToast("Please enter your user ID")
                return@setOnClickListener
            }

            if (!binding.switchChoice.isChecked && binding.etParentId.text.toString().trim()
                    .isEmpty()
            ) {
                showToast("Please enter your parent ID")
                return@setOnClickListener
            }

            if (binding.etLocation.text.toString().trim().isEmpty()) {
                showToast("Please enter your location")
                return@setOnClickListener
            }

            userModel.userId = binding.etUserID.text.toString().trim()
            userModel.location = binding.etLocation.text.toString().trim()
            if (!binding.switchChoice.isChecked) {
                userModel.parentId = binding.etParentId.text.toString().trim()
            }
            callback(userModel)
            dialog.dismiss()
        }
    }.show()


}