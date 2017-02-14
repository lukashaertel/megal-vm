package org.softlang.util

import kotlin.reflect.KClass

/**
 * Returns true if the block did not fail with an exception.
 */
fun <T : Exception> nofail(with: KClass<T>, block: () -> Unit) = try {
    block()
    true
} catch(ex: Exception) {
    !with.java.isInstance(ex)
}
