package io.cloudflight.jems.server.project.service.contracting

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationAccessDeniedException

private const val CONTRACTING_MANAGEMENT_ERROR_CODE_PREFIX = "S-PCM"
private const val CONTRACTING_MANAGEMENT_ERROR_KEY_PREFIX = "use.case.project.contracting.management"


class ContractingDeniedException : ApplicationAccessDeniedException(
    code = "$CONTRACTING_MANAGEMENT_ERROR_CODE_PREFIX-001",
    i18nMessage = I18nMessage("$CONTRACTING_MANAGEMENT_ERROR_KEY_PREFIX.denied"),
)
