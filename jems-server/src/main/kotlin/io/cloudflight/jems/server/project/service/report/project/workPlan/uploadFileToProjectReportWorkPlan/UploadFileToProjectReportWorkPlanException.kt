package io.cloudflight.jems.server.project.service.report.project.workPlan.uploadFileToProjectReportWorkPlan

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException
import io.cloudflight.jems.server.common.exception.ApplicationNotFoundException
import io.cloudflight.jems.server.common.exception.ApplicationUnprocessableException

private const val ERROR_CODE_PREFIX = "S-UFTPRWP"
private const val ERROR_KEY_PREFIX = "use.case.upload.file.to.project.report.work.plan"

class UploadFileToProjectReportWorkPlanException(cause: Throwable) : ApplicationException(
    code = ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$ERROR_KEY_PREFIX.failed"),
    cause = cause,
)

class ActivityNotFoundException(activityId: Long) : ApplicationNotFoundException(
    code = "$ERROR_CODE_PREFIX-001",
    i18nMessage = I18nMessage(
        i18nKey = "$ERROR_KEY_PREFIX.activity.not.found",
        i18nArguments = mapOf("activityId" to activityId.toString())
    ),
)

class DeliverableNotFoundException(deliverableId: Long) : ApplicationNotFoundException(
    code = "$ERROR_CODE_PREFIX-002",
    i18nMessage = I18nMessage(
        i18nKey = "$ERROR_KEY_PREFIX.deliverable.not.found",
        i18nArguments = mapOf("deliverableId" to deliverableId.toString())
    ),
)

class OutputNotFoundException(outputId: Long) : ApplicationNotFoundException(
    code = "$ERROR_CODE_PREFIX-003",
    i18nMessage = I18nMessage(
        i18nKey = "$ERROR_KEY_PREFIX.output.not.found",
        i18nArguments = mapOf("outputId" to outputId.toString())
    ),
)

class FileTypeNotSupported : ApplicationUnprocessableException(
    code = "$ERROR_CODE_PREFIX-004",
    i18nMessage = I18nMessage("$ERROR_KEY_PREFIX.type.not.supported"),
)
