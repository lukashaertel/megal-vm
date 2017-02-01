package org.softlang.megal.grammar

import org.softlang.megal.model.toDeclaration

fun main(args: Array<String>) {
    // Parse test file
    val module = readModule("src/test/resources/Values.megal")

    // Translate and process
    toDeclaration.visit(module).apply {
        print(this)
    }
}