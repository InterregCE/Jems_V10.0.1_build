package io.cloudflight.jems.server.project.service.report.partner.expenditure.control.updateProjectPartnerReportExpenditureVerification

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.programme.service.typologyerrors.ProgrammeTypologyErrorsPersistence
import io.cloudflight.jems.server.project.authorization.CanEditPartnerControlReport
import io.cloudflight.jems.server.project.repository.report.expenditure.control.ExpenditureVerificationUpdate
import io.cloudflight.jems.server.project.service.report.model.partner.expenditure.control.ProjectPartnerReportExpenditureVerification
import io.cloudflight.jems.server.project.service.report.model.partner.expenditure.control.ProjectPartnerReportExpenditureVerificationUpdate
import io.cloudflight.jems.server.project.service.report.partner.expenditure.control.ProjectReportControlExpenditurePersistence
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal

@Service
class UpdateProjectPartnerControlReportExpenditureVerification(
    private val reportExpenditurePersistence: ProjectReportControlExpenditurePersistence,
    private val typologyPersistence: ProgrammeTypologyErrorsPersistence,
) : UpdateProjectPartnerControlReportExpenditureVerificationInteractor {

    @CanEditPartnerControlReport
    @Transactional
    @ExceptionWrapper(UpdateProjectPartnerControlReportExpenditureVerificationException::class)
    override fun updatePartnerReportExpenditureVerification(
        partnerId: Long,
        reportId: Long,
        expenditureVerification: List<ProjectPartnerReportExpenditureVerificationUpdate>
    ): List<ProjectPartnerReportExpenditureVerification> {
        val valuesToBeUpdated = reportExpenditurePersistence
            .getPartnerControlReportExpenditureVerification(partnerId, reportId = reportId)
            .updateWith(
                newValues = expenditureVerification.associateBy { it.id },
                allowedTypologyErrorIds = typologyPersistence.getAllTypologyErrors().mapTo(HashSet()) { it.id }
            )

        validateTypologyErrorIds(valuesToBeUpdated)

        return reportExpenditurePersistence.updatePartnerControlReportExpenditureVerification(
            partnerId = partnerId, reportId = reportId, valuesToBeUpdated.toUpdateModel()
        )
    }

    private fun validateTypologyErrorIds(expenditures: List<ProjectPartnerReportExpenditureVerification>) {
        val invalidTypologyIds = expenditures.filter {
            it.deductedAmount.isNotZero() && it.typologyOfErrorId == null
        }.mapTo(HashSet()) { it.id }

        if (invalidTypologyIds.isNotEmpty()) {
            throw TypologyOfErrorMissing(invalidTypologyIds)
        }
    }

    private fun List<ProjectPartnerReportExpenditureVerification>.updateWith(
        newValues: Map<Long, ProjectPartnerReportExpenditureVerificationUpdate>,
        allowedTypologyErrorIds: Set<Long>,
    ): List<ProjectPartnerReportExpenditureVerification> = map {
        it.apply {
            if (newValues.containsKey(it.id)) {
                partOfSample = newValues[it.id]!!.partOfSample
                certifiedAmount = newValues[it.id]!!.certifiedAmount
                deductedAmount = (declaredAmountAfterSubmission ?: BigDecimal.ZERO).minus(certifiedAmount)
                typologyOfErrorId = with(newValues[it.id]!!.typologyOfErrorId) {
                    return@with if (this == null || allowedTypologyErrorIds.contains(this)) this else null
                }
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
            verificationComment = it.verificationComment,
        )
    }

    private fun BigDecimal.isNotZero() = compareTo(BigDecimal.ZERO) != 0

}
