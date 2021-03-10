package com.ms.spotifycloneapp.ui

import android.os.Bundle
import android.support.v4.media.session.PlaybackStateCompat
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.RequestManager
import com.google.android.material.snackbar.Snackbar
import com.ms.spotifycloneapp.R
import com.ms.spotifycloneapp.adapters.SwipeSongAdapter
import com.ms.spotifycloneapp.data.entities.Song
import com.ms.spotifycloneapp.exoplayer.isPlaying
import com.ms.spotifycloneapp.exoplayer.toSong
import com.ms.spotifycloneapp.other.Status.*
import com.ms.spotifycloneapp.ui.viewmodels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.*
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var glide: RequestManager

    private val mainViewModel: MainViewModel by viewModels()

    @Inject
    lateinit var swipeSongAdapter: SwipeSongAdapter


    private var playbackState: PlaybackStateCompat? = null

    private var curPlayingSong: Song? = null;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        subscribeToObserver()

        vpSong.adapter = swipeSongAdapter

        vpSong.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                if (playbackState?.isPlaying == true) {
                    mainViewModel.playOrToggleSong(swipeSongAdapter.songs[position])
                } else {
                    curPlayingSong = swipeSongAdapter.songs[position]
                }
            }
        })

        ivPlayPause.setOnClickListener {
            curPlayingSong?.let {
                mainViewModel.playOrToggleSong(it, true)
            }
        }

        swipeSongAdapter.setItemClickListener {
            navHostFragment.findNavController().navigate(
                R.id.globalActionToSongFragment
            )
        }

        navHostFragment.findNavController().addOnDestinationChangedListener { _, destination, _ ->
            when(destination.id){
                R.id.songFragment -> hideBottomBar()
                R.id.homeFragment -> showBottomBar()
                else -> showBottomBar()
            }
        }
    }

    private fun hideBottomBar(){
        ivCurSongImage.isVisible = false
        vpSong.isVisible = false
        ivPlayPause.isVisible = false
    }

    private fun showBottomBar(){
        ivCurSongImage.isVisible = true
        vpSong.isVisible = true
        ivPlayPause.isVisible = true
    }

    private fun swipeViewPagerToCurrentSong(song: Song) {
        val newItemSong = swipeSongAdapter.songs.indexOf(song)

        if (newItemSong != -1) {
            vpSong.currentItem = newItemSong
            curPlayingSong = song
        }
    }

    private fun subscribeToObserver() {
        mainViewModel.mediaItems.observe(this) {
            it?.let { result ->
                when (result.status) {
                    SUCCESS -> {
                        result.data?.let { songs ->
                            swipeSongAdapter.songs = songs
                            if (songs.isNotEmpty()) {
                                glide.load((curPlayingSong ?: songs[0]).imageUrl)
                                    .into(ivCurSongImage)
                            }
                            swipeViewPagerToCurrentSong(curPlayingSong ?: return@observe)
                        }
                    }
                    ERROR -> Unit
                    LOADING -> Unit
                }
            }
        }

        mainViewModel.curPlayingSong.observe(this, {
            Timber.d("checkCurPlay %s", "called")

            if (it != null) return@observe
            curPlayingSong = it?.toSong()
            glide.load(curPlayingSong?.imageUrl).into(ivCurSongImage)
            swipeViewPagerToCurrentSong(curPlayingSong ?: return@observe)
        })

        mainViewModel.playbackState.observe(this) {
            playbackState = it
            ivPlayPause.setImageResource(
                if (playbackState?.isPlaying == true) R.drawable.ic_pause
                else R.drawable.ic_play
            )
        }

        mainViewModel.isConnected.observe(this) {
            it?.getContentIfNotHandled()?.let {
                when (it.status) {
                    SUCCESS -> Unit
                    ERROR -> {
                        Snackbar.make(
                            rootLayout, it.message ?: "Error occured",
                            Snackbar.LENGTH_LONG
                        ).show()
                    }
                    LOADING -> Unit
                }
            }
        }

        mainViewModel.networkError.observe(this) {
            it?.getContentIfNotHandled()?.let {
                when (it.status) {
                    SUCCESS -> Unit
                    ERROR -> {
                        Snackbar.make(
                            rootLayout, it.message ?: "Error occured",
                            Snackbar.LENGTH_LONG
                        ).show()
                    }
                    LOADING -> Unit
                }
            }
        }
    }
}