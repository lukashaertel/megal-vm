package org.softlang.util.cli

import com.sun.org.apache.bcel.internal.classfile.Unknown
import org.softlang.util.cli.*
import java.io.PrintStream
import kotlin.reflect.KProperty


/**
 * Captures one boolean bound to a key.
 * @param configure The configuration method
 */
fun OptionParser.boolean(
        name: String,
        configure: OptionSingleDelegate<Boolean>.() -> Unit) =
        OptionSingleDelegate(String::toBoolean, name, true)
                .apply(configure)
                .apply {
                    this@boolean.rules += this
                }

/**
 * Captures booleans bound to a key.
 * @param configure The configuration method
 */
fun OptionParser.booleans(
        name: String,
        configure: OptionListDelegate<Boolean>.() -> Unit) =
        OptionListDelegate(String::toBoolean, name, true)
                .apply(configure)
                .apply {
                    this@booleans.rules += this
                }

/**
 * Captures one int bound to a key.
 * @param configure The configuration method
 */
fun OptionParser.int(
        name: String,
        configure: OptionSingleDelegate<Int>.() -> Unit) =
        OptionSingleDelegate(String::toInt, name)
                .apply(configure)
                .apply { this@int.rules += this }

/**
 * Captures ints bound to a key.
 * @param configure The configuration method
 */
fun OptionParser.ints(
        name: String,
        configure: OptionListDelegate<Int>.() -> Unit) =
        OptionListDelegate(String::toInt, name)
                .apply(configure)
                .apply { this@ints.rules += this }

/**
 * Captures one float bound to a key.
 * @param configure The configuration method
 */
fun OptionParser.float(
        name: String,
        configure: OptionSingleDelegate<Float>.() -> Unit) =
        OptionSingleDelegate(String::toFloat, name)
                .apply(configure)
                .apply { this@float.rules += this }

/**
 * Captures floats bound to a key.
 * @param configure The configuration method
 */
fun OptionParser.floats(
        name: String,
        configure: OptionListDelegate<Float>.() -> Unit) =
        OptionListDelegate(String::toFloat, name)
                .apply(configure)
                .apply { this@floats.rules += this }

/**
 * Captures one string bound to a key.
 * @param configure The configuration method
 */
fun OptionParser.string(
        name: String,
        configure: OptionSingleDelegate<String>.() -> Unit) =
        OptionSingleDelegate(String::toString, name)
                .apply(configure)
                .apply { this@string.rules += this }

/**
 * Captures strings bound to a key.
 * @param configure The configuration method
 */
fun OptionParser.strings(
        name: String,
        configure: OptionListDelegate<String>.() -> Unit) =
        OptionListDelegate(String::toString, name)
                .apply(configure)
                .apply { this@strings.rules += this }

/**
 * Captures all extra arguments, i.e., those who are not bound by a key.
 * @param configure The configuration method
 */
fun OptionParser.extra(configure: ExtraDelegate.() -> Unit) =
        ExtraDelegate()
                .apply(configure)
                .apply { this@extra.rules += this }

/**
 * Captures all unknown arguments, i.e., those who are bound by a key but
 * unknown to the parser.
 * @param configure The configuration method
 */
fun OptionParser.unknown(configure: UnknownDelegate.() -> Unit) =
        UnknownDelegate()
                .apply(configure)
                .apply { this@unknown.rules += this }
