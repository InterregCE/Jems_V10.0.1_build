package io.cloudflight.jems.server.project.service.report.partner.control.expenditure.updateProjectPartnerReportExpenditureVerification

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.programme.service.typologyerrors.ProgrammeTypologyErrorsPersistence
import io.cloudflight.jems.server.project.authorization.CanEditPartnerControlReport
import io.cloudflight.jems.server.project.repository.report.partner.model.ExpenditureVerificationUpdate
import io.cloudflight.jems.server.project.service.partner.PartnerPersistence
import io.cloudflight.jems.server.project.service.report.model.partner.control.expenditure.ParkExpenditureData
import io.cloudflight.jems.server.project.service.report.model.partner.expenditure.control.ProjectPartnerReportExpenditureVerification
import io.cloudflight.jems.server.project.service.report.model.partner.expenditure.control.ProjectPartnerReportExpenditureVerificationUpdate
import io.cloudflight.jems.server.project.service.report.partner.ProjectPartnerReportPersistence
import io.cloudflight.jems.server.project.service.report.partner.control.expenditure.PartnerReportParkedExpenditurePersistence
import io.cloudflight.jems.server.project.service.report.partner.control.expenditure.ProjectPartnerReportExpenditureVerificationPersistence
import io.cloudflight.jems.server.project.service.report.partner.partnerReportExpenditureParked
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal

@Service
class UpdateProjectPartnerControlReportExpenditureVerification(
    private val reportExpenditurePersistence: ProjectPartnerReportExpenditureVerificationPersistence,
    private val reportParkedExpenditurePersistence: PartnerReportParkedExpenditurePersistence,
    private val typologyPersistence: ProgrammeTypologyErrorsPersistence,
    private val partnerPersistence: PartnerPersistence,
    private val reportPersistence: ProjectPartnerReportPersistence,
    private val auditPublisher: ApplicationEventPublisher,
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
                partnerId = partnerId,
                reportId = reportId,
                parkedOldIds = parkedOldIds,
                unparkedOldIds = unparkedOldIds,
                newVerifications = it
            )
        }
    }

    private fun updateParkedItems(
        partnerId: Long,
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

        if (newlyParked.isNotEmpty() || newlyUnparked.isNotEmpty())
            publishAuditLogs(
                partnerId,
                reportId,
                newVerifications.filter { it.id in newlyParked },
                newVerifications.filter { it.id in newlyUnparked }
            )
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
                partOfSample = newValues[it.id]!!.parked || newValues[it.id]!!.partOfSample
                certifiedAmount = newValues[it.id]!!.certifiedAmount
                deductedAmount = if (newValues[it.id]!!.parked) BigDecimal.ZERO else
                    (declaredAmountAfterSubmission ?: BigDecimal.ZERO).minus(certifiedAmount)
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

    private fun publishAuditLogs(
        partnerId: Long,
        reportId: Long,
        parked: Collection<ProjectPartnerReportExpenditureVerification>,
        unparked: Collection<ProjectPartnerReportExpenditureVerification>
    ) {
        val report = reportPersistence.getPartnerReportById(partnerId = partnerId, reportId = reportId)
        val projectId = partnerPersistence.getProjectIdForPartnerId(id = partnerId, report.version)
        if (parked.isNotEmpty()) {
            auditPublisher.publishEvent(
                partnerReportExpenditureParked(
                    context = this,
                    projectId = projectId,
                    partnerReport = report,
                    isParked = true,
                    expenditureIds = parked.map { it.number }
                )
            )
        }
        if (unparked.isNotEmpty()) {
            auditPublisher.publishEvent(
                partnerReportExpenditureParked(
                    context = this,
                    projectId = projectId,
                    partnerReport = report,
                    isParked = false,
                    expenditureIds = unparked.map { it.number }
                )
            )
        }
    }

    private fun Collection<ProjectPartnerReportExpenditureVerification>.toParkData(reportId: Long) = map {
        ParkExpenditureData(
            expenditureId = it.id,
            originalReportId = it.parkingMetadata?.reportOfOriginId ?: reportId,
            originalNumber = it.parkingMetadata?.originalExpenditureNumber ?: it.number
        )
    }

}
