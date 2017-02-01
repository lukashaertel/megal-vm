package org.softlang.util

import org.antlr.v4.runtime.tree.ParseTree
import org.softlang.megal.grammar.MegalBaseVisitor

/**
 * Applies a visitor to an iterable.
 * @receiver The visitor to apply
 * @param iterable The iterable to map
 * @return Returns the visitor applied to the elements
 */
fun <T : MegalBaseVisitor<U>, U> T.mapvisit(iterable: Iterable<ParseTree>)
        = iterable.map { visit(it) }

/**
 * Applies a visitor to a sequence.
 * @receiver The visitor to apply
 * @param sequence The sequence to map
 * @return Returns the visitor applied to the elements
 */
fun <T : MegalBaseVisitor<U>, U> T.mapvisit(sequence: Sequence<ParseTree>)
        = sequence.map { visit(it) }

/**
 * Applies a visitor to a list.
 * @receiver The visitor to apply
 * @param list The list to map
 * @return Returns the visitor applied to the elements
 */
fun <T : MegalBaseVisitor<U>, U> T.mapvisit(list: List<ParseTree>)
        = list.map { visit(it) }