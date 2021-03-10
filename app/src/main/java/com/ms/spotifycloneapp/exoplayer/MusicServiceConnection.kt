package com.ms.spotifycloneapp.exoplayer

import android.content.ComponentName
import android.content.Context
import android.media.browse.MediaBrowser
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.ms.spotifycloneapp.other.Constants.NETWORK_ERROR
import com.ms.spotifycloneapp.other.Event
import com.ms.spotifycloneapp.other.Resource
import timber.log.Timber

/**
 * @AUTHOR: Mehedi Hasan
 * @DATE: 3/2/2021, Tue
 */
class MusicServiceConnection(
    context: Context
) {
    private val _isConnected = MutableLiveData<Event<Resource<Boolean>>>()
    val isConnected: LiveData<Event<Resource<Boolean>>> = _isConnected

    private val _networkError = MutableLiveData<Event<Resource<Boolean>>>()
    val networkError: LiveData<Event<Resource<Boolean>>> = _networkError

    private val _playbackState = MutableLiveData<PlaybackStateCompat?>()
    val playbackState: LiveData<PlaybackStateCompat?> = _playbackState

    private val _curPlayingSong = MutableLiveData<MediaMetadataCompat?>()
    val curPlayingSong: LiveData<MediaMetadataCompat?> = _curPlayingSong

    lateinit var mediaController: MediaControllerCompat

    private var mediaBrowserConnectionCallback = MediaBrowserConnectionCallback(context)

    private var mediaControllerCallback = MediaControllerCallback();

    val transportControls: MediaControllerCompat.TransportControls
        get() = mediaController.transportControls

    fun subscribe(parentId: String, callback: MediaBrowserCompat.SubscriptionCallback){
        mediaBrowser.subscribe(parentId, callback)
    }

    fun unsubscribe(parentId: String, callback: MediaBrowserCompat.SubscriptionCallback){
        mediaBrowser.unsubscribe(parentId, callback)
    }

    private val mediaBrowser = MediaBrowserCompat(
        context,
        ComponentName(
            context, MusicService::class.java
        ),
        mediaBrowserConnectionCallback,
        null
    ).apply {
        connect()
    }

    private inner class MediaBrowserConnectionCallback(
        private val context: Context
    ): MediaBrowserCompat.ConnectionCallback(){

        override fun onConnected() {
            Log.d("MusicServiceConnection", "CONNECTED")

            mediaController = MediaControllerCompat(context, mediaBrowser.sessionToken)
                .apply {
                    registerCallback(mediaControllerCallback)
                }

            /*mediaControllerCallback.onMetadataChanged(
                mediaController.metadata
            )
            mediaControllerCallback
                .onPlaybackStateChanged(mediaController.playbackState)*/

            _isConnected.postValue(Event(Resource.success(true)))
        }


        override fun onConnectionSuspended() {
            Log.d("MusicServiceConnection", "SUSPENDED")

            _isConnected.postValue(Event(Resource.error("Connection was suspended", false)))
        }

        override fun onConnectionFailed() {
            Log.d("MusicServiceConnection", "FAILED")

            _isConnected.postValue(Event(Resource.error("Couldn't connect to media browser", false)))
        }
    }

    private inner class MediaControllerCallback : MediaControllerCompat.Callback() {


        override fun onPlaybackStateChanged(state: PlaybackStateCompat?) {
            Timber.d("onMetadataChanged %s", "onPlaybackStateChanged")

            _playbackState.postValue(state)
        }

        override fun onMetadataChanged(metadata: MediaMetadataCompat?) {
            Timber.d("onMetadataChanged %s", "called")

            _curPlayingSong.postValue(metadata)
        }

        override fun onSessionEvent(event: String?, extras: Bundle?) {
            super.onSessionEvent(event, extras)
            Timber.d("onMetadataChanged %s", "onSessionEvent")

            when (event) {
                NETWORK_ERROR -> _networkError.postValue(
                    Event(
                        Resource.error(
                            "Couldn't connect to server, please check your internet connection",
                            null
                        )
                    )
                )
            }
        }

        override fun onSessionDestroyed() {
            Timber.d("onMetadataChanged %s", "onSessionDestroyed")

            mediaBrowserConnectionCallback.onConnectionSuspended()
        }
    }

}