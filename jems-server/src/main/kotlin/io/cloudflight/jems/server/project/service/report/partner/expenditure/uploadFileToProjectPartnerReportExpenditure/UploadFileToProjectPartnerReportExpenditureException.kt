package io.cloudflight.jems.server.project.service.report.partner.expenditure.uploadFileToProjectPartnerReportExpenditure

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException
import io.cloudflight.jems.server.common.exception.ApplicationNotFoundException
import io.cloudflight.jems.server.common.exception.ApplicationUnprocessableException

private const val UPLOAD_FILE_TO_PROJECT_PARTNER_REPORT_EXPENDITURE_ERROR_CODE_PREFIX = "S-UFTPPRE"
private const val UPLOAD_FILE_TO_PROJECT_PARTNER_REPORT_EXPENDITURE_ERROR_KEY_PREFIX = "use.case.upload.file.to.project.partner.report.expenditure"

class UploadFileToProjectPartnerReportExpenditureException(cause: Throwable) : ApplicationException(
    code = UPLOAD_FILE_TO_PROJECT_PARTNER_REPORT_EXPENDITURE_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$UPLOAD_FILE_TO_PROJECT_PARTNER_REPORT_EXPENDITURE_ERROR_KEY_PREFIX.failed"),
    cause = cause
)

class ExpenditureNotFoundException(expenditureId: Long) : ApplicationNotFoundException(
    code = "$UPLOAD_FILE_TO_PROJECT_PARTNER_REPORT_EXPENDITURE_ERROR_CODE_PREFIX-001",
    i18nMessage = I18nMessage(
        i18nKey = "$UPLOAD_FILE_TO_PROJECT_PARTNER_REPORT_EXPENDITURE_ERROR_KEY_PREFIX.expenditure.not.found",
        i18nArguments = mapOf("expenditureId" to expenditureId.toString())
    ),
)

class FileTypeNotSupported : ApplicationUnprocessableException(
    code = "$UPLOAD_FILE_TO_PROJECT_PARTNER_REPORT_EXPENDITURE_ERROR_CODE_PREFIX-002",
    i18nMessage = I18nMessage("$UPLOAD_FILE_TO_PROJECT_PARTNER_REPORT_EXPENDITURE_ERROR_KEY_PREFIX.type.not.supported")
)
