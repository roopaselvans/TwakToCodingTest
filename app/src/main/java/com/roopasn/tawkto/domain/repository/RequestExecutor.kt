package com.roopasn.tawkto.domain.repository

/**
 * Request Executor interface
 */
interface RequestExecutor {
    /**
     * Execute the submitted request block asynchronously using coroutines
     *
     * @param request Requested block to get executed
     */
    fun queueRequest(request: suspend () -> Unit)
}