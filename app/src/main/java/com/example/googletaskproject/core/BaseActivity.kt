package com.example.googletaskproject.core

import android.Manifest
import android.app.Dialog
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.viewbinding.ViewBinding
import com.example.googletaskproject.R
import com.example.googletaskproject.ui.components.showNetworkDialog
import com.example.googletaskproject.utils.Const.TAG
import com.example.googletaskproject.utils.helper.NetworkHelper
import com.example.googletaskproject.utils.helper.NetworkMonitor
import com.permissionx.guolindev.PermissionX
import com.permissionx.guolindev.callback.RequestCallback
import com.permissionx.guolindev.request.ExplainScope
import com.permissionx.guolindev.request.ForwardScope

abstract class BaseActivity<VB : ViewBinding> : AppCompatActivity() {

    private var _binding: VB? = null
    protected val binding get() = _binding!!
    private lateinit var networkMonitor: NetworkMonitor
    private lateinit var noNetworkDialog: Dialog
    abstract fun inflateBinding(layoutInflater: LayoutInflater): VB

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.enableEdgeToEdge()
        _binding = inflateBinding(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        networkMonitor = NetworkMonitor(this)
        noNetworkDialog = showNetworkDialog()

        networkMonitor.isNetworkAvailable.observe(this) { isAvailable ->
            Log.d(TAG, "onCreate: isAvailable = $isAvailable")
            if (!isAvailable) {
                noNetworkDialog.show()
            } else {
                noNetworkDialog.dismiss()
            }
        }

        initViews(binding.root)
        initListeners(binding.root)
    }

    override fun onDestroy() {
        super.onDestroy()
        noNetworkDialog.let {
            noNetworkDialog.dismiss()
        }
        _binding = null
    }

    override fun onResume() {
        super.onResume()
        if (!NetworkHelper.isNetworkAvailable(this)) {
            noNetworkDialog.let { noNetworkDialog.show() }
        }
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

        PermissionX.init(this).permissions(permissions)
            .onExplainRequestReason { scope: ExplainScope, deniedList: List<String> ->
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
