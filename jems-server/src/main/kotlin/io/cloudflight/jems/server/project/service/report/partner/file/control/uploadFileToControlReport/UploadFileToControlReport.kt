package io.cloudflight.jems.server.project.service.report.partner.file.control.uploadFileToControlReport

import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.project.authorization.CanEditPartnerControlReportFile
import io.cloudflight.jems.server.project.service.file.model.ProjectFile
import io.cloudflight.jems.server.project.service.file.uploadProjectFile.isFileTypeInvalid
import io.cloudflight.jems.server.project.service.partner.PartnerPersistence
import io.cloudflight.jems.server.project.service.report.ProjectReportPersistence
import io.cloudflight.jems.server.project.service.report.ProjectReportFilePersistence
import io.cloudflight.jems.server.project.service.report.model.partner.ReportStatus
import io.cloudflight.jems.server.project.service.report.model.file.JemsFileType
import io.cloudflight.jems.server.project.service.report.model.file.JemsFileMetadata
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UploadFileToControlReport(
    private val reportFilePersistence: ProjectReportFilePersistence,
    private val reportPersistence: ProjectReportPersistence,
    private val partnerPersistence: PartnerPersistence,
    private val securityService: SecurityService,
) : UploadFileToControlReportInteractor {

    @CanEditPartnerControlReportFile
    @Transactional
    @ExceptionWrapper(UploadFileToControlReportException::class)
    override fun uploadToControlReport(partnerId: Long, reportId: Long, file: ProjectFile): JemsFileMetadata {
        val report = reportPersistence.getPartnerReportById(partnerId, reportId = reportId)

        if (report.status != ReportStatus.InControl)
            throw ReportNotInControl()

        if (isFileTypeInvalid(file))
            throw FileTypeNotSupported()

        with(JemsFileType.ControlDocument) {
            val projectId = partnerPersistence.getProjectIdForPartnerId(partnerId)
            val location = generatePath(projectId, partnerId, reportId)

            if (reportFilePersistence.existsFile(exactPath = location, fileName = file.name))
                throw FileAlreadyExists(file.name)

            return reportFilePersistence.addAttachmentToPartnerReport(
                file = file.getFileMetadata(projectId, partnerId, location, type = this, securityService.getUserIdOrThrow()),
            )
        }
    }

}
