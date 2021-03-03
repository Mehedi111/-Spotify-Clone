package com.ms.spotifycloneapp.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.ms.spotifycloneapp.R
import com.ms.spotifycloneapp.adapters.SongAdapter
import com.ms.spotifycloneapp.other.Status
import com.ms.spotifycloneapp.ui.viewmodels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_home.*
import javax.inject.Inject

/**
 * @AUTHOR: Mehedi Hasan
 * @DATE: 3/3/2021, Wed
 */
@AndroidEntryPoint
class HomeFragment : Fragment(R.layout.fragment_home) {
    lateinit var mainViewModel: MainViewModel

    ///private val mainViewModel: MainViewModel by activityViewModels()

    @Inject
    lateinit var songAdapter: SongAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainViewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)
        setUpRV()
        subscribeToObserver()

        songAdapter.setOnItemClickListener {
            mainViewModel.playOrToggleSong(it)
        }
    }

    private fun setUpRV() = rvAllSongs.apply {
        adapter = songAdapter
        layoutManager = LinearLayoutManager(requireContext())
    }

    private fun subscribeToObserver() {
        mainViewModel.mediaItem.observe(viewLifecycleOwner) { result ->
            when(result.status){

                Status.SUCCESS -> {
                    allSongsProgressBar.isVisible = false
                    result.data?.let {
                        songAdapter.songs = it
                    }
                }
                Status.ERROR -> Unit
                Status.LOADING -> allSongsProgressBar.isVisible = true
            }
        }
    }
}