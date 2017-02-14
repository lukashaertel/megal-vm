package org.softlang.megal.grammar

import org.junit.Test
import org.junit.Assert.*
import org.softlang.megal.grammar.MegalParser.*
import org.softlang.megal.model.toDeclaration
import java.net.URI

/**
 * Checks if examples are covered and parsed correctly.
 */
class ExampleCoverageTest {

    /**
     * Loads one of the example modules in the directory, captures parser errors
     * as a JUnit failure
     * @param block The block to execute with the loaded module
     */
    private fun String.loadInto(block: ModuleContext.() -> Unit) {
        // Capture standard error stream
        val error = captureStandardError {
            readModule("src/test/resources/$this.megal").apply(block)
        }

        // Print any resulting error messages not covered by exceptions
        if (error.isNotEmpty())
            fail("Problems while parsing sample $this: $error")
    }

    @Test
    fun captureWorks() {
        // Test if the capture facility works
        assertEquals("failed", captureStandardError({
            System.err.print("failed")
        }, false))
    }

    @Test
    fun coversDoc() {
        // This example covers edges and continued edges
        "Doc".loadInto {
            declaration(0).statement().apply {
                listOf("A", "b", "C") in node()
            }

            group(0).apply {
                declaration(0).statement().apply {
                    listOf("C", "b", "A") in node()
                }

                "/*\n * Main documentation\n */" in DOC()

                declaration(1).submodule().apply {
                    declaration(0).statement().apply {
                        listOf("A", "b", "C") in node()
                    }

                    group(0).apply {
                        "/*\n     * Submodule documentation\n     */" in DOC()
                        declaration(0).statement().apply {
                            listOf("C", "b", "A") in node()
                        }
                    }
                }
            }
        }
    }

    @Test
    fun coversEdges() {
        // This example covers edges and continued edges
        "Edges".loadInto {
            declaration(0).statement().apply {
                listOf("A", "b", "C") in node()
            }

            declaration(1).statement().apply {
                listOf("C", "b", "A") in node()
            }

            declaration(2).statement().apply {
                listOf("X", "b", "A") in node()

                cont(0).apply {
                    listOf("b", "C") in node()
                }
                cont(1).apply {
                    listOf("b", "D") in node()
                }
            }
        }
    }


    @Test
    fun coversImports() {
        // This example covers imports and substitutions
        "Imports".loadInto {
            declaration(0).imports().apply {
                "A" in ID()
            }

            declaration(1).imports().apply {
                "B" in ID()

                substitution(0).apply {
                    "x" in ID(0)
                    "a" in ID(1)
                }

                substitution(1).apply {
                    "y" in ID(0)
                    "b" in ID(1)
                }
            }
        }
    }


    @Test
    fun coversLiterals() {
        // This example covers literals and nested URLs
        "Literals".loadInto {
            declaration(0).statement().apply {
                node(2).literal().apply {
                    assertEquals(
                            parseLiteral(LITERAL().text),
                            listOf(URI.create("http://google.com")))
                }
            }

            declaration(1).statement().apply {
                node(2).literal().apply {
                    assertEquals(
                            parseLiteral(LITERAL().text),
                            listOf(URI.create("file://file.txt"),
                                    URI.create("select://lines?from=1&to=10"),
                                    URI.create("json://name")))
                }
            }
        }
    }

    @Test
    fun coversModules() {
        // This example covers modules and submodules
        "Modules".loadInto {
            "Main" in ID()

            declaration(0).submodule().apply {
                "State1" in ID()
            }

            declaration(1).submodule().apply {
                "State2" in ID()

                declaration(0).submodule().apply {
                    "Substate1" in ID()
                }
            }
        }
    }


    @Test
    fun coversPrefixes() {
        // This example covers edge prefixes like labels and slots
        "Prefixes".loadInto {
            declaration(0).statement().apply {
                ID().apply {
                    "s" in ID()
                }

                listOf("A", "b", "C") in node()
            }
        }
    }

    @Test
    fun coversRelationships() {
        // This example covers edge prefixes like labels and slots
        "Relationships".loadInto {
            declaration(0).statement().apply {
                listOf("r", "<", "X") in node()

                cart(0).apply {
                    "Y" in node()
                }
            }

            declaration(1).statement().apply {
                listOf("f", "<", "A") in node()

                cart(0).apply {
                    "B" in node()
                }
                cart(1).apply {
                    "C" in node()
                }
            }
        }
    }

    @Test
    fun coversSubmoduleEdges() {
        // This example covers assignment of edges based on indentation
        "SubmoduleEdges".loadInto {
            declaration(0).submodule().apply {
                declaration(0).statement().apply {
                    listOf("A", "b", "C") in node()
                }

                declaration(1).submodule().apply {
                    declaration(0).statement().apply {
                        listOf("C", "b", "A") in node()
                    }
                }
            }
        }
    }

    @Test
    fun coversValues() {
        // This example covers nodes
        "Values".loadInto {
            declaration(0).statement().apply {
                node(2).json().value().array().apply {
                    "\"string\"" in value(0)

                    "42" in value(1)

                    value(2).obj().pair(0).apply {
                        "\"x\"" in STRING()
                        value().apply {
                            "1" in NUMBER()
                        }
                    }

                    "true" in value(3)

                    "false" in value(4)

                    "null" in value(5)
                }

                cont(0).node(1).primary().apply {
                    "node" in ID()

                    tuple().apply {
                        "a" in node(0)

                        "1" in node(1)

                        node(2).primary().apply {
                            "b" in ID()

                            tuple().apply {
                                listOf("c", "d") in node()
                            }
                        }
                    }
                }

                cont(1).node(1).tuple().apply {
                    "1" in node(0)

                    "3" in node(1)

                    "a" in node(2)
                }
            }
        }
    }

    @Test
    fun coversExpressiveness() {
        // This example covers linguistic architecture expressiveness
        "Expressiveness".loadInto {
            toDeclaration.visit(this).apply {
                print(this)
            }
        }
    }
}