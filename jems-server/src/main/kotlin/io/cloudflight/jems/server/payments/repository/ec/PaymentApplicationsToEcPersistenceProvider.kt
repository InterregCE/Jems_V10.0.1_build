package io.cloudflight.jems.server.payments.repository.ec

import io.cloudflight.jems.server.accountingYears.repository.AccountingYearRepository
import io.cloudflight.jems.server.payments.entity.AccountingYearEntity
import io.cloudflight.jems.server.payments.entity.PaymentApplicationsToEcEntity
import io.cloudflight.jems.server.payments.model.ec.PaymentApplicationsToEc
import io.cloudflight.jems.server.payments.model.ec.PaymentApplicationsToEcDetail
import io.cloudflight.jems.server.payments.model.ec.PaymentApplicationsToEcUpdate
import io.cloudflight.jems.server.payments.model.regular.PaymentEcStatus
import io.cloudflight.jems.server.payments.service.paymentApplicationsToEc.PaymentApplicationsToEcPersistence
import io.cloudflight.jems.server.programme.entity.fund.ProgrammeFundEntity
import io.cloudflight.jems.server.programme.repository.fund.ProgrammeFundRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
class PaymentApplicationsToEcPersistenceProvider(
    private val paymentApplicationsToEcRepository: PaymentApplicationsToEcRepository,
    private val programmeFundRepository: ProgrammeFundRepository,
    private val accountingYearRepository: AccountingYearRepository,
) : PaymentApplicationsToEcPersistence {
    @Transactional
    override fun updatePaymentApplicationsToEc(paymentApplicationsToEcUpdate: PaymentApplicationsToEcUpdate): PaymentApplicationsToEcDetail {
        val programmeFund = programmeFundRepository.findById(paymentApplicationsToEcUpdate.programmeFundId)
            .orElseThrow { ProgrammeFundNotFound() }
        val accountingYear = accountingYearRepository.findById(paymentApplicationsToEcUpdate.accountingYearId)
            .orElseThrow { AccountingYearNotFound() }

        val savedEcPayment = if (paymentApplicationsToEcUpdate.id == null) {
            paymentApplicationsToEcRepository.save(
                PaymentApplicationsToEcEntity(
                    programmeFund = programmeFund,
                    accountingYear = accountingYear,
                    status = PaymentEcStatus.Draft
                )
            )
        } else {
            val existingEcPayment = paymentApplicationsToEcRepository.getById(paymentApplicationsToEcUpdate.id)
            existingEcPayment.update(programmeFund, accountingYear)
        }

        return savedEcPayment.toDetailModel()
    }

    private fun PaymentApplicationsToEcEntity.update(
        programmeFundEntity: ProgrammeFundEntity,
        accountingYearEntity: AccountingYearEntity
    ): PaymentApplicationsToEcEntity {
        this.programmeFund = programmeFundEntity
        this.accountingYear = accountingYearEntity
        return this
    }

    @Transactional(readOnly = true)
    override fun getPaymentApplicationsToEcDetail(id: Long): PaymentApplicationsToEcDetail =
        paymentApplicationsToEcRepository.getById(id).toDetailModel()

    @Transactional(readOnly = true)
    override fun findAll(pageable: Pageable): Page<PaymentApplicationsToEc> =
        paymentApplicationsToEcRepository.findAll(pageable).toModel()

    @Transactional
    override fun deleteById(id: Long) {
        paymentApplicationsToEcRepository.deleteById(id)
    }

}
