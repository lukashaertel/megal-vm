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