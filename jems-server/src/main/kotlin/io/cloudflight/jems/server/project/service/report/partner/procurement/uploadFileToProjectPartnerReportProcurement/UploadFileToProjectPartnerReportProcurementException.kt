package io.cloudflight.jems.server.project.service.report.partner.procurement.uploadFileToProjectPartnerReportProcurement

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException
import io.cloudflight.jems.server.common.exception.ApplicationNotFoundException

private const val UPLOAD_FILE_TO_PROJECT_PARTNER_REPORT_PROCUREMENT_ERROR_CODE_PREFIX = "S-UFTPPRP"
private const val UPLOAD_FILE_TO_PROJECT_PARTNER_REPORT_PROCUREMENT_ERROR_KEY_PREFIX = "use.case.upload.file.to.project.partner.report.procurement"

class UploadFileToProjectPartnerReportProcurementException(cause: Throwable) : ApplicationException(
    code = UPLOAD_FILE_TO_PROJECT_PARTNER_REPORT_PROCUREMENT_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$UPLOAD_FILE_TO_PROJECT_PARTNER_REPORT_PROCUREMENT_ERROR_KEY_PREFIX.failed"),
    cause = cause
)

class ProcurementNotFoundException(procurementId: Long) : ApplicationNotFoundException(
    code = "$UPLOAD_FILE_TO_PROJECT_PARTNER_REPORT_PROCUREMENT_ERROR_CODE_PREFIX-001",
    i18nMessage = I18nMessage(
        i18nKey = "$UPLOAD_FILE_TO_PROJECT_PARTNER_REPORT_PROCUREMENT_ERROR_KEY_PREFIX.procurement.not.found",
        i18nArguments = mapOf("procurementId" to procurementId.toString())
    ),
)
