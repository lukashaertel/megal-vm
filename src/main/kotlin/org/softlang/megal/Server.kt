package org.softlang.megal

import org.softlang.util.Many

/**
 * A server is a repository independent container maintaining services and
 * aggregating instances.
 */
class Server {
    var instances: List<Instance>
            by Many(Instance::server)


}