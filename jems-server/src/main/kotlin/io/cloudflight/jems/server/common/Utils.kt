package io.cloudflight.jems.server.common

import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.server.common.file.service.model.JemsFileMetadata

import org.springframework.transaction.support.TransactionSynchronization
import org.springframework.transaction.support.TransactionSynchronizationManager


const val SENSITIVE_TRANSLATION_MAKS = "************"
const val SENSITIVE_FILE_NAME_MAKS = "*********.***"

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

fun Set<InputTranslation>.anonymize() = map {
    InputTranslation(language = it.language, translation = SENSITIVE_TRANSLATION_MAKS)
}.toSet()

fun JemsFileMetadata.anonymize() {
    this.name = SENSITIVE_FILE_NAME_MAKS
}
