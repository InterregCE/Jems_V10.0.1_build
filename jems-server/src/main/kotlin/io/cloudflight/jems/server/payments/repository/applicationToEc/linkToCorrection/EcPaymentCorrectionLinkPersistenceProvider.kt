package io.cloudflight.jems.server.payments.repository.applicationToEc.linkToCorrection

import com.querydsl.core.Tuple
import com.querydsl.core.types.dsl.BooleanExpression
import com.querydsl.jpa.impl.JPAQueryFactory
import io.cloudflight.jems.server.payments.entity.PaymentToEcCorrectionExtensionEntity
import io.cloudflight.jems.server.payments.entity.QPaymentToEcCorrectionExtensionEntity
import io.cloudflight.jems.server.payments.model.ec.CorrectionInEcPaymentMetadata
import io.cloudflight.jems.server.payments.model.ec.EcPaymentCorrectionExtension
import io.cloudflight.jems.server.payments.model.ec.PaymentToEcCorrectionLinkingUpdate
import io.cloudflight.jems.server.payments.model.regular.PaymentSearchRequestScoBasis
import io.cloudflight.jems.server.payments.repository.applicationToEc.PaymentApplicationsToEcRepository
import io.cloudflight.jems.server.payments.repository.regular.joinWithAnd
import io.cloudflight.jems.server.payments.service.ecPayment.linkToCorrection.EcPaymentCorrectionLinkPersistence
import io.cloudflight.jems.server.project.entity.auditAndControl.QAuditControlCorrectionEntity
import io.cloudflight.jems.server.project.entity.contracting.QProjectContractingMonitoringEntity
import io.cloudflight.jems.server.project.repository.auditAndControl.correction.AuditControlCorrectionRepository
import io.cloudflight.jems.server.project.service.auditAndControl.model.AuditControlStatus
import io.cloudflight.jems.server.project.service.auditAndControl.model.ProjectCorrectionFinancialDescription
import io.cloudflight.jems.server.project.service.contracting.model.ContractingMonitoringExtendedOption
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal

