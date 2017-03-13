package org.softlang.util

/**
 * Simple indentation method.
 */
fun String.indent(indentation: String) =
        indentation + replace(Regex("\\r?\\n|\\r"), "$0$indentation")

/**
 * Concatenates strings if both are not null, otherwise returns an empty string.
 */
infix fun String?.cat(next: String?) = when {
    this == null -> ""
    next == null -> ""
    else -> "$this$next"
}

/**
 * Concatenates strings if both are not null, otherwise returns null.
 */
infix fun String?.ncat(next: String?) = when {
    this == null -> null
    next == null -> null
    else -> "$this$next"
}