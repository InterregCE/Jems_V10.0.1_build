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
                startDate = LocalDate.now(),
                partnerReport = reportRepository.findByIdAndPartnerId(reportId, partnerId),
                lastCertifiedReportIdWhenCreation = lastCertifiedReportId
            )
        ).toModel()
    }

    @Transactional
    override fun updatePartnerControlReportOverview(
        partnerId: Long,
        reportId: Long,
        controlOverview: ControlOverview
    ): ControlOverview {
        return controlOverviewRepository.save(
            PartnerReportControlOverviewEntity(
                partnerReportId = reportId,
                startDate = controlOverview.startDate,
                partnerReport = reportRepository.findByIdAndPartnerId(reportId, partnerId),
                requestsForClarifications = controlOverview.requestsForClarifications,
                receiptOfSatisfactoryAnswers = controlOverview.receiptOfSatisfactoryAnswers,
                endDate = controlOverview.endDate,
                findingDescription = controlOverview.findingDescription,
                followUpMeasuresForNextReport = controlOverview.followUpMeasuresForNextReport,
                followUpMeasuresFromLastReport = controlOverview.followUpMeasuresFromLastReport,
                conclusion = controlOverview.conclusion
            )
        ).toModel()
    }

    @Transactional
    override fun updatePartnerControlReportOverviewEndDate(
        partnerId: Long,
        reportId: Long,
        endDate: LocalDate
    ): ControlOverview {
        val controlOverview = controlOverviewRepository.findByPartnerReportPartnerIdAndPartnerReportId(partnerId, reportId).toModel()
        return controlOverviewRepository.save(
            PartnerReportControlOverviewEntity(
                partnerReportId = reportId,
                startDate = controlOverview.startDate,
                partnerReport = reportRepository.findByIdAndPartnerId(reportId, partnerId),
                requestsForClarifications = controlOverview.requestsForClarifications,
                receiptOfSatisfactoryAnswers = controlOverview.receiptOfSatisfactoryAnswers,
                endDate = endDate,
                findingDescription = controlOverview.findingDescription,
                followUpMeasuresForNextReport = controlOverview.followUpMeasuresForNextReport,
                followUpMeasuresFromLastReport = controlOverview.followUpMeasuresFromLastReport,
                conclusion = controlOverview.conclusion
            )
        ).toModel()
    }
}
