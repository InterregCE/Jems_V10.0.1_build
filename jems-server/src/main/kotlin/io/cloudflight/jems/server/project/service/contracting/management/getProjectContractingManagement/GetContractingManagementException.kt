package io.cloudflight.jems.server.project.service.contracting.management.getProjectContractingManagement

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException

private const val GET_CONTRACTING_MANAGEMENT_ERROR_CODE_PREFIX = "S-GPCM"
private const val GET_CONTRACTING_MANAGEMENT_ERROR_KEY_PREFIX = "use.case.get.project.contracting.management"

class GetContractingManagementException(cause: Throwable) : ApplicationException(
    code = GET_CONTRACTING_MANAGEMENT_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$GET_CONTRACTING_MANAGEMENT_ERROR_KEY_PREFIX.failed"),
    cause = cause
)
