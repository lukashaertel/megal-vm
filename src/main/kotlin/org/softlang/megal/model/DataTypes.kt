package org.softlang.megal.model

import org.antlr.v4.runtime.tree.ErrorNode

/**
 * Primary declaration.
 */
interface Declaration

/**
 * Module of named declarations.
 */
data class Module(
        val name: String,
        val declarations: List<Declaration>) : Declaration

/**
 * Documented group.
 */
data class Group(
        val documentation: String,
        val declarations: List<Declaration>) : Declaration

/**
 * Statement.
 */
data class Statement(
        val label: String?,
        val source: Node,
        val connector: Node,
        val target: List<Node>,
        val bind: Node?,
        val continuations: List<Continuation>) : Declaration

/**
 * Continuation of a statement.
 */
data class Continuation(val connector: Node, val target: Node)

/**
 * Primary expression.
 */
interface Node

/**
 * Named node with an argument.
 */
data class Primary(
        val isabstract: Boolean,
        val name: String,
        val argument: Node?) : Node

/**
 * Tuple of nodes.
 */
data class Tuple(val entries: List<Node>) : Node

/**
 * Operation node.
 */
data class Op(val operator: String) : Node

/**
 * Data node.
 */
data class Data(val data: Any?) : Node

/**
 * Module import with substitutions.
 */
data class Imports(
        val ref: String,
        val substitutions: List<Substitution>) : Declaration

/**
 * Node substitution.
 */
data class Substitution(val from: String, val to: String)

/**
 * Declaration in error state.
 */
data class DeclarationError(val error: ErrorNode) : Declaration

/**
 * Node context in error state.
 */
data class NodeError(val error: ErrorNode) : Node
