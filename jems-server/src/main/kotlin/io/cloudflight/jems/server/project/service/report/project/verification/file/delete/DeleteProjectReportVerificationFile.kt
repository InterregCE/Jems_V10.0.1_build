package io.cloudflight.jems.server.project.service.report.project.verification.file.delete

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.common.file.service.JemsFilePersistence
import io.cloudflight.jems.server.common.file.service.model.JemsFileType.VerificationDocument
import io.cloudflight.jems.server.project.authorization.CanEditReportVerificationCommunication
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class DeleteProjectReportVerificationFile(
    private val filePersistence: JemsFilePersistence,
) : DeleteProjectReportVerificationFileInteractor {

    @CanEditReportVerificationCommunication
    @Transactional
    @ExceptionWrapper(DeleteProjectReportVerificationFileException::class)
    override fun delete(projectId: Long, reportId: Long, fileId: Long) {
        val filePath = VerificationDocument.generatePath(projectId, reportId)

        if (!filePersistence.existsFile(exactPath = filePath, fileId = fileId))
            throw FileNotFound()

        filePersistence.deleteFile(type = VerificationDocument, fileId = fileId)
    }
}
