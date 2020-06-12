package ru.ratanov.vlcplayer.view

import android.content.Context
import android.graphics.drawable.Animatable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.SeekBar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.view_controls.view.*
import ru.ratanov.vlcplayer.R
import ru.ratanov.vlcplayer.utils.TimeUtils

class ControlsView(context: Context, attrs: AttributeSet) : ConstraintLayout(context, attrs) {

    private var isPlaying = false

    val progressChangedSubject = PublishSubject.create<Long>()
    val stopTrackingSubject = PublishSubject.create<Long>()
    val startTrackingSubject = PublishSubject.create<Long>()
    val playingSubject = PublishSubject.create<Boolean>()

    init {
        LayoutInflater.from(context).inflate(R.layout.view_controls, this, true)
        initSeekBar()
        initPlayPauseButton()
    }

    fun updateTimeLabels(current: Long, length: Long) {
        currentTimeLabel.text = TimeUtils.formatTime(current)
        lengthTimeLabel.text = TimeUtils.formatTime(length)
        seekBar.max = length.toInt()
        seekBar.progress = current.toInt()
    }

    private fun initSeekBar() {
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (!fromUser) return
                progressChangedSubject.onNext(progress.toLong())
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
                startTrackingSubject.onNext(seekBar.progress.toLong())
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                stopTrackingSubject.onNext(seekBar.progress.toLong())
            }
        })
    }

    private fun initPlayPauseButton() {
        playPauseButton.setOnClickListener {
            if (isPlaying) {
                isPlaying = false
                playPauseButton.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.play_to_pause))
            } else {
                isPlaying = true
                playPauseButton.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.pause_to_play))
            }

            playingSubject.onNext(isPlaying)
            (playPauseButton.drawable as Animatable).start()
        }
    }

}