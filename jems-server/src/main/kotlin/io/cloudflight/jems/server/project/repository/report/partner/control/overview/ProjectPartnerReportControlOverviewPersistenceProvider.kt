package io.cloudflight.jems.server.project.repository.report.partner.control.overview

import io.cloudflight.jems.server.project.entity.report.control.overview.PartnerReportControlOverviewEntity
import io.cloudflight.jems.server.project.repository.report.partner.ProjectPartnerReportRepository
import io.cloudflight.jems.server.project.service.report.model.partner.control.overview.ControlOverview
import io.cloudflight.jems.server.project.service.report.partner.control.overview.ProjectPartnerReportControlOverviewPersistence
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

@Repository
class ProjectPartnerReportControlOverviewPersistenceProvider(
    private val controlOverviewRepository: ProjectPartnerReportControlOverviewRepository,
    private val reportRepository: ProjectPartnerReportRepository
): ProjectPartnerReportControlOverviewPersistence {

    @Transactional(readOnly = true)
    override fun getPartnerControlReportOverview(
        partnerId: Long,
        reportId: Long
    ): ControlOverview {
        return controlOverviewRepository.findByPartnerReportPartnerIdAndPartnerReportId(partnerId, reportId).toModel()
    }

    @Transactional
    override fun createPartnerControlReportOverview(partnerId: Long, reportId: Long, lastCertifiedReportId: Long?): ControlOverview {
        return controlOverviewRepository.save(
            PartnerReportControlOverviewEntity(
                partnerReportId = 0L,
                partnerReport = reportRepository.findByIdAndPartnerId(reportId, partnerId),
                startDate = LocalDate.now(),
                lastCertifiedReportIdWhenCreation = lastCertifiedReportId,
                requestsForClarifications = null,
                receiptOfSatisfactoryAnswers = null,
                endDate = null,
                findingDescription = null,
                followUpMeasuresFromLastReport = null,
                conclusion = null,
                followUpMeasuresForNextReport = null,
            )
        ).toModel()
    }

    @Transactional
    override fun updatePartnerControlReportOverview(partnerId: Long, reportId: Long, controlOverview: ControlOverview) =
        controlOverviewRepository.findByPartnerReportPartnerIdAndPartnerReportId(partnerId, reportId = reportId).apply {
            requestsForClarifications = controlOverview.requestsForClarifications
            receiptOfSatisfactoryAnswers = controlOverview.receiptOfSatisfactoryAnswers
            findingDescription = controlOverview.findingDescription
            followUpMeasuresFromLastReport = controlOverview.followUpMeasuresFromLastReport
            followUpMeasuresForNextReport = controlOverview.followUpMeasuresForNextReport
            conclusion = controlOverview.conclusion
        }.toModel()

    @Transactional
    override fun updatePartnerControlReportOverviewEndDate(
        partnerId: Long,
        reportId: Long,
        endDate: LocalDate
    ) {
        val controlReport = controlOverviewRepository.findByPartnerReportPartnerIdAndPartnerReportId(partnerId, reportId)
        controlReport.endDate = endDate
    }

}
