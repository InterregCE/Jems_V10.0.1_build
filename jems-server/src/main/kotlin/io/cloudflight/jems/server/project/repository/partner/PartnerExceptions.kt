package io.cloudflight.jems.server.project.repository.partner

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationNotFoundException

const val PROJECT_ERROR_CODE_PREFIX = "P-PP"
const val PROJECT_ERROR_KEY_PREFIX = "project.partner"


class PartnerNotFoundInProjectException(projectId: Long, partnerId: Long) : ApplicationNotFoundException(
    code = "$PROJECT_ERROR_CODE_PREFIX-001",
    i18nMessage = I18nMessage(
        "$PROJECT_ERROR_KEY_PREFIX.not.found.in.project",
        mapOf("projectId" to projectId.toString(), "partnerId" to partnerId.toString())
    )
)
