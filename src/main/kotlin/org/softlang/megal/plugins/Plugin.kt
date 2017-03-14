package org.softlang.megal.plugins

import org.softlang.megal.content.*
//import org.softlang.megal.content.by
//import org.softlang.megal.content.from
import java.io.InputStreamReader
import java.net.URI

/**
 * Base interface for all configurable plugins.
 */
interface Plugin

/**
 * Root navigation plugin, resolves root URI to content.
 */
interface  BaseResolver : Plugin {
    /**
     * Initializes root navigation.
     * @param uri The root URI
     * @return Returns resolved content
     */
    operator fun get(uri: URI): Content

    /**
     * Initializes root navigation.
     * @param uri The root URI as a string
     * @return Returns resolved content
     */
    operator fun get(uri: String) = get(URI(uri))
}

/**
 * Creates a simple root navigation plugin.
 * @param method The implementation
 * @return Returns a new root navigation plugin
 */
inline fun baseBy(crossinline method: (URI) -> Content) =
        object : BaseResolver {
            override fun get(uri: URI) = method(uri)
        }

/**
 * Nested navigation plugin, resolves navigating URI in a given context.
 */
interface NestedResolver : Plugin {
    /**
     * Navigates to the [uri] in a given [context].
     * @param uri The nested URI
     * @param context The context to resolve in
     * @return Returns the next resolved content
     */
    operator fun get(uri: URI, context: Content): Content

    /**
     * Navigates to the [uri] in a given [context].
     * @param uri The nested URI as a string
     * @param context The context to resolve in
     * @return Returns the next resolved content
     */
    operator fun get(uri: String, context: Content) = get(URI(uri), context)
}

/**
 * Creates a simple nested navigation plugin.
 * @param method The implementation
 * @return Returns a new nested navigation plugin
 */
inline fun nestedBy(crossinline method: (URI, Content) -> Content) =
        object : NestedResolver {
            override fun get(uri: URI, context: Content) = method(uri, context)
        }

/**
 * Evaluation plugin, used for inference, verification et cetera.
 */
interface EvalPlugin : Plugin {
    /**
     * Evaluates the given input as arbitrary content.
     * @param content The content to handle
     * @return Returns result content
     */
    fun eval(content: Content): Content
}

/**
 * Creates a simple evaluation plugin.
 * @param method The implementation
 * @return Returns a new evaluation plugin
 */
inline fun evalBy(crossinline method: (Content) -> Content) =
        object : EvalPlugin {
            override fun eval(content: Content) = method(content)
        }