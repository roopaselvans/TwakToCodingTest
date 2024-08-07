package com.roopasn.tawkto.infrastructure.log

interface Logger {
    fun enable(enable: Boolean = true)
    fun i(tag: String, message: String)
    fun d(tag: String, message: String)
    fun w(tag: String, message: String)
    fun e(tag: String, message: String)
}