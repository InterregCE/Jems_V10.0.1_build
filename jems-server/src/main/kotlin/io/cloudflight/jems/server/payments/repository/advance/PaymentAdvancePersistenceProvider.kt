package io.cloudflight.jems.server.payments.repository.advance

import io.cloudflight.jems.server.common.exception.ResourceNotFoundException
import io.cloudflight.jems.server.common.file.repository.JemsFileMetadataRepository
import io.cloudflight.jems.server.common.file.service.JemsProjectFileService
import io.cloudflight.jems.server.common.file.service.model.JemsFileType
import io.cloudflight.jems.server.payments.entity.AdvancePaymentEntity
import io.cloudflight.jems.server.payments.model.advance.AdvancePayment
import io.cloudflight.jems.server.payments.model.advance.AdvancePaymentDetail
import io.cloudflight.jems.server.payments.model.advance.AdvancePaymentSearchRequest
import io.cloudflight.jems.server.payments.model.advance.AdvancePaymentStatus
import io.cloudflight.jems.server.payments.model.advance.AdvancePaymentUpdate
import io.cloudflight.jems.server.payments.repository.toDetailModel
import io.cloudflight.jems.server.payments.repository.toEntity
import io.cloudflight.jems.server.payments.repository.toModel
import io.cloudflight.jems.server.payments.repository.toModelList
import io.cloudflight.jems.server.payments.service.advance.PaymentAdvancePersistence
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
import java.time.LocalDate

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
) : PaymentAdvancePersistence {

    @Transactional(readOnly = true)
    override fun list(pageable: Pageable, filters: AdvancePaymentSearchRequest): Page<AdvancePayment> =
        advancePaymentRepository.filterPayments(pageable, filters).toModelList()

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
        return advancePaymentRepository.getById(paymentId).toDetailModel()
    }

    @Transactional
    override fun updateAdvancePaymentStatus(paymentId: Long, status: AdvancePaymentStatus, currentUserId: Long): AdvancePaymentDetail {
        return advancePaymentRepository.getById(paymentId).apply {
            if (isPaymentAuthorizedInfo == false && status == AdvancePaymentStatus.AUTHORIZED) {
                isPaymentAuthorizedInfo = true
                paymentAuthorizedDate = LocalDate.now()
                paymentAuthorizedInfoUser = getUserOrNull(currentUserId)
            } else if (status == AdvancePaymentStatus.DRAFT) {
                isPaymentAuthorizedInfo = false
                paymentAuthorizedDate = null
                paymentAuthorizedInfoUser = null
            }

            val isNewlyConfirmed = status == AdvancePaymentStatus.CONFIRMED
            isPaymentConfirmed = isNewlyConfirmed
            paymentConfirmedDate = if (isNewlyConfirmed) LocalDate.now() else null
            paymentConfirmedUser = if (isNewlyConfirmed) getUserOrNull(currentUserId) else null
        }.toDetailModel()
    }

    @Transactional
    override fun updatePaymentDetail(paymentDetail: AdvancePaymentUpdate): AdvancePaymentDetail {
        val existing = paymentDetail.id?.let { advancePaymentRepository.getById(it) }
        val version = if (existing?.isPaymentAuthorizedInfo == true) existing.projectVersion else
            projectVersion.getLatestApprovedOrCurrent(paymentDetail.projectId)
        val project = projectPersistence.getProject(paymentDetail.projectId, version)
        val partner = partnerPersistence.getById(paymentDetail.partnerId, version).toSummary()

        return advancePaymentRepository.save(
            paymentDetail.toEntity(
                project = project,
                partner = partner,
                projectVersion = version,
                existing = existing,
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

    private fun setSourceOfContribution(
        entity: AdvancePaymentEntity,
        fundId: Long?,
        contributionId: Long?,
        contributionSpfId: Long?
    ) {
        // only one of these ids should be set
        entity.programmeFund = if (fundId != null) {
            programmeFundRepository.getById(fundId)
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
            userRepository.getById(userId)
        } else null

}
