package io.cloudflight.jems.server.payments.repository

import io.cloudflight.jems.server.payments.PaymentPersistence
import io.cloudflight.jems.server.payments.entity.PaymentGroupingId
import io.cloudflight.jems.server.payments.service.model.PartnerPayment
import io.cloudflight.jems.server.payments.service.model.PartnerPaymentSimple
import io.cloudflight.jems.server.payments.service.model.PaymentConfirmedInfo
import io.cloudflight.jems.server.payments.service.model.PaymentDetail
import io.cloudflight.jems.server.payments.service.model.PaymentPartnerInstallment
import io.cloudflight.jems.server.payments.service.model.PaymentPartnerInstallmentUpdate
import io.cloudflight.jems.server.payments.service.model.PaymentPerPartner
import io.cloudflight.jems.server.payments.service.model.PaymentToCreate
import io.cloudflight.jems.server.payments.service.model.PaymentToProject
import io.cloudflight.jems.server.payments.service.model.PaymentType
import io.cloudflight.jems.server.programme.repository.fund.ProgrammeFundRepository
import io.cloudflight.jems.server.project.repository.ProjectRepository
import io.cloudflight.jems.server.project.repository.lumpsum.ProjectLumpSumRepository
import io.cloudflight.jems.server.project.repository.partner.ProjectPartnerRepository
import io.cloudflight.jems.server.project.repository.partner.toProjectPartnerDetail
import io.cloudflight.jems.server.project.service.ProjectPersistence
import io.cloudflight.jems.server.user.entity.UserEntity
import io.cloudflight.jems.server.user.repository.user.UserRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.time.LocalDate

@Repository
class PaymentPersistenceProvider(
    private val paymentRepository: PaymentRepository,
    private val paymentPartnerRepository: PaymentPartnerRepository,
    private val paymentPartnerInstallmentRepository: PaymentPartnerInstallmentRepository,
    private val projectRepository: ProjectRepository,
    private val projectPartnerRepository: ProjectPartnerRepository,
    private val projectLumpSumRepository: ProjectLumpSumRepository,
    private val projectPersistence: ProjectPersistence,
    private val userRepository: UserRepository,
    private val fundRepository: ProgrammeFundRepository
) : PaymentPersistence {

    @Transactional(readOnly = true)
    override fun existsById(id: Long) =
        paymentRepository.existsById(id)

    @Transactional(readOnly = true)
    override fun getAllPaymentToProject(pageable: Pageable): Page<PaymentToProject> {
        return paymentRepository.findAll(pageable).toListModel(
            getLumpSum = { projectId, orderNr -> projectLumpSumRepository.getByIdProjectIdAndIdOrderNr(projectId, orderNr) },
            getProject = { projectId, version -> projectPersistence.getProject(projectId, version) },
            getConfirm = { id -> getConfirmedInfosForPayment(id) }
        )
    }

    @Transactional(readOnly = true)
    override fun getConfirmedInfosForPayment(paymentId: Long): PaymentConfirmedInfo {
        val paymentPartners = paymentPartnerRepository.findAllByPaymentId(paymentId)
        var amountPaid = BigDecimal.ZERO
        var lastPaymentDate: LocalDate? = null
        paymentPartners.forEach { paymentPartner ->
            val installments = paymentPartnerInstallmentRepository.findAllByPaymentPartnerId(paymentPartner.id)
            installments
                .filter { it.isPaymentConfirmed == true }
                .forEach { installment ->
                    amountPaid = amountPaid.add(installment.amountPaid)
                    if (installment.paymentConfirmedDate != null &&
                        (lastPaymentDate == null || installment.paymentConfirmedDate!!.isAfter(lastPaymentDate))) {
                        lastPaymentDate = installment.paymentConfirmedDate
                    }
                }
        }
        return PaymentConfirmedInfo(id = paymentId, amountPaidPerFund = amountPaid, dateOfLastPayment = lastPaymentDate)
    }

    @Transactional
    override fun deleteAllByProjectIdAndOrderNrIn(projectId: Long, orderNr: Set<Int>) {
        paymentRepository.deleteAllByProjectIdAndOrderNr(projectId, orderNr)
    }

    @Transactional
    override fun getAmountPerPartnerByProjectIdAndLumpSumOrderNrIn(
        projectId: Long,
        orderNrsToBeAdded: MutableSet<Int>
    ): List<PaymentPerPartner> =
        paymentRepository
            .getAmountPerPartnerByProjectIdAndLumpSumOrderNrIn(projectId, orderNrsToBeAdded)
            .toListModel()

    @Transactional
    override fun savePaymentToProjects(projectId: Long, paymentsToBeSaved: Map<PaymentGroupingId, PaymentToCreate>) {
        val projectEntity = projectRepository.getById(projectId)
        val paymentEntities = paymentRepository.saveAll(paymentsToBeSaved.map { (id, model) ->
            model.toEntity(
                projectEntity = projectEntity,
                paymentType =  PaymentType.FTLS,
                orderNr = id.orderNr,
                fundEntity = fundRepository.getById(id.programmeFundId))
        }).associateBy { PaymentGroupingId(it.orderNr, it.fund.id) }

        paymentEntities.forEach { (paymentId, entity) ->
            paymentPartnerRepository.saveAll(
                paymentsToBeSaved[paymentId]!!.partnerPayments.map { it.toEntity(entity) }
            )
        }
    }

    @Transactional(readOnly = true)
    override fun getPaymentDetails(paymentId: Long): PaymentDetail =
        paymentRepository.getById(paymentId).toDetailModel(
            partnerPayments = getAllPartnerPayments(paymentId)
        )

    @Transactional(readOnly = true)
    override fun getAllPartnerPayments(paymentId: Long): List<PartnerPayment> =
        paymentPartnerRepository.findAllByPaymentId(paymentId)
            .map { it.toModel(
                projectPartnerRepository.getById(it.partnerId).toProjectPartnerDetail(),
                findPaymentPartnerInstallments(it.id)
            ) }

    @Transactional(readOnly = true)
    override fun getAllPartnerPaymentsForPartner(partnerId: Long) =
        paymentPartnerRepository.findAllByPartnerId(partnerId).map {
            PartnerPaymentSimple(fundId = it.payment.fund.id, it.amountApprovedPerPartner ?: BigDecimal.ZERO)
        }

    @Transactional(readOnly = true)
    override fun getPaymentPartnerId(paymentId: Long, partnerId: Long): Long =
        this.paymentPartnerRepository.getIdByPaymentIdAndPartnerId(paymentId, partnerId)

    @Transactional(readOnly = true)
    override fun findPaymentPartnerInstallments(paymentPartnerId: Long): List <PaymentPartnerInstallment> =
        this.paymentPartnerInstallmentRepository.findAllByPaymentPartnerId(paymentPartnerId).toModelList()

    @Transactional(readOnly = true)
    override fun findByPartnerId(partnerId: Long) =
        paymentPartnerInstallmentRepository.findAllByPaymentPartnerPartnerId(partnerId).toModelList()

    @Transactional
    override fun updatePaymentPartnerInstallments(
        paymentPartnerId: Long,
        toDeleteInstallmentIds: Set<Long>,
        paymentPartnerInstallments: List<PaymentPartnerInstallmentUpdate>
    ): List<PaymentPartnerInstallment> {
        paymentPartnerInstallmentRepository.deleteAllByIdInBatch(toDeleteInstallmentIds)

        return paymentPartnerInstallmentRepository.saveAll(
            paymentPartnerInstallments.map {
                it.toEntity(
                    paymentPartner = paymentPartnerRepository.getById(paymentPartnerId),
                    savePaymentInfoUser = getUserOrNull(it.savePaymentInfoUserId),
                    paymentConfirmedUser = getUserOrNull(it.paymentConfirmedUserId)
                )
            }
        ).toModelList()
    }

    private fun getUserOrNull(userId: Long?): UserEntity? =
        if (userId != null) {
            userRepository.getById(userId)
        } else null

}
