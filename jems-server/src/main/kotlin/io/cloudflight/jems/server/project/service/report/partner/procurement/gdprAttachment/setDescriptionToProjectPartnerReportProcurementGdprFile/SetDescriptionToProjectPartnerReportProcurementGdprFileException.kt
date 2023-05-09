package io.cloudflight.jems.server.project.service.report.partner.procurement.gdprAttachment.setDescriptionToProjectPartnerReportProcurementGdprFile

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationAccessDeniedException
import io.cloudflight.jems.server.common.exception.ApplicationException
import io.cloudflight.jems.server.common.exception.ApplicationNotFoundException

private const val SET_DESCRIPTION_TO_PROJECT_PARTNER_REPORT_GDPR_FILE_ERROR_CODE_PREFIX = "S-SDTPPRGF"
private const val SET_DESCRIPTION_TO_PROJECT_PARTNER_REPORT_GDPR_FILE_ERROR_KEY_PREFIX = "use.case.set.description.to.project.partner.report.gdpr.file"

class SetDescriptionToProjectPartnerReportFileException(cause: Throwable) : ApplicationException(
    code = SET_DESCRIPTION_TO_PROJECT_PARTNER_REPORT_GDPR_FILE_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$SET_DESCRIPTION_TO_PROJECT_PARTNER_REPORT_GDPR_FILE_ERROR_KEY_PREFIX.failed"),
    cause = cause
)

class FileNotFound : ApplicationNotFoundException(
    code = "$SET_DESCRIPTION_TO_PROJECT_PARTNER_REPORT_GDPR_FILE_ERROR_CODE_PREFIX-001",
    i18nMessage = I18nMessage("$SET_DESCRIPTION_TO_PROJECT_PARTNER_REPORT_GDPR_FILE_ERROR_KEY_PREFIX.not.found"),
)


class SensitiveFileException: ApplicationAccessDeniedException(
    code = "$SET_DESCRIPTION_TO_PROJECT_PARTNER_REPORT_GDPR_FILE_ERROR_CODE_PREFIX-002",
    i18nMessage = I18nMessage("$SET_DESCRIPTION_TO_PROJECT_PARTNER_REPORT_GDPR_FILE_ERROR_KEY_PREFIX.sensitive.failed"),
)
