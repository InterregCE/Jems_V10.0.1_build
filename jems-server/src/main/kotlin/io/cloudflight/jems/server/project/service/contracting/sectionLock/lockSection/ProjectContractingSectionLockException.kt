package io.cloudflight.jems.server.project.service.contracting.sectionLock.lockSection

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException

private const val CONTRACTING_SECTION_LOCK_ERROR_CODE_PREFIX = "S-CSLK"
private const val CONTRACTING_SECTION_LOCK_ERROR_KEY_PREFIX = "project.contracting.section.lock"

class ProjectContractingSectionLockException(cause: Throwable): ApplicationException(
    code = CONTRACTING_SECTION_LOCK_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$CONTRACTING_SECTION_LOCK_ERROR_KEY_PREFIX.failed"),
    cause = cause
)