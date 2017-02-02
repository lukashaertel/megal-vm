package org.softlang.util

/**
 * Created by pazuzu on 2/2/17.
 */

fun String.indent(indentation: String) =
        indentation + replace(Regex("\\r?\\n|\\r"), "$0$indentation")