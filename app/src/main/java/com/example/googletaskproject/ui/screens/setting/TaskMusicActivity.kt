package com.example.googletaskproject.ui.screens.setting

import android.media.MediaPlayer
import android.view.LayoutInflater
import android.view.View
import com.example.googletaskproject.R
import com.example.googletaskproject.core.BaseActivity
import com.example.googletaskproject.databinding.ActivityTaskMusicBinding
import com.example.googletaskproject.data.model.TaskRingtoneModel
import com.example.googletaskproject.ui.screens.setting.adapter.TaskMusicAdapter

class TaskMusicActivity : BaseActivity<ActivityTaskMusicBinding>() {

    private val adapter = TaskMusicAdapter()

    override fun inflateBinding(layoutInflater: LayoutInflater) =
        ActivityTaskMusicBinding.inflate(layoutInflater)


    override fun initViews(view: View) {
        val ringtoneList = listOf(
            TaskRingtoneModel("None", -1),
            TaskRingtoneModel("Ringtone 1", R.raw.ringtone_1),
            TaskRingtoneModel("Ringtone 2", R.raw.ringtone_2),
            TaskRingtoneModel("Ringtone 3", R.raw.ringtone_3),
            TaskRingtoneModel("Ringtone 4", R.raw.ringtone_4),
            TaskRingtoneModel("Ringtone 5", R.raw.ringtone_5),
        )
        binding.rv.adapter = adapter
        adapter.setData(ringtoneList)
    }

    override fun initListeners(view: View) {
        binding.btnBack.setOnClickListener { finish() }
        adapter.setCallback {
            playRingtoneFromRaw(it.assetName)
        }
    }


    private fun playRingtoneFromRaw(rawResId: Int) {
        try {
            val assetFileDescriptor = resources.openRawResourceFd(rawResId) ?: return
            val mediaPlayer = MediaPlayer().apply {
                setDataSource(
                    assetFileDescriptor.fileDescriptor,
                    assetFileDescriptor.startOffset,
                    assetFileDescriptor.length
                )
                prepare()
                start()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}