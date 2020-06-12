package ru.ratanov.vlcplayer.utils

object TimeUtils {

    fun formatTime(milliseconds: Long): String {
        val seconds = milliseconds / 1000
        val s = seconds % 60
        val m = (seconds / 60) % 60
        val h = (seconds / (60 * 60)) % 24
        return String.format("%d:%02d:%02d", h, m, s)
    }

}