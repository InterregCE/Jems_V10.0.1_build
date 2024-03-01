package io.cloudflight.jems.server.payments.service.account.listPaymentAccount

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.payments.model.account.PaymentAccount
import io.cloudflight.jems.server.payments.model.account.PaymentAccountOverview
import io.cloudflight.jems.server.payments.model.account.PaymentAccountOverviewContribution
import io.cloudflight.jems.server.payments.model.account.PaymentAccountOverviewDetail
import io.cloudflight.jems.server.payments.model.account.PaymentAccountStatus
import io.cloudflight.jems.server.payments.model.ec.AccountingYear
import io.cloudflight.jems.server.payments.service.account.PaymentAccountPersistence
import io.cloudflight.jems.server.payments.service.account.finance.PaymentAccountFinancePersistence
import io.cloudflight.jems.server.payments.service.account.submissionToSfcDate
import io.cloudflight.jems.server.programme.service.fund.model.ProgrammeFund
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import java.math.BigDecimal

class PaymentAccountsListServiceTest: UnitTest() {

    private val fund = mockk<ProgrammeFund>()
    private val year1 = mockk<AccountingYear>()
    private val year2 = mockk<AccountingYear>()

    private val account1 = PaymentAccount(
        id = 1L,
        fund = fund,
        accountingYear = year1,
        status = PaymentAccountStatus.DRAFT,
        nationalReference = "national reference 1",
        technicalAssistance = BigDecimal.valueOf(45L),
        submissionToSfcDate = submissionToSfcDate,
        sfcNumber = "sfc number 1",
        comment = "comment 1",
    )

    private val account2 = PaymentAccount(
        id = 2L,
        fund = fund,
        accountingYear = year2,
        status = PaymentAccountStatus.FINISHED,
        nationalReference = "national reference 2",
        technicalAssistance = BigDecimal.valueOf(-7L),
        submissionToSfcDate = submissionToSfcDate,
        sfcNumber = "sfc number 2",
        comment = "comment 2",
    )

    val expectedAccountsOverviewList = listOf(
        PaymentAccountOverview(
            programmeFund = fund,
            paymentAccounts = listOf(
                PaymentAccountOverviewDetail(
                    id = 1L,
                    accountingYear = year1,
                    status = PaymentAccountStatus.DRAFT,
                    totalEligibleExpenditure = BigDecimal.valueOf(40L),
                    nationalReference = "national reference 1",
                    technicalAssistance = BigDecimal.valueOf(45L),
                    totalPublicContribution = BigDecimal.ZERO,
                    totalClaimInclTA = BigDecimal.valueOf(85L),
                    submissionToSfcDate = submissionToSfcDate,
                    sfcNumber = "sfc number 1",
                ),
                PaymentAccountOverviewDetail(
                    id = 2L,
                    accountingYear = year2,
                    status = PaymentAccountStatus.FINISHED,
                    totalEligibleExpenditure = BigDecimal.valueOf(100L),
                    nationalReference = "national reference 2",
                    technicalAssistance = BigDecimal.valueOf(-7L),
                    totalPublicContribution = BigDecimal.valueOf(250L),
                    totalClaimInclTA = BigDecimal.valueOf(93L),
                    submissionToSfcDate = submissionToSfcDate,
                    sfcNumber = "sfc number 2",
                ),
            )
        )
    )

    @MockK
    lateinit var  paymentAccountPersistence: PaymentAccountPersistence

    @MockK
    lateinit var paymentAccountFinancePersistence: PaymentAccountFinancePersistence

    @InjectMockKs
    lateinit var service: PaymentAccountsListService

    @Test
    fun listAccounts() {
        every { paymentAccountPersistence.getAllAccounts() } returns listOf(account1, account2)
        every { paymentAccountFinancePersistence.getEcPaymentTotalsForFinishedPaymentAccounts() } returns mapOf(
            1L to PaymentAccountOverviewContribution(BigDecimal.valueOf(40L), BigDecimal.ZERO),
            2L to PaymentAccountOverviewContribution(BigDecimal.ZERO, BigDecimal.valueOf(50L)),
        )
        every { paymentAccountFinancePersistence.getCorrectionTotalsForFinishedPaymentAccounts() } returns mapOf(
            2L to PaymentAccountOverviewContribution(BigDecimal.valueOf(100L), BigDecimal.valueOf(200L)),
        )

        Assertions.assertThat(service.listPaymentAccount()).isEqualTo(expectedAccountsOverviewList)
    }
}
