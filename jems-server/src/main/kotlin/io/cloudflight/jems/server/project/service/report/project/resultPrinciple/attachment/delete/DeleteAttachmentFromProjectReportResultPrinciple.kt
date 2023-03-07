package io.cloudflight.jems.server.project.service.report.project.resultPrinciple.attachment.delete

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.common.file.service.JemsFilePersistence
import io.cloudflight.jems.server.project.authorization.CanEditProjectReport
import io.cloudflight.jems.server.project.service.report.model.file.JemsFileType
import io.cloudflight.jems.server.project.service.report.project.resultPrinciple.ProjectReportResultPrinciplePersistence
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class DeleteAttachmentFromProjectReportResultPrinciple(
    private val filePersistence: JemsFilePersistence,
    private val resultPrinciplePersistence: ProjectReportResultPrinciplePersistence,
) : DeleteAttachmentFromProjectReportResultPrincipleInteractor {

    @CanEditProjectReport
    @Transactional
    @ExceptionWrapper(DeleteAttachmentFromProjectReportResultPrincipleException::class)
    override fun delete(projectId: Long, reportId: Long, resultNumber: Int) {
        val fileId = resultPrinciplePersistence.getProjectResultPrinciples(projectId = projectId, reportId = reportId)
            .projectResults.first { it.resultNumber == resultNumber }.attachment?.id

        if (fileId == null || !filePersistence.existsFile(JemsFileType.ProjectResult, fileId))
            throw FileNotFound()

        return filePersistence.deleteFile(JemsFileType.ProjectResult, fileId)
    }
}

