package io.cloudflight.jems.server.project.service.contracting.management.updateProjectContractingManagement

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException

private const val UPDATE_CONTRACTING_MANAGEMENT_ERROR_CODE_PREFIX = "S-UPCM"
private const val UPDATE_CONTRACTING_MANAGEMENT_ERROR_KEY_PREFIX = "use.case.update.project.contracting.management"

class UpdateContractingManagementException(cause: Throwable) : ApplicationException(
    code = UPDATE_CONTRACTING_MANAGEMENT_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$UPDATE_CONTRACTING_MANAGEMENT_ERROR_KEY_PREFIX.failed"),
    cause = cause
)
