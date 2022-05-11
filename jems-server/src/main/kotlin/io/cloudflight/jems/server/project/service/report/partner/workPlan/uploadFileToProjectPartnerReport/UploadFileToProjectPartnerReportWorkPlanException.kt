package io.cloudflight.jems.server.project.service.report.partner.workPlan.uploadFileToProjectPartnerReport

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException
import io.cloudflight.jems.server.common.exception.ApplicationNotFoundException
import io.cloudflight.jems.server.common.exception.ApplicationUnprocessableException

private const val UPLOAD_FILE_TO_PROJECT_PARTNER_REPORT_WP_ERROR_CODE_PREFIX = "S-UFTPPRWP"
private const val UPLOAD_FILE_TO_PROJECT_PARTNER_REPORT_WP_ERROR_KEY_PREFIX = "use.case.upload.file.to.project.partner.report.work.plan"

class UploadFileToProjectPartnerReportWorkPlanException(cause: Throwable) : ApplicationException(
    code = UPLOAD_FILE_TO_PROJECT_PARTNER_REPORT_WP_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$UPLOAD_FILE_TO_PROJECT_PARTNER_REPORT_WP_ERROR_KEY_PREFIX.failed"),
    cause = cause
)

class ActivityNotFoundException(activityId: Long) : ApplicationNotFoundException(
    code = "$UPLOAD_FILE_TO_PROJECT_PARTNER_REPORT_WP_ERROR_CODE_PREFIX-001",
    i18nMessage = I18nMessage(
        i18nKey = "$UPLOAD_FILE_TO_PROJECT_PARTNER_REPORT_WP_ERROR_KEY_PREFIX.activity.not.found",
        i18nArguments = mapOf("activityId" to activityId.toString())
    ),
)

class DeliverableNotFoundException(deliverableId: Long) : ApplicationNotFoundException(
    code = "$UPLOAD_FILE_TO_PROJECT_PARTNER_REPORT_WP_ERROR_CODE_PREFIX-002",
    i18nMessage = I18nMessage(
        i18nKey = "$UPLOAD_FILE_TO_PROJECT_PARTNER_REPORT_WP_ERROR_KEY_PREFIX.deliverable.not.found",
        i18nArguments = mapOf("deliverableId" to deliverableId.toString())
    ),
)

class OutputNotFoundException(outputId: Long) : ApplicationNotFoundException(
    code = "$UPLOAD_FILE_TO_PROJECT_PARTNER_REPORT_WP_ERROR_CODE_PREFIX-003",
    i18nMessage = I18nMessage(
        i18nKey = "$UPLOAD_FILE_TO_PROJECT_PARTNER_REPORT_WP_ERROR_KEY_PREFIX.output.not.found",
        i18nArguments = mapOf("outputId" to outputId.toString())
    ),
)

class FileTypeNotSupported : ApplicationUnprocessableException(
    code = "$UPLOAD_FILE_TO_PROJECT_PARTNER_REPORT_WP_ERROR_CODE_PREFIX-004",
    i18nMessage = I18nMessage("$UPLOAD_FILE_TO_PROJECT_PARTNER_REPORT_WP_ERROR_CODE_PREFIX.type.not.supported")
)
