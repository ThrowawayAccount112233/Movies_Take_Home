package com.throwaway.movies_take_home.data.model

data class Genre(
    val name: String,
    val count: Int
) {
    companion object {
        fun all(count: Int = 0) = Genre(ALL, count)
    }
}

/**
 * Whether this genre is the "All" genre, which is maintained client side.
 */
fun Genre.isAll() = name == ALL
const val ALL = "All"

