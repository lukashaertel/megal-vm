package org.softlang.util

/**
 * Mapper of object types to indices, used in algorithms that are index based
 */
data class Mapper<T>(val forward: Map<T, Int>, val backward: Map<Int, T>) {
    /**
     * Number of mapped objects.
     */
    val size get() = forward.size

    operator fun get(item: T) = forward.getValue(item)

    operator fun get(item: Int) = backward.getValue(item)
}

/**
 * A point in a matrix.
 */
data class Point(val column: Int, val row: Int)

/**
 * An entry in a matrix.
 */
data class Entry(val column: Int, val row: Int, val value: Int)

/**
 * A matrix storing integer values.
 */
data class IntMatrix(val columns: Int, val backing: IntArray) {
    /**
     * Number of rows in this matrix.
     */
    val rows get() = backing.size / columns

    /**
     * Gets the entry at that position.
     */
    operator fun get(column: Int, row: Int) =
            backing[column + row * columns]

    /**
     * Gets the entry at that position.
     */
    operator fun get(point: Point) =
            backing[point.column + point.row * columns]

    /**
     * Gets the entry at that position.
     */
    operator fun set(column: Int, row: Int, value: Int) {
        backing[column + row * columns] = value
    }

    /**
     * Gets the entry at that position.
     */
    operator fun set(point: Point, value: Int) {
        backing[point.column + point.row * columns] = value
    }

    /**
     * Gets all entries
     */
    val entries get() = backing
            .withIndex()
            .map {
                Entry(it.index % columns, it.index / columns, it.value)
            }

    override fun hashCode() =
            columns + 13 * backing.contentHashCode()

    override fun equals(other: Any?) =
            other is IntMatrix
                    && columns == other.columns
                    && backing.contentEquals(other.backing)
}

/**
 * Creates a symmetric [IntMatrix].
 */
fun intMatrix(dim: Int) =
        IntMatrix(dim, IntArray(dim * dim))

/**
 * Creates an [IntMatrix] of the given size.
 */
fun intMatrix(columns: Int, rows: Int) =
        IntMatrix(columns, IntArray(columns * rows))

/**
 * Creates a symmetric [IntMatrix], sets entries using an initializer.
 */
fun intMatrix(dim: Int, initializer: (Point) -> Int) =
        IntMatrix(dim, IntArray(dim * dim, { i ->
            initializer(Point(i % dim, i / dim))
        }))

/**
 * Creates an [IntMatrix] of the given size, sets entries using an initializer.
 */
fun intMatrix(columns: Int, rows: Int, initializer: (Point) -> Int) =
        IntMatrix(columns, IntArray(columns * rows, { i ->
            initializer(Point(i % columns, i / columns))
        }))


/**
 * Creates a mapper for the given elements.
 */
fun <T> mapper(items: Iterable<T>): Mapper<T> {
    // Create map builders
    val forward = mutableMapOf<T, Int>()
    val backward = mutableMapOf<Int, T>()

    // Associate index-wise
    items.withIndex().forEach {
        forward += it.value to it.index
        backward += it.index to it.value
    }

    // Return mapper on unmodifiable maps
    return Mapper(forward.toMap(), backward.toMap())
}

/**
 * Floyd-Warshall All-Shortest-Paths
 */
fun <T> fwasp(graph: Map<T, Set<T>>): Map<Pair<T, T>, List<T>> {
    // Obtain all nodes
    val nodes = graph.values.fold(graph.keys) { a, b ->
        a union b
    }

    // Use mapper to utilize int arrays
    return mapper(nodes).let { m ->
        // Pseudo-infinity to prevent integer underflows
        val INFINITY = m.size * 2

        // Initialize distance and next matrix
        val dist = intMatrix(m.size) { INFINITY }
        val next = intMatrix(m.size) { -1 }

        // Copy graph into matrix
        for ((n, es) in graph) {
            val mn = m[n]
            for (e in es) {
                val me = m[e]
                dist[mn, me] = 1
                next[mn, me] = me
            }
        }

        // Perform standard Floyd-Warshall
        for (k in 0 until m.size)
            for (i in 0 until m.size)
                for (j in 0 until m.size) {
                    // Get previous distance metrics
                    val ij = dist[i, j]
                    val ik = dist[i, k]
                    val kj = dist[k, j]
                    // Compare and update appropriately
                    if (ij > ik + kj) {
                        dist[i, j] = ik + kj
                        next[i, j] = next[i, k]
                    }
                }

        // For all reachable pairs
        dist.entries.filter {
            it.value != INFINITY
        }.associate {
            // Extract keys as they are used often
            val u = it.column
            val v = it.row

            // Generate path from u by Floyd-Warshall path reconstruction
            (m[u] to m[v]) to generateSequence(u) {
                if (it == v) null else next[it, v].toNull()
            }.map(m::get).toList()
        }
    }
}

/**
 * Calculates all shortest paths for graphs defined by [I] initial nodes, [E]
 * edges and actual node types [N].
 */
fun <I, E, N> fwasp(
        initial: Set<I>,
        edges: Set<E>,
        nodeOf: (I) -> N,
        sourceOf: (E) -> N,
        targetOf: (E) -> N):
        Map<Pair<I, N>, List<E>> {
    // Mapping inversion for nodes and edges
    val unmapInitial = initial.associateBy { nodeOf(it) }
    val unmapEdge = edges.associateBy { sourceOf(it) to targetOf(it) }

    val nodes = initial.map(nodeOf) union
            edges.map(sourceOf) union
            edges.map(targetOf)

    // Apply Floyd-Warshall on mapped graph
    return fwasp(nodes.associate { n ->
        // From node via all edges starting at that node
        n to edges
                .filter { e -> n == sourceOf(e) }
                .map(targetOf)
                .toSet()
    }).filterKeys {
        // Some transitions have no satisfied initial nodes, skip those
        unmapInitial.containsKey(it.first)
    }.mapKeys {
        // Unmap key origin and target
        unmapInitial.getValue(it.key.first) to it.key.second
    }.mapValues {
        // Unmap value transitions
        it.value.pairs.map { unmapEdge.getValue(it) }
    }
}

fun main(args: Array<String>) {
    val graph = mapOf(
            "a" to setOf("b", "f", "d"),
            "b" to setOf("c"),
            "c" to setOf("d"),
            "d" to setOf("f")
    )

    val apsp = fwasp(graph)
    print(apsp)
}