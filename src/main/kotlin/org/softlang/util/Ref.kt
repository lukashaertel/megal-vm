package org.softlang.util

import kotlin.reflect.KMutableProperty1
import kotlin.reflect.KProperty

class Ref<S, P>(
        val to: KMutableProperty1<P, S?>,
        val listener: (P?, P?) -> Unit = { f, t -> }) {
    /**
     * Current value of the field
     */
    private var current: P? = null

    private var recurrent = false

    private var issuing = false

    operator fun getValue(site: S, property: KProperty<*>): P? {
        return current
    }

    operator fun setValue(site: S, property: KProperty<*>, new: P?) {
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

            // Reset old value
            if (old != null)
                to.set(old, null)

            // Set new value
            if (new != null)
                to.set(new, site)

            // Issue delta to listener
            try {
                issuing = true
                listener(old, new)
            } finally {
                issuing = false
            }

        } finally {
            recurrent = false
        }
    }
}