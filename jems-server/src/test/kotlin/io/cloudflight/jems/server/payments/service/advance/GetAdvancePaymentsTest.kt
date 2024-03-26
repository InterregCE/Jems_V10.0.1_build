package io.cloudflight.jems.server.payments.service.advance

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.payments.model.advance.AdvancePayment
import io.cloudflight.jems.server.payments.model.advance.AdvancePaymentSearchRequest
import io.cloudflight.jems.server.payments.model.advance.AdvancePaymentSettlement
import io.cloudflight.jems.server.payments.service.advance.getAdvancePayments.GetAdvancePayments
import io.cloudflight.jems.server.programme.service.fund.model.ProgrammeFund
import io.cloudflight.jems.server.project.service.ProjectVersionPersistence
import io.cloudflight.jems.server.project.service.application.ApplicationStatus
import io.cloudflight.jems.server.project.service.model.ProjectVersion
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerRole
import io.cloudflight.jems.server.toScaledBigDecimal
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import java.math.BigDecimal
import java.time.LocalDate
import java.time.ZonedDateTime

class GetAdvancePaymentsTest: UnitTest() {


    companion object {

        val currentDate = LocalDate.now()
        private val fund = ProgrammeFund(id = 5L, selected = true)

        private val advancePayment = AdvancePayment(
            id = 1L,
            projectCustomIdentifier = "dummyProj1",
            projectAcronym = "dummyAcronym",
            partnerType = ProjectPartnerRole.LEAD_PARTNER,
            partnerSortNumber = null,
            partnerAbbreviation = "abbr.",
            programmeFund = fund,
            amountPaid = 120.87.toScaledBigDecimal(),
            paymentDate = currentDate,
            amountSettled = 100.00.toScaledBigDecimal(),
            paymentSettlements = listOf(
                AdvancePaymentSettlement(
                    id = 1L,
                    number = 1,
                    amountSettled = BigDecimal(57.9),
                    settlementDate = currentDate.minusDays(1),
                    comment = "half"
                ),
                AdvancePaymentSettlement(
                    id = 1L,
                    number = 1,
                    amountSettled = BigDecimal(42.1),
                    settlementDate = currentDate.minusDays(1),
                    comment = "half"
                )
            ),
            partnerNameInOriginalLanguage = "name org lang",
            partnerNameInEnglish = "name en",
            projectId = 10L,
            linkedProjectVersion = "v1.0",
            lastApprovedProjectVersion = "v1.0"
        )
    }

    @MockK
    private lateinit var paymentPersistence: PaymentAdvancePersistence

    @MockK
    private lateinit var projectVersionPersistence: ProjectVersionPersistence

    @InjectMockKs
    private lateinit var getAdvancePayments: GetAdvancePayments


    @Test
    fun `list advance payments`() {

        val filters = mockk<AdvancePaymentSearchRequest>()
        every { paymentPersistence.list(Pageable.unpaged(), filters) } returns PageImpl(listOf(advancePayment))
        every { projectVersionPersistence.getAllVersionsByProjectIdIn(setOf(10L)) } returns listOf(
            ProjectVersion(
                version = "v1.0",
                createdAt = ZonedDateTime.now().minusDays(1),
                projectId = 10L,
                current = true,
                status = ApplicationStatus.CONTRACTED
            )
        )

        val result = advancePayment.copy(amountSettled = 100.00.toScaledBigDecimal())
        assertThat(getAdvancePayments.list(Pageable.unpaged(), filters)).contains(result)
    }

}
