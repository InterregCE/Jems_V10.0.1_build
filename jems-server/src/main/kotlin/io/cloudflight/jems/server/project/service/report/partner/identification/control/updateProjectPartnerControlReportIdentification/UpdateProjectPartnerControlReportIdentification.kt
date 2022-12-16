package io.cloudflight.jems.server.project.service.report.partner.identification.control.updateProjectPartnerControlReportIdentification

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.programme.repository.ProgrammeDataRepository
import io.cloudflight.jems.server.project.authorization.CanEditPartnerControlReport
import io.cloudflight.jems.server.project.service.ProjectPersistence
import io.cloudflight.jems.server.project.service.contracting.monitoring.getProjectContractingMonitoring.GetContractingMonitoringService
import io.cloudflight.jems.server.project.service.partner.PartnerPersistence
import io.cloudflight.jems.server.project.service.report.partner.ProjectPartnerReportPersistence
import io.cloudflight.jems.server.project.service.report.model.partner.ReportStatus
import io.cloudflight.jems.server.project.service.report.model.partner.identification.control.ProjectPartnerControlReport
import io.cloudflight.jems.server.project.service.report.model.partner.identification.control.ProjectPartnerControlReportChange
import io.cloudflight.jems.server.project.service.report.partner.identification.ProjectPartnerReportIdentificationPersistence
import io.cloudflight.jems.server.project.service.report.model.partner.identification.control.ReportVerification
import io.cloudflight.jems.server.project.service.report.partner.identification.ProjectPartnerReportDesignatedControllerPersistence
import io.cloudflight.jems.server.project.service.report.partner.identification.ProjectPartnerReportVerificationPersistence
import io.cloudflight.jems.server.project.service.report.partner.identification.control.toModelObject
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UpdateProjectPartnerControlReportIdentification(
    private val reportPersistence: ProjectPartnerReportPersistence,
    private val reportIdentificationPersistence: ProjectPartnerReportIdentificationPersistence,
    private val reportDesignatedControllerPersistence: ProjectPartnerReportDesignatedControllerPersistence,
    private val reportReportVerificationPersistence: ProjectPartnerReportVerificationPersistence,
    private val partnerPersistence: PartnerPersistence,
    private val projectPersistence: ProjectPersistence,
    private val programmeDataRepository: ProgrammeDataRepository,
    private val getContractingMonitoringService: GetContractingMonitoringService,
) : UpdateProjectPartnerControlReportIdentificationInteractor {

    @CanEditPartnerControlReport
    @Transactional
    @ExceptionWrapper(UpdateProjectPartnerControlReportIdentificationException::class)
    override fun updateControlIdentification(
        partnerId: Long,
        reportId: Long,
        data: ProjectPartnerControlReportChange,
    ): ProjectPartnerControlReport {
        val report = reportPersistence.getPartnerReportById(partnerId, reportId = reportId)
        validateReportInControl(status = report.status)
        validateVerificationSize(data.reportVerification)

        val identification = reportIdentificationPersistence.updatePartnerControlReportIdentification(
            partnerId = partnerId,
            reportId = reportId,
            data = data,
        )

        val designatedController = reportDesignatedControllerPersistence.updateDesignatedController(
            partnerId,
            reportId,
            data.designatedController
        )

        val reportVerification = reportReportVerificationPersistence.updateReportVerification(
            partnerId,
            reportId,
            data.reportVerification
        )

        val projectId = partnerPersistence.getProjectIdForPartnerId(partnerId, report.version)

        return toModelObject(
            report = report,
            projectTitle = projectPersistence.getProject(projectId = projectId, version = report.version).title,
            programmeTitle = programmeDataRepository.findById(1L).orElse(null)?.title,
            startAndEndDate = getContractingMonitoringService.getContractMonitoringDates(projectId),
            identification = identification,
            designatedController = designatedController,
            reportVerification = reportVerification
        )
    }

    private fun validateReportInControl(status: ReportStatus) {
        if (status.controlNotOpenAnymore())
            throw ReportNotInControl()
    }

    private fun validateVerificationSize(verification: ReportVerification) {
        if (verification.verificationInstances.size > 5)
            throw ControlIdentificationTooManyVerifications()
    }
}
