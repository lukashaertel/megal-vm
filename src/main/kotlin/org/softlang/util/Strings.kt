package org.softlang.util

/**
 * Simple indentation method.
 */
fun String.indent(indentation: String) =
        indentation + replace(Regex("\\r?\\n|\\r"), "$0$indentation")