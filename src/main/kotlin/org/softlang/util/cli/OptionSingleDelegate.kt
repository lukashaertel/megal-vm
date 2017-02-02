package org.softlang.util.cli

import kotlin.reflect.KProperty

/**
 * Delegate that handles parsing of an option.
 *
 * @property spec The parser specification
 * @property flag True if this is a boolean flag
 * @property shorthand The shorthand for the option
 * @property default The default value
 * @property description The description of the option
 */
class OptionSingleDelegate<T>(
        private val spec: (String) -> T,
        name: String,
        flag: Boolean = false,
        shorthand: String? = null,
        description: String? = null,
        var default: T? = null)
    : OptionDelegate(name, flag, shorthand, description) {

    operator fun getValue(self: OptionParser, property: KProperty<*>): T {
        // Find values
        val values = self.parsed.filter {
            it.first == "-$shorthand" || it.first == "--${property.name}"
        }.map { it.second }.map(spec)

        // Return parsed values
        if (values.isNotEmpty()) {
            // Assert that just one value is there
            if (values.size > 1)
                throw IllegalArgumentException("Multiple arguments for " +
                        "${property.name}.")

            // Return this one value
            return values.first()
        }


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