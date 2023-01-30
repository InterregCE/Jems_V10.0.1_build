package io.cloudflight.jems.server.project.repository.report.partner.control.certificate

import io.cloudflight.jems.server.common.file.entity.JemsFileMetadataEntity
import io.cloudflight.jems.server.common.file.service.JemsProjectFileService
import io.cloudflight.jems.server.project.entity.report.control.certificate.PartnerReportControlFileEntity
import io.cloudflight.jems.server.project.service.report.model.file.JemsFileCreate
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
    override fun listReportFiles(reportId: Long, pageable: Pageable): Page<PartnerReportControlFile> {
        return reportControlFileRepository.findAllByReportId(reportId, pageable).toModel()
    }

    @Transactional
    override fun saveReportControlFile(reportId: Long, file: JemsFileCreate) {
        persistFileAndCreateLink(file = file) {
            reportControlFileRepository.save(createControlCertificateEntity(reportId, it))
        }
    }

    private fun persistFileAndCreateLink(file: JemsFileCreate, additionalStep: (JemsFileMetadataEntity) -> Unit) =
        fileRepository.persistProjectFileAndPerformAction(file = file, additionalStep = additionalStep)

    private fun createControlCertificateEntity(reportId: Long, file: JemsFileMetadataEntity): PartnerReportControlFileEntity {
        return PartnerReportControlFileEntity(
            reportId = reportId,
            generatedFile = file,
            signedFile = null,
        )
    }
}
