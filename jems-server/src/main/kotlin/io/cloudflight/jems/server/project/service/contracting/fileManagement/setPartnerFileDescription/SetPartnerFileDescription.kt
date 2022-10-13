package io.cloudflight.jems.server.project.service.contracting.fileManagement.setPartnerFileDescription

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.common.validator.GeneralValidatorService
import io.cloudflight.jems.server.project.authorization.CanUpdateProjectContractingPartner
import io.cloudflight.jems.server.project.service.report.file.ProjectReportFilePersistence
import io.cloudflight.jems.server.project.service.report.model.file.ProjectPartnerReportFileType
import io.cloudflight.jems.server.project.service.report.partner.file.setDescriptionToFile.FileNotFound
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class SetPartnerFileDescription(
    private val reportFilePersistence: ProjectReportFilePersistence,
    private val generalValidator: GeneralValidatorService
) : SetPartnerFileDescriptionInteractor {

    companion object {
        const val DESCRIPTION_MAX_LENGTH = 250
    }

    @CanUpdateProjectContractingPartner
    @Transactional
    @ExceptionWrapper(SetDescriptionToPartnerFileException::class)
    override fun setPartnerFileDescription(partnerId: Long, fileId: Long, description: String) {
        validateDescription(description)

        val isFileExists = reportFilePersistence.existsFileByPartnerIdAndFileIdAndFileTypeIn(
            partnerId = partnerId,
            fileId = fileId,
            setOf(ProjectPartnerReportFileType.ContractPartnerDoc)
        )
        if (!isFileExists)
            throw FileNotFound()

        reportFilePersistence.setDescriptionToFile(fileId, description)
    }

    private fun validateDescription(description: String) {
        generalValidator.throwIfAnyIsInvalid(
            generalValidator.maxLength(description, DESCRIPTION_MAX_LENGTH, "description"),
        )
    }
}
