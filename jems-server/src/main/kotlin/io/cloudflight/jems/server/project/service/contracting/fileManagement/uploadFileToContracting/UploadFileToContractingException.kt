package io.cloudflight.jems.server.project.service.contracting.fileManagement.uploadFileToContracting

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException
import io.cloudflight.jems.server.common.exception.ApplicationNotFoundException
import io.cloudflight.jems.server.common.exception.ApplicationUnprocessableException

private const val UPLOAD_FILE_TO_CONTRACTING_ERROR_CODE_PREFIX = "S-UFTC"
private const val UPLOAD_FILE_TO_CONTRACTING_ERROR_KEY_PREFIX = "use.case.upload.file.to.contracting"

class UploadFileToContractingException(cause: Throwable) : ApplicationException(
    code = UPLOAD_FILE_TO_CONTRACTING_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$UPLOAD_FILE_TO_CONTRACTING_ERROR_KEY_PREFIX.failed"),
    cause = cause,
)

class ProjectNotApprovedException : ApplicationUnprocessableException(
    code = "$UPLOAD_FILE_TO_CONTRACTING_ERROR_CODE_PREFIX-001",
    i18nMessage = I18nMessage("$UPLOAD_FILE_TO_CONTRACTING_ERROR_KEY_PREFIX.wrong.status"),
)

class FileTypeNotSupported : ApplicationUnprocessableException(
    code = "$UPLOAD_FILE_TO_CONTRACTING_ERROR_CODE_PREFIX-002",
    i18nMessage = I18nMessage("$UPLOAD_FILE_TO_CONTRACTING_ERROR_KEY_PREFIX.file.type.not.supported"),
)

class PartnerNotFound(partnerId: Long, projectId: Long): ApplicationNotFoundException(
    code = "$UPLOAD_FILE_TO_CONTRACTING_ERROR_CODE_PREFIX-003",
    i18nMessage = I18nMessage("$UPLOAD_FILE_TO_CONTRACTING_ERROR_KEY_PREFIX.partner.not.found"),
    message = "There is no partner ID=$partnerId related to project ID=$projectId",
)
