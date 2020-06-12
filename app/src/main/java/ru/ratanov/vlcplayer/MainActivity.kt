package ru.ratanov.vlcplayer

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import org.videolan.libvlc.LibVLC
import org.videolan.libvlc.Media
import org.videolan.libvlc.MediaPlayer
import ru.ratanov.vlcplayer.utils.TimeUtils
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    val PLAYLIST =
        "http://192.168.0.180:8090/torrent/view/37d8d3b552405f83cb19dd244adc96b6e847e1e7/La.Belle.Epoque.2019.BDRip.1.41Gb.MegaPeer.avi"

    private lateinit var libVLC: LibVLC
    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var media: Media
    private var isSeeking = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // "--no-drop-late-frames", "--no-skip-frames", "--rtsp-tcp"
        libVLC = LibVLC(this, arrayListOf("-vvv"))
        mediaPlayer = MediaPlayer(libVLC)
    }

    override fun onStart() {
        super.onStart()
        mediaPlayer.attachViews(video_layout, null, false, false)
        media = Media(libVLC, Uri.parse(PLAYLIST))
        media = Media(libVLC, assets.openFd("bbb.m4v"));

        mediaPlayer.media = media
        media.release()
        mediaPlayer.play()

        updateTimeLabels()
        listenPosition()
    }

    override fun onStop() {
        super.onStop()
        mediaPlayer.stop()
        mediaPlayer.detachViews()
    }

    override fun onDestroy() {
        super.onDestroy()
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
                controlsView.updateTimeLabels(current, length)
            }
    }

    @SuppressLint("CheckResult")
    private fun listenPosition() {

        controlsView.startTrackingSubject
            .subscribe { position ->
                isSeeking = true
                seekLabel.visibility = View.VISIBLE
            }

        controlsView.stopTrackingSubject
            .subscribe { position ->
                isSeeking = false
                seekLabel.visibility = View.INVISIBLE
                mediaPlayer.time = position
            }

        controlsView.progressChangedSubject
            .subscribe { position ->
                seekLabel.text = TimeUtils.formatTime(position)
            }

        controlsView.playingSubject
            .subscribe { isPlaying ->
                if (isPlaying) mediaPlayer.pause() else mediaPlayer.play()
            }
    }

}