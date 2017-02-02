package org.softlang.util

fun <E> List<E>.skip(num: Int) = subList(num, size)

fun <E> List<E>.tail() = skip(1)

infix fun <E> E.then(list: List<E>) = listOf(this) + list

/**
 * Checks if list contains item, returns false if item is null and list is
 * not nullable.
 */
fun <E> List<E>.contains(item: E?) =
        if (item == null)
            false
        else contains(item)