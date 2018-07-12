package uk.ac.ncl.openlab.irismsg.common

import javax.inject.Inject
import javax.inject.Singleton

/**
 * A simple global event bus for objects to pass messages arbitrary to each other
 *
 * - @Inject it onto your objects and subscribe to events with EventBus#on
 * - Send events to any listeners with EventBus#emit
 * - Remember to clear listeners when things get unallocated e.g. Fragment#onStop
 */
@Singleton
class EventBus @Inject constructor () {
    
    private val eventListeners: MutableMap<String, List<Listener>> = mutableMapOf()
    
    /** Subscribe to a specific event with a listener */
    fun on (event: String, listener: Listener) {
        eventListeners[event] = (eventListeners[event] ?: listOf())
                .toMutableList()
                .apply { add(listener) }
    }
    
    /** Unsubscribe a specific listener */
    fun off (event: String, listener: Listener) {
        eventListeners[event] = (eventListeners[event] ?: listOf())
                .filter { it !== listener }
    }
    
    /** Emit an event to the bus, notifying listeners */
    fun emit (event: String, payload: Any? = null) {
        eventListeners[event]?.forEach { it.handler(payload) }
    }
    
    /** Clear listeners for a specific event (or all events if null) */
    fun clear (event: String?) {
        when (event) {
            null -> eventListeners.forEach { eventListeners[it.key] = listOf() }
            else -> eventListeners[event] = listOf()
        }
    }
    
    /** A listener to a specific event with an optional payload */
    data class Listener (val handler: (payload: Any?) -> Unit)
}