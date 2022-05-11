package io.cloudflight.jems.server.project.service.report.partner.contribution.uploadFileToProjectPartnerReportContribution

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException
import io.cloudflight.jems.server.common.exception.ApplicationNotFoundException
import io.cloudflight.jems.server.common.exception.ApplicationUnprocessableException

private const val UPLOAD_FILE_TO_PROJECT_PARTNER_REPORT_CONTRIBUTION_ERROR_CODE_PREFIX = "S-UPPRC"
private const val UPLOAD_FILE_TO_PROJECT_PARTNER_REPORT_CONTRIBUTION_ERROR_KEY_PREFIX = "use.case.upload.file.to.project.partner.report.contribution"

class UploadFileToProjectPartnerReportContributionException(cause: Throwable) : ApplicationException(
    code = UPLOAD_FILE_TO_PROJECT_PARTNER_REPORT_CONTRIBUTION_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$UPLOAD_FILE_TO_PROJECT_PARTNER_REPORT_CONTRIBUTION_ERROR_KEY_PREFIX.failed"),
    cause = cause
)

class ContributionNotFoundException(contributionId: Long) : ApplicationNotFoundException(
    code = "$UPLOAD_FILE_TO_PROJECT_PARTNER_REPORT_CONTRIBUTION_ERROR_CODE_PREFIX-001",
    i18nMessage = I18nMessage(
        "$UPLOAD_FILE_TO_PROJECT_PARTNER_REPORT_CONTRIBUTION_ERROR_KEY_PREFIX.not.found",
        i18nArguments = mapOf("contributionId" to contributionId.toString())
    ),
)

class FileTypeNotSupported : ApplicationUnprocessableException(
    code = "$UPLOAD_FILE_TO_PROJECT_PARTNER_REPORT_CONTRIBUTION_ERROR_CODE_PREFIX-002",
    i18nMessage = I18nMessage("$UPLOAD_FILE_TO_PROJECT_PARTNER_REPORT_CONTRIBUTION_ERROR_KEY_PREFIX.type.not.supported")
)
