package org.softlang.megal.content

import kotlin.reflect.KClass

/**
 * Content components.
 */

/**
 * A type exposing a MIME type definition and a runtime class.
 */
data class Type<T : Any>(
        val mime: Mime,
        val rtt: KClass<T>) {
    override fun toString() = "$mime: ${rtt.simpleName}"
}

/**
 * A provider for a given [Type], as defined by a producer.
 */
data class Provider<T : Any>(
        val type: Type<T>,
        val get: () -> T)

/**
 * An adapter for two [Type]s, as defined by a transformation.
 */
data class Adapter<T : Any, U : Any>(
        val src: Type<T>,
        val dst: Type<U>,
        val transformation: (T) -> U)

/**
 * Creates a provider from the MIME type, the reified type argument and the
 * provider method.
 */
inline fun <reified T : Any>
        provider(mime: Mime, noinline get: () -> T) =
        Provider(Type(mime, T::class), get)

/**
 * Creates a provider from the MIME type as a string, the reified type argument
 * and the provider method.
 */
inline fun <reified T : Any>
        provider(mime: String, noinline get: () -> T) =
        provider(parseMime(mime), get)

/**
 * Creates an adapter from the MIME types, the reified type arguments and the
 * transformation method.
 */
inline fun <reified T : Any, reified U : Any>
        adapter(src: Mime, dst: Mime, noinline transformation: (T) -> U) =
        Adapter(Type(src, T::class), Type(dst, U::class), transformation)

/**
 * Creates an adapter from the MIME types as strings, the reified type arguments
 * and the transformation method.
 */
inline fun <reified T : Any, reified U : Any>
        adapter(src: String, dst: String, noinline transformation: (T) -> U) =
        adapter(parseMime(src), parseMime(dst), transformation)
