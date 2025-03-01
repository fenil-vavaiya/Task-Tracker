package com.example.googletaskproject.utils.helper

import android.app.Activity
import android.media.MediaPlayer
import java.io.IOException

class MediaPlayerUtils(private val context: Activity) {
    private var mediaPlayer: MediaPlayer? = null

    // Change music and play media
    fun playTaskMusic(rawResId: Int) {
        try {
            // Release the previous MediaPlayer if it exists
            destroyMediaPlayer()

            val assetFileDescriptor = context.resources.openRawResourceFd(rawResId) ?: return
            mediaPlayer = MediaPlayer().apply {
                setDataSource(
                    assetFileDescriptor.fileDescriptor,
                    assetFileDescriptor.startOffset,
                    assetFileDescriptor.length
                )
                prepare()
                isLooping = false
                setOnPreparedListener { start() }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    // Pause MediaPlayer
    fun pauseMediaPlayer() {
        mediaPlayer?.let {
            if (it.isPlaying) {
                it.pause()
            }
        }
    }

    // Resume MediaPlayer
    fun resumeMediaPlayer() {
        mediaPlayer?.let {
            if (!it.isPlaying) {
                it.start()
            }
        }
    }

    // Destroy MediaPlayer
    fun destroyMediaPlayer() {
        mediaPlayer?.let {
            if (it.isPlaying) {
                it.stop()
            }
            it.release()
            mediaPlayer = null
        }
    }
}
