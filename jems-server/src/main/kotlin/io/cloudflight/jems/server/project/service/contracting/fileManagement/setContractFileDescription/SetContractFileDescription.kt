package io.cloudflight.jems.server.project.service.contracting.fileManagement.setContractFileDescription

import io.cloudflight.jems.server.common.file.service.JemsFilePersistence
import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.common.file.service.JemsProjectFileService
import io.cloudflight.jems.server.common.validator.GeneralValidatorService
import io.cloudflight.jems.server.project.authorization.CanEditContractInfo
import io.cloudflight.jems.server.project.service.report.model.file.JemsFileType
import io.cloudflight.jems.server.project.service.report.partner.file.setDescriptionToFile.FileNotFound
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class SetContractFileDescription(
    private val filePersistence: JemsFilePersistence,
    private val fileService: JemsProjectFileService,
    private val generalValidator: GeneralValidatorService
): SetContractFileDescriptionInteractor {

    @CanEditContractInfo
    @Transactional
    @ExceptionWrapper(SetDescriptionToContractFileException::class)
    override fun setContractFileDescription(projectId: Long, fileId: Long, description: String) {
        validateDescription(description)

        if (!filePersistence.existsFileByProjectIdAndFileIdAndFileTypeIn(
                projectId = projectId,
                fileId = fileId,
                setOf(JemsFileType.Contract, JemsFileType.ContractDoc)
            )
        )
            throw FileNotFound()

        fileService.setDescription(fileId, description)
    }


    private fun validateDescription(text: String) {
        generalValidator.throwIfAnyIsInvalid(
            generalValidator.maxLength(text, 250, "description"),
        )
    }
}
