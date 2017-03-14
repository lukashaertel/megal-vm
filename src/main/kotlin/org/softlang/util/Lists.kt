package org.softlang.util

/**
 * Skips [num] elements in the list.
 */
fun <E> List<E>.skip(num: Int) = subList(num, size)

/**
 * Returns the tail of the list.
 */
fun <E> List<E>.tail() = skip(1)

/**
 * Constructs a list from the first element and a list of remaining elements.
 */
infix fun <E> E.then(list: List<E>) = listOf(this) + list

/**
 * Checks if list contains item, returns false if item is null and list is
 * not nullable.
 */
fun <E> List<E>.contains(item: E?) =
        if (item == null)
            false
        else contains(item)

/**
 * Returns consecutive values of the list as pairs.
 */
val <E> List<E>.pairs: List<Pair<E, E>> get() = (1 until size)
        .map { get(it - 1) to get(it) }

/**
 * Decomposes the list as head and tail for pair variable assignments.
 */
val <E> List<E>.decomposed: Pair<E, List<E>> get() = first() to tail()