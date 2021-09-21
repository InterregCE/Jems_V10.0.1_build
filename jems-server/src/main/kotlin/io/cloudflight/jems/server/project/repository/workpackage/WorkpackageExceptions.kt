package io.cloudflight.jems.server.project.repository.workpackage

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationNotFoundException

const val PROJECT_ERROR_CODE_PREFIX = "P-WP"
const val PROJECT_ERROR_KEY_PREFIX = "project.workpackage"


class InvestmentNotFoundInProjectException(projectId: Long, investmentId: Long) : ApplicationNotFoundException(
    code = "$PROJECT_ERROR_CODE_PREFIX-001",
    i18nMessage = I18nMessage(
        "$PROJECT_ERROR_KEY_PREFIX.investment.not.found.in.project",
        mapOf("projectId" to projectId.toString(), "InvestmentId" to investmentId.toString())
    )
)
