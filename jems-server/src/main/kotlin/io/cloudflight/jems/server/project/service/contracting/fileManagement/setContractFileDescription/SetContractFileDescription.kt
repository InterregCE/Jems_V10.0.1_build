package io.cloudflight.jems.server.project.service.contracting.fileManagement.setContractFileDescription

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.common.validator.GeneralValidatorService
import io.cloudflight.jems.server.project.authorization.CanEditContractInfo
import io.cloudflight.jems.server.project.service.report.file.ProjectReportFilePersistence
import io.cloudflight.jems.server.project.service.report.model.file.ProjectPartnerReportFileType
import io.cloudflight.jems.server.project.service.report.partner.file.setDescriptionToFile.FileNotFound
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class SetContractFileDescription(
    private val reportFilePersistence: ProjectReportFilePersistence,
    private val generalValidator: GeneralValidatorService
): SetContractFileDescriptionInteractor {

    @CanEditContractInfo
    @Transactional
    @ExceptionWrapper(SetDescriptionToContractFileException::class)
    override fun setContractFileDescription(projectId: Long, fileId: Long, description: String) {
        validateDescription(description)

        if (!reportFilePersistence.existsFileByProjectIdAndFileIdAndFileTypeIn(
                projectId = projectId,
                fileId = fileId,
                setOf(ProjectPartnerReportFileType.Contract, ProjectPartnerReportFileType.ContractDoc)
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
