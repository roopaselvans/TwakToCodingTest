package com.roopasn.tawkto.common.utils

import com.roopasn.tawkto.infrastructure.log.DefaultLogger
import com.roopasn.tawkto.infrastructure.log.Logger

object LocalLog {
    private var mLogger: Logger = DefaultLogger()

    fun enable(enable: Boolean) {
        mLogger.enable(enable)
    }

    fun i(tag: String, message: String) {
        mLogger.i(tag, message)
    }

    fun d(tag: String, message: String) {
        mLogger.d(tag, message)
    }

    fun w(tag: String, message: String) {
        mLogger.d(tag, message)
    }

    fun e(tag: String, message: String) {
        mLogger.e(tag, message)
    }
}