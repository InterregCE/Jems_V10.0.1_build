package io.cloudflight.jems.server.project.service.report.project.workPlan.updateProjectReportWorkPlan

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException
import io.cloudflight.jems.server.common.exception.ApplicationNotFoundException
import io.cloudflight.jems.server.common.exception.ApplicationUnprocessableException

private const val ERROR_CODE_PREFIX = "S-UPRWP"
private const val ERROR_KEY_PREFIX = "use.case.update.project.report.work.plan"

class UpdateProjectReportWorkPlanException(cause: Throwable) : ApplicationException(
    code = ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$ERROR_KEY_PREFIX.failed"),
    cause = cause
)

class ReportAlreadyClosed : ApplicationUnprocessableException(
    code = "$ERROR_CODE_PREFIX-001",
    i18nMessage = I18nMessage(i18nKey = "$ERROR_KEY_PREFIX.report.already.closed"),
)

class WorkPackageNotFoundException(workPackageId: Long) : ApplicationNotFoundException(
    code = "$ERROR_CODE_PREFIX-002",
    i18nMessage = I18nMessage(
        i18nKey = "$ERROR_KEY_PREFIX.wp.not.found",
        i18nArguments = mapOf("workPackageId" to workPackageId.toString())
    ),
)

class WorkPackageActivityNotFoundException(activityId: Long) : ApplicationNotFoundException(
    code = "$ERROR_CODE_PREFIX-003",
    i18nMessage = I18nMessage(
        i18nKey = "$ERROR_KEY_PREFIX.activity.not.found.on.wp",
        i18nArguments = mapOf("activityId" to activityId.toString())
    ),
)

class WorkPackageActivityDeliverableNotFoundException(deliverableId: Long) : ApplicationNotFoundException(
    code = "$ERROR_CODE_PREFIX-004",
    i18nMessage = I18nMessage(
        i18nKey = "$ERROR_KEY_PREFIX.deliverable.not.found.on.activity.on.wp",
        i18nArguments = mapOf("deliverableId" to deliverableId.toString())
    ),
)

class WorkPackageOutputNotFoundException(outputId: Long) : ApplicationNotFoundException(
    code = "$ERROR_CODE_PREFIX-005",
    i18nMessage = I18nMessage(
        i18nKey = "$ERROR_KEY_PREFIX.outputId.not.found.on.wp",
        i18nArguments = mapOf("outputId" to outputId.toString())
    ),
)

class WorkPackageInvestmentNotFoundException(investmentId: Long) : ApplicationNotFoundException(
    code = "$ERROR_CODE_PREFIX-006",
    i18nMessage = I18nMessage(
        i18nKey = "$ERROR_KEY_PREFIX.investmentId.not.found.on.wp",
        i18nArguments = mapOf("investmentId" to investmentId.toString())
    ),
)
