package com.ms.spotifycloneapp.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.bumptech.glide.RequestManager
import com.ms.spotifycloneapp.R
import com.ms.spotifycloneapp.data.entities.Song
import com.ms.spotifycloneapp.exoplayer.toSong
import com.ms.spotifycloneapp.other.Status
import com.ms.spotifycloneapp.ui.viewmodels.MainViewModel
import com.ms.spotifycloneapp.ui.viewmodels.SongViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_song.*
import javax.inject.Inject

/**
 * @AUTHOR: Mehedi Hasan
 * @DATE: 3/10/2021, Wed
 */

@AndroidEntryPoint
class SongFragment : Fragment(R.layout.fragment_song) {

    @Inject
    lateinit var glide: RequestManager

    private val mainViewModel: MainViewModel by activityViewModels()
    private val songViewModel: SongViewModel by viewModels()

    private var curPlayingSong: Song? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        subscribeToObservers()
    }

    private fun updateView(song: Song) {
        val title = "${song.title} - ${song.subtitle}"
        tvSongName.text = title
        glide.load(song.imageUrl).into(ivSongImage)
    }

    private fun subscribeToObservers() {
        mainViewModel.mediaItems.observe(viewLifecycleOwner) {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        resource.data?.let { song ->
                            if (curPlayingSong == null && song.isNotEmpty()) {
                                curPlayingSong = song[0]
                                updateView(song[0])
                            }
                        }
                    }
                    else -> Unit
                }

            }
        }

        mainViewModel.curPlayingSong.observe(viewLifecycleOwner) {
            if (it == null) return@observe
            curPlayingSong = it.toSong()
            updateView(curPlayingSong!!)
        }
    }
}