package org.softlang.megal.grammar

import org.softlang.util.nofail
import java.net.URI
import java.net.URISyntaxException

/**
 * Accepts the text of a LITERAL when it starts with '<', ends with '>', and
 * all parts in it separated by '|' are URIs.
 * @param text The text to accept
 */
fun acceptLiteral(text: String) =
        text.startsWith("<")
                && text.endsWith(">")
                && text.substring(1, text.length - 1)
                .splitToSequence('|')
                .map(String::trim)
                .all {
                    nofail(URISyntaxException::class) {
                        URI(it)
                    }
                }

/**
 * Parses a LITERAL into a list of URIs.
 * @param text The text to parse
 * @return Returns an immutable list of URIs
 */
fun parseLiteral(text: String) =
        text.substring(1, text.length - 1)
                .splitToSequence('|')
                .map(String::trim)
                .map(::URI)
                .toList()