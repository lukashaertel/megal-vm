package org.softlang.megal.structs

/**
 * A navigation preliminary or terminal result.
 */
data class Location(val position: Set<Any>, val remaining: String) {
    /**
     * True if location is terminal, i.e., no remaining navigation is to be
     * performed.
     */
    val terminal: Boolean get() = remaining.isBlank()
}
