package io.cloudflight.jems.server.project.service.contracting

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationAccessDeniedException

private const val CONTRACTING_ERROR_CODE_PREFIX = "S-PC"
private const val CONTRACTING_ERROR_KEY_PREFIX = "use.case.project.contracting"

class ContractingDeniedException : ApplicationAccessDeniedException(
    code = "$CONTRACTING_ERROR_CODE_PREFIX-001",
    i18nMessage = I18nMessage("$CONTRACTING_ERROR_KEY_PREFIX.denied"),
)

class ContractingModificationDeniedException : ApplicationAccessDeniedException(
    code = "$CONTRACTING_ERROR_CODE_PREFIX-002",
    i18nMessage = I18nMessage("$CONTRACTING_ERROR_KEY_PREFIX.modification.denied"),
)
