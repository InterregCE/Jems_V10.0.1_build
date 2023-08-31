package io.cloudflight.jems.server.payments.repository.applicationToEc

import io.cloudflight.jems.server.payments.accountingYears.repository.AccountingYearRepository
import io.cloudflight.jems.server.payments.entity.AccountingYearEntity
import io.cloudflight.jems.server.payments.entity.PaymentApplicationToEcEntity
import io.cloudflight.jems.server.payments.model.ec.PaymentApplicationToEc
import io.cloudflight.jems.server.payments.model.ec.PaymentApplicationToEcDetail
import io.cloudflight.jems.server.payments.model.ec.PaymentApplicationToEcUpdate
import io.cloudflight.jems.server.payments.model.regular.PaymentEcStatus
import io.cloudflight.jems.server.payments.service.paymentApplicationsToEc.PaymentApplicationToEcPersistence
import io.cloudflight.jems.server.programme.entity.fund.ProgrammeFundEntity
import io.cloudflight.jems.server.programme.repository.fund.ProgrammeFundRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
class PaymentApplicationToEcPersistenceProvider(
    private val paymentApplicationsToEcRepository: PaymentApplicationsToEcRepository,
    private val programmeFundRepository: ProgrammeFundRepository,
    private val accountingYearRepository: AccountingYearRepository,
) : PaymentApplicationToEcPersistence {

    @Transactional
    override fun createPaymentApplicationToEc(paymentApplicationsToEcUpdate: PaymentApplicationToEcUpdate): PaymentApplicationToEcDetail {
        val programmeFund = programmeFundRepository.getById(paymentApplicationsToEcUpdate.programmeFundId)
        val accountingYear = accountingYearRepository.getById(paymentApplicationsToEcUpdate.accountingYearId)

        return paymentApplicationsToEcRepository.save(
            PaymentApplicationToEcEntity(
                programmeFund = programmeFund,
                accountingYear = accountingYear,
                status = PaymentEcStatus.Draft
            )
        ).toDetailModel()
    }

    @Transactional
    override fun updatePaymentApplicationToEc(paymentApplicationsToEcUpdate: PaymentApplicationToEcUpdate): PaymentApplicationToEcDetail {
        val programmeFund = programmeFundRepository.getById(paymentApplicationsToEcUpdate.programmeFundId)
        val accountingYear = accountingYearRepository.getById(paymentApplicationsToEcUpdate.accountingYearId)
        val existingEcPayment = paymentApplicationsToEcRepository.getById(paymentApplicationsToEcUpdate.id!!)

        existingEcPayment.update(programmeFund, accountingYear)

        return existingEcPayment.toDetailModel()
    }

    private fun PaymentApplicationToEcEntity.update(
        programmeFundEntity: ProgrammeFundEntity,
        accountingYearEntity: AccountingYearEntity
    ): PaymentApplicationToEcEntity {
        this.programmeFund = programmeFundEntity
        this.accountingYear = accountingYearEntity
        return this
    }

    @Transactional(readOnly = true)
    override fun getPaymentApplicationToEcDetail(id: Long): PaymentApplicationToEcDetail =
        paymentApplicationsToEcRepository.getById(id).toDetailModel()

    @Transactional(readOnly = true)
    override fun findAll(pageable: Pageable): Page<PaymentApplicationToEc> =
        paymentApplicationsToEcRepository.findAll(pageable).toModel()

    @Transactional
    override fun deleteById(id: Long) {
        paymentApplicationsToEcRepository.deleteById(id)
    }

}
