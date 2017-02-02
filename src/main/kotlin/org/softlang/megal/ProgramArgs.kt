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
        |strings will be proessed in standalone mode.""".trimMargin()) {

    val server by boolean("server") {
        shorthand = "s"
        default = false
        description = "If set, runs the MegaL server."
    }

    val root by string("root") {
        shorthand = "r"
        description = "Root directory to run the process in."
    }

    val port by int("port") {
        shorthand = "p"
        default = 3000
        description = "Which port should be bound by the MegaL server."
    }

    val files by extra {
        description = "Resources that will be evaluated."
        default = listOf()
    }

    val options by unknown {
        description = "Unknown options will be used to configure the root " +
                "model's bootstrapper."
    }
}