package io.cloudflight.jems.server.project.service.report.partner.control.file

import io.cloudflight.jems.server.project.service.report.model.file.JemsFileCreate
import io.cloudflight.jems.server.project.service.report.model.file.JemsFileType
import io.cloudflight.jems.server.project.service.report.model.file.JemsFileMetadata
import io.cloudflight.jems.server.project.service.report.model.partner.control.file.PartnerReportControlFile
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface ProjectPartnerReportControlFilePersistence {
    fun saveReportControlFile(reportId: Long, file: JemsFileCreate)

    fun listReportControlFiles(reportId: Long, pageable: Pageable): Page<PartnerReportControlFile>

    fun existsByFileId(fileId: Long): Boolean

    fun updateCertificateAttachment(
        fileId: Long,
        file: JemsFileCreate
    ): JemsFileMetadata

    fun deleteCertificateAttachment(fileId: Long)

    fun getByReportIdAndId(reportId: Long, fileId: Long): PartnerReportControlFile

    fun countReportControlFilesByFileType(reportId: Long, fileType: JemsFileType): Long
}
