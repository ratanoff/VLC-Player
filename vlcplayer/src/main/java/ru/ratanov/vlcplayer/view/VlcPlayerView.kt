package ru.ratanov.vlcplayer.view

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.os.Handler
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.view_vlc_player.view.*
import org.videolan.libvlc.LibVLC
import org.videolan.libvlc.Media
import org.videolan.libvlc.MediaPlayer
import ru.ratanov.vlcplayer.R
import ru.ratanov.vlcplayer.utils.TimeUtils
import java.util.concurrent.TimeUnit

class VlcPlayerView(context: Context, attrs: AttributeSet) : ConstraintLayout(context, attrs) {

    private lateinit var libVLC: LibVLC
    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var media: Media
    private var isSeeking = false

    init {
        LayoutInflater.from(context).inflate(R.layout.view_vlc_player, this, true)
    }

    fun play(url: String) {
        libVLC = LibVLC(context, arrayListOf("-vvv"))
        mediaPlayer = MediaPlayer(libVLC)
        mediaPlayer.attachViews(video_layout, null, false, false)
        media = Media(libVLC, Uri.parse(url))
        mediaPlayer.media = media
        media.release()
        mediaPlayer.play()

        updateTimeLabels()
        listenPosition()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        mediaPlayer.stop()
        mediaPlayer.detachViews()
        mediaPlayer.release()
        libVLC.release()
    }

    @SuppressLint("CheckResult")
    private fun updateTimeLabels() {
        Observable.interval(1, TimeUnit.SECONDS)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .filter { !isSeeking }
            .subscribe {
                val current = mediaPlayer.time
                val length = media.duration
                controlPanel.updateTimeLabels(current, length)
            }
    }

    @SuppressLint("CheckResult")
    private fun listenPosition() {

        controlPanel.startTrackingSubject
            .subscribe { position ->
                isSeeking = true
                seekLabel.visibility = View.VISIBLE
            }

        controlPanel.stopTrackingSubject
            .subscribe { position ->
                isSeeking = false
                seekLabel.visibility = View.INVISIBLE
                mediaPlayer.time = position
                hideControlPanel()
            }

        controlPanel.progressChangedSubject
            .subscribe { position ->
                seekLabel.text = TimeUtils.formatTime(position)
            }

        controlPanel.playingSubject
            .subscribe { isPlaying ->
                if (isPlaying) mediaPlayer.pause() else mediaPlayer.play()
            }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            val controlPanelHeight = resources.getDimension(R.dimen.control_panel_height)
            controlPanel.animate().y((height - controlPanelHeight))
            Handler().postDelayed({
                if (!isSeeking) hideControlPanel()
            }, 4000)
        }
        return super.onTouchEvent(event)
    }

    private fun hideControlPanel() {
        controlPanel.animate().y(height.toFloat())
    }

}