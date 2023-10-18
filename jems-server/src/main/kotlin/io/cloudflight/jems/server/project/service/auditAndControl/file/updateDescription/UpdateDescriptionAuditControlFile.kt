package io.cloudflight.jems.server.project.service.auditAndControl.file.updateDescription

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.common.file.service.JemsFilePersistence
import io.cloudflight.jems.server.common.file.service.JemsProjectFileService
import io.cloudflight.jems.server.common.file.service.model.JemsFileType
import io.cloudflight.jems.server.common.validator.GeneralValidatorService
import io.cloudflight.jems.server.project.authorization.CanEditProjectAuditAndControl
import io.cloudflight.jems.server.project.service.auditAndControl.AuditControlPersistence
import io.cloudflight.jems.server.project.service.auditAndControl.ProjectAuditAndControlValidator
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UpdateDescriptionAuditControlFile(
    private val generalValidator: GeneralValidatorService,
    private val fileService: JemsProjectFileService,
    private val filePersistence: JemsFilePersistence,
    private val auditControlPersistence: AuditControlPersistence,
    private val auditAndControlValidator: ProjectAuditAndControlValidator,
) : UpdateDescriptionAuditControlFileInteractor {

    @CanEditProjectAuditAndControl
    @Transactional
    @ExceptionWrapper(UpdateDescriptionAuditControlFileException::class)
    override fun updateDescription(projectId: Long, auditControlId: Long, fileId: Long, description: String) {
        validateDescription(description)

        if (!filePersistence.existsFile(exactPath = JemsFileType.AuditControl.generatePath(projectId, auditControlId), fileId = fileId))
            throw FileNotFound()

        val auditControl = auditControlPersistence.getByIdAndProjectId(auditControlId = auditControlId, projectId = projectId)
        auditAndControlValidator.verifyAuditControlOngoing(auditControl)

        fileService.setDescription(fileId = fileId, description = description)
    }

    private fun validateDescription(text: String) {
        generalValidator.throwIfAnyIsInvalid(
            generalValidator.maxLength(text, 250, "description"),
        )
    }
}
