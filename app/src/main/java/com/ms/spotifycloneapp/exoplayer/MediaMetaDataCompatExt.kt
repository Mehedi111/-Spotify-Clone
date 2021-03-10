package com.ms.spotifycloneapp.exoplayer

import android.support.v4.media.MediaMetadataCompat
import com.ms.spotifycloneapp.data.entities.Song

/**
 * @AUTHOR: Mehedi Hasan
 * @DATE: 3/9/2021, Tue
 */
fun MediaMetadataCompat.toSong(): Song? {
    return description?.let {
        Song(
            it.mediaId ?: "",
            it.title.toString(),
            it.subtitle.toString(),
            it.mediaUri.toString(),
            it.iconUri.toString()
        )
    }
}