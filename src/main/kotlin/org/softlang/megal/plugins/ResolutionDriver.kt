package org.softlang.megal.plugins

import org.softlang.megal.content.*
import org.softlang.util.decomposed
import org.softlang.util.hasAny
import java.io.InputStreamReader
import java.net.URI
import kotlin.reflect.KClass
import kotlin.reflect.full.createInstance
import kotlin.reflect.full.isSuperclassOf

/**
 * TODO: Prototype implementation, rewrite probably a lot
 */

/**
 * Abstract name for configured resolver types.
 */
interface ResolverConfig {
    /**
     * Gets the name of the class implementing the resolver.
     */
    val name: KClass<out Plugin>
}

/**
 * Base resolver with associated resolution information.
 * @property baseResolver The class handling base resolution.
 * @property realizes Schemes realized by this base resolver.
 */
data class BaseResolverConfig(
        val baseResolver: KClass<out BaseResolver>,
        val realizes: Set<String>) : ResolverConfig {
    override val name: KClass<out Plugin>
        get() = baseResolver

    /**
     * Instance of the resolver
     */
    val instance by lazy { baseResolver.createInstance() }
}

/**
 * Nested resolver with associated resolution information.
 * @property nestedResolver The class handling nested resolution.
 * @property realizes Schemes realized by this nested resolver.
 * @property accepts The MIME types accepted by this nested resolver.
 * @property after The resolvers this nested resolver may appear after.
 */
data class NestedResolverConfig(
        val nestedResolver: KClass<out NestedResolver>,
        val realizes: Set<String>,
        val accepts: Set<Mime>,
        val after: Set<KClass<out Plugin>>) : ResolverConfig {
    override val name: KClass<out Plugin>
        get() = nestedResolver

    /**
     * Instance of the resolver
     */
    val instance by lazy { nestedResolver.createInstance() }
}


/**
 * Resolution driver configured by a set of resolver configs.
 */
data class ResolutionDriver(val resolvers: Set<ResolverConfig>) {
    /**
     * All configured base resolvers in [resolvers].
     */
    val baseResolvers by lazy {
        resolvers.filterIsInstance<BaseResolverConfig>()
    }

    /**
     * All configured nested resolvers in [resolvers].
     */
    val nestedResolvers by lazy {
        resolvers.filterIsInstance<NestedResolverConfig>()
    }


    /**
     * Base resolvers accessible by their supported scheme.
     */
    val baseResolversForScheme by lazy {
        baseResolvers.fold(emptyMap<String, BaseResolverConfig>()) { l, r ->
            l + r.realizes.associate { it to r }
        }
    }


    /**
     * Performs nested resolution on the given URIs.
     */
    fun resolve(vararg uris: URI): Content {
        /// Decompose the URIs, as the first item is dealt with differently
        val (head, tail) = uris.asList().decomposed

        // Initial resolver, as provided by the realized scheme
        val init = baseResolversForScheme[head.scheme]
                ?: throw NoSuchElementException(head.scheme)

        // Current content, as resolved by the initial resolver
        var parent: ResolverConfig = init
        var context = init.instance[head]

        // Process all elements in the tail
        for (uri in tail)
            nestedResolvers
                    //TODO: More efficient way to find the appropriate NR
                    //TODO: Maybe include RTT into type spec
                    .filter { (_, r, _, _) -> uri.scheme in r }
                    .filter { (_, _, a, _) -> context.availableMimes hasAny a }
                    .filter { (_, _, _, a) ->
                        a.any {
                            it.isSuperclassOf(parent.name)
                        }
                    }
                    .first().let {
                // Resolve next element with the resolver
                context = it.instance[uri, context]
                parent = it
            }

        // Return the result of the resolution as output
        return context
    }
}


class HTTPResolver : BaseResolver {
    override fun get(uri: URI): Content {

        // Cast to URL, open a connection
        uri.toURL().openConnection().let { connection ->
            val rawMime = parseMime(connection.contentType)
            val mime = rawMime.unparameterized

            when {
            // Read text types with a given encoding as string
                mime.top == "text" -> return mime by {
                    // Read or guess encoding
                    val encoding = rawMime
                            .parsedParameters
                            .getOrDefault("charset", "UTF-8")

                    // Read connection to end
                    connection
                            .getInputStream()
                            .reader(charset(encoding))
                            .use(InputStreamReader::readText)
                }

            // Read everything else as byte array
                else -> return mime by {
                    connection
                            .getInputStream()
                            .use {
                                it.readBytes(connection.contentLength)
                            }
                }
            }
        }
    }
}

class SelectResolver : NestedResolver {
    override fun get(uri: URI, context: Content): Content {
        // Get parameters by analyzing the URIs query params
        val pars = uri.query.split('&').associate {
            it.substringBefore('=') to it.substringAfter('=')
        }

        // Extract relevant parameters
        val start = pars.getValue("start").toInt()
        val end = pars.getValue("end").toInt()

        // Make subrange selection from parameters
        fun selector(str: String) =
                when (uri.path) {
                    "/lines" -> str
                            .lineSequence()
                            .drop(start - 1)
                            .take(end - (start - 1))
                            .joinToString(System.lineSeparator())
                    "/chars" -> str
                            .substring(start, end)
                    else -> throw IllegalArgumentException("Unknown path")
                }

        // All top level text mime types are presented to subrange selection
        val new = context
                .availableTypes
                .filter { it.mime.top == "text" && it.rtt == String::class }
                .map { provider(it.mime) { context.into(it.mime, ::selector) } }
                .toSet()

        return Content(providers = new)
    }

}

fun main(args: Array<String>) {

    val rd = ResolutionDriver(setOf(
            // HTTP resolver configured to handle http(s) URIs
            BaseResolverConfig(
                    HTTPResolver::class,
                    setOf("http", "https")),
            // Select resolver configured to handle select URIs
            NestedResolverConfig(
                    SelectResolver::class,
                    setOf("select"),
                    setOf(
                            Mime("text", "html"),
                            Mime("text", "plain")),
                    setOf(
                            BaseResolver::class
                    ))
    ))

    // Resolve an example
    val x = rd.resolve(
            URI("https://www.google.de"),
            URI("select:/lines?start=1&end=3"))
    println(x.availableTypes)
    x.into("text/html") { s: String ->
        println(s)
    }

    val y = rd.resolve(
            URI("http://s.4cdn.org/image/title/60.jpg"))
    println(y.availableTypes)
    y.into("image/jpeg") { s: ByteArray ->
        s.joinToString { "$it" }
    }
}