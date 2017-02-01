package org.softlang.megal.grammar

import org.antlr.v4.runtime.*
import org.antlr.v4.runtime.tree.ParseTree
import org.junit.Assert
import org.softlang.megal.grammar.MegalParser.*
import java.io.ByteArrayOutputStream
import java.io.PrintStream
import kotlin.reflect.KClass

/**
 * Reads a module from a file.
 * @param file The filename of the file to read.
 */
fun readModule(file: String): ModuleContext {
    // Setup parser pipeline
    val str = ANTLRFileStream(file)
    val lex = MegalLexer(str)
    val cts = CommonTokenStream(lex)
    val par = MegalParser(cts)

    // Parse, assume not null
    return par.module()
}

fun String.unifyNewlines() = replace("\r\n", "\n").replace("\r", "\n")

/**
 * Gets the location of the parse tree node.
 */
val ParseTree.location: String
    get() {
        val payload = payload
        when (payload) {
        // When the payload is a token, use the position
            is Token -> return "(${payload.line}, ${payload.charPositionInLine + 1})"

        // When the payload is a rule context, use the first child
            is RuleContext -> return payload.getChild(0).location

        // Cannot handle it
            else -> error("Unknown payload type")
        }
    }

/**
 * Asserts that the text is in the parse tree node, will always return true or
 * fail with an assert exception.
 */
operator fun ParseTree.contains(value: String): Boolean {
    // Assert that text is equal
    if (value.unifyNewlines() != text.unifyNewlines())
        Assert.fail("Expected $value, got $text at $location")
    return true
}

/**
 * Asserts that the text items are in the parse tree nodes, will always return
 * true or fail with an assert exception.
 */
operator fun List<ParseTree>.contains(values: List<String>): Boolean {
    // If any mismatching input, fail
    for ((a, b) in zip(values))
        if (b.unifyNewlines() != a.text)
            Assert.fail("Expected ${values.joinToString { it }}, got ${joinToString { it.text }} at ${a.location}")
    return true
}

/**
 * Captures the standard error stream, sets cc true.
 * @param block The block to capture around
 */
fun captureStandardError(block: () -> Unit) = captureStandardError(block, true)

/**
 * Captures the standard error stream; if [cc] is set, prints the output to the
 * original stream.
 * @param block The block to capture around
 * @param cc True if cc to the original stream
 */
fun captureStandardError(block: () -> Unit, cc: Boolean): String {
    // Store the previous error stream
    val store = System.err
    try {
        // Make a byte stream to route into
        val target = ByteArrayOutputStream()

        // Use the stream and print to it in UTF-8
        target.use {
            System.setErr(PrintStream(target, true, "utf-8"))
            block()
        }

        // Read the value of the target byte stream as a UTF-8 string
        val result = String(target.toByteArray(), Charsets.UTF_8)

        // If cc, print to the original stream
        if (cc)
            store.print(result)

        // Return the result
        return result

    } finally {
        System.setErr(store)
    }
}