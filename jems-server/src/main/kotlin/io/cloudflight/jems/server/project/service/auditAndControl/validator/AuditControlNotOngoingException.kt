package io.cloudflight.jems.server.project.service.auditAndControl.validator

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationUnprocessableException

class AuditControlNotOngoingException : ApplicationUnprocessableException(
    code = "project.audit.and.control.validator",
    i18nMessage = I18nMessage("project.audit.and.control.validator.failed"),
)
