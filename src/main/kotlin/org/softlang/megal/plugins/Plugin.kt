package org.softlang.megal.plugins

import org.softlang.megal.content.Content
import org.softlang.megal.content.by
import org.softlang.megal.content.from
import java.io.InputStreamReader
import java.net.URI

/**
 * Base interface for all configurable plugins.
 */
interface Plugin

/**
 * Root navigation plugin, resolves root URI to content.
 */
abstract class NavRootPlugin : Plugin {
    /**
     * Initializes root navigation.
     * @param uri The root URI
     * @return Returns resolved content
     */
    abstract operator fun get(uri: URI): Content

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
inline fun navRootBy(crossinline method: (URI) -> Content) =
        object : NavRootPlugin() {
            override fun get(uri: URI) = method(uri)
        }

/**
 * Nested navigation plugin, resolves navigating URI in a given context.
 */
interface NavAfterPlugin : Plugin {
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
inline fun navAfterBy(crossinline method: (URI, Content) -> Content) =
        object : NavAfterPlugin {
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

fun main(args: Array<String>) {
    val httpr = navRootBy {
        // Cast to URL, open a connection
        it.toURL().openConnection().let { connection ->
            // Read or find character set
            val encoding by lazy {
                connection.contentEncoding ?: connection.contentType
                        .substringAfter("charset=").substringBefore(";")
            }

            if (connection.contentType.startsWith("text/"))
            // If content type supplied starts with text, interpret as text
                connection.contentType.substringBefore(";") by {
                    connection
                            .getInputStream()
                            .reader(charset(encoding))
                            .use(InputStreamReader::readText)
                }
            else
            // Otherwise interpret as bytes
                connection.contentType.substringBefore(";") by {
                    connection
                            .getInputStream()
                            .use {
                                it.readBytes(connection.contentLength)
                            }
                }
        }

    }

    val x = httpr["https://www.google.de"]
    println(x.types)
    println(x["text/html"])

    val y = httpr["http://s.4cdn.org/image/title/60.jpg"]
    println(y.types)
    println(y["image/jpeg"])
}