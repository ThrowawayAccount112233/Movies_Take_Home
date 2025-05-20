package com.throwaway.movies_take_home.data.network.model

/**
 * Class representing the general [SUCCESS] or [ERROR] of an attempt to retrieve a resource.
 * Optionally wraps the data to send downstream in either case.
 *
 * Used to abstract repository implementation details away from UseCase classes who
 * handle how to propagate [Status] to UI layer.
 */
data class Resource<out T>(val status: Status, val data: T?, val message: String?) {
    companion object {
        fun <T> success(data: T?): Resource<T> {
            return Resource(Status.SUCCESS, data, null)
        }

        fun <T> error(msg: String, data: T? = null): Resource<T> {
            return Resource(Status.ERROR, data, msg)
        }
    }
}

enum class Status {
    SUCCESS,
    ERROR
}