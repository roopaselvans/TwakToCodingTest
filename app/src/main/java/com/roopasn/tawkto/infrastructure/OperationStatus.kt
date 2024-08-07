package com.roopasn.tawkto.common

data class ErrorDetail(val code: Int, val message: String, val exception: Exception? = null)

/**
 * Any background operation status which involves network call or long lasting calls
 *
 * @param data Fetched data to be passed
 * @param error error detail if operation is failed
 */
sealed class OperationStatus<T>(val data: T? = null, val error: ErrorDetail? = null) {
    /**
     * Operation is successful
     *
     * @param data fetched data
     */
    class Success<T>(data: T) : OperationStatus<T>(data)

    /**
     * Operation is failed
     *
     * @param error error details
     * @param data optional data is some partial data available
     */
    class Error<T>(error: ErrorDetail, data: T? = null) : OperationStatus<T>(data, error)

    /**
     * Operation is loading
     *
     * @param data cached data is any will be provided here
     */
    class Loading<T>(data: T? = null) : OperationStatus<T>(data)
}
