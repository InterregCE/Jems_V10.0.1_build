package io.cloudflight.jems.server.project.service.sharedFolderFile.description

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.common.file.service.JemsFilePersistence
import io.cloudflight.jems.server.common.file.service.JemsProjectFileService
import io.cloudflight.jems.server.common.file.service.model.JemsFileType
import io.cloudflight.jems.server.common.validator.GeneralValidatorService
import io.cloudflight.jems.server.project.authorization.CanEditSharedFolder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class SetDescriptionToSharedFolderFile(
    private val generalValidator: GeneralValidatorService,
    private val filePersistence: JemsFilePersistence,
    private val fileService: JemsProjectFileService,
) : SetDescriptionToSharedFolderFileInteractor {

    @CanEditSharedFolder
    @Transactional
    @ExceptionWrapper(SetDescriptionToSharedFolderFileException::class)
    override fun set(projectId: Long, fileId: Long, description: String) {
        validateDescription(description)

        if (!filePersistence.existsFileByProjectIdAndFileIdAndFileTypeIn(projectId, fileId, setOf(JemsFileType.SharedFolder))) {
            throw FileNotFound()
        }

        fileService.setDescription(fileId, description)
    }

    private fun validateDescription(text: String) {
        generalValidator.throwIfAnyIsInvalid(
            generalValidator.maxLength(text, 250, "description"),
        )
    }
}
