package io.cloudflight.jems.server.project.service.checklist.consolidateInstance

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationAccessDeniedException

const val CONSOLIDATE_CHECKLIST_INSTANCE_ERROR_CODE_PREFIX = "S-CHIN"
const val CONSOLIDATE_CHECKLIST_INSTANCE_ERROR_KEY_PREFIX = "use.case.consolidate.checklist.instance"

class ConsolidateChecklistNotAllowed : ApplicationAccessDeniedException (
    code = "$CONSOLIDATE_CHECKLIST_INSTANCE_ERROR_CODE_PREFIX-001",
    i18nMessage = I18nMessage("$CONSOLIDATE_CHECKLIST_INSTANCE_ERROR_KEY_PREFIX.failed")
)

