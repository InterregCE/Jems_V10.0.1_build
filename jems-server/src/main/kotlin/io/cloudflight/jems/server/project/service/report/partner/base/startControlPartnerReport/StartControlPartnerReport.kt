package io.cloudflight.jems.server.project.service.report.partner.base.startControlPartnerReport

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.controllerInstitution.service.ControllerInstitutionPersistence
import io.cloudflight.jems.server.project.authorization.CanEditPartnerControlReport
import io.cloudflight.jems.server.project.service.partner.PartnerPersistence
import io.cloudflight.jems.server.project.service.report.partner.ProjectPartnerReportPersistence
import io.cloudflight.jems.server.project.service.report.model.partner.ProjectPartnerReport
import io.cloudflight.jems.server.project.service.report.model.partner.ReportStatus
import io.cloudflight.jems.server.project.service.report.partner.control.overview.ProjectPartnerReportControlOverviewPersistence
import io.cloudflight.jems.server.project.service.report.partner.identification.ProjectPartnerReportDesignatedControllerPersistence
import io.cloudflight.jems.server.project.service.report.partner.partnerReportStartedControl
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class StartControlPartnerReport(
    private val reportPersistence: ProjectPartnerReportPersistence,
    private val partnerPersistence: PartnerPersistence,
    private val controlInstitutionPersistence: ControllerInstitutionPersistence,
    private val reportDesignatedControllerPersistence: ProjectPartnerReportDesignatedControllerPersistence,
    private val controlOverviewPersistence: ProjectPartnerReportControlOverviewPersistence,
    private val auditPublisher: ApplicationEventPublisher
) : StartControlPartnerReportInteractor {

    @CanEditPartnerControlReport
    @Transactional
    @ExceptionWrapper(StartControlPartnerReportException::class)
    override fun startControl(partnerId: Long, reportId: Long): ReportStatus {
        val report = reportPersistence.getPartnerReportById(partnerId = partnerId, reportId = reportId)
        validateReportIsSubmitted(report)

        val lastCertifiedReportId = reportPersistence.getLastCertifiedPartnerReportId(partnerId)
        controlOverviewPersistence.createPartnerControlReportOverview(partnerId, reportId, lastCertifiedReportId)

        val institution = controlInstitutionPersistence.getControllerInstitutions(setOf(partnerId)).values.firstOrNull()
        reportDesignatedControllerPersistence.create(partnerId, reportId, institution!!.id)

        return reportPersistence.startControlOnReportById(
            partnerId = partnerId,
            reportId = reportId,
        ).also {
            auditPublisher.publishEvent(
                partnerReportStartedControl(
                    context = this,
                    projectId = partnerPersistence.getProjectIdForPartnerId(id = partnerId, it.version),
                    report = it,
                )
            )
        }.status
    }

    private fun validateReportIsSubmitted(report: ProjectPartnerReport) {
        if (report.status != ReportStatus.Submitted)
            throw ReportNotSubmitted()
    }
}
