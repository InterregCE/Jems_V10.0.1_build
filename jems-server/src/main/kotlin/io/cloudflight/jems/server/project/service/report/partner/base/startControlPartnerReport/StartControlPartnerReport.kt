package io.cloudflight.jems.server.project.service.report.partner.base.startControlPartnerReport

import io.cloudflight.jems.plugin.contract.pre_condition_check.ControlReportSamplingCheckPlugin
import io.cloudflight.jems.server.call.service.CallPersistence
import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.controllerInstitution.service.ControllerInstitutionPersistence
import io.cloudflight.jems.server.notification.handler.PartnerReportStatusChanged
import io.cloudflight.jems.server.plugin.JemsPluginRegistry
import io.cloudflight.jems.server.project.authorization.CanEditPartnerControlReport
import io.cloudflight.jems.server.project.service.ProjectPersistence
import io.cloudflight.jems.server.project.service.partner.PartnerPersistence
import io.cloudflight.jems.server.project.service.report.partner.ProjectPartnerReportPersistence
import io.cloudflight.jems.server.project.service.report.model.partner.ProjectPartnerReport
import io.cloudflight.jems.server.project.service.report.model.partner.ReportStatus
import io.cloudflight.jems.server.project.service.report.partner.control.overview.ProjectPartnerReportControlOverviewPersistence
import io.cloudflight.jems.server.project.service.report.partner.expenditure.ProjectPartnerReportExpenditurePersistence
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
    private val auditPublisher: ApplicationEventPublisher,
    private val jemsPluginRegistry: JemsPluginRegistry,
    private val expenditurePersistence: ProjectPartnerReportExpenditurePersistence,
    private val callPersistence: CallPersistence,
    private val projectPersistence: ProjectPersistence,
) : StartControlPartnerReportInteractor {

    @CanEditPartnerControlReport
    @Transactional
    @ExceptionWrapper(StartControlPartnerReportException::class)
    override fun startControl(partnerId: Long, reportId: Long): ReportStatus {
        val report = reportPersistence.getPartnerReportById(partnerId = partnerId, reportId = reportId)
        validateReportIsSubmitted(report)

        val lastCertifiedReportId = reportPersistence.getLastCertifiedPartnerReportId(partnerId)
        controlOverviewPersistence.createPartnerControlReportOverview(partnerId, reportId, lastCertifiedReportId)

        // perform auto-sampling
        expenditurePersistence.markAsSampledAndLock(
            expenditureIds = getSampledExpenditureIdsFromPlugin(partnerId, reportId = reportId)
        )

        val institution = controlInstitutionPersistence.getControllerInstitutions(setOf(partnerId)).values.firstOrNull()
        reportDesignatedControllerPersistence.create(partnerId, reportId, institution!!.id)

        return reportPersistence.updateStatusAndTimes(
            partnerId = partnerId,
            reportId = reportId,
            status = ReportStatus.InControl,
        ).also {
            val projectId = partnerPersistence.getProjectIdForPartnerId(id = partnerId, it.version)
            val projectSummary = projectPersistence.getProjectSummary(projectId)

            auditPublisher.publishEvent(PartnerReportStatusChanged(this, projectSummary, it, report.status))
            auditPublisher.publishEvent(partnerReportStartedControl(this, projectId, it))
        }.status
    }

    private fun validateReportIsSubmitted(report: ProjectPartnerReport) {
        if (report.status != ReportStatus.Submitted)
            throw ReportNotSubmitted()
    }

    private fun getSampledExpenditureIdsFromPlugin(partnerId: Long, reportId: Long): Set<Long> {
        val pluginKey = callPersistence.getCallSimpleByPartnerId(partnerId).controlReportSamplingCheckPluginKey
        val plugin = jemsPluginRegistry.get(ControlReportSamplingCheckPlugin::class, key = pluginKey)
        return runCatching { plugin.check(partnerId = partnerId, reportId = reportId).sampledExpenditureIds }
            .getOrElse { setOf() }
    }
}
