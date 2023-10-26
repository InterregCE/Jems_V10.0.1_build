package io.cloudflight.jems.server.project.service.auditAndControl.getPartnerAndPartnerReportData

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException

private const val GET_PROJECT_AUDIT_CONTROL_CORRECTION_IDENTIFICATION_PARTNER_DATA_ERROR_CODE_PREFIX = "S-GPAPRD"
private const val GET_PROJECT_AUDIT_CONTROL_CORRECTION_IDENTIFICATION_PARTNER_DATA_KEY_PREFIX =
    "use.case.get.project.audit.control.correction.identification.get.partner.data"

class GetPartnerAndPartnerReportException(cause: Throwable): ApplicationException(
    code = GET_PROJECT_AUDIT_CONTROL_CORRECTION_IDENTIFICATION_PARTNER_DATA_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$GET_PROJECT_AUDIT_CONTROL_CORRECTION_IDENTIFICATION_PARTNER_DATA_KEY_PREFIX.failed"),
    cause = cause
)

