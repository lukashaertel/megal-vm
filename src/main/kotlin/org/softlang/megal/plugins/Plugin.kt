package org.softlang.megal.plugins

import org.softlang.megal.model.Declaration
import org.softlang.megal.model.Node
import org.softlang.megal.structs.Location
import org.softlang.megal.structs.Result

/**
 * Base interface for all configurable plugins.
 */
interface Plugin

/**
 * Navigates in hierarchical bindings.
 */
interface NavPlugin : Plugin {
    /**
     * Initializes navigation based on protocol.
     */
    fun initialize(protocol: String): Set<Any>

    /**
     * Navigates the locations, plugins will be composed and this result will
     * be fed back into the process.
     */
    fun navigate(location: Location): Location?
}

/**
 * Performs model inference.
 */
interface InferencePlugin : Plugin {
    fun infer(declaration: Declaration): Declaration
}