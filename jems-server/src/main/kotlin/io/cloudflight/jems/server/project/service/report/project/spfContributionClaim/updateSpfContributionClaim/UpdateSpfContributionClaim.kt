package io.cloudflight.jems.server.project.service.report.project.spfContributionClaim.updateSpfContributionClaim

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.project.authorization.CanEditProjectReport
import io.cloudflight.jems.server.project.repository.report.project.spfContributionClaim.ProjectReportSpfContributionClaimPersistenceProvider
import io.cloudflight.jems.server.project.service.report.model.project.ProjectReportStatus
import io.cloudflight.jems.server.project.service.report.model.project.spfContributionClaim.ProjectReportSpfContributionClaim
import io.cloudflight.jems.server.project.service.report.model.project.spfContributionClaim.ProjectReportSpfContributionClaimUpdate
import io.cloudflight.jems.server.project.service.report.project.base.ProjectReportPersistence
import io.cloudflight.jems.server.project.service.report.project.spfContributionClaim.fillInTotalReportedSoFar
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UpdateSpfContributionClaim(
    private val projectReportSpfContributionClaimPersistenceProvider: ProjectReportSpfContributionClaimPersistenceProvider,
    private val reportPersistence: ProjectReportPersistence
): UpdateSpfContributionClaimInteractor {

    @CanEditProjectReport
    @Transactional
    @ExceptionWrapper(UpdateSpfContributionClaimException::class)
    override fun update(
        projectId: Long,
        reportId: Long,
        toUpdate: List<ProjectReportSpfContributionClaimUpdate>
    ): List<ProjectReportSpfContributionClaim> {
        validateReportStatus(status = reportPersistence.getReportById(projectId, reportId = reportId).status)

        val existingContributions = projectReportSpfContributionClaimPersistenceProvider.getSpfContributionClaimsFor(reportId)
        validateData(existingData = existingContributions, newData = toUpdate)

        val idToUpdatedAmount = toUpdate.associateBy ({it.id}, {it.currentlyReported})
        return projectReportSpfContributionClaimPersistenceProvider.updateContributionClaimReportedAmount(
            reportId,  idToUpdatedAmount
        ).also { it.fillInTotalReportedSoFar() }
    }


    private fun validateReportStatus(status: ProjectReportStatus) {
        if(status.isClosed() || !status.isOpenForNumbersChanges())
            throw ReportStatusNotValidException()
    }


    fun validateData(
        existingData: List<ProjectReportSpfContributionClaim>,
        newData: List<ProjectReportSpfContributionClaimUpdate>) {
        val existingIds = existingData.map { it.id }
        val newDataIds = newData.map { it.id }

        if (existingIds != newDataIds) {
            throw ContributionSourcesException()
        }
    }

}
