package org.softlang.util.cli

/**
 * Base for option delegates.
 */
abstract class OptionDelegate(val name: String?,
                              val flag: Boolean,
                              var shorthand: String?,
                              var description: String?) {
    abstract val mandatory: Boolean
}