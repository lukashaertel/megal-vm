package org.softlang.util.cli

import kotlin.reflect.KProperty

/**
 * Delegate that handles parsing of extra arguments.
 *
 * @property default The default value
 * @property description The description of the option
 */
class ExtraDelegate(
        description: String? = null,
        var default: List<String>? = null)
    : OptionDelegate(null, false, null, description) {


    operator fun getValue(self: OptionParser, property: KProperty<*>): List<String> {
        // Filter where key applies
        val extra = self.parsed.filter { it.first == null }.map { it.second }

        // Return parsed values
        if (extra.isNotEmpty())
            return extra

        // Return default value or throw an exception
        val default = default
        if (default == null)
            throw IllegalArgumentException("Expecting extra arguments.")
        else
            return default
    }

    override val mandatory: Boolean
        get() = default == null
}