package com.example.googletaskproject.core

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.viewbinding.ViewBinding
import com.example.googletaskproject.R
import com.permissionx.guolindev.PermissionX
import com.permissionx.guolindev.callback.RequestCallback
import com.permissionx.guolindev.request.ExplainScope
import com.permissionx.guolindev.request.ForwardScope

abstract class BaseActivity<VB : ViewBinding> : AppCompatActivity() {

    private var _binding: VB? = null
    protected val binding get() = _binding!!

    abstract fun inflateBinding(layoutInflater: LayoutInflater): VB

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.enableEdgeToEdge()
        _binding = inflateBinding(layoutInflater)
        setContentView(binding.root)
        initViews(binding.root)
        initListeners(binding.root)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    abstract fun initViews(view: View)
    abstract fun initListeners(view: View)

    fun requestPermissionIfNeeded(permissions: List<String>, requestCallback: RequestCallback?) {
        var allGranted = true
        for (permission in permissions) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    permission
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                allGranted = false
            }
        }
        if (allGranted) {
            requestCallback!!.onResult(true, permissions, ArrayList())
            return
        }

        PermissionX.init(this).permissions(permissions).onExplainRequestReason { scope: ExplainScope, deniedList: List<String> ->
            var message = ""
            if (permissions.size == 1) {
                if (deniedList.contains(Manifest.permission.READ_CALENDAR)) {
                    message = this.getString(R.string.permission_explain_calendar)
                }
            } else {
                if (deniedList.size == 1) {
                    if (deniedList.contains(Manifest.permission.READ_CALENDAR)) {
                        message = this.getString(R.string.permission_explain_calendar)
                    }
                } else {
                    message = this.getString(R.string.permission_explain_calendar)
                }
            }
            scope.showRequestReasonDialog(deniedList, message, getString(R.string.ok))
        }
            .onForwardToSettings { scope: ForwardScope, deniedList: List<String> ->
                var message = ""
                if (permissions.size == 1) {
                    if (deniedList.contains(Manifest.permission.READ_CALENDAR)) {
                        message = this.getString(R.string.permission_explain_calendar)
                    }
                } else {
                    if (deniedList.size == 1) {
                        if (deniedList.contains(Manifest.permission.READ_CALENDAR)) {
                            message = this.getString(R.string.permission_explain_calendar)
                        }
                    } else {
                        message = this.getString(R.string.permission_explain_calendar)
                    }
                }
                scope.showForwardToSettingsDialog(
                    deniedList,
                    message, getString(R.string.settings), getString(R.string.cancel)
                )
            }.request { allGranted, grantedList, deniedList ->
                requestCallback?.onResult(
                    allGranted,
                    grantedList,
                    deniedList
                )
            }
    }
}
