package io.cloudflight.jems.server.project.service.auditAndControl.file.updateDescription

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.common.file.service.JemsFilePersistence
import io.cloudflight.jems.server.common.file.service.JemsProjectFileService
import io.cloudflight.jems.server.common.file.service.model.JemsFileType
import io.cloudflight.jems.server.common.validator.GeneralValidatorService
import io.cloudflight.jems.server.project.authorization.CanEditAuditControl
import io.cloudflight.jems.server.project.service.auditAndControl.AuditControlPersistence
import io.cloudflight.jems.server.project.service.auditAndControl.validator.ProjectAuditAndControlValidator.Companion.verifyAuditControlOngoing
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UpdateDescriptionAuditControlFile(
    private val generalValidator: GeneralValidatorService,
    private val fileService: JemsProjectFileService,
    private val filePersistence: JemsFilePersistence,
    private val auditControlPersistence: AuditControlPersistence,
) : UpdateDescriptionAuditControlFileInteractor {

    @CanEditAuditControl
    @Transactional
    @ExceptionWrapper(UpdateDescriptionAuditControlFileException::class)
    override fun updateDescription(auditControlId: Long, fileId: Long, description: String) {
        validateDescription(description)

        val auditControl = auditControlPersistence.getById(auditControlId = auditControlId)
        verifyAuditControlOngoing(auditControl)

        val filePath = JemsFileType.AuditControl.generatePath(auditControl.projectId, auditControlId)
        if (!filePersistence.existsFile(exactPath = filePath, fileId = fileId))
            throw FileNotFound()

        fileService.setDescription(fileId = fileId, description = description)
    }

    private fun validateDescription(text: String) {
        generalValidator.throwIfAnyIsInvalid(
            generalValidator.maxLength(text, 250, "description"),
        )
    }
}
