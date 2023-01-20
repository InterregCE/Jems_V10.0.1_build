package io.cloudflight.jems.server.plugin.services.report

import io.cloudflight.jems.plugin.contract.models.report.partner.control.overview.ControlWorkOverviewData
import io.cloudflight.jems.plugin.contract.services.report.PartnerControlReportDataProvider
import io.cloudflight.jems.server.project.service.report.model.partner.identification.control.ReportVerification
import io.cloudflight.jems.server.project.service.report.partner.control.overview.getReportControlWorkOverview.GetReportControlWorkOverviewService
import io.cloudflight.jems.server.project.service.report.partner.identification.ProjectPartnerReportDesignatedControllerPersistence
import io.cloudflight.jems.server.project.service.report.partner.identification.ProjectPartnerReportVerificationPersistence
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional


@Service
class PartnerControlReportDataProviderImpl(
    private val designatedControllerPersistence: ProjectPartnerReportDesignatedControllerPersistence,
    private val reportVerificationPersistence: ProjectPartnerReportVerificationPersistence,
    private val controlReportOverviewService: GetReportControlWorkOverviewService
) : PartnerControlReportDataProvider {

    companion object {
        fun emptyVerification() = ReportVerification(
            generalMethodologies = emptySet(),
            verificationInstances = emptySet(),
            riskBasedVerificationApplied = false,
            riskBasedVerificationDescription = null
        )
    }

    @Transactional(readOnly = true)
    override fun getDesignatedController(partnerId: Long, reportId: Long) =
        designatedControllerPersistence.getControlReportDesignatedController(partnerId, reportId).toDataModel()

    @Transactional(readOnly = true)
    override fun getControlReportVerification(partnerId: Long, reportId: Long) =
        reportVerificationPersistence.getControlReportVerification(partnerId, reportId)
            .orElse(emptyVerification()).toDataModel()

    @Transactional(readOnly = true)
    override fun getControlWorkOverview(partnerId: Long, reportId: Long): ControlWorkOverviewData =
        controlReportOverviewService.get(partnerId, reportId).let {
            ControlWorkOverviewData(
                declaredByPartner = it.declaredByPartner,
                inControlSample = it.inControlSample,
                parked = it.parked,
                deductedByControl = it.deductedByControl,
                eligibleAfterControl = it.eligibleAfterControl,
                eligibleAfterControlPercentage = it.eligibleAfterControlPercentage
            )

    }
}