package io.cloudflight.jems.server.project.service.report.partner.file.control

import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.project.service.partner.PartnerPersistence
import io.cloudflight.jems.server.project.service.report.ProjectReportPersistence
import io.cloudflight.jems.server.project.service.report.file.ProjectReportFilePersistence
import io.cloudflight.jems.server.project.service.report.model.ReportStatus
import io.cloudflight.jems.server.project.service.report.model.file.ProjectPartnerReportFileType
import org.springframework.stereotype.Service


@Service
class ControlReportFileAuthorizationService(
    private val reportPersistence: ProjectReportPersistence,
    private val partnerPersistence: PartnerPersistence,
    private val reportFilePersistence: ProjectReportFilePersistence,
    private val securityService: SecurityService,
) {

    fun validateChangeToFileAllowed(partnerId: Long, reportId: Long, fileId: Long) {
        val report = reportPersistence.getPartnerReportById(partnerId, reportId = reportId)

        if (report.status != ReportStatus.InControl)
            throw ReportNotInControl()

        val projectId = partnerPersistence.getProjectIdForPartnerId(partnerId)
        // to make sure fileId corresponds to correct report, we need to verify it through location path
        val reportPrefix = ProjectPartnerReportFileType.PartnerControlReport.generatePath(projectId, partnerId, reportId)

        val author = reportFilePersistence.getFileAuthor(partnerId = partnerId, pathPrefix = reportPrefix, fileId = fileId)
            ?: throw FileNotFound()

        if (author.id != securityService.getUserIdOrThrow())
            throw UserIsNotOwnerOfFile()
    }

}
