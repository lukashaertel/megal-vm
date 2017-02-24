package org.softlang.megal.content

import com.oracle.webservices.internal.api.message.ContentType
import org.json.JSONObject
import org.softlang.util.then
import kotlin.reflect.KClass

/**
 * Mime type
 */
typealias Mime = String

/**
 * Content type as pair of mime- and runtime type
 */
typealias Type<T> = Pair<Mime, KClass<T>>

/**
 * Pair of mime types without runtime types
 */
typealias MimePair = Pair<Mime, Mime>

/**
 * Pair of content types
 */
typealias TypePair<T, U> = Pair<Type<T>, Type<U>>

/**
 * Makes a type based on the call site.
 * @param X The runtime type
 * @param mime The mime type
 * @return Returns a full type
 */
inline fun <reified X : Any> type(mime: Mime): Type<X> =
        mime to X::class

/**
 * Type of plain text.
 */
val textPlain: Type<String> = type("text/plain")

/**
 * Type of HTML.
 */
val textHtml: Type<String> = type("text/html")

/**
 * Type of application octet-stream.
 */
val appOctetStream: Type<ByteArray> = type("application/octet-stream")

/**
 * Type of application JSON.
 */
val appJson: Type<JSONObject> = type("application/json")

/**
 * Abstract adaptable content. [types] denotes all supported content types.
 * The [adapt] methods perform transformation. Objects are equal, if all
 * shared content types are equal and the objects share at least one content
 * type. Hash code is calculated for plain text, octet-streams or the first
 * proper adaptable type. String representations are calculated from first
 * plain text or first proper text type.
 */
abstract class Content {
    /**
     * All supported types in the content.
     */
    abstract val types: Collection<Type<*>>

    /**
     * Returns true if [mime] type is supported.
     * @param mime The mime type to check for
     * @return Returns true if supported.
     */
    open operator fun contains(mime: Mime) =
            types.any { it.first == mime }

    /**
     * Returns true if [type] is supported.
     * @param type The type to check for
     * @return Returns true if supported.
     */
    open operator fun contains(type: Type<*>) =
            type in types

    /**
     * Adapts to the given content type.
     * @param X The result type
     * @param to The content type to adapt to
     * @return Returns as [X]
     */
    abstract fun <X : Any> adapt(to: Type<X>): X

    /**
     * Adapts to the given content type.
     * @param to The mime type to adapt to
     * @return Returns arbitrary for mime
     */
    abstract fun adapt(to: Mime): Any

    /**
     * Wraps the adapt method by inferring the type parameter from the call
     * site and just using the mime type.
     * @param X The result type
     * @param mime The result mime type
     * @return Returns as [X]
     */
    operator inline fun <reified X : Any> invoke(mime: Mime) =
            adapt(mime to X::class)

    /**
     * Wraps the adapt method for untyped conversion
     * @param mime The mime type
     */
    operator fun get(mime: Mime) =
            adapt(mime)

    override fun hashCode() = when {
    // Empty content
        types.isEmpty() -> 1
    // Adaption to plain text
        textPlain in this -> adapt(textPlain).hashCode()
    // Adaption to bytes
        appOctetStream in this -> adapt(appOctetStream).contentHashCode()
    // First proper type
        else -> adapt(types.first().first).hashCode()
    }

    override fun equals(other: Any?) =
            if (other is Content)
                types.any {
                    it in other
                } && types.all {
                    it !in other || adapt(it.first) == other.adapt(it.first)
                }
            else
                false

    override fun toString(): String {
        // Empty content
        if (types.isEmpty())
            return "<empty>"

        // If plaintext is contained, use it
        if (textPlain in this)
            return adapt(textPlain)

        // Find first text conversion
        val firstText = types.firstOrNull { it.first.startsWith("text/") }

        // If there is a text conversion, use it
        if (firstText != null)
            return adapt(firstText.first).toString()

        // Handle non-adaptable type
        return "<content: $types>"
    }
}

/**
 * Empty content value.
 */
val empty: Content = object : Content() {
    override val types get() = emptyList<Type<*>>()
    override fun contains(mime: Mime) =
            false

    override fun contains(type: Type<*>) =
            false


    override fun <X : Any> adapt(to: Type<X>): X {
        throw IllegalArgumentException("$to unsupported.")
    }

    override fun adapt(to: Mime): Any {
        throw IllegalArgumentException("$to unsupported.")
    }

}

/**
 * Unbound transformer of content.
 * @param T The source type
 * @param U The target type
 * @property source The source content type
 * @property target The target content type
 */
