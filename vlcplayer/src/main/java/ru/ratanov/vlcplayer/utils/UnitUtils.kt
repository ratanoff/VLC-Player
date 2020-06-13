package ru.ratanov.vlcplayer.utils

import android.content.res.Resources

object UnitUtils {

    fun pxToDp(px: Float): Float {
        return px / Resources.getSystem().displayMetrics.density
    }

}