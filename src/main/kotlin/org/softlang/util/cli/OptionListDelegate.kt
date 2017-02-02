package org.softlang.util.cli

import kotlin.reflect.KProperty

/**
 * Delegate that handles parsing of an option, allowing for repeated
 * occurrences of a single option.
 *
 * @property spec The parser specification
 * @property shorthand The shorthand for the option
 * @property default The default value
 * @property description The description of the option
 */
class OptionListDelegate<T>(
        val spec: (String) -> T,
        name: String,
        flag: Boolean = false,
        shorthand: String? = null,
        description: String? = null,
        var default: List<T>? = null)
    : OptionDelegate(name, flag, shorthand, description) {

    operator fun getValue(self: OptionParser, property: KProperty<*>): List<T> {
        // Find values
        val values = self.parsed.filter {
            it.first == "-$shorthand" || it.first == "--${property.name}"
        }.map { it.second }.map(spec)

        // Return parsed values
        if (values.isNotEmpty())
            return values

        // Return default value or throw an exception
        val default = default
        if (default == null)
            throw IllegalArgumentException("Expecting arguments for " +
                    "${property.name}.")
        else
            return default
    }

    override val mandatory: Boolean
        get() = default == null
}