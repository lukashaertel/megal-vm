package org.softlang.util

/**
 * Returns true if there is an intersection of the collections.
 */
infix fun <E> Collection<E>.hasAny(other: Collection<E>) =
        if (size < other.size)
            any { it in other }
        else
            other.any { it in this }