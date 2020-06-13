package ru.ratanov.vlcplayer

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity: AppCompatActivity() {

    val PLAYLIST =
        "http://192.168.0.180:8090/torrent/view/37d8d3b552405f83cb19dd244adc96b6e847e1e7/La.Belle.Epoque.2019.BDRip.1.41Gb.MegaPeer.avi"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        vlcPlayerView.play(PLAYLIST)
    }

}