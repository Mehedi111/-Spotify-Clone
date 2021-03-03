package com.ms.spotifycloneapp.exoplayer.callback

import android.widget.Toast
import com.google.android.exoplayer2.ExoPlaybackException
import com.google.android.exoplayer2.Player
import com.ms.spotifycloneapp.exoplayer.MusicService

/**
 * @AUTHOR: Mehedi Hasan
 * @DATE: 3/1/2021, Mon
 */
class MusicPlayerEventListener(
    private val musicService: MusicService
): Player.EventListener {

    override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
        super.onPlayerStateChanged(playWhenReady, playbackState)
        if (playbackState == Player.STATE_READY && !playWhenReady){
            musicService.stopForeground(false)
        }
    }

    override fun onPlayerError(error: ExoPlaybackException) {
        super.onPlayerError(error)
        Toast.makeText(musicService, "An unknown error occurred! ", Toast.LENGTH_SHORT).show()
    }
}