package io.cloudflight.jems.server.project.service.report.project.resultPrinciple.attachment.download

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.common.file.service.JemsFilePersistence
import io.cloudflight.jems.server.common.file.service.model.JemsFileType
import io.cloudflight.jems.server.project.authorization.CanRetrieveProjectReport
import io.cloudflight.jems.server.project.service.report.project.resultPrinciple.ProjectReportResultPrinciplePersistence
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class DownloadAttachmentFromProjectReportResultPrinciple(
    private val filePersistence: JemsFilePersistence,
    private val resultPrinciplePersistence: ProjectReportResultPrinciplePersistence,
) : DownloadAttachmentFromProjectReportResultPrincipleInteractor {

    @CanRetrieveProjectReport
    @Transactional(readOnly = true)
    @ExceptionWrapper(DownloadAttachmentFromProjectReportResultPrincipleException::class)
    override fun download(projectId: Long, reportId: Long, resultNumber: Int): Pair<String, ByteArray> {
        val fileId = resultPrinciplePersistence.getProjectResultPrinciples(projectId = projectId, reportId = reportId)
            .projectResults.first { it.resultNumber == resultNumber }.attachment?.id

        if (fileId == null || !filePersistence.existsFile(JemsFileType.ProjectResult, fileId))
            throw FileNotFound()

        return filePersistence.downloadFile(JemsFileType.ProjectResult, fileId) ?: throw FileNotFound()
    }
}

