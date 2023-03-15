package io.cloudflight.jems.server.project.service.report.partner.control.expenditure.updateProjectPartnerReportExpenditureVerification

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.programme.service.typologyerrors.ProgrammeTypologyErrorsPersistence
import io.cloudflight.jems.server.project.authorization.CanEditPartnerControlReport
import io.cloudflight.jems.server.project.repository.report.partner.model.ExpenditureVerificationUpdate
import io.cloudflight.jems.server.project.service.report.model.partner.control.expenditure.ParkExpenditureData
import io.cloudflight.jems.server.project.service.report.model.partner.expenditure.control.ProjectPartnerReportExpenditureVerification
import io.cloudflight.jems.server.project.service.report.model.partner.expenditure.control.ProjectPartnerReportExpenditureVerificationUpdate
import io.cloudflight.jems.server.project.service.report.partner.control.expenditure.PartnerReportParkedExpenditurePersistence
import io.cloudflight.jems.server.project.service.report.partner.control.expenditure.ProjectPartnerReportExpenditureVerificationPersistence
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal

@Service
class UpdateProjectPartnerControlReportExpenditureVerification(
    private val reportExpenditurePersistence: ProjectPartnerReportExpenditureVerificationPersistence,
    private val reportParkedExpenditurePersistence: PartnerReportParkedExpenditurePersistence,
    private val typologyPersistence: ProgrammeTypologyErrorsPersistence
) : UpdateProjectPartnerControlReportExpenditureVerificationInteractor {

    @CanEditPartnerControlReport
    @Transactional
    @ExceptionWrapper(UpdateProjectPartnerControlReportExpenditureVerificationException::class)
    override fun updatePartnerReportExpenditureVerification(
        partnerId: Long,
        reportId: Long,
        expenditureVerification: List<ProjectPartnerReportExpenditureVerificationUpdate>
    ): List<ProjectPartnerReportExpenditureVerification> {
        val existingVerifications = reportExpenditurePersistence
            .getPartnerControlReportExpenditureVerification(partnerId, reportId = reportId)

        val parkedOldIds = existingVerifications.getParkedIds()
        val unparkedOldIds = existingVerifications.getNotParkedIds()

        val valuesToBeUpdated = existingVerifications
            .updateWith(
                newValues = expenditureVerification.associateBy { it.id },
                allowedTypologyErrorIds = typologyPersistence.getAllTypologyErrors().mapTo(HashSet()) { it.id }
            )

        validateParkedExpenditures(valuesToBeUpdated)
        validateTypologyErrorIds(valuesToBeUpdated)

        return reportExpenditurePersistence.updatePartnerControlReportExpenditureVerification(
            partnerId = partnerId, reportId = reportId, valuesToBeUpdated.toUpdateModel()
        ).also {
            updateParkedItems(
                reportId = reportId,
                parkedOldIds = parkedOldIds,
                unparkedOldIds = unparkedOldIds,
                newVerifications = it
            )
        }
    }

    private fun updateParkedItems(
        reportId: Long,
        parkedOldIds: List<Long>,
        unparkedOldIds: List<Long>,
        newVerifications: Collection<ProjectPartnerReportExpenditureVerification>,
    ) {
        val parkedNew = newVerifications.getParkedIds()
        val unparkedNew = newVerifications.getNotParkedIds()

        val newlyParked = parkedNew.minus(parkedOldIds)
        val newlyUnparked = unparkedNew.minus(unparkedOldIds)

        reportParkedExpenditurePersistence.parkExpenditures(
            newVerifications.filter { it.id in newlyParked }.toParkData(reportId)
        )
        reportParkedExpenditurePersistence.unParkExpenditures(newlyUnparked)
    }

    private fun Collection<ProjectPartnerReportExpenditureVerification>.getParkedIds() =
        filter { it.parked }.map { it.id }

    private fun Collection<ProjectPartnerReportExpenditureVerification>.getNotParkedIds() =
        filter { !it.parked }.map { it.id }

    private fun validateTypologyErrorIds(expenditures: List<ProjectPartnerReportExpenditureVerification>) {
        val invalidTypologyIds = expenditures.filter {
            it.deductedAmount.isNotZero() && it.typologyOfErrorId == null
        }.mapTo(HashSet()) { it.id }

        if (invalidTypologyIds.isNotEmpty()) {
            throw TypologyOfErrorMissing(invalidTypologyIds)
        }
    }

    private fun validateParkedExpenditures(expenditures: List<ProjectPartnerReportExpenditureVerification>) {
        val invalidExpenditureIds = expenditures.filter {
            it.parked && (it.typologyOfErrorId != null || it.certifiedAmount.isNotZero())
        }.mapTo(HashSet()) { it.id }

        if (invalidExpenditureIds.isNotEmpty()) {
            throw InvalidParkedExpenditure(invalidExpenditureIds)
        }
    }

    private fun List<ProjectPartnerReportExpenditureVerification>.updateWith(
        newValues: Map<Long, ProjectPartnerReportExpenditureVerificationUpdate>,
        allowedTypologyErrorIds: Set<Long>,
    ): List<ProjectPartnerReportExpenditureVerification> = map {
        it.apply {
            if (newValues.containsKey(it.id)) {
                certifiedAmount = newValues[it.id]!!.certifiedAmount
                deductedAmount = if (newValues[it.id]!!.parked) BigDecimal.ZERO else
                    (declaredAmountAfterSubmission ?: BigDecimal.ZERO).minus(certifiedAmount)
                partOfSample = (deductedAmount.compareTo(BigDecimal.ZERO) != 0) || newValues[it.id]!!.parked || newValues[it.id]!!.partOfSample
                typologyOfErrorId = with(newValues[it.id]!!.typologyOfErrorId) {
                    return@with if (this == null || allowedTypologyErrorIds.contains(this)) this else null
                }
                parked = newValues[it.id]!!.parked
                verificationComment = newValues[it.id]!!.verificationComment
            }
        }
    }

    private fun Collection<ProjectPartnerReportExpenditureVerification>.toUpdateModel() = map {
        ExpenditureVerificationUpdate(
            id = it.id,
            partOfSample = it.partOfSample,
            certifiedAmount = it.certifiedAmount,
            deductedAmount = it.deductedAmount,
            typologyOfErrorId = it.typologyOfErrorId,
            parked = it.parked,
            verificationComment = it.verificationComment,
        )
    }

    private fun BigDecimal.isNotZero() = compareTo(BigDecimal.ZERO) != 0

    private fun Collection<ProjectPartnerReportExpenditureVerification>.toParkData(reportId: Long) = map {
        ParkExpenditureData(
            expenditureId = it.id,
            originalReportId = it.parkingMetadata?.reportOfOriginId ?: reportId,
            originalNumber = it.parkingMetadata?.originalExpenditureNumber ?: it.number
        )
    }
}
