package io.cloudflight.jems.server.project.service.contracting.partner.documentsLocation.updateDocumentsLocation

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException

private const val UPDATE_PROJECT_CONTRACTING_PARTNER_DOCUMENTS_LOCATION_ERROR_CODE_PREFIX = "S-UPCPDL"
private const val UPDATE_PROJECT_CONTRACTING_PARTNER_DOCUMENTS_LOCATION_ERROR_KEY_PREFIX = "use.case.update.project.contracting.partner.documents.location"

class UpdateContractingPartnerDocumentsLocationException(cause: Throwable) : ApplicationException(
    code = UPDATE_PROJECT_CONTRACTING_PARTNER_DOCUMENTS_LOCATION_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$UPDATE_PROJECT_CONTRACTING_PARTNER_DOCUMENTS_LOCATION_ERROR_KEY_PREFIX.failed"),
    cause = cause
)
