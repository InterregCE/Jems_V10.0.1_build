package io.cloudflight.jems.server.payments.repository.advance

import com.querydsl.core.types.dsl.CaseBuilder
import com.querydsl.jpa.impl.JPAQueryFactory
import io.cloudflight.jems.server.common.exception.ResourceNotFoundException
import io.cloudflight.jems.server.common.file.repository.JemsFileMetadataRepository
import io.cloudflight.jems.server.common.file.service.JemsProjectFileService
import io.cloudflight.jems.server.common.file.service.model.JemsFileType
import io.cloudflight.jems.server.payments.entity.AdvancePaymentEntity
import io.cloudflight.jems.server.payments.entity.QAdvancePaymentEntity
import io.cloudflight.jems.server.payments.entity.QAdvancePaymentSettlementEntity
import io.cloudflight.jems.server.payments.model.advance.AdvancePayment
import io.cloudflight.jems.server.payments.model.advance.AdvancePaymentDetail
import io.cloudflight.jems.server.payments.model.advance.AdvancePaymentSearchRequest
import io.cloudflight.jems.server.payments.model.advance.AdvancePaymentUpdate
import io.cloudflight.jems.server.payments.repository.toDetailModel
import io.cloudflight.jems.server.payments.repository.toEntity
import io.cloudflight.jems.server.payments.repository.toModel
import io.cloudflight.jems.server.payments.repository.toModelList
import io.cloudflight.jems.server.payments.service.advance.PaymentAdvancePersistence
import io.cloudflight.jems.server.programme.entity.fund.QProgrammeFundEntity
import io.cloudflight.jems.server.programme.repository.fund.ProgrammeFundRepository
import io.cloudflight.jems.server.project.service.ProjectPersistence
import io.cloudflight.jems.server.project.service.ProjectVersionPersistence
import io.cloudflight.jems.server.project.service.partner.PartnerPersistence
import io.cloudflight.jems.server.project.service.partner.cofinancing.ProjectPartnerCoFinancingPersistence
import io.cloudflight.jems.server.user.entity.UserEntity
import io.cloudflight.jems.server.user.repository.user.UserRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal

