package org.softlang.megal.content

import org.softlang.util.cat
import org.softlang.util.toNull

/**
 * Specification of a MIME type.
 */
data class Mime(
        val top: String,
        val tree: String?,
        val sub: String,
        val suffix: String?,
        val parameters: String?) {
    /**
     * Secondary constructor initializing only mandatory fields.
     */
    constructor(top: String, sub: String) : this(top, null, sub, null, null)

    /**
     * True if the MIME type is generic
     */
    val isGeneric get() = sub == "*"

    /**
     * Makes the MIME type generic
     */
    val lifted get() = Mime(top, tree, "*", suffix, parameters)

    /**
     * Removes parameters from the MIME type
     */
    val unparameterized get() = Mime(top, tree, sub, suffix, null)

    /**
     * True if [mime] is covered by the receiver.
     */
    operator fun contains(mime: Mime) =
            (isGeneric && this == mime.lifted) || this == mime

    /**
     * Converts the MIME type into a string.
     */
    override fun toString() =
            "$top/${tree cat "."}$sub${"+" cat suffix}${";" cat parameters}"
}

/**
 * Regex that parses MIME types.
 */
private val MIME_REGEX = Regex("""([^/]+)/(?:([^.]+)\.)?([^+;]+)(?:\+([^;]+))?(?:;(.+))?""")


/**
 * Parses the [string] as a MIME type and returns it. Throws an illegal
 * argument exception if the format is invalid.
 */
fun parseMime(string: String): Mime {
    // Match using regular expressions
    val match = MIME_REGEX.matchEntire(string) ?: throw IllegalArgumentException()

    // Destruct
    val (top, tree, sub, suffix, parameters) = match.destructured

    // Translate into mime type
    return Mime(top, tree.toNull(), sub, suffix.toNull(), parameters.toNull())
}
