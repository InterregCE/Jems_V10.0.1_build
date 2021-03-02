package io.cloudflight.jems.server.call.service

interface CallPersistence {
    fun hasAnyCallPublished(): Boolean
}
