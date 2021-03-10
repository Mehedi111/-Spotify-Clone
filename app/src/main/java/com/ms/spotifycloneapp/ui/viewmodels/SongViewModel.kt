package com.ms.spotifycloneapp.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ms.spotifycloneapp.exoplayer.MusicService
import com.ms.spotifycloneapp.exoplayer.MusicServiceConnection
import com.ms.spotifycloneapp.exoplayer.currentPlayBackPosition
import com.ms.spotifycloneapp.other.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * @AUTHOR: Mehedi Hasan
 * @DATE: 3/10/2021, Wed
 */
@HiltViewModel
class SongViewModel @Inject constructor(
    musicServiceConnection: MusicServiceConnection
) : ViewModel() {

    private val playbackState = musicServiceConnection.playbackState

    private var _curSongDuration = MutableLiveData<Long>()
    val curSongDuration: LiveData<Long> = _curSongDuration

    private var _curPlayingPosition = MutableLiveData<Long>()
    var curPlayingPosition: LiveData<Long> = _curPlayingPosition


    init {
        updateCurrentPlayerPosition()
    }

    private fun updateCurrentPlayerPosition() {
        viewModelScope.launch {
            while (true) {
                val pos = playbackState.value?.currentPlayBackPosition
                if (curPlayingPosition.value != pos) {
                    _curPlayingPosition.postValue(pos!!)
                    _curSongDuration.postValue(MusicService.curSongDuration)
                }
                delay(Constants.UPDATE_PLAYER_POSITION_INTERVAL)
            }
        }
    }
}