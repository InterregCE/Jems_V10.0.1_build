package io.cloudflight.jems.server.project.service.checklist.getAllInstances

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException

const val GET_ALL_CHECKLIST_INSTANCES_ERROR_CODE_PREFIX = "S-CHIN"
const val GET_ALL_CHECKLIST_INSTANCES_ERROR_KEY_PREFIX = "use.case.get.all.checklist.instances"

class GetAllChecklistInstancesException(cause: Throwable) : ApplicationException(
    code = "$GET_ALL_CHECKLIST_INSTANCES_ERROR_CODE_PREFIX-001",
    i18nMessage = I18nMessage("$GET_ALL_CHECKLIST_INSTANCES_ERROR_KEY_PREFIX.failed"), cause = cause
)
