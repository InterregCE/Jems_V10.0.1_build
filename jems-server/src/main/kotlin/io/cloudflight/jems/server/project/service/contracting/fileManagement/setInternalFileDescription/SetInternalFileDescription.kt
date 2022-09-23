package io.cloudflight.jems.server.project.service.contracting.fileManagement.setInternalFileDescription

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.common.validator.GeneralValidatorService
import io.cloudflight.jems.server.project.authorization.CanSetProjectToContracted
import io.cloudflight.jems.server.project.service.report.file.ProjectReportFilePersistence
import io.cloudflight.jems.server.project.service.report.model.file.ProjectPartnerReportFileType
import io.cloudflight.jems.server.project.service.report.partner.file.setDescriptionToFile.FileNotFound
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class SetInternalFileDescription(
    private val reportFilePersistence: ProjectReportFilePersistence,
    private val generalValidator: GeneralValidatorService
): SetInternalFileDescriptionInteractor {


    @CanSetProjectToContracted
    @Transactional
    @ExceptionWrapper(SetDescriptionToInternalFileException::class)
    override fun setInternalFileDescription(projectId: Long, fileId: Long, description: String) {
        validateDescription(description)

        if (!reportFilePersistence.existsFileByProjectIdAndFileIdAndFileTypeIn(
                projectId = projectId,
                fileId = fileId,
                setOf(ProjectPartnerReportFileType.ContractInternal)
            )
        )
            throw FileNotFound()

        reportFilePersistence.setDescriptionToFile(fileId, description)
    }

    private fun validateDescription(text: String) {
        generalValidator.throwIfAnyIsInvalid(
            generalValidator.maxLength(text, 250, "description"),
        )
    }
}
