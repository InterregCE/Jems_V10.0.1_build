package io.cloudflight.jems.server.payments.repository.applicationToEc.linkToCorrection

import com.querydsl.core.Tuple
import com.querydsl.core.types.dsl.BooleanExpression
import com.querydsl.jpa.impl.JPAQueryFactory
import io.cloudflight.jems.server.payments.entity.PaymentToEcCorrectionExtensionEntity
import io.cloudflight.jems.server.payments.entity.QPaymentToEcCorrectionExtensionEntity
import io.cloudflight.jems.server.payments.model.ec.CorrectionInEcPaymentMetadata
import io.cloudflight.jems.server.payments.model.ec.PaymentToEcCorrectionExtension
import io.cloudflight.jems.server.payments.model.ec.PaymentToEcCorrectionLinkingUpdate
import io.cloudflight.jems.server.payments.model.regular.PaymentSearchRequestScoBasis
import io.cloudflight.jems.server.payments.repository.applicationToEc.EcPaymentRepository
import io.cloudflight.jems.server.payments.repository.regular.joinWithAnd
import io.cloudflight.jems.server.payments.service.ecPayment.linkToCorrection.EcPaymentCorrectionLinkPersistence
import io.cloudflight.jems.server.project.entity.auditAndControl.QAuditControlCorrectionEntity
import io.cloudflight.jems.server.project.entity.contracting.QProjectContractingMonitoringEntity
import io.cloudflight.jems.server.project.repository.auditAndControl.correction.AuditControlCorrectionRepository
import io.cloudflight.jems.server.project.service.auditAndControl.model.AuditControlStatus
import io.cloudflight.jems.server.project.service.auditAndControl.model.ProjectCorrectionFinancialDescription
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal

@Repository
class EcPaymentCorrectionLinkPersistenceProvider(
    private val ecPaymentRepository: EcPaymentRepository,
    private val ecPaymentCorrectionExtensionRepository: EcPaymentCorrectionExtensionRepository,
    private val auditControlCorrectionRepository: AuditControlCorrectionRepository,
    private val jpaQueryFactory: JPAQueryFactory
) : EcPaymentCorrectionLinkPersistence {

    @Transactional(readOnly = true)
    override fun getCorrectionExtension(correctionId: Long): PaymentToEcCorrectionExtension =
        ecPaymentCorrectionExtensionRepository.getReferenceById(correctionId).toModel()

    @Transactional
    override fun selectCorrectionToEcPayment(correctionIds: Set<Long>, ecPaymentId: Long) {
        val ecPayment = ecPaymentRepository.getReferenceById(ecPaymentId)
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
                    correctionId = it.get(correction.id)!!,
                    auditControlNr = it.get(correction.auditControl.number)!!,
                    correctionNr = it.get(correction.orderNr)!!,
                    projectId = it.get(correction.auditControl.project.id)!!,
                    typologyProv94 = it.get(contractingMonitoring.typologyProv94),
                    typologyProv95 = it.get(contractingMonitoring.typologyProv95),
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
            it.correctedUnionContribution = it.unionContribution
            it.correctedTotalEligibleWithoutArt94or95 = it.totalEligibleWithoutArt94or95
        }
    }

    @Transactional
    override fun updateCorrectionLinkedToEcPaymentCorrectedAmounts(
        correctionId: Long,
        ecPaymentCorrectionLinkingUpdate: PaymentToEcCorrectionLinkingUpdate
    ): PaymentToEcCorrectionExtension =
        ecPaymentCorrectionExtensionRepository.getReferenceById(correctionId).apply {
            this.correctedAutoPublicContribution = ecPaymentCorrectionLinkingUpdate.correctedAutoPublicContribution
            this.correctedPublicContribution = ecPaymentCorrectionLinkingUpdate.correctedPublicContribution
            this.correctedPrivateContribution = ecPaymentCorrectionLinkingUpdate.correctedPrivateContribution
            this.correctedTotalEligibleWithoutArt94or95 = ecPaymentCorrectionLinkingUpdate.correctedTotalEligibleWithoutArt94or95
            this.correctedUnionContribution = ecPaymentCorrectionLinkingUpdate.correctedUnionContribution
            this.correctedFundAmount = ecPaymentCorrectionLinkingUpdate.correctedFundAmount
            this.comment = ecPaymentCorrectionLinkingUpdate.comment
        }.toModel()

    @Transactional
    override fun updatePaymentToEcFinalScoBasis(toUpdate: Map<Long, PaymentSearchRequestScoBasis>) {
        ecPaymentCorrectionExtensionRepository.findAllById(toUpdate.keys).forEach {
            it.finalScoBasis = toUpdate[it.correctionId]!!
            if (toUpdate[it.correctionId]!! == PaymentSearchRequestScoBasis.DoesNotFallUnderArticle94Nor95) {
                it.correctedFundAmount = it.fundAmount
                it.correctedTotalEligibleWithoutArt94or95 = it.totalEligibleWithoutArt94or95
                it.correctedUnionContribution = BigDecimal.ZERO
            }
        }
    }

    @Transactional
    override fun createCorrectionExtension(
        financialDescription: ProjectCorrectionFinancialDescription,
        totalEligibleWithoutArt94or95: BigDecimal,
        unionContribution: BigDecimal
    ) {
        val correctionEntity = auditControlCorrectionRepository.getReferenceById(financialDescription.correctionId)
        val correctionExtensionEntity = PaymentToEcCorrectionExtensionEntity(
            correctionId = financialDescription.correctionId,
            correction = correctionEntity,
            paymentApplicationToEc = null,
            fundAmount = financialDescription.fundAmount,
            correctedFundAmount = financialDescription.fundAmount,
            publicContribution = financialDescription.publicContribution,
            correctedPublicContribution = financialDescription.publicContribution,
            autoPublicContribution = financialDescription.autoPublicContribution,
            correctedAutoPublicContribution = financialDescription.autoPublicContribution,
            privateContribution = financialDescription.privateContribution,
            correctedPrivateContribution = financialDescription.privateContribution,
            comment = null,
            finalScoBasis = null,
            totalEligibleWithoutArt94or95 = totalEligibleWithoutArt94or95,
            correctedTotalEligibleWithoutArt94or95 = totalEligibleWithoutArt94or95,
            unionContribution = unionContribution,
            correctedUnionContribution = unionContribution
        )

        ecPaymentCorrectionExtensionRepository.save(correctionExtensionEntity)
    }

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
            .fetch()
            .toSet()
    }

}