@Repository
class PaymentAdvancePersistenceProvider(
    private val advancePaymentRepository: AdvancePaymentRepository,
    private val projectVersion: ProjectVersionPersistence,
    private val projectPersistence: ProjectPersistence,
    private val partnerPersistence: PartnerPersistence,
    private val partnerCoFinancingPersistence: ProjectPartnerCoFinancingPersistence,
    private val userRepository: UserRepository,
    private val programmeFundRepository: ProgrammeFundRepository,
    private val fileRepository: JemsProjectFileService,
    private val reportFileRepository: JemsFileMetadataRepository,
    private val jpaQueryFactory: JPAQueryFactory,
): PaymentAdvancePersistence {

    @Transactional(readOnly = true)
    override fun list(pageable: Pageable, filters: AdvancePaymentSearchRequest): Page<AdvancePayment> {
        return fetchAdvancePayments(pageable, filters)
    }

    @Transactional(readOnly = true)
    override fun existsById(id: Long) =
        advancePaymentRepository.existsById(id)

    @Transactional(readOnly = true)
    override fun getPaymentsByProjectId(projectId: Long): List<AdvancePayment> =
         advancePaymentRepository.findAllByProjectId(projectId).map { it.toModel() }

    @Transactional
    override fun deleteByPaymentId(paymentId: Long) {
        advancePaymentRepository.deleteById(paymentId)
    }

    @Transactional(readOnly = true)
    override fun getPaymentDetail(paymentId: Long): AdvancePaymentDetail {
       return advancePaymentRepository.getReferenceById(paymentId).toDetailModel()
    }

    @Transactional
    override fun updatePaymentDetail(paymentDetail: AdvancePaymentUpdate): AdvancePaymentDetail {
        val existing = paymentDetail.id?.let { advancePaymentRepository.getReferenceById(it) }
        val version = existing?.projectVersion ?: projectVersion.getLatestApprovedOrCurrent(paymentDetail.projectId)
        val project = projectPersistence.getProject(paymentDetail.projectId, version)
        val partner = partnerPersistence.getById(paymentDetail.partnerId, version)

        return advancePaymentRepository.save(
                paymentDetail.toEntity(
                    project = project,
                    partner = partner,
                    projectVersion = version,
                    paymentAuthorizedUser = getUserOrNull(paymentDetail.paymentAuthorizedUserId),
                    paymentConfirmedUser = getUserOrNull(paymentDetail.paymentConfirmedUserId)
                ).also {
                    setSourceOfContribution(
                        entity = it,
                        fundId = paymentDetail.programmeFundId,
                        contributionId = paymentDetail.partnerContributionId,
                        contributionSpfId = paymentDetail.partnerContributionSpfId
                    )
                }
            ).toDetailModel()
    }

    @Transactional
    override fun deletePaymentAdvanceAttachment(fileId: Long) {
        fileRepository.delete(
            reportFileRepository.findByTypeAndId(JemsFileType.PaymentAdvanceAttachment, fileId)
                ?: throw ResourceNotFoundException("file")
        )
    }

    @Transactional(readOnly = true)
    override fun getConfirmedPaymentsForProject(projectId: Long, pageable: Pageable): Page<AdvancePayment> =
        advancePaymentRepository.findAllByProjectIdAndIsPaymentConfirmedTrue(projectId, pageable).toModelList()


    private fun fetchAdvancePayments(pageable: Pageable, filters: AdvancePaymentSearchRequest): Page<AdvancePayment> {
        val specPayment = QAdvancePaymentEntity.advancePaymentEntity
        val specSettlement = QAdvancePaymentSettlementEntity.advancePaymentSettlementEntity
        val specProgrammeFund = QProgrammeFundEntity.programmeFundEntity

        val results = jpaQueryFactory
            .select(
                specPayment.id,
                specPayment.projectCustomIdentifier,
                specPayment.projectAcronym,
                specPayment.partnerRole,
                specPayment.partnerSortNumber,
                specPayment.partnerAbbreviation,
                specPayment.isPaymentAuthorizedInfo,
                specPayment.amountPaid(),
                specSettlement.amountSettled.sum().`as`("amountSettled"),
                specPayment.paymentDate,
                specProgrammeFund,
                specPayment.partnerContributionId,
                specPayment.partnerContributionName,
                specPayment.partnerContributionSpfId,
                specPayment.partnerContributionSpfName,
                specPayment.partnerNameInOriginalLanguage,
                specPayment.partnerNameInEnglish,
                specPayment.projectId,
                specPayment.projectVersion
            ).from(specPayment)
            .leftJoin(specSettlement)
                .on(specSettlement.advancePayment.id.eq(specPayment.id))
            .leftJoin(specProgrammeFund)
                .on(specPayment.programmeFund.id.eq(specProgrammeFund.id))
            .where(filters.transformToWhereClause(specPayment))
            .groupBy(specPayment)
            .offset(pageable.offset)
            .limit(pageable.pageSize.toLong())
            .orderBy(pageable.sort.toQueryDslOrderBy())
            .fetchResults()

        return results.toPageResult(pageable)
    }

    private fun setSourceOfContribution(
        entity: AdvancePaymentEntity,
        fundId: Long?,
        contributionId: Long?,
        contributionSpfId: Long?
    ) {
        // only one of these ids should be set
        entity.programmeFund = if (fundId != null) {
            programmeFundRepository.getReferenceById(fundId)
        } else null
        if (contributionId != null) {
            val contributions =
                partnerCoFinancingPersistence.getCoFinancingAndContributions(entity.partnerId, entity.projectVersion)
            val contribution = contributions.partnerContributions.firstOrNull { it.id == contributionId }
            entity.partnerContributionId = contributionId
            entity.partnerContributionName = contribution?.name
        }
        if (contributionSpfId != null) {
            val contributions =
                partnerCoFinancingPersistence.getSpfCoFinancingAndContributions(entity.partnerId, entity.projectVersion)
            val contribution = contributions.partnerContributions.firstOrNull { it.id == contributionSpfId }
            entity.partnerContributionSpfId = contributionSpfId
            entity.partnerContributionSpfName = contribution?.name
        }
    }

    private fun getUserOrNull(userId: Long?): UserEntity? =
        if (userId != null) {
            userRepository.getReferenceById(userId)
        } else null

    private fun QAdvancePaymentEntity.amountPaid() =
        CaseBuilder().`when`(this.isPaymentConfirmed.isTrue).then(this.amountPaid).otherwise(BigDecimal.ZERO)
}
