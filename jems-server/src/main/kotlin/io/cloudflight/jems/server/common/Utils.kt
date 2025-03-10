package io.cloudflight.jems.server.common

import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.server.common.file.service.model.JemsFile
import io.cloudflight.jems.server.common.file.service.model.JemsFileMetadata
import io.cloudflight.jems.server.project.service.lumpsum.model.CLOSURE_PERIOD_NUMBER
import io.cloudflight.jems.server.project.service.model.ProjectPeriod
import io.cloudflight.jems.server.project.service.model.ProjectPeriodBase
import org.springframework.transaction.support.TransactionSynchronization
import org.springframework.transaction.support.TransactionSynchronizationManager
import java.time.LocalDate


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

fun JemsFile.anonymize() {
    this.name = SENSITIVE_FILE_NAME_MAKS
    this.description = SENSITIVE_TRANSLATION_MAKS
}

fun ProjectPeriodBase.toLimits(startDate: LocalDate) = Pair(
    startDate.plusMonths(start.toLong() - 1),
    startDate.plusMonths(end.toLong()).minusDays(1)
)

fun Map<Int, ProjectPeriod>.toLimits(startDate: LocalDate): Map<Int, Pair<LocalDate, LocalDate>> {
    val lastNonClosurePeriod = this.minus(CLOSURE_PERIOD_NUMBER).maxByOrNull { it.key }!!.value

    val periodsWithClosure = if (CLOSURE_PERIOD_NUMBER !in keys) this else this.minus(CLOSURE_PERIOD_NUMBER)
        .plus(CLOSURE_PERIOD_NUMBER to ProjectPeriod(
            number = CLOSURE_PERIOD_NUMBER,
            start = lastNonClosurePeriod.end.plus(1),
            end = lastNonClosurePeriod.end.plus(1),
        ))
    return periodsWithClosure.mapValues { it.value.toLimits(startDate) }
}

