package io.cloudflight.jems.server.project.service.application

import io.cloudflight.jems.server.common.validator.GeneralValidatorService
import java.time.LocalDate

data class ApplicationActionInfo(
    val note: String?,
    val date: LocalDate?
)

fun ApplicationActionInfo.ifIsValid(generalValidatorService: GeneralValidatorService) {
    generalValidatorService.throwIfAnyIsInvalid(
        generalValidatorService.maxLength(note, 10000, "note"),
        generalValidatorService.notNull(date, "decisionDate"),
        date?.let {
            generalValidatorService.dateNotInFuture(
                date,
                "decisionDate"
            )
        } ?: mapOf()
    )
}
