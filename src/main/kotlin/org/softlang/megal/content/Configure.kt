package org.softlang.megal.content

/**
 * Content configuration utilities.
 */

/**
 * Content configuration object, used as recevier in [config].
 */
class Configure<T : Any>(val type: Type<T>) {
    /**
     * Backing for current providers.
     */
    val providers = mutableSetOf<Provider<*>>()
    /**
     * Backing for current adapters.
     */
    val adapters = mutableSetOf<Adapter<*, *>>()

    /**
     * Provide the currently configuring type by a provider.
     */
    fun by(get: () -> T) {
        providers += Provider(type, get)
    }

    /**
     * Adds a transformation to the given type.
     */
    inline fun <reified U : Any>
            to(dst: Mime, noinline transformation: (T) -> U) {
        adapters += Adapter(type, Type(dst, U::class), transformation)
    }

    /**
     * Adds a transformation to the given type.
     */
    inline fun <reified U : Any>
            to(dst: String, noinline transformation: (T) -> U) =
            to(parseMime(dst), transformation)

    /**
     * Compiles and retrieves the currently composed content.
     */
    val content get() = Content(
            providers.toSet(),
            adapters.toSet())

}

/**
 * Starts configuration on a new type.
 */
inline fun <reified T : Any>
        config(mime: Mime, configure: Configure<T>.() -> Unit) =
        Configure(Type(mime, T::class)).apply(configure).content

/**
 * Starts configuration on a new type.
 */
inline fun <reified T : Any>
        config(mime: String, configure: Configure<T>.() -> Unit) =
        config(parseMime(mime), configure)

