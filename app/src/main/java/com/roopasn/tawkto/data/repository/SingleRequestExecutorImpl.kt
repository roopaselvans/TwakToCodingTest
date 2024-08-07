package com.roopasn.tawkto.data.repository

import com.roopasn.tawkto.common.utils.LocalLog
import com.roopasn.tawkto.domain.repository.RequestExecutor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Network Manager concrete implementation which accept many requests and process one request at a time in sequence.
 */
class SingleRequestExecutorImpl internal constructor() : RequestExecutor {
    companion object {
        private const val TAG = "SingleExecutor"
    }

    // request queue
    private val mRequestQueue: MutableList<suspend () -> Unit> = mutableListOf()

    private var mIsFetchInProgress = false

    /**
     * Queue request to internal queue and process the queue
     */
    override fun queueRequest(request: suspend () -> Unit) {
        synchronized(mRequestQueue) {
            mRequestQueue.add(request)
        }

        // Process the queue
        processRequestQueue()
    }

    /**
     * Process the queue till it become empty one after another sequentially
     */
    private fun processRequestQueue() {
        // If already process then simply return
        if (mIsFetchInProgress) return

        // Start process requests in the queue
        LocalLog.i(TAG, "processRequestQueue: starting...")
        mIsFetchInProgress = true

        // Use coroutine with IO dispatcher for network requests
        CoroutineScope(Dispatchers.IO).launch {
            while (true) {
                // Get next request to process
                val request: suspend () -> Unit = synchronized(mRequestQueue) {
                    if (mRequestQueue.isEmpty()) {
                        LocalLog.i(TAG, "processRequestQueue: completed")
                        mIsFetchInProgress = false
                        return@launch
                    }
                    mRequestQueue.removeAt(0)
                }

                LocalLog.i(TAG, "processRequestQueue: processing:$request")
                request.invoke()
            }
        }
    }
}