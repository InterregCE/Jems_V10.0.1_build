package io.cloudflight.jems.server.project.repository.report.project.financialOverview.costCategory

import io.cloudflight.jems.server.project.service.report.model.project.financialOverview.costCategory.CertificateCostCategoryCurrentlyReported
import io.cloudflight.jems.server.project.service.report.model.project.financialOverview.costCategory.CertificateCostCategoryPreviouslyReported
import io.cloudflight.jems.server.project.service.report.model.project.financialOverview.costCategory.ReportCertificateCostCategory
import io.cloudflight.jems.server.project.service.report.project.financialOverview.ProjectReportCertificateCostCategoryPersistence
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
class ProjectReportCertificateCostCategoryPersistenceProvider(
    private val certificateCostCategoryRepository: ReportProjectCertificateCostCategoryRepository,
) : ProjectReportCertificateCostCategoryPersistence {

    @Transactional(readOnly = true)
    override fun getCostCategories(projectId: Long, reportId: Long): ReportCertificateCostCategory =
        certificateCostCategoryRepository
            .findFirstByReportEntityProjectIdAndReportEntityId(projectId = projectId, reportId = reportId)
            .toModel()

    @Transactional(readOnly = true)
    override fun getCostCategoriesCumulative(reportIds: Set<Long>) = CertificateCostCategoryPreviouslyReported(
        certificateCostCategoryRepository.findCumulativeForReportIds(reportIds)
    )

    @Transactional
    override fun updateCurrentlyReportedValues(
        projectId: Long,
        reportId: Long,
        currentlyReported: CertificateCostCategoryCurrentlyReported
    ) {
        certificateCostCategoryRepository
            .findFirstByReportEntityProjectIdAndReportEntityId(projectId = projectId, reportId = reportId).apply {
                staffCurrent = currentlyReported.currentlyReported.staff
                officeCurrent = currentlyReported.currentlyReported.office
                travelCurrent = currentlyReported.currentlyReported.travel
                externalCurrent = currentlyReported.currentlyReported.external
                equipmentCurrent = currentlyReported.currentlyReported.equipment
                infrastructureCurrent = currentlyReported.currentlyReported.infrastructure
                otherCurrent = currentlyReported.currentlyReported.other
                lumpSumCurrent = currentlyReported.currentlyReported.lumpSum
                unitCostCurrent = currentlyReported.currentlyReported.unitCost
                sumCurrent = currentlyReported.currentlyReported.sum
            }
    }
}
