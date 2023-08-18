package io.cloudflight.jems.server.payments.controller

import io.cloudflight.jems.api.accountingYear.AccountingYearDTO
import io.cloudflight.jems.api.payments.dto.PaymentApplicationToEcDTO
import io.cloudflight.jems.api.payments.dto.PaymentApplicationToEcDetailDTO
import io.cloudflight.jems.api.payments.dto.PaymentApplicationToEcSummaryDTO
import io.cloudflight.jems.api.payments.dto.PaymentApplicationToEcUpdateDTO
import io.cloudflight.jems.api.payments.dto.PaymentEcStatusDTO
import io.cloudflight.jems.api.programme.dto.fund.ProgrammeFundDTO
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.payments.model.ec.PaymentApplicationToEc
import io.cloudflight.jems.server.payments.model.ec.PaymentApplicationToEcDetail
import io.cloudflight.jems.server.payments.model.ec.PaymentApplicationToEcSummary
import io.cloudflight.jems.server.payments.model.ec.PaymentApplicationToEcUpdate
import io.cloudflight.jems.server.payments.model.regular.AccountingYear
import io.cloudflight.jems.server.payments.model.regular.PaymentEcStatus
import io.cloudflight.jems.server.payments.service.paymentApplicationsToEc.createPaymentApplicationToEc.CreatePaymentApplicationToEcInteractor
import io.cloudflight.jems.server.payments.service.paymentApplicationsToEc.deletePaymentApplicationToEc.DeletePaymentApplicationToEcInteractor
import io.cloudflight.jems.server.payments.service.paymentApplicationsToEc.getPaymentApplicationsToEc.GetPaymentApplicationsToEcInteractor
import io.cloudflight.jems.server.payments.service.paymentApplicationsToEc.getPaymentApplicationToEcDetail.GetPaymentApplicationToEcDetailInteractor
import io.cloudflight.jems.server.payments.service.paymentApplicationsToEc.updatePaymentApplicationToEcDetail.UpdatePaymentApplicationToEcDetailInteractor
import io.cloudflight.jems.server.programme.service.fund.model.ProgrammeFund
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import java.time.LocalDate

class PaymentApplicationsToEcControllerTest : UnitTest() {
    companion object {
        private const val paymentApplicationsToEcId = 1L
        private val accountingYearDTO =
            AccountingYearDTO(2L, 2021, LocalDate.of(2021, 1, 1), LocalDate.of(2022, 6, 30))
        private val accountingYear = AccountingYear(2L, 2021, LocalDate.of(2021, 1, 1), LocalDate.of(2022, 6, 30))
        private val fundDTO = ProgrammeFundDTO(id = 3L, selected = true)
        private val fund = ProgrammeFund(id = 3L, selected = true)

        private val paymentApplicationToEcDTO = PaymentApplicationToEcDTO(
            id = paymentApplicationsToEcId,
            programmeFund = fundDTO,
            accountingYear = accountingYearDTO,
            status = PaymentEcStatusDTO.Draft
        )
        private val paymentApplicationToEc = PaymentApplicationToEc(
            id = paymentApplicationsToEcId,
            programmeFund = fund,
            accountingYear = accountingYear,
            status = PaymentEcStatus.Draft
        )

        private val paymentApplicationsToEcUpdate = PaymentApplicationToEcUpdate(
            id = paymentApplicationsToEcId,
            programmeFundId = fund.id,
            accountingYearId = accountingYear.id
        )

        private val paymentApplicationsToEcSummaryDTO = PaymentApplicationToEcSummaryDTO(
            programmeFund = fundDTO,
            accountingYear = accountingYearDTO
        )
        private val paymentApplicationsToEcSummary = PaymentApplicationToEcSummary(
            programmeFund = fund,
            accountingYear = accountingYear
        )

        private val paymentApplicationsToEcDetailDTO = PaymentApplicationToEcDetailDTO(
            id = paymentApplicationsToEcId,
            status = PaymentEcStatusDTO.Draft,
            paymentApplicationsToEcSummary = paymentApplicationsToEcSummaryDTO
        )
        private val paymentApplicationsToEcDetail = PaymentApplicationToEcDetail(
            id = paymentApplicationsToEcId,
            status = PaymentEcStatus.Draft,
            paymentApplicationsToEcSummary = paymentApplicationsToEcSummary
        )
    }

        @MockK
        lateinit var getPaymentApplicationsToEc: GetPaymentApplicationsToEcInteractor

        @MockK
        lateinit var getPaymentApplicationToEcDetail: GetPaymentApplicationToEcDetailInteractor

        @MockK
        lateinit var updatePaymentApplicationToEcDetail: UpdatePaymentApplicationToEcDetailInteractor

        @MockK
        lateinit var createPaymentApplicationToEc: CreatePaymentApplicationToEcInteractor

        @MockK
        lateinit var deletePaymentApplicationToEc: DeletePaymentApplicationToEcInteractor

        @InjectMockKs
        private lateinit var controller: PaymentApplicationToEcController

        @Test
        fun getPaymentApplicationsToEc() {
            every { getPaymentApplicationsToEc.getPaymentApplicationsToEc(Pageable.unpaged()) } returns PageImpl(listOf(paymentApplicationToEc))

            assertThat(controller.getPaymentApplicationsToEc(Pageable.unpaged())).isEqualTo(
                PageImpl(listOf(paymentApplicationToEcDTO))
            )
        }

        @Test
        fun getPaymentApplicationToEcDetail() {
            every { getPaymentApplicationToEcDetail.getPaymentApplicationToEcDetail(paymentApplicationsToEcId) } returns paymentApplicationsToEcDetail

            assertThat(controller.getPaymentApplicationToEcDetail(paymentApplicationsToEcId)).isEqualTo(
                paymentApplicationsToEcDetailDTO
            )
        }

        @Test
        fun updatePaymentApplicationToEcDetail() {
            val updateDTO = PaymentApplicationToEcUpdateDTO(
                id = paymentApplicationsToEcId,
                programmeFundId = fund.id,
                accountingYearId = accountingYear.id
            )
            every {
                updatePaymentApplicationToEcDetail.updatePaymentApplicationToEc(paymentApplicationsToEcUpdate)
            } returns paymentApplicationsToEcDetail

            assertThat(controller.updatePaymentApplicationToEc(updateDTO)).isEqualTo(paymentApplicationsToEcDetailDTO)
        }

        @Test
        fun createPaymentApplicationToEc() {
            val updateDTO = PaymentApplicationToEcUpdateDTO(
                id = paymentApplicationsToEcId,
                programmeFundId = fund.id,
                accountingYearId = accountingYear.id
            )

            every {
                createPaymentApplicationToEc.createPaymentApplicationToEc(paymentApplicationsToEcUpdate)
            } returns paymentApplicationsToEcDetail

            assertThat(controller.createPaymentApplicationToEc(updateDTO)).isEqualTo(paymentApplicationsToEcDetailDTO)
        }

        @Test
        fun deletePaymentApplicationsToEc() {
            every { deletePaymentApplicationToEc.deleteById(paymentApplicationsToEcId) } returns Unit

            controller.deletePaymentApplicationToEc(paymentApplicationsToEcId)
            verify(exactly = 1) { deletePaymentApplicationToEc.deleteById(paymentApplicationsToEcId) }

        }
}
