package org.softlang.megal

import org.softlang.util.Many
import org.softlang.util.One

/**
 * An instance on a server, instances are for projects or repositories and
 * aggregate VM relevant data and configuration.
 */
class Instance {
    var server: Server?
            by One(Server::instances)

    var roots: List<Root>
            by Many(Root::instance)
}
