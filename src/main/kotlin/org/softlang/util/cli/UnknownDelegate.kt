package org.softlang.util.cli

import kotlin.reflect.KProperty

/**
 * Delegate that handles parsing of unknown arguments.
 *
 * @property description The description of the option
 */
class UnknownDelegate(
        description: String? = null)
    : OptionDelegate(null, false, null, description) {


    operator fun getValue(self: OptionParser, property: KProperty<*>): List<String> {
        // Just return unknown elements, default does not make sense here
        return self.unknown
    }

    override val mandatory: Boolean
        get() = false
}