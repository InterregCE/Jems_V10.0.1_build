package io.cloudflight.jems.server.project.repository.report.partner.control.file

import io.cloudflight.jems.server.common.file.entity.JemsFileMetadataEntity
import io.cloudflight.jems.server.common.file.service.JemsProjectFileService
import io.cloudflight.jems.server.project.entity.report.control.certificate.PartnerReportControlFileEntity
import io.cloudflight.jems.server.project.service.report.model.file.JemsFileCreate
import io.cloudflight.jems.server.project.service.report.model.file.JemsFileMetadata
import io.cloudflight.jems.server.project.service.report.model.file.JemsFileType
import io.cloudflight.jems.server.project.service.report.model.partner.control.file.PartnerReportControlFile
import io.cloudflight.jems.server.project.service.report.partner.control.file.ProjectPartnerReportControlFilePersistence
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
class ProjectPartnerReportControlFilePersistenceProvider(
    private val reportControlFileRepository: ProjectPartnerReportControlFileRepository,
    private val fileRepository: JemsProjectFileService,
) : ProjectPartnerReportControlFilePersistence {

    @Transactional(readOnly = true)
    override fun listReportControlFiles(reportId: Long, pageable: Pageable): Page<PartnerReportControlFile> {
        return reportControlFileRepository.findAllByReportId(reportId, pageable).toModel()
    }

    @Transactional(readOnly = true)
    override fun existsByFileId(fileId: Long): Boolean {
        return reportControlFileRepository.existsById(fileId)
    }

    @Transactional
    override fun updateCertificateAttachment(fileId: Long, file: JemsFileCreate): JemsFileMetadata {
        val certificate = reportControlFileRepository.findById(fileId).get()
        certificate.signedFile.deleteIfPresent()

        return persistFileAndCreateLink(file = file) { certificate.signedFile = it }
    }

    @Transactional
    override fun deleteCertificateAttachment(fileId: Long) {
        val certificate = reportControlFileRepository.findById(fileId).get()
        val file = certificate.signedFile
        certificate.signedFile = null
        file.deleteIfPresent()
    }

    @Transactional(readOnly = true)
    override fun getByReportIdAndId(reportId: Long, fileId: Long): PartnerReportControlFile {
        return reportControlFileRepository.findByReportIdAndId(reportId, fileId).toModel()
    }

    @Transactional(readOnly = true)
    override fun countReportControlFilesByFileType(reportId: Long, fileType: JemsFileType): Long {
        return reportControlFileRepository.countAllByReportIdAndGeneratedFileType(reportId, fileType)
    }

    @Transactional
    override fun saveReportControlFile(reportId: Long, file: JemsFileCreate) {
        persistFileAndCreateLink(file = file) {
            reportControlFileRepository.save(createControlReportFileEntity(reportId, it))
        }
    }

    private fun persistFileAndCreateLink(file: JemsFileCreate, additionalStep: (JemsFileMetadataEntity) -> Unit) =
        fileRepository.persistProjectFileAndPerformAction(file = file, additionalStep = additionalStep)

    private fun createControlReportFileEntity(reportId: Long, file: JemsFileMetadataEntity): PartnerReportControlFileEntity {
        return PartnerReportControlFileEntity(
            reportId = reportId,
            generatedFile = file,
            signedFile = null,
        )
    }

    private fun JemsFileMetadataEntity?.deleteIfPresent() {
        if (this != null) {
            fileRepository.delete(this)
        }
    }
}
