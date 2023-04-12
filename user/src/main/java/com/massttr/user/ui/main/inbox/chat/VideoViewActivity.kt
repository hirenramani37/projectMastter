package com.massttr.user.ui.main.inbox.chat

import android.graphics.Color
import android.os.Bundle
import android.view.View
import com.common.base.BaseActivity
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.DefaultMediaSourceFactory
import com.google.android.exoplayer2.source.MediaSourceFactory
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import com.massttr.user.R
import com.massttr.user.databinding.ActivityVideoBinding
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi


@ObsoleteCoroutinesApi
@DelicateCoroutinesApi
class VideoViewActivity : BaseActivity<ActivityVideoBinding>(R.layout.activity_video),
    View.OnClickListener  {

    private lateinit var simpleExoPlayer: SimpleExoPlayer
    private lateinit var mediaDataSourceFactory: DataSource.Factory
    var url = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if(intent.hasExtra("url")){
            url = intent.getStringExtra("url")?:""
           // binding.andExoPlayerView.setSource(url);
        }
    }

    private fun initializePlayer() {

        mediaDataSourceFactory = DefaultDataSourceFactory(this, Util.getUserAgent(this, "mediaPlayerSample"))

        val mediaSource = ProgressiveMediaSource.Factory(mediaDataSourceFactory).createMediaSource(
            MediaItem.fromUri(url))

        val mediaSourceFactory: MediaSourceFactory = DefaultMediaSourceFactory(mediaDataSourceFactory)

        simpleExoPlayer = SimpleExoPlayer.Builder(this)
            .setMediaSourceFactory(mediaSourceFactory)
            .build()

        simpleExoPlayer.addMediaSource(mediaSource)

        simpleExoPlayer.playWhenReady = true

        binding.playerView.setShutterBackgroundColor(Color.TRANSPARENT)
        binding.playerView.player = simpleExoPlayer
        //binding.playerView.
        binding.playerView.requestFocus()
    }

    private fun releasePlayer() {
        simpleExoPlayer.release()
    }

    public override fun onStart() {
        super.onStart()

        if (Util.SDK_INT > 23) initializePlayer()
    }

    public override fun onResume() {
        super.onResume()

        if (Util.SDK_INT <= 23) initializePlayer()
    }

    public override fun onPause() {
        super.onPause()

        if (Util.SDK_INT <= 23) releasePlayer()
    }

    public override fun onStop() {
        super.onStop()

        if (Util.SDK_INT > 23) releasePlayer()
    }

    override fun onClick(v: View?) {

    }
}