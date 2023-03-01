package io.cloudflight.jems.server.project.service.contracting.sectionLock.getLockedSections

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException

private const val CONTRACTING_LOCKED_SECTIONS_ERROR_CODE_PREFIX = "S-CGLKS"
private const val CONTRACTING_LOCKED_SECTIONS_ERROR_KEY_PREFIX = "project.contracting.locked.sections"

class ProjectContractingGetLockedSectionsException(cause: Throwable): ApplicationException(
    code = CONTRACTING_LOCKED_SECTIONS_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$CONTRACTING_LOCKED_SECTIONS_ERROR_KEY_PREFIX.failed"),
    cause = cause
)
