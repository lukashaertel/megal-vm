package org.softlang.megal

import org.wasabifx.wasabi.app.AppConfiguration
import org.wasabifx.wasabi.app.AppServer

/**
 * Main program entry point
 * @param args The arguments passed to the program
 */
fun main(args: Array<String>) {
    // Parse program arguments
    ProgramArgs(args).apply {
        // Print help message
        if (help)
            printHelp(System.out)

        // Run the appropriate program
        if (server) {
            // Run server on args
            runServer(this)
        } else {
            // Print help if no files specified, otherwise run standalone
            if (files.isEmpty() && !help)
                printHelp(System.out)
            else
                runStandalone(this)
        }
    }
}

/**
 * Runs the server with the given params
 * @param programArgs The program arguments
 */
fun runServer(programArgs: ProgramArgs) {
    AppServer(AppConfiguration(port = programArgs.port)).apply {
        get("/", {
            response.send("Top kek")
        })
    }.start(wait = true)
}

/**
 * Processes in standalone mode with the given params
 * @param programArgs The program arguments
 */
fun runStandalone(programArgs: ProgramArgs) {

}