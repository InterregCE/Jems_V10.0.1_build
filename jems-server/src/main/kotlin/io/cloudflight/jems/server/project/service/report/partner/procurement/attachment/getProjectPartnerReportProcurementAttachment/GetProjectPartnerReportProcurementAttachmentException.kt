package io.cloudflight.jems.server.project.service.report.partner.procurement.attachment.getProjectPartnerReportProcurementAttachment

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException

private const val GET_PROJECT_PARTNER_REPORT_PROCUREMENT_ATTACHMENT_ERROR_CODE_PREFIX = "S-GPPRPA"
private const val GET_PROJECT_PARTNER_REPORT_PROCUREMENT_ATTACHMENT_ERROR_KEY_PREFIX = "use.case.get.project.partner.report.procurement.attachment"

class GetProjectPartnerReportProcurementAttachmentException(cause: Throwable) : ApplicationException(
    code = GET_PROJECT_PARTNER_REPORT_PROCUREMENT_ATTACHMENT_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$GET_PROJECT_PARTNER_REPORT_PROCUREMENT_ATTACHMENT_ERROR_KEY_PREFIX.failed"),
    cause = cause,
)
