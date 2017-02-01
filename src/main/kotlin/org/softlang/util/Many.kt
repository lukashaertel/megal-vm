package org.softlang.util

import kotlin.reflect.KMutableProperty1
import kotlin.reflect.KProperty

class Many<S, P>(
        val to: KMutableProperty1<P, S?>,
        val listener: (List<P>, List<P>) -> Unit = { f, t -> }) {
    /**
     * Current value of the field
     */
    private var current: List<P> = emptyList()

    private var recurrent = false

    private var issuing = false

    operator fun getValue(site: S, property: KProperty<*>): List<P> {
        return current
    }


    operator fun setValue(site: S, property: KProperty<*>, new: List<P>) {
        if (issuing)
            throw IllegalAccessException("Change to value while issuing delta.")

        if (recurrent)
            return

        try {
            recurrent = true

            // Store current value and stop if no real change
            val old = current
            if (old == new)
                return

            // Transfer new value
            current = new

            val minus = old - new
            val plus = new - old

            for (x in minus)
                to.set(x, null)

            for (x in plus)
                to.set(x, site)


            // Issue delta to listener
            try {
                issuing = true
                listener(minus, plus)
            } finally {
                issuing = false
            }

        } finally {
            recurrent = false
        }
    }
}