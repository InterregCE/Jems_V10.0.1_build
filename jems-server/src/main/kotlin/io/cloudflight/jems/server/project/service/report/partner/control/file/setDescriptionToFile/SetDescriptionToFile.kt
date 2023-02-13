package io.cloudflight.jems.server.project.service.report.partner.control.file.setDescriptionToFile

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.common.file.service.JemsFilePersistence
import io.cloudflight.jems.server.common.file.service.JemsProjectFileService
import io.cloudflight.jems.server.common.validator.GeneralValidatorService
import io.cloudflight.jems.server.project.authorization.CanEditPartnerControlReportFile
import io.cloudflight.jems.server.project.service.partner.PartnerPersistence
import io.cloudflight.jems.server.project.service.report.model.file.JemsFileType
import io.cloudflight.jems.server.project.service.report.partner.file.setDescriptionToFile.FileNotFound
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional


@Service
class SetDescriptionToFile(
    private val generalValidator: GeneralValidatorService,
    private val partnerPersistence: PartnerPersistence,
    private val filePersistence: JemsFilePersistence,
    private val fileService: JemsProjectFileService,
): SetDescriptionToFileInteractor {


    @CanEditPartnerControlReportFile
    @Transactional
    @ExceptionWrapper(SetDescriptionToFileException::class)
    override fun setDescription(partnerId: Long, reportId: Long, fileId: Long, description: String) {
        validateDescription(text = description)

        val projectId = partnerPersistence.getProjectIdForPartnerId(partnerId)
        // to make sure fileId corresponds to correct report, we need to verify it through location path
        val fileTypes = listOf(JemsFileType.ControlCertificate, JemsFileType.ControlReport)
        val fileDoesNotExist = fileTypes.none { fileType ->
            val pathPrefix = fileType.generatePath(projectId, partnerId, reportId)
            filePersistence.existsFile(partnerId = partnerId, pathPrefix = pathPrefix, fileId = fileId)
        }

        if (fileDoesNotExist) throw FileNotFound()

        fileService.setDescription(fileId = fileId, description = description)
    }


    private fun validateDescription(text: String) {
        generalValidator.throwIfAnyIsInvalid(
            generalValidator.maxLength(text, 250, "description"),
        )
    }
}
