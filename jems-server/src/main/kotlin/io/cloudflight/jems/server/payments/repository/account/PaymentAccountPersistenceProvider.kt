package io.cloudflight.jems.server.payments.repository.account

import io.cloudflight.jems.server.common.exception.ResourceNotFoundException
import io.cloudflight.jems.server.common.file.repository.JemsFileMetadataRepository
import io.cloudflight.jems.server.common.file.service.JemsSystemFileService
import io.cloudflight.jems.server.common.file.service.model.JemsFileType
import io.cloudflight.jems.server.payments.accountingYears.repository.AccountingYearRepository
import io.cloudflight.jems.server.payments.entity.AccountingYearEntity
import io.cloudflight.jems.server.payments.entity.account.PaymentAccountEntity
import io.cloudflight.jems.server.payments.model.account.PaymentAccount
import io.cloudflight.jems.server.payments.model.account.PaymentAccountStatus
import io.cloudflight.jems.server.payments.model.account.PaymentAccountUpdate
import io.cloudflight.jems.server.payments.service.account.PaymentAccountPersistence
import io.cloudflight.jems.server.programme.entity.fund.ProgrammeFundEntity
import io.cloudflight.jems.server.programme.repository.fund.ProgrammeFundRepository
import io.cloudflight.jems.server.project.repository.file.ProjectFileRepository
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal

@Repository
class PaymentAccountPersistenceProvider(
    private val repository: PaymentAccountRepository,
    private val accountingYearRepository: AccountingYearRepository,
    private val programmeFundRepository: ProgrammeFundRepository,
    private val fileRepository: JemsSystemFileService,
    private val reportFileRepository: JemsFileMetadataRepository
) : PaymentAccountPersistence {

    @Transactional(readOnly = true)
    override fun getByPaymentAccountId(paymentAccountId: Long): PaymentAccount =
        repository.getById(paymentAccountId).toModel()

    @Transactional(readOnly = true)
    override fun getAllAccounts(): List<PaymentAccount> =
        repository.findAll().sortedBy { it.accountingYear.year }.toModel()

    @Transactional(readOnly = true)
    override fun findByFundAndYear(fundId: Long, accountingYearId: Long): PaymentAccount =
        repository.findByProgrammeFundIdAndAccountingYearId(fundId = fundId, yearId = accountingYearId).toModel()

    @Transactional
    override fun updatePaymentAccount(paymentAccountId: Long, paymentAccount: PaymentAccountUpdate): PaymentAccount {
        val existingAccount = repository.getById(paymentAccountId)

        existingAccount.updateWith(paymentAccount)

        return existingAccount.toModel()
    }

    @Transactional
    override fun deletePaymentAccountsByFunds(idsToDelete: Set<Long>) =
        repository.deleteAllByProgrammeFundIdIn(idsToDelete)

    @Transactional
    override fun persistPaymentAccountsByFunds(programmeFundIds: Set<Long>) {
        val accountingYears = accountingYearRepository.findAllByOrderByYear()
        val programmeFundEntities = programmeFundRepository.findAllById(programmeFundIds)

        repository.saveAll(
            programmeFundEntities.flatMap { fund -> generateAccountsForProgrammeFund(fund, accountingYears) }
        )
    }

    @Transactional
    override fun finalizePaymentAccount(paymentAccountId: Long): PaymentAccountStatus =
        this.repository.getById(paymentAccountId).apply {
            status = PaymentAccountStatus.FINISHED
        }.status

    @Transactional
    override fun reOpenPaymentAccount(paymentAccountId: Long): PaymentAccountStatus =
        this.repository.getById(paymentAccountId).apply {
            status = PaymentAccountStatus.DRAFT
        }.status

    @Transactional
    override fun deletePaymentAccountAttachment(fileId: Long) {
        fileRepository.delete(
            reportFileRepository.findByTypeAndId(JemsFileType.PaymentAccountAttachment, fileId)
                ?: throw ResourceNotFoundException("file")
        )
    }

    private fun generateAccountsForProgrammeFund(
        programmeFundEntity: ProgrammeFundEntity,
        accountingYears: List<AccountingYearEntity>
    ): List<PaymentAccountEntity> {
        val paymentAccountList = ArrayList<PaymentAccountEntity>()
        accountingYears.forEach {
            paymentAccountList.add(
                PaymentAccountEntity(
                    programmeFund = programmeFundEntity,
                    accountingYear = it,
                    nationalReference = "",
                    technicalAssistance = BigDecimal.ZERO,
                    submissionToSfcDate = null,
                    sfcNumber = "",
                    comment = "",
                    status = PaymentAccountStatus.DRAFT
                )
            )
        }

        return paymentAccountList
    }

    private fun PaymentAccountEntity.updateWith(newData: PaymentAccountUpdate) {
        nationalReference = newData.nationalReference
        technicalAssistance = newData.technicalAssistance
        submissionToSfcDate = newData.submissionToSfcDate
        sfcNumber = newData.sfcNumber
        comment = newData.comment
    }

}
