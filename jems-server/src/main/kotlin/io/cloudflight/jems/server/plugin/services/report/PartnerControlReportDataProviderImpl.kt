package io.cloudflight.jems.server.plugin.services.report

import io.cloudflight.jems.plugin.contract.models.report.partner.control.ProjectPartnerReportExpenditureVerificationData
import io.cloudflight.jems.plugin.contract.models.report.partner.control.overview.ControlDeductionOverviewData
import io.cloudflight.jems.plugin.contract.models.report.partner.control.overview.ControlOverviewData
import io.cloudflight.jems.plugin.contract.models.report.partner.control.overview.ControlWorkOverviewData
import io.cloudflight.jems.plugin.contract.services.report.PartnerControlReportDataProvider
import io.cloudflight.jems.server.project.service.report.model.partner.identification.control.ReportVerification
import io.cloudflight.jems.server.project.service.report.partner.control.expenditure.ProjectPartnerReportExpenditureVerificationPersistence
import io.cloudflight.jems.server.project.service.report.partner.control.overview.getReportControlDeductionOverview.GetReportControlDeductionOverviewCalculator
import io.cloudflight.jems.server.project.service.report.partner.control.overview.getReportControlOverview.GetReportControlOverviewCalculator
import io.cloudflight.jems.server.project.service.report.partner.control.overview.getReportControlWorkOverview.GetReportControlWorkOverviewService
import io.cloudflight.jems.server.project.service.report.partner.identification.ProjectPartnerReportDesignatedControllerPersistence
import io.cloudflight.jems.server.project.service.report.partner.identification.ProjectPartnerReportVerificationPersistence
import io.cloudflight.jems.server.user.service.UserPersistence
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional


@Service
class PartnerControlReportDataProviderImpl(
    private val designatedControllerPersistence: ProjectPartnerReportDesignatedControllerPersistence,
    private val reportVerificationPersistence: ProjectPartnerReportVerificationPersistence,
    private val reportControlOverviewService: GetReportControlOverviewCalculator,
    private val reportControlWorkOverviewService: GetReportControlWorkOverviewService,
    private val reportExpenditurePersistence: ProjectPartnerReportExpenditureVerificationPersistence,
    private val reportControlDeductionOverviewService: GetReportControlDeductionOverviewCalculator,
    private val userPersistence: UserPersistence,
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
        designatedControllerPersistence.getControlReportDesignatedController(partnerId, reportId)
            .let { reportDesignatedController ->
                reportDesignatedController.toDataModel(
                    userPersistence.findAllByIds(
                        listOf(
                            reportDesignatedController.controllingUserId ?: -1,
                            reportDesignatedController.controllerReviewerId ?: -1
                        )
                    ).associateBy { it.id }
                )
            }

    @Transactional(readOnly = true)
    override fun getControlReportVerification(partnerId: Long, reportId: Long) =
        reportVerificationPersistence.getControlReportVerification(partnerId, reportId)
            .orElse(emptyVerification()).toDataModel()


    @Transactional(readOnly = true)
    override fun getControlOverview(partnerId: Long, reportId: Long): ControlOverviewData =
        reportControlOverviewService.get(partnerId, reportId).toDataModel()

    @Transactional(readOnly = true)
    override fun getControlWorkOverview(partnerId: Long, reportId: Long): ControlWorkOverviewData =
        reportControlWorkOverviewService.get(partnerId, reportId).toDataModel()

    @Transactional(readOnly = true)
    override fun getExpenditureVerification(
        partnerId: Long,
        reportId: Long
    ): List<ProjectPartnerReportExpenditureVerificationData> =
        reportExpenditurePersistence.getPartnerControlReportExpenditureVerification(partnerId, reportId)
            .toModelDataList()

    @Transactional(readOnly = true)
    override fun getReportControlDeductionOverview(
        partnerId: Long,
        reportId: Long,
    ): ControlDeductionOverviewData {
       return reportControlDeductionOverviewService.get(partnerId, reportId).toDataModel()
    }
}