@Repository
class EcPaymentCorrectionLinkPersistenceProvider(
    private val ecPaymentRepository: PaymentApplicationsToEcRepository,
    private val ecPaymentCorrectionExtensionRepository: EcPaymentCorrectionExtensionRepository,
    private val auditControlCorrectionRepository: AuditControlCorrectionRepository,
    private val jpaQueryFactory: JPAQueryFactory
) : EcPaymentCorrectionLinkPersistence {

    @Transactional(readOnly = true)
    override fun getCorrectionExtension(correctionId: Long): EcPaymentCorrectionExtension =
        ecPaymentCorrectionExtensionRepository.getById(correctionId).toModel()

    @Transactional
    override fun selectCorrectionToEcPayment(correctionIds: Set<Long>, ecPaymentId: Long) {
        val ecPayment = ecPaymentRepository.getById(ecPaymentId)
        ecPaymentCorrectionExtensionRepository.findAllById(correctionIds).forEach {
            it.paymentApplicationToEc = ecPayment
        }
    }

    @Transactional(readOnly = true)
    override fun getCorrectionsLinkedToEcPayment(ecPaymentId: Long): Map<Long, CorrectionInEcPaymentMetadata> {
        val paymentToEcCorrectionExtension = QPaymentToEcCorrectionExtensionEntity.paymentToEcCorrectionExtensionEntity
        val correction = QAuditControlCorrectionEntity.auditControlCorrectionEntity
        val contractingMonitoring = QProjectContractingMonitoringEntity.projectContractingMonitoringEntity

        val results = jpaQueryFactory.select(
            correction.id,
            correction.auditControl.number,
            correction.orderNr,
            correction.auditControl.project.id,

            paymentToEcCorrectionExtension.finalScoBasis,
            contractingMonitoring.typologyProv94,
            contractingMonitoring.typologyProv95,
        )
            .from(paymentToEcCorrectionExtension)
            .leftJoin(correction)
            .on(correction.id.eq(paymentToEcCorrectionExtension.correction.id))
            .leftJoin(contractingMonitoring)
            .on(correction.auditControl.project.id.eq(contractingMonitoring.projectId))
            .where(paymentToEcCorrectionExtension.paymentApplicationToEc.id.eq(ecPaymentId))
            .fetch()
            .map { it: Tuple ->
                CorrectionInEcPaymentMetadata(
                    correctionId = it.get(0, Long::class.java)!!,
                    auditControlNr = it.get(1, Int::class.java)!!,
                    correctionNr = it.get(2, Int::class.java)!!,
                    projectId = it.get(3, Long::class.java)!!,

                    finalScoBasis = it.get(4, PaymentSearchRequestScoBasis::class.java),
                    typologyProv94 = it.get(5, ContractingMonitoringExtendedOption::class.java)!!,
                    typologyProv95 = it.get(6, ContractingMonitoringExtendedOption::class.java)!!,
                )
            }

        return results.associateBy { it.correctionId }

    }

    @Transactional
    override fun deselectCorrectionFromEcPaymentAndResetFields(correctionIds: Set<Long>) {
        ecPaymentCorrectionExtensionRepository.findAllById(correctionIds).forEach {
            it.paymentApplicationToEc = null
            it.correctedPublicContribution = it.publicContribution
            it.correctedAutoPublicContribution = it.autoPublicContribution
            it.correctedPrivateContribution = it.privateContribution
        }
    }

    @Transactional
    override fun updateCorrectionLinkedToEcPaymentCorrectedAmounts(
        correctionId: Long,
        ecPaymentCorrectionLinkingUpdate: PaymentToEcCorrectionLinkingUpdate
    ): EcPaymentCorrectionExtension =
        ecPaymentCorrectionExtensionRepository.getById(correctionId).apply {
            this.correctedAutoPublicContribution = ecPaymentCorrectionLinkingUpdate.correctedAutoPublicContribution
            this.correctedPublicContribution = ecPaymentCorrectionLinkingUpdate.correctedPublicContribution
            this.correctedPrivateContribution = ecPaymentCorrectionLinkingUpdate.correctedPrivateContribution
            this.comment = ecPaymentCorrectionLinkingUpdate.comment
        }.toModel()

    @Transactional
    override fun updatePaymentToEcFinalScoBasis(toUpdate: Map<Long, PaymentSearchRequestScoBasis>) {
        ecPaymentCorrectionExtensionRepository.findAllById(toUpdate.keys).forEach {
            it.finalScoBasis = toUpdate[it.correctionId]!!
        }
    }

    @Transactional
    override fun createCorrectionExtension(financialDescription: ProjectCorrectionFinancialDescription) {
        val correctionEntity = auditControlCorrectionRepository.getById(financialDescription.correctionId)
        val correctionExtensionEntity = PaymentToEcCorrectionExtensionEntity(
            correctionId = financialDescription.correctionId,
            correction = correctionEntity,
            paymentApplicationToEc = null,
            fundAmount = financialDescription.fundAmount.negateIf(financialDescription.deduction),
            publicContribution = financialDescription.publicContribution.negateIf(financialDescription.deduction),
            correctedPublicContribution = financialDescription.publicContribution.negateIf(financialDescription.deduction),
            autoPublicContribution = financialDescription.autoPublicContribution.negateIf(financialDescription.deduction),
            correctedAutoPublicContribution = financialDescription.autoPublicContribution.negateIf(financialDescription.deduction),
            privateContribution = financialDescription.privateContribution.negateIf(financialDescription.deduction),
            correctedPrivateContribution = financialDescription.privateContribution.negateIf(financialDescription.deduction),
            comment = null,
            finalScoBasis = null
        )

        ecPaymentCorrectionExtensionRepository.save(correctionExtensionEntity)
    }

    private fun BigDecimal.negateIf(isDeduction: Boolean): BigDecimal =
        if (isDeduction) this.negate() else this

    @Transactional(readOnly = true)
    override fun getCorrectionIdsAvailableForEcPayments(fundId: Long): Set<Long> {
        val specCorrection = QAuditControlCorrectionEntity.auditControlCorrectionEntity
        val paymentToEcCorrectionExtension = QPaymentToEcCorrectionExtensionEntity.paymentToEcCorrectionExtensionEntity
        val whereExpressions = mutableListOf<BooleanExpression>(
            specCorrection.programmeFund.id.eq(fundId),
            specCorrection.status.eq(AuditControlStatus.Closed),
            paymentToEcCorrectionExtension.paymentApplicationToEc.isNull,
        )

        return jpaQueryFactory
            .select(specCorrection.id)
            .from(specCorrection)
            .leftJoin(paymentToEcCorrectionExtension)
            .on(specCorrection.id.eq(paymentToEcCorrectionExtension.correctionId))
            .where(whereExpressions.joinWithAnd())
            .fetch().toSet()
    }

}
