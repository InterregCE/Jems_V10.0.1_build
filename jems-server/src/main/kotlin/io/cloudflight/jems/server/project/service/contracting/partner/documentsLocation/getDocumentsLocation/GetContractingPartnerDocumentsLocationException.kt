package io.cloudflight.jems.server.project.service.contracting.partner.documentsLocation.getDocumentsLocation

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException

private const val GET_PROJECT_CONTRACTING_PARTNER_DOCUMENTS_LOCATION_ERROR_CODE_PREFIX = "S-GPCPDL"
private const val GET_PROJECT_CONTRACTING_PARTNER_DOCUMENTS_LOCATION_ERROR_KEY_PREFIX = "use.case.get.project.contracting.partner.documents.location"

class GetContractingPartnerDocumentsLocationException(cause: Throwable) : ApplicationException(
    code = GET_PROJECT_CONTRACTING_PARTNER_DOCUMENTS_LOCATION_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$GET_PROJECT_CONTRACTING_PARTNER_DOCUMENTS_LOCATION_ERROR_KEY_PREFIX.failed"),
    cause = cause
)
