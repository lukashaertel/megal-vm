package org.softlang.megal

import org.softlang.util.One

/**
 * A root module, represents an individual verifiable module, it aggregates
 * local configurations and maintains state of transient objects.
 */
class Root {
    var instance: Instance?
            by One(Instance::roots)
}