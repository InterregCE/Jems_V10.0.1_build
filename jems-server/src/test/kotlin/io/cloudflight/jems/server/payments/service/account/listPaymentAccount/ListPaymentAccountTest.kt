package io.cloudflight.jems.server.payments.service.account.listPaymentAccount

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.payments.model.account.PaymentAccountOverview
import io.cloudflight.jems.server.payments.model.account.PaymentAccountOverviewDetail
import io.cloudflight.jems.server.payments.model.account.PaymentAccountStatus
import io.cloudflight.jems.server.payments.model.ec.AccountingYear
import io.cloudflight.jems.server.payments.service.account.submissionToSfcDate
import io.cloudflight.jems.server.programme.service.fund.model.ProgrammeFund
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.math.BigDecimal

class ListPaymentAccountTest : UnitTest() {

    private val fund = mockk<ProgrammeFund>()
    private val year1 = mockk<AccountingYear>()
    private val year2 = mockk<AccountingYear>()

    val accountsOverviewList = listOf(
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
    lateinit var paymentAccountsListService: PaymentAccountsListService

    @InjectMockKs
    lateinit var interactor: ListPaymentAccount

    @Test
    fun listAccounts() {


        every { paymentAccountsListService.listPaymentAccount() } returns accountsOverviewList

        assertThat(interactor.listPaymentAccount()).isEqualTo(
            listOf(
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
        )
    }
}
