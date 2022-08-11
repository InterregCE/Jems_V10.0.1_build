package io.cloudflight.jems.server.project.service.contracting.reporting.updateContractingReporting

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException
import io.cloudflight.jems.server.common.exception.ApplicationNotFoundException
import io.cloudflight.jems.server.common.exception.ApplicationUnprocessableException
import io.cloudflight.jems.server.programme.service.checklist.create.CREATE_PROGRAMME_CHECKLIST_ERROR_CODE_PREFIX
import io.cloudflight.jems.server.project.service.contracting.model.reporting.ProjectContractingReportingSchedule
import java.time.LocalDate

private const val UPDATE_CONTRACTING_REPORTING_ERROR_CODE_PREFIX = "S-UPCR"
private const val UPDATE_CONTRACTING_REPORTING_ERROR_KEY_PREFIX = "use.case.update.project.contracting.reporting"

class UpdateContractingReportingException(cause: Throwable) : ApplicationException(
    code = UPDATE_CONTRACTING_REPORTING_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$UPDATE_CONTRACTING_REPORTING_ERROR_KEY_PREFIX.failed"),
    cause = cause,
)

class ProjectHasNotBeenApprovedYet : ApplicationNotFoundException(
    code = "$UPDATE_CONTRACTING_REPORTING_ERROR_CODE_PREFIX-001",
    i18nMessage = I18nMessage("$UPDATE_CONTRACTING_REPORTING_ERROR_KEY_PREFIX.project.is.not.contracted"),
)

class ContractingStartDateIsMissing : ApplicationUnprocessableException(
    code = "$UPDATE_CONTRACTING_REPORTING_ERROR_CODE_PREFIX-002",
    i18nMessage = I18nMessage("$UPDATE_CONTRACTING_REPORTING_ERROR_KEY_PREFIX.startDate.is.missing"),
    message = "We cannot validate deadlines unless we have project start date and period length.",
)

class MaxAmountOfDeadlinesReached(maxAmount: Int) : ApplicationUnprocessableException(
    code = "$UPDATE_CONTRACTING_REPORTING_ERROR_CODE_PREFIX-003",
    i18nMessage = I18nMessage(
        "$UPDATE_CONTRACTING_REPORTING_ERROR_KEY_PREFIX.max.amount.reached",
        i18nArguments = mapOf(
            "maxSize" to maxAmount.toString()
        )
    )
)

class InvalidPeriodNumbers(invalidPeriodNumbers: Collection<Int>) : ApplicationUnprocessableException(
    code = "$UPDATE_CONTRACTING_REPORTING_ERROR_CODE_PREFIX-004",
    i18nMessage = I18nMessage("$UPDATE_CONTRACTING_REPORTING_ERROR_KEY_PREFIX.invalid.periodNumber"),
    message = "Provided period numbers are invalid for this project $invalidPeriodNumbers",
)

class DeadlinesDoNotFitPeriod(
    invalidDeadlineDates: Collection<Triple<ProjectContractingReportingSchedule, LocalDate, LocalDate>>,
) : ApplicationUnprocessableException(
    code = "$UPDATE_CONTRACTING_REPORTING_ERROR_CODE_PREFIX-005",
    i18nMessage = I18nMessage("$UPDATE_CONTRACTING_REPORTING_ERROR_KEY_PREFIX.invalid.periodNumber"),
    formErrors = invalidDeadlineDates.map {
        "${it.first.date} does not fit period ${it.first.periodNumber}" to
            I18nMessage("wrong.date", i18nArguments = mapOf("start" to "${it.second}", "end" to "${it.third}"))
    }.toMap(),
    message = "Following dates are invalid: " +
        invalidDeadlineDates.joinToString(", ") {
            "${it.first.date} does not fit into period ${it.first.periodNumber} (${it.second} - ${it.third})"
        },
)
