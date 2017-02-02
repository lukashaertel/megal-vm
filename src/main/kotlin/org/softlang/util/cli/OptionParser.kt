package org.softlang.util.cli

import org.softlang.util.*
import java.io.PrintStream

/**
 * Base class for objects that parse arguments
 */
abstract class OptionParser(val args: Array<String>,
                            val name: String? = null,
                            val description: String? = null) {
    /**
     * All rules
     */
    var rules: List<OptionDelegate> = emptyList()

    /**
     * Gets all indicators of keys
     */
    val keys by lazy {
        rules.flatMap {
            listOf("-${it.shorthand}", "--${it.name}")
        }
    }

    /**
     * Gets all indicators of boolean flags
     */
    val flags by lazy {
        rules.filter { it.flag }.flatMap {
            listOf("-${it.shorthand}", "--${it.name}")
        }
    }

    /**
     * All arguments in a parsed fashion.
     */
    val parsed: List<Pair<String?, String>> by lazy {
        fun eval(input: List<String>): List<Pair<String?, String>> {
            val head = input.getOrNull(0)
            val next = input.getOrNull(1)

            // No head, end of list
            if (head == null)
                return listOf()

            // Head is a boolean, special treatment applies
            if (head in flags)
                return when (next) {
                // Consume following true or false
                    "true", "false" -> head to next then eval(input.skip(2))
                // Otherwise regard as true
                    else -> head to "true" then eval(input.tail())
                }

            // Head does not start with dash, it's a free argument
            if (!head.startsWith("-"))
                return null to head then eval(input.tail())

            // If no next, regard head as free argument anyways, otherwise
            // standard treatment applies
            if (next == null)
                return listOf(null to head)
            else
                return head to next then eval(input.skip(2))
        }

        // Return by recursive evaluator application
        eval(args.toList())
    }

    /**
     * All unknown assignments
     */
    val unknown by lazy {
        parsed.filter { it.first !in keys }.map { it.second }
    }


    fun printHelp(printStream: PrintStream) = printStream.apply {

        // Print title
        if (name != null) {
            println("Name: $name")
            println()
        }

        // Print program description
        if (description != null) {
            println("Description:")
            println(description.indent("  "))
            println()
        }

        // Print parser rules
        for (rule in rules) {
            val shorthand = rule.shorthand
            val flag = rule.flag
            val name = rule.name
            val description = rule.description

            when (rule) {
            // Handle regular options
                is OptionSingleDelegate<*>, is OptionListDelegate<*> -> {
                    print("  ")

                    // Format shorthand if present
                    if (shorthand != null)
                        print("-$shorthand, ")
                    else
                        print("    ")

                    // Format long name
                    print("--$name")

                    // Prepare value
                    val value = when (rule) {
                        is OptionSingleDelegate<*> ->
                            rule.default?.toString() ?: "VALUE"
                        is OptionListDelegate<*> ->
                            rule.default?.toString() ?: "VALUE"
                        else -> error("Should never get here")
                    }

                    // Print value and finalize line
                    println(" $value:")

                    // If description present print description
                    if (description != null)
                        println(description.indent("    "))
                }
            // Handle extra options
                is ExtraDelegate -> {
                    // Prepare value
                    val value = rule.default?.toString() ?: "VALUE"

                    // Print value and finalize line
                    println("  Extra arguments $value:")

                    // If description present print description
                    if (description != null)
                        println(description.indent("    "))

                }
            // Handle unknown options
                is UnknownDelegate -> {
                    println("  For all unknown options:")

                    // If description present print description
                    if (description != null)
                        print(description.indent("    "))
                    else
                        println("Handled but not specified any further."
                                .indent("    "))
                }
            }

            // Separate rules
            if (rule != rules.last())
                println()
        }
    }
}