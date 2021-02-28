package com.ms.spotifycloneapp.data.remote

import com.google.firebase.firestore.FirebaseFirestore
import com.ms.spotifycloneapp.data.entities.Song
import com.ms.spotifycloneapp.other.Constants
import kotlinx.coroutines.tasks.await

/**
 * @AUTHOR: Mehedi Hasan
 * @DATE: 2/28/2021, Sun
 */
class MusicDatabase {
    private val firestore = FirebaseFirestore.getInstance()
    private val songCollection = firestore.collection(Constants.SONG_COLLECTION)

    suspend fun getAllSongs(): List<Song> {
        return try {
            songCollection.get().await().toObjects(Song::class.java)
        } catch (e: Exception) {
            emptyList()
        }
    }
}