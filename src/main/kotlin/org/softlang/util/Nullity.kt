package org.softlang.util

/**
 * Maps the value if it is not null, otherwise returns null itself.
 */
fun <T, U> T?.mapn(function: (T) -> U) = if (this == null)
    null
else
    function(this)

fun <T : Any> firstnn(vararg items: T?) = listOf(*items)
        .filterNotNull()
        .firstOrNull()

/**
 * Applies empty-string-to-null conversion.
 */
fun String?.toNull() = when (this) {
    null -> null
    "" -> null
    else -> this
}

/**
 * Applies positive integer minus-one-to-null conversion.
 */
fun Int?.toNull() = when (this) {
    null -> null
    -1 -> null
    else -> this
}