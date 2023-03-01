package io.cloudflight.jems.server.project.service.contracting.fileManagement.setPartnerFileDescription

import io.cloudflight.jems.server.common.file.service.JemsFilePersistence
import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.common.file.service.JemsProjectFileService
import io.cloudflight.jems.server.common.validator.GeneralValidatorService
import io.cloudflight.jems.server.project.authorization.CanUpdateProjectContractingPartner
import io.cloudflight.jems.server.project.service.contracting.ContractingValidator
import io.cloudflight.jems.server.project.service.report.model.file.JemsFileType
import io.cloudflight.jems.server.project.service.report.partner.file.setDescriptionToFile.FileNotFound
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class SetPartnerFileDescription(
    private val filePersistence: JemsFilePersistence,
    private val fileService: JemsProjectFileService,
    private val generalValidator: GeneralValidatorService,
    private val validator: ContractingValidator
) : SetPartnerFileDescriptionInteractor {

    companion object {
        const val DESCRIPTION_MAX_LENGTH = 250
    }

    @CanUpdateProjectContractingPartner
    @Transactional
    @ExceptionWrapper(SetDescriptionToPartnerFileException::class)
    override fun setPartnerFileDescription(partnerId: Long, fileId: Long, description: String) {
        validator.validatePartnerLock(partnerId)
        validateDescription(description)

        val isFileExists = filePersistence.existsFileByPartnerIdAndFileIdAndFileTypeIn(
            partnerId = partnerId,
            fileId = fileId,
            setOf(JemsFileType.ContractPartnerDoc)
        )
        if (!isFileExists)
            throw FileNotFound()

        fileService.setDescription(fileId, description)
    }

    private fun validateDescription(description: String) {
        generalValidator.throwIfAnyIsInvalid(
            generalValidator.maxLength(description, DESCRIPTION_MAX_LENGTH, "description"),
        )
    }
}
