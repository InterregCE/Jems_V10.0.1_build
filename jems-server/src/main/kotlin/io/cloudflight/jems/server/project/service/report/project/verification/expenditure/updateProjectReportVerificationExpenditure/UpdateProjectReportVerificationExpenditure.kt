package io.cloudflight.jems.server.project.service.report.project.verification.expenditure.updateProjectReportVerificationExpenditure

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.project.authorization.CanEditProjectReportVerificationByReportId
import io.cloudflight.jems.server.project.service.report.model.partner.control.expenditure.ParkExpenditureData
import io.cloudflight.jems.server.project.service.report.model.project.verification.expenditure.ProjectReportVerificationExpenditureLine
import io.cloudflight.jems.server.project.service.report.model.project.verification.expenditure.ProjectReportVerificationExpenditureLineUpdate
import io.cloudflight.jems.server.project.service.report.partner.control.expenditure.PartnerReportParkedExpenditurePersistence
import io.cloudflight.jems.server.project.service.report.project.verification.expenditure.ProjectReportVerificationExpenditurePersistence
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.ZonedDateTime

@Service
class UpdateProjectReportVerificationExpenditure(
    private val projectReportExpenditureVerificationPersistence: ProjectReportVerificationExpenditurePersistence,
    private val partnerReportParkedExpenditurePersistence: PartnerReportParkedExpenditurePersistence,
): UpdateProjectReportVerificationExpenditureInteractor {

    @CanEditProjectReportVerificationByReportId
    @Transactional
    @ExceptionWrapper(UpdateProjectReportVerificationExpenditureException::class)
    override fun updateExpenditureVerification(
        projectReportId: Long,
        expenditureVerificationUpdate: List<ProjectReportVerificationExpenditureLineUpdate>
    ): List<ProjectReportVerificationExpenditureLine> {

        val existingExpenditures = projectReportExpenditureVerificationPersistence
            .getProjectReportExpenditureVerification(projectReportId)

        val parkedOldIds = existingExpenditures.getParkedIds()
        val unparkedOldIds = existingExpenditures.getNotParkedIds()

        return projectReportExpenditureVerificationPersistence.updateProjectReportExpenditureVerification(
            projectReportId = projectReportId, expenditureVerificationUpdate
        ).also {
            updateParkedItems(
                projectReportId = projectReportId,
                parkedOldIds = parkedOldIds,
                unparkedOldIds = unparkedOldIds,
                newVerifications = it
            )
        }
    }

    private fun updateParkedItems(
        projectReportId: Long,
        parkedOldIds: List<Long>,
        unparkedOldIds: List<Long>,
        newVerifications: Collection<ProjectReportVerificationExpenditureLine>,
    ) {
        val parkedNew = newVerifications.getParkedIds()
        val unParkedNew = newVerifications.getNotParkedIds()

        val newlyParked = parkedNew.minus(parkedOldIds)
        val newlyUnParkedExpenditureIds = unParkedNew.minus(unparkedOldIds)

        partnerReportParkedExpenditurePersistence.parkExpenditures(
            newVerifications.filter { it.expenditure.id in newlyParked }.toParkData(projectReportId)
        )
        partnerReportParkedExpenditurePersistence.unParkExpenditures(newlyUnParkedExpenditureIds)
    }

    private fun Collection<ProjectReportVerificationExpenditureLine>.getParkedIds() =
        filter { it.parked }.map { it.expenditure.id!! }

    private fun Collection<ProjectReportVerificationExpenditureLine>.getNotParkedIds() =
        filter { !it.parked }.map { it.expenditure.id!! }

    private fun Collection<ProjectReportVerificationExpenditureLine>.toParkData(projectReportId: Long) = map {
        ParkExpenditureData(
            expenditureId = it.expenditure.id,
            originalReportId = it.expenditure.parkingMetadata?.reportOfOriginId
                ?: it.expenditure.partnerReportId, // if it was not parked, current report is report of origin
            originalProjectReportId = it.expenditure.parkingMetadata?.reportProjectOfOriginId ?: projectReportId,
            originalNumber = it.expenditure.parkingMetadata?.originalExpenditureNumber ?: it.expenditure.number,
            parkedOn = ZonedDateTime.now()
        )
    }
}
