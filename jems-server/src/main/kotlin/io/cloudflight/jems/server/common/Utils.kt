package io.cloudflight.jems.server.common

import org.springframework.transaction.support.TransactionSynchronization
import org.springframework.transaction.support.TransactionSynchronizationManager

inline fun <T> T.afterCommit(crossinline block: T.() -> Unit): T {
    TransactionSynchronizationManager.registerSynchronization(
        object : TransactionSynchronization {
            override fun afterCommit() {
                block()
            }
        })
    return this
}
