package io.cloudflight.jems.server.project.repository.report.expenditure

import io.cloudflight.jems.server.project.repository.report.ProjectPartnerReportRepository
import io.cloudflight.jems.server.project.service.report.model.expenditure.ProjectPartnerReportExpenditureCost
import io.cloudflight.jems.server.project.service.report.partner.expenditure.ProjectReportExpenditurePersistence
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import kotlin.collections.HashSet

@Repository
class ProjectReportExpenditurePersistenceProvider(
    private val reportRepository: ProjectPartnerReportRepository,
    private val reportExpenditureRepository: ProjectPartnerReportExpenditureRepository,
) : ProjectReportExpenditurePersistence {

    @Transactional(readOnly = true)
    override fun getPartnerReportExpenditureCosts(
        partnerId: Long,
        reportId: Long,
    ): List<ProjectPartnerReportExpenditureCost> =
        reportExpenditureRepository.findTop150ByPartnerReportIdAndPartnerReportPartnerIdOrderById(
            partnerId = partnerId,
            reportId = reportId,
        ).toModel()

    @Transactional
    override fun updatePartnerReportExpenditureCosts(
        partnerId: Long,
        reportId: Long,
        expenditureCosts: List<ProjectPartnerReportExpenditureCost>,
    ): List<ProjectPartnerReportExpenditureCost> {
        val reportEntity = reportRepository.findByIdAndPartnerId(partnerId = partnerId, id = reportId)

        val toNotBeDeletedIds = expenditureCosts.mapNotNullTo(HashSet()) { it.id }
        val existingIds = reportExpenditureRepository.findExistingExpenditureIdsFor(reportEntity)

        reportExpenditureRepository.deleteAllById(existingIds.minus(toNotBeDeletedIds))
        return reportExpenditureRepository.saveAll(expenditureCosts.toEntities(reportEntity)).toModel()
    }

}
