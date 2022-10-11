package io.cloudflight.jems.server.project.service.contracting.fileManagement.listPartnerFiles

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException
import io.cloudflight.jems.server.common.exception.ApplicationUnprocessableException

private const val LIST_CONTRACTING_PARTNER_FILES_ERROR_CODE_PREFIX = "S-LCPF"
private const val LIST_CONTRACTING_PARTNER_FILES_ERROR_KEY_PREFIX = "use.case.list.contracting.partner.files"

class ListContractingPartnerFilesException(cause: Throwable) : ApplicationException(
    code = LIST_CONTRACTING_PARTNER_FILES_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$LIST_CONTRACTING_PARTNER_FILES_ERROR_KEY_PREFIX.failed"),
    cause = cause,
)
