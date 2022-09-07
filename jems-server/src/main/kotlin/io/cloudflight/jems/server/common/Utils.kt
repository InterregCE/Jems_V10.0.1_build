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

fun getCountryCodeForCountry(country: String) =
    Regex("\\(([A-Z]{2})\\)$").find(country)?.value?.substring(1, 3) ?: ""


fun getNuts3CodeForNuts3Region(nuts3Region: String): String =
    Regex("\\(([A-Z0-9]{5})\\)\$").find(nuts3Region)?.value?.substring(1, 6) ?: ""
