package org.softlang.megal

import org.softlang.util.*
import org.softlang.util.cli.*

/**
 * Arguments that are passed to the MegaL program CLI
 */
class ProgramArgs(args: Array<String>) : OptionParser(args,
        name = "MegaL CLI",
        description = """
        |MegaL commandline interface, used to analyze a file in
        |standalone or to run the MegaL server. All consecutive
        |strings will be processed in standalone mode.""".trimMargin(),
        example = """
        |megal -q "linst: elementOf" file1.megal file2.megal
        |megal -s -p 80 -r /srv/linarch""".trimMargin()) {

    val server by boolean("server") {
        shorthand = "s"
        default = false
        description = "If set, runs the MegaL server."
    }

    val root by string("root") {
        shorthand = "r"
        description = "Root directory to run the process in."
        default = "/srv/megal"
    }

    val port by int("port") {
        shorthand = "p"
        default = 3000
        description = "Which port should be bound by the MegaL server."
    }

    val queries by strings("queries") {
        shorthand = "q"
        default = listOf()
        description = "Queries that are to be executed in standalone mode."
    }

    val files by extra {
        description = "Resources that will be evaluated."
        default = listOf()
    }

    val options by unknown {
        description = "Unknown options configure the root bootstrapper."
    }

    val help by boolean("help") {
        shorthand = "h"
        default = false
        description = "Prints this help message."
    }
}