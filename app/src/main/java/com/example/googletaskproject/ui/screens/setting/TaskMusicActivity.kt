package com.example.googletaskproject.ui.screens.setting

import android.view.LayoutInflater
import android.view.View
import com.example.googletaskproject.R
import com.example.googletaskproject.core.BaseActivity
import com.example.googletaskproject.data.model.TaskRingtoneModel
import com.example.googletaskproject.databinding.ActivityTaskMusicBinding
import com.example.googletaskproject.ui.screens.setting.adapter.TaskMusicAdapter
import com.example.googletaskproject.utils.helper.MediaPlayerUtils

class TaskMusicActivity : BaseActivity<ActivityTaskMusicBinding>() {

    private val adapter = TaskMusicAdapter()
    private lateinit var mediaPlayer: MediaPlayerUtils
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
        mediaPlayer = MediaPlayerUtils(this)
    }

    override fun initListeners(view: View) {
        binding.btnBack.setOnClickListener { finish() }
        adapter.setCallback {
            handlerPlayer(it)
        }
    }


    private fun handlerPlayer(
        item: TaskRingtoneModel
    ) {
        mediaPlayer.pauseMediaPlayer()
        mediaPlayer.playTaskMusic(item.assetName)
    }

    override fun onDestroy() {
        mediaPlayer.destroyMediaPlayer()
        super.onDestroy()
    }
}