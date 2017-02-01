package org.softlang.megal.model

import org.antlr.v4.runtime.tree.ErrorNode
import org.json.JSONTokener
import org.softlang.megal.grammar.MegalBaseVisitor
import org.softlang.megal.grammar.MegalParser.*
import org.softlang.util.firstnn
import org.softlang.util.mapn
import org.softlang.util.mapvisit


/**
 * Visitor that performs transformation from parse context to declarations.
 */
val toDeclaration = object : MegalBaseVisitor<Declaration>() {
    /**
     * Obtains a module for a module context.
     */
    override fun visitModule(ctx: ModuleContext) = Module(
            ctx.ID().text,
            mapvisit(ctx.declaration()) + mapvisit(ctx.group()))

    /**
     * Obtains a module for a submodule context.
     */
    override fun visitSubmodule(ctx: SubmoduleContext) = Module(
            ctx.ID().text,
            mapvisit(ctx.declaration()) + mapvisit(ctx.group()))

    /**
     * Obtains a module for a group context.
     */
    override fun visitGroup(ctx: GroupContext) = Group(
            ctx.DOC().text,
            mapvisit(ctx.declaration())
    )

    /**
     * Obtains a statement for a statement context
     */
    override fun visitStatement(ctx: StatementContext) = Statement(
            ctx.ID()?.text,
            // Source and target in first two nodes
            toNode.visit(ctx.node(0)),
            toNode.visit(ctx.node(1)),
            // Targets in third and `cart` nodes
            listOf(toNode.visit(ctx.node(2))) + ctx.cart().map {
                toNode.visit(it.node())
            },
            // Binding in an optional node
            ctx.bind().mapn { toNode.visit(it) },
            // Continuations in respective nodes
            ctx.cont().map {
                Continuation(
                        toNode.visit(it.node(0)),
                        toNode.visit(it.node(1)))
            }
    )


    /**
     * Obtains an import for an import context.
     */
    override fun visitImports(ctx: ImportsContext) = Imports(
            ctx.ID().text,
            ctx.substitution().map {
                Substitution(it.ID(0).text, it.ID(1).text)
            })

    /**
     * Translate a parse error.
     */
    override fun visitErrorNode(node: ErrorNode) = DeclarationError(node)
}

/**
 * Visitor that performs transformation from parse context to nodes.
 */
val toNode = object : MegalBaseVisitor<Node>() {
    override fun visitPrimary(ctx: PrimaryContext) = Primary(
            ctx.abstr() != null,
            ctx.ID().text,
            // The first not null element will be mapped as argument
            firstnn(ctx.obj(), ctx.array(), ctx.tuple()).mapn {
                visit(it)
            }
    )

    /**
     * Obtains a node from a tuple.
     */
    override fun visitTuple(ctx: TupleContext) = Tuple(mapvisit(ctx.node()))

    /**
     * Obtains a node from an operation.
     */
    override fun visitOp(ctx: OpContext) = Op(ctx.text)

    /**
     * Obtains a node from any JSON data.
     */
    override fun visitJson(ctx: JsonContext) = Data(
            JSONTokener(ctx.text).nextValue())

    /**
     * Translate a parse error.
     */
    override fun visitErrorNode(node: ErrorNode) = NodeError(node)
}