package io.cloudflight.jems.server.project.service.report.partner.identification.control.getProjectPartnerControlReportIdentification

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.programme.repository.ProgrammeDataRepository
import io.cloudflight.jems.server.project.authorization.CanViewPartnerControlReport
import io.cloudflight.jems.server.project.service.ProjectPersistence
import io.cloudflight.jems.server.project.service.contracting.monitoring.getProjectContractingMonitoring.GetContractingMonitoringService
import io.cloudflight.jems.server.project.service.partner.PartnerPersistence
import io.cloudflight.jems.server.project.service.report.partner.ProjectPartnerReportPersistence
import io.cloudflight.jems.server.project.service.report.model.partner.ReportStatus
import io.cloudflight.jems.server.project.service.report.model.partner.identification.control.ProjectPartnerControlReport
import io.cloudflight.jems.server.project.service.report.partner.identification.ProjectPartnerReportDesignatedControllerPersistence
import io.cloudflight.jems.server.project.service.report.partner.identification.ProjectPartnerReportVerificationPersistence
import io.cloudflight.jems.server.project.service.report.partner.identification.ProjectPartnerReportIdentificationPersistence
import io.cloudflight.jems.server.project.service.report.partner.identification.control.toModelObject
import io.cloudflight.jems.server.project.service.report.partner.identification.getProjectPartnerReportIdentification.GetProjectPartnerReportIdentificationService.Companion.emptyIdentification
import io.cloudflight.jems.server.project.service.report.partner.identification.getProjectPartnerReportIdentification.GetProjectPartnerReportIdentificationService.Companion.emptyVerification
import io.cloudflight.jems.server.project.service.report.partner.identification.getProjectPartnerReportIdentification.GetProjectPartnerReportIdentificationService.Companion.emptyDesignatedController
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GetProjectPartnerControlReportIdentification(
    private val reportPersistence: ProjectPartnerReportPersistence,
    private val reportIdentificationPersistence: ProjectPartnerReportIdentificationPersistence,
    private val partnerPersistence: PartnerPersistence,
    private val projectPersistence: ProjectPersistence,
    private val designatedControllerPersistence: ProjectPartnerReportDesignatedControllerPersistence,
    private val reportVerificationPersistence: ProjectPartnerReportVerificationPersistence,
    private val programmeDataRepository: ProgrammeDataRepository,
    private val getContractingMonitoringService: GetContractingMonitoringService,
) : GetProjectPartnerControlReportIdentificationInteractor {

    @CanViewPartnerControlReport
    @Transactional(readOnly = true)
    @ExceptionWrapper(GetProjectPartnerControlReportIdentificationException::class)
    override fun getControlIdentification(partnerId: Long, reportId: Long): ProjectPartnerControlReport {
        val report = reportPersistence.getPartnerReportById(partnerId, reportId = reportId)
        validateReportAfterInControl(status = report.status)

        val identification = reportIdentificationPersistence
            .getPartnerReportIdentification(partnerId = partnerId, reportId = reportId)
            .orElse(emptyIdentification())

        val projectId = partnerPersistence.getProjectIdForPartnerId(partnerId, report.version)
        val project = projectPersistence.getProject(
            projectId = projectId,
            version = report.version,
        )

        val startAndEndDate = getContractingMonitoringService.getContractMonitoringDates(projectId)

        val designatedController = designatedControllerPersistence
            .getControlReportDesignatedController(partnerId, reportId)

        val reportVerification = reportVerificationPersistence
            .getControlReportVerification(partnerId, reportId)
            .orElse(emptyVerification())

        return toModelObject(
            report = report,
            projectTitle = project.title,
            programmeTitle = programmeDataRepository.findById(1L).orElse(null)?.title,
            startAndEndDate = startAndEndDate,
            identification = identification,
            designatedController = designatedController,
            reportVerification = reportVerification
        )
    }

    private fun validateReportAfterInControl(status: ReportStatus) {
        if (status.controlNotStartedYet())
            throw ReportNotInControl()
    }

}
