package com.example.googletaskproject.ui.components

import android.content.Context
import android.util.Log
import android.view.View
import com.example.googletaskproject.core.SessionManager
import com.example.googletaskproject.data.model.UserModel
import com.example.googletaskproject.databinding.DialogYourDetailsBinding
import com.example.googletaskproject.presentation.TaskViewmodel
import com.example.googletaskproject.utils.Const
import com.example.googletaskproject.utils.Const.TAG
import com.example.googletaskproject.utils.extensions.context.createCustomDialog
import com.example.googletaskproject.utils.extensions.context.showToast

fun Context.showUserDetails(
    userModel: UserModel, viewmodel: TaskViewmodel, callback: (UserModel) -> Unit
) {
    createCustomDialog(
        DialogYourDetailsBinding::inflate, isCancelable = false
    ) { dialog, binding ->

        binding.switchChoice.setOnCheckedChangeListener { _, b ->
            binding.tvCheckBox.text = if (b) "Parent" else "Child"
            binding.etGroupId.visibility = if (b) View.GONE else View.VISIBLE
        }

        binding.btnPositive.setOnClickListener {
            if (binding.etUserID.text.toString().trim().isEmpty()) {
                showToast("Please enter your user ID")
                return@setOnClickListener
            }

            if (!binding.switchChoice.isChecked && binding.etGroupId.text.toString().trim()
                    .isEmpty()
            ) {
                showToast("Please enter your group ID")
                return@setOnClickListener
            }

            if (binding.etLocation.text.toString().trim().isEmpty()) {
                showToast("Please enter your location")
                return@setOnClickListener
            }

            userModel.userId = binding.etUserID.text.toString().trim()
            userModel.location = binding.etLocation.text.toString().trim()

            if (binding.switchChoice.isChecked) {  // User is parent
                userModel.groupId = binding.etUserID.text.toString().trim()
                userModel.role = Const.ROLE_PARENT


                SessionManager.putObject(Const.USER_INFO, userModel)
                callback(userModel)
                dialog.dismiss()

            } else {  // User is child
                userModel.role = Const.ROLE_CHILD
                userModel.groupId = binding.etGroupId.text.toString().trim()

                viewmodel.isTeamExists(binding.etGroupId.text.toString().trim()) { exists ->
                    Log.d(TAG, "showUserDetails: etGroupId = ${binding.etGroupId.text.toString().trim()}")
                    Log.d(TAG, "showUserDetails: exists = $exists")
                    if (!exists) {
                        showToast("Please enter a valid group ID")
                        return@isTeamExists // Stops execution here if group ID is invalid
                    }
                    SessionManager.putObject(Const.USER_INFO, userModel)
                    callback(userModel)
                    dialog.dismiss()
                }
            }
        }
    }.show()
}