abstract class Transformer<T : Any, U : Any>(
        val source: Type<T>,
        val target: Type<U>) {

    /**
     * Transforms the source value into the target domain
     */
    abstract fun transform(source: T): U

    fun bind(base: Content) =
            object : Content() {
                override val types: Collection<Type<*>>
                    get() = listOf(target)

                // The unchecked cast is valid since type is bound to X by
                // call site.
                @Suppress("UNCHECKED_CAST")
                override fun <X : Any> adapt(to: Type<X>) =
                        if (to == target)
                            transform(base.adapt(source)) as X
                        else
                            throw IllegalArgumentException("$to unsupported.")

                override fun adapt(to: Mime) =
                        if (to == target.first)
                            transform(base.adapt(source))
                        else
                            throw IllegalArgumentException("$to unsupported.")
            }
}

/**
 * Implements singular content by a provider function.
 * @param X The result type
 * @receiver The mime type to register for
 * @param provider The content provider
 * @return Returns content
 */
inline infix fun <reified X : Any> Mime.by(crossinline provider: () -> X) =
        object : Content() {
            /**
             * The only supported type
             */
            val type get() = this@by to X::class

            override val types get() = listOf(type)

            // The unchecked cast is valid since provider is bound to X by
            // call site.
            @Suppress("UNCHECKED_CAST")
            override fun <X : Any> adapt(to: Type<X>): X =
                    if (to == type)
                        provider() as X
                    else
                        throw IllegalArgumentException("$to unsupported.")

            override fun adapt(to: Mime) =
                    if (to == type.first)
                        provider()
                    else
                        throw IllegalArgumentException("$to unsupported.")
        }

/**
 * Composes the given contents in a multiplexer.
 * @param content The content providers to use
 * @return Returns a single content provider
 */
fun from(content: List<Content>): Content {
    // Map providers by their supported types
    val providers = content
            .flatMap { c -> c.types.map { it to c } }
            .fold(mapOf<Type<*>, Content>(), { l, r -> l + r })

    // Map raw providers by their supported mime types
    val rawProviders = content
            .flatMap { c -> c.types.map { it.first to c } }
            .fold(mapOf<Mime, Content>(), { l, r -> l + r })

    // Implement content by lookup in the providers
    return object : Content() {
        override val types get() = providers.keys

        override fun contains(mime: Mime) =
                mime in rawProviders

        override fun contains(type: Type<*>) =
                type in providers

        override fun <X : Any> adapt(to: Type<X>) =
                providers.getValue(to).adapt(to)

        override fun adapt(to: Mime) =
                rawProviders.getValue(to).adapt(to)
    }
}

/**
 * Composes the given contents in a multiplexer.
 * @param content The content providers to use
 * @return Returns a new content provider
 */
fun from(vararg content: Content) =
        from(content.toList())

/**
 * Composes the receiver and [other]. See also the composition method [from].
 * @receiver The first content
 * @param other The other content
 * @return Returns a new content provider
 */
infix fun Content.or(other: Content) =
        from(this, other)

/**
 * Implements a transformer by a function.
 * @param T The source type
 * @param U The target type
 * @param mimePair The source and target mime types
 * @param transformation The transformation implementation
 */
inline fun <reified T : Any, reified U : Any> tx(
        mimePair: MimePair, crossinline transformation: (T) -> U) =
        object : Transformer<T, U>(
                mimePair.first to T::class,
                mimePair.second to U::class) {
            override fun transform(source: T) = transformation(source)
        }


/**
 * Enriches content with transformers.
 * @receiver The content to enrich
 * @param transformers The transformers to use
 * @return Returns a new content with the original and the transformer results
 */
fun Content.with(transformers: List<Transformer<*, *>>) =
        from(this then transformers.map { it.bind(this) })

/**
 * Enriches content with transformers.
 * @receiver The content to enrich
 * @param transformers The transformers to use
 * @return Returns a new content with the original and the transformer results
 */
fun Content.with(vararg transformers: Transformer<*, *>) =
        with(transformers.toList())

fun main(args: Array<String>) {
    val content = "text/plain" by { "Garr" }
    val secondary = "text/plain" by { "Garr" }

    println(content == secondary)

    val contentEx = content.with(
            tx("text/plain" to "application/xml") { s: String ->
                "<document>$s</document>"
            },
            tx("text/plain" to "application/octet-stream") { s: String ->
                s.toByteArray()
            }
    )

    println(contentEx)

    println(contentEx["text/plain"])
    println(contentEx["application/xml"])
    println(contentEx["application/octet-stream"])
}