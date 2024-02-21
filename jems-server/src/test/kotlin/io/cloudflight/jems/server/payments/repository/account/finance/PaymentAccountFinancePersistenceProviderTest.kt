package io.cloudflight.jems.server.payments.repository.account.finance

import com.querydsl.core.Tuple
import com.querydsl.jpa.impl.JPAQuery
import com.querydsl.jpa.impl.JPAQueryFactory
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.payments.entity.PaymentToEcPriorityAxisOverviewEntity
import io.cloudflight.jems.server.payments.entity.QPaymentApplicationToEcEntity
import io.cloudflight.jems.server.payments.entity.QPaymentToEcPriorityAxisOverviewEntity
import io.cloudflight.jems.server.payments.entity.account.QPaymentAccountEntity
import io.cloudflight.jems.server.payments.entity.account.QPaymentAccountPriorityAxisOverviewEntity
import io.cloudflight.jems.server.payments.model.account.PaymentAccountOverviewContribution
import io.cloudflight.jems.server.payments.model.account.PaymentAccountStatus
import io.cloudflight.jems.server.payments.model.ec.PaymentToEcAmountSummaryLine
import io.cloudflight.jems.server.payments.model.ec.PaymentToEcOverviewType
import io.cloudflight.jems.server.programme.entity.QProgrammePriorityEntity
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.math.BigDecimal

class PaymentAccountFinancePersistenceProviderTest : UnitTest() {

    companion object {
        private val paymentToEcPriorityAxis = listOf(
            PaymentToEcPriorityAxisOverviewEntity(
                id = 1L,
                paymentApplicationToEc = mockk(),
                priorityAxis = mockk {
                    every { id } returns 11L
                    every { code } returns "P01"
                },
                type = PaymentToEcOverviewType.DoesNotFallUnderArticle94Nor95,
                totalEligibleExpenditure = BigDecimal(101),
                totalUnionContribution = BigDecimal(102),
                totalPublicContribution = BigDecimal(103)
            ),
            PaymentToEcPriorityAxisOverviewEntity(
                id = 2L,
                paymentApplicationToEc = mockk(),
                priorityAxis = mockk {
                    every { id } returns 22L
                    every { code } returns "P02"
                },
                type = PaymentToEcOverviewType.DoesNotFallUnderArticle94Nor95,
                totalEligibleExpenditure = BigDecimal(201),
                totalUnionContribution = BigDecimal(202),
                totalPublicContribution = BigDecimal(203)
            ),
            PaymentToEcPriorityAxisOverviewEntity(
                id = 3L,
                paymentApplicationToEc = mockk(),
                priorityAxis = mockk {
                    every { id } returns 33L
                    every { code } returns "P03"
                },
                type = PaymentToEcOverviewType.FallsUnderArticle94Or95,
                totalEligibleExpenditure = BigDecimal(301),
                totalUnionContribution = BigDecimal.ZERO,
                totalPublicContribution = BigDecimal(302),
            )
        )

        private val overviewContribution = mapOf(
            1L to PaymentAccountOverviewContribution(BigDecimal(100), BigDecimal(50)),
            2L to PaymentAccountOverviewContribution(BigDecimal(200), BigDecimal(100)),
            3L to PaymentAccountOverviewContribution(BigDecimal(300), BigDecimal(150)),
        )
    }

    @MockK
    lateinit var jpaQueryFactory: JPAQueryFactory

    @InjectMockKs
    lateinit var persistence: PaymentAccountFinancePersistenceProvider

    @Test
    fun getTotalsForFinishedEcPayments() {
        val query = mockk<JPAQuery<Tuple>>()
        val paymentToEcPriorityAxisOverview = QPaymentToEcPriorityAxisOverviewEntity.paymentToEcPriorityAxisOverviewEntity
        val programmePriority = QProgrammePriorityEntity.programmePriorityEntity

        val totalEligibleExpr = paymentToEcPriorityAxisOverview.totalEligibleExpenditure.sum()
        val totalUnionExpr = paymentToEcPriorityAxisOverview.totalUnionContribution.sum()
        val totalPublicExpr = paymentToEcPriorityAxisOverview.totalPublicContribution.sum()

        every { jpaQueryFactory.select(any(), any(), any(), any(), any()) } returns query
        every { query.from(paymentToEcPriorityAxisOverview) } returns query
        every { query.leftJoin(programmePriority).on(any()) } returns query
        every { query.where(any()) } returns query
        every { query.groupBy(programmePriority.id) } returns query
        every { query.fetch() } returns paymentToEcPriorityAxis.map {
            mockk<Tuple> {
                every { get(programmePriority.id) } returns it.priorityAxis?.id
                every { get(programmePriority.code) } returns it.priorityAxis?.code
                every { get(totalEligibleExpr) } returns it.totalEligibleExpenditure
                every { get(totalUnionExpr) } returns it.totalUnionContribution
                every { get(totalPublicExpr) } returns it.totalPublicContribution
            }
        }

        assertThat(persistence.getTotalsForFinishedEcPayments(setOf(1L, 2L, 3L))).isEqualTo(
            mapOf(
                11L to PaymentToEcAmountSummaryLine(
                    priorityAxis = "P01",
                    totalEligibleExpenditure = BigDecimal(101),
                    totalUnionContribution = BigDecimal(102),
                    totalPublicContribution = BigDecimal(103),
                ),
                22L to PaymentToEcAmountSummaryLine(
                    priorityAxis = "P02",
                    totalEligibleExpenditure = BigDecimal(201),
                    totalUnionContribution = BigDecimal(202),
                    totalPublicContribution = BigDecimal(203),
                ),
                33L to PaymentToEcAmountSummaryLine(
                    priorityAxis = "P03",
                    totalEligibleExpenditure = BigDecimal(301),
                    totalUnionContribution = BigDecimal.ZERO,
                    totalPublicContribution = BigDecimal(302),
                ),
            )
        )
    }

    @Test
    fun getOverviewTotalsForFinishedPaymentAccounts() {
        val query = mockk<JPAQuery<Tuple>>()
        val paymentToEc = QPaymentApplicationToEcEntity.paymentApplicationToEcEntity
        val paymentToEcPriorityAxisOverview = QPaymentToEcPriorityAxisOverviewEntity.paymentToEcPriorityAxisOverviewEntity
        val paymentAccount = QPaymentAccountEntity.paymentAccountEntity
        val paymentAccountPriorityAxisOverview = QPaymentAccountPriorityAxisOverviewEntity.paymentAccountPriorityAxisOverviewEntity

        val totalEligibleAccountExpr = paymentAccountPriorityAxisOverview.totalEligibleExpenditure.sum()
        val totalPublicAccountExpr = paymentAccountPriorityAxisOverview.totalPublicContribution.sum()
        val totalEligibleEcExpr = paymentToEcPriorityAxisOverview.totalEligibleExpenditure.sum()
        val totalUnionEcExpr = paymentToEcPriorityAxisOverview.totalUnionContribution.sum()
        val totalPublicEcExpr = paymentToEcPriorityAxisOverview.totalPublicContribution.sum()
        val totalEligibleExpr = totalEligibleAccountExpr.add(totalEligibleEcExpr).add(totalUnionEcExpr)
        val totalPublicExpr = totalPublicAccountExpr.add(totalPublicEcExpr)

        every { jpaQueryFactory.select(any(), any(), any()) } returns query
        every { query.from(paymentAccount) } returns query
        every { query.leftJoin(paymentAccountPriorityAxisOverview).on(any()) } returns query
        every { query.leftJoin(paymentToEc).on(any()) } returns query
        every { query.leftJoin(paymentToEcPriorityAxisOverview).on(any()) } returns query
        every { query.where(paymentAccount.status.eq(PaymentAccountStatus.FINISHED)) } returns query
        every { query.groupBy(paymentAccount.id) } returns query
        every { query.fetch() } returns overviewContribution.map {
            mockk<Tuple> {
                every { get(paymentAccount.id) } returns it.key
                every { get(totalEligibleExpr) } returns it.value.totalEligibleExpenditure
                every { get(totalPublicExpr) } returns it.value.totalPublicContribution
            }
        }

        assertThat(persistence.getOverviewContributionForPaymentAccounts()).isEqualTo(overviewContribution)
    }
}
