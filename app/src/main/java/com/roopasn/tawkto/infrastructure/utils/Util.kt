package com.roopasn.tawkto.infrastructure.utils

import android.os.Build

object Util {
    fun getSdkInt(): Int {
        return Build.VERSION.SDK_INT
    }
}