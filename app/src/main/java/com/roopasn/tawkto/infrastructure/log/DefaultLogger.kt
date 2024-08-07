package com.roopasn.tawkto.infrastructure.log

import android.util.Log

class DefaultLogger : Logger {
    private var mIsEnabled = true
    override fun enable(enable: Boolean) {
        mIsEnabled = enable
    }

    override fun i(tag: String, message: String) {
        if (mIsEnabled) {
            Log.i(tag, message)
        }
    }

    override fun d(tag: String, message: String) {
        if (mIsEnabled) {
            Log.d(tag, message)
        }
    }

    override fun w(tag: String, message: String) {
        if (mIsEnabled) {
            Log.w(tag, message)
        }
    }

    override fun e(tag: String, message: String) {
        if (mIsEnabled) {
            Log.e(tag, message)
        }
    }
}