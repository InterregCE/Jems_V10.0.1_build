package io.cloudflight.jems.server.project.service.contracting.fileManagement.deletePartnerFile

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException

private const val DELETE_CONTRACTING_PARTNER_FILE_ERROR_CODE_PREFIX = "S-DELCPF"
private const val DELETE_CONTRACTING_PARTNER_FILE_ERROR_KEY_PREFIX = "use.case.delete.contracting.partner.file"

class DeleteContractingPartnerFileException(cause: Throwable): ApplicationException(
    code = DELETE_CONTRACTING_PARTNER_FILE_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$DELETE_CONTRACTING_PARTNER_FILE_ERROR_KEY_PREFIX.failed"),
    cause = cause
)

