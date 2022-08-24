package io.cloudflight.jems.server.project.service.report.partner.identification.control.getProjectPartnerControlReportIdentification

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.programme.repository.ProgrammeDataRepository
import io.cloudflight.jems.server.project.authorization.CanViewPartnerControlReport
import io.cloudflight.jems.server.project.service.ProjectPersistence
import io.cloudflight.jems.server.project.service.contracting.monitoring.getProjectContractingMonitoring.GetContractingMonitoringService
import io.cloudflight.jems.server.project.service.partner.PartnerPersistence
import io.cloudflight.jems.server.project.service.report.ProjectReportPersistence
import io.cloudflight.jems.server.project.service.report.model.ReportStatus
import io.cloudflight.jems.server.project.service.report.model.identification.control.ProjectPartnerControlReport
import io.cloudflight.jems.server.project.service.report.partner.identification.ProjectReportIdentificationPersistence
import io.cloudflight.jems.server.project.service.report.partner.identification.control.toModelObject
import io.cloudflight.jems.server.project.service.report.partner.identification.getProjectPartnerReportIdentification.GetProjectPartnerReportIdentification.Companion.emptyIdentification
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GetProjectPartnerControlReportIdentification(
    private val reportPersistence: ProjectReportPersistence,
    private val reportIdentificationPersistence: ProjectReportIdentificationPersistence,
    private val partnerPersistence: PartnerPersistence,
    private val projectPersistence: ProjectPersistence,
    private val programmeDataRepository: ProgrammeDataRepository,
    private val getContractingMonitoringService: GetContractingMonitoringService,
) : GetProjectPartnerControlReportIdentificationInteractor {

    @CanViewPartnerControlReport
    @Transactional(readOnly = true)
    @ExceptionWrapper(GetProjectPartnerControlReportIdentificationException::class)
    override fun getControlIdentification(partnerId: Long, reportId: Long): ProjectPartnerControlReport {
        val report = reportPersistence.getPartnerReportById(partnerId, reportId = reportId)
        validateReportInControl(status = report.status)

        val identification = reportIdentificationPersistence
            .getPartnerReportIdentification(partnerId = partnerId, reportId = reportId)
            .orElse(emptyIdentification())

        val projectId = partnerPersistence.getProjectIdForPartnerId(partnerId, report.version)
        val project = projectPersistence.getProject(
            projectId = projectId,
            version = report.version,
        )

        val startAndEndDate = getContractingMonitoringService.getContractMonitoringDates(projectId)

        return toModelObject(
            report = report,
            projectTitle = project.title,
            programmeTitle = programmeDataRepository.findById(1L).orElse(null)?.title,
            startAndEndDate = startAndEndDate,
            identification = identification,
        )
    }

    private fun validateReportInControl(status: ReportStatus) {
        if (status != ReportStatus.InControl)
            throw ReportNotInControl()
    }

}
