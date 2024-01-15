package io.cloudflight.jems.server.payments.controller.applicationToEc

import io.cloudflight.jems.api.payments.dto.applicationToEc.AccountingYearDTO
import io.cloudflight.jems.api.payments.dto.PaymentApplicationToEcCreateDTO
import io.cloudflight.jems.api.payments.dto.PaymentApplicationToEcDTO
import io.cloudflight.jems.api.payments.dto.PaymentApplicationToEcDetailDTO
import io.cloudflight.jems.api.payments.dto.PaymentApplicationToEcSummaryDTO
import io.cloudflight.jems.api.payments.dto.PaymentApplicationToEcSummaryUpdateDTO
import io.cloudflight.jems.api.payments.dto.PaymentEcStatusDTO
import io.cloudflight.jems.api.payments.dto.PaymentEcStatusUpdateDTO
import io.cloudflight.jems.api.payments.dto.applicationToEc.AccountingYearAvailabilityDTO
import io.cloudflight.jems.api.programme.dto.fund.ProgrammeFundDTO
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.payments.model.ec.PaymentApplicationToEc
import io.cloudflight.jems.server.payments.model.ec.PaymentApplicationToEcCreate
import io.cloudflight.jems.server.payments.model.ec.PaymentApplicationToEcDetail
import io.cloudflight.jems.server.payments.model.ec.PaymentApplicationToEcSummary
import io.cloudflight.jems.server.payments.model.ec.PaymentApplicationToEcSummaryUpdate
import io.cloudflight.jems.server.payments.model.ec.AccountingYear
import io.cloudflight.jems.server.payments.model.ec.AccountingYearAvailability
import io.cloudflight.jems.server.payments.model.regular.PaymentEcStatus
import io.cloudflight.jems.server.payments.service.ecPayment.createPaymentApplicationToEc.CreatePaymentApplicationToEcInteractor
import io.cloudflight.jems.server.payments.service.ecPayment.deletePaymentApplicationToEc.DeletePaymentApplicationToEcInteractor
import io.cloudflight.jems.server.payments.service.ecPayment.finalizePaymentApplicationToEc.FinalizePaymentApplicationToEcInteractor
import io.cloudflight.jems.server.payments.service.ecPayment.getAvailableAccountingYearsForPaymentFund.GetAvailableAccountingYearsForPaymentFundInteractor
import io.cloudflight.jems.server.payments.service.ecPayment.getPaymentApplicationToEcDetail.GetPaymentApplicationToEcDetailInteractor
import io.cloudflight.jems.server.payments.service.ecPayment.getPaymentApplicationToEcList.GetPaymentApplicationToEcListInteractor
import io.cloudflight.jems.server.payments.service.ecPayment.reOpenFinalizedEcPaymentApplication.ReOpenFinalizedEcPaymentApplicationInteractor
import io.cloudflight.jems.server.payments.service.ecPayment.updatePaymentApplicationToEcDetail.UpdatePaymentApplicationToEcDetailInteractor
import io.cloudflight.jems.server.programme.service.fund.model.ProgrammeFund
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import java.math.BigDecimal
import java.time.LocalDate

class PaymentApplicationToEcControllerTest : UnitTest() {
    companion object {
        private const val paymentApplicationsToEcId = 1L
        private val accountingYearDTO =
            AccountingYearDTO(2L, 2021, LocalDate.of(2021, 1, 1), LocalDate.of(2022, 6, 30))
        private val accountingYear = AccountingYear(2L, 2021, LocalDate.of(2021, 1, 1), LocalDate.of(2022, 6, 30))
        private val fundDTO = ProgrammeFundDTO(id = 3L, selected = true)
        private val fund = ProgrammeFund(id = 3L, selected = true)
        private val submissionDate = LocalDate.now()

        private val startDate = LocalDate.now()
        private val endDate = LocalDate.now().plusDays(5)

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


        private val paymentApplicationsToEcCreate = PaymentApplicationToEcCreate(
            id = null,
            programmeFundId = fund.id,
            accountingYearId = accountingYear.id,
            nationalReference = "National Reference",
            technicalAssistanceEur = BigDecimal.valueOf(105.32),
            submissionToSfcDate = submissionDate,
            sfcNumber = "SFC number",
            comment = "Comment"
        )

        private val paymentApplicationsToEcUpdate = PaymentApplicationToEcSummaryUpdate(
            id = paymentApplicationsToEcId,
            nationalReference = "National Reference",
            technicalAssistanceEur = BigDecimal.valueOf(105.32),
            submissionToSfcDate = submissionDate,
            sfcNumber = "SFC number",
            comment = "Comment"
        )

        private val paymentApplicationsToEcSummaryDTO = PaymentApplicationToEcSummaryDTO(
            programmeFund = fundDTO,
            accountingYear = accountingYearDTO,
            nationalReference = "National Reference",
            technicalAssistanceEur = BigDecimal.valueOf(105.32),
            submissionToSfcDate = submissionDate,
            sfcNumber = "SFC number",
            comment = "Comment"
        )
        private val paymentApplicationsToEcSummary = PaymentApplicationToEcSummary(
            programmeFund = fund,
            accountingYear = accountingYear,
            nationalReference = "National Reference",
            technicalAssistanceEur = BigDecimal.valueOf(105.32),
            submissionToSfcDate = submissionDate,
            sfcNumber = "SFC number",
            comment = "Comment"
        )

        private val paymentApplicationsToEcDetailDTO = PaymentApplicationToEcDetailDTO(
            id = paymentApplicationsToEcId,
            status = PaymentEcStatusDTO.Draft,
            availableToReOpen = false,
            paymentApplicationToEcSummary = paymentApplicationsToEcSummaryDTO
        )

        private fun paymentApplicationsToEcDetail(status: PaymentEcStatus = PaymentEcStatus.Draft, isAvailableToReOpen: Boolean = false) =
            PaymentApplicationToEcDetail(
                id = paymentApplicationsToEcId,
                status = status,
                isAvailableToReOpen = isAvailableToReOpen,
                paymentApplicationToEcSummary = paymentApplicationsToEcSummary
            )

        private fun paymentEcStatusUpdate(status: PaymentEcStatusDTO, isAvailableToReOpen: Boolean) = PaymentEcStatusUpdateDTO(
            status = status,
            availableToReOpen = isAvailableToReOpen
        )
    }

    @MockK
    lateinit var getPaymentApplicationsToEc: GetPaymentApplicationToEcListInteractor

    @MockK
    lateinit var getPaymentApplicationToEcDetail: GetPaymentApplicationToEcDetailInteractor

    @MockK
    lateinit var updatePaymentApplicationToEcDetail: UpdatePaymentApplicationToEcDetailInteractor

    @MockK
    lateinit var createPaymentApplicationToEc: CreatePaymentApplicationToEcInteractor

    @MockK
    lateinit var finalizePaymentApplicationToEc: FinalizePaymentApplicationToEcInteractor

    @MockK
    lateinit var deletePaymentApplicationToEc: DeletePaymentApplicationToEcInteractor

    @MockK
    lateinit var reOpenFinalizedEcPaymentApplication: ReOpenFinalizedEcPaymentApplicationInteractor

    @MockK
    lateinit var getAvailableAccountingYearsForPaymentFund: GetAvailableAccountingYearsForPaymentFundInteractor


    @InjectMockKs
    private lateinit var controller: PaymentApplicationToEcController

    @Test
    fun getPaymentApplicationsToEc() {
        every { getPaymentApplicationsToEc.getPaymentApplicationsToEc(Pageable.unpaged()) } returns PageImpl(
            listOf(
                paymentApplicationToEc
            )
        )

        assertThat(controller.getPaymentApplicationsToEc(Pageable.unpaged())).isEqualTo(
            PageImpl(listOf(paymentApplicationToEcDTO))
        )
    }

    @Test
    fun getPaymentApplicationToEcDetail() {
        every { getPaymentApplicationToEcDetail.getPaymentApplicationToEcDetail(paymentApplicationsToEcId) } returns paymentApplicationsToEcDetail()

        assertThat(controller.getPaymentApplicationToEcDetail(paymentApplicationsToEcId)).isEqualTo(
            paymentApplicationsToEcDetailDTO
        )
    }

    @Test
    fun updatePaymentApplicationToEcDetail() {
        val updateDTO = PaymentApplicationToEcSummaryUpdateDTO(
            id = paymentApplicationsToEcId,
            nationalReference = "National Reference",
            technicalAssistanceEur = BigDecimal.valueOf(105.32),
            submissionToSfcDate = submissionDate,
            sfcNumber = "SFC number",
            comment = "Comment"
        )
        every {
            updatePaymentApplicationToEcDetail.updatePaymentApplicationToEc(paymentApplicationsToEcId, paymentApplicationsToEcUpdate)
        } returns paymentApplicationsToEcDetail()

        assertThat(controller.updatePaymentApplicationToEc(paymentApplicationsToEcId, updateDTO)).isEqualTo(paymentApplicationsToEcDetailDTO)
    }

    @Test
    fun createPaymentApplicationToEc() {
        val createDTO = PaymentApplicationToEcCreateDTO(
            programmeFundId = fundDTO.id!!,
            accountingYearId = accountingYearDTO.id,
            nationalReference = "National Reference",
            technicalAssistanceEur = BigDecimal.valueOf(105.32),
            submissionToSfcDate = submissionDate,
            sfcNumber = "SFC number",
            comment = "Comment"
        )
        every {
            createPaymentApplicationToEc.createPaymentApplicationToEc(paymentApplicationsToEcCreate)
        } returns paymentApplicationsToEcDetail()

        assertThat(controller.createPaymentApplicationToEc(createDTO)).isEqualTo(paymentApplicationsToEcDetailDTO)
    }

    @Test
    fun deletePaymentApplicationsToEc() {
        every { deletePaymentApplicationToEc.deleteById(paymentApplicationsToEcId) } returns Unit

        controller.deletePaymentApplicationToEc(paymentApplicationsToEcId)
        verify(exactly = 1) { deletePaymentApplicationToEc.deleteById(paymentApplicationsToEcId) }

    }

    @Test
    fun finalizePaymentApplicationToEc() {
        every { finalizePaymentApplicationToEc.finalizePaymentApplicationToEc(paymentApplicationsToEcId) } returns paymentApplicationsToEcDetail(
            status = PaymentEcStatus.Finished,
            isAvailableToReOpen = true
        )

        assertThat(controller.finalizePaymentApplicationToEc(paymentApplicationsToEcId)).isEqualTo(
            paymentEcStatusUpdate(
                PaymentEcStatusDTO.Finished,
                true
            )
        )
    }


    @Test
    fun reOpenFinalizedEcPaymentApplication() {
        every { reOpenFinalizedEcPaymentApplication.reOpen(paymentApplicationsToEcId) } returns paymentApplicationsToEcDetail(
            status = PaymentEcStatus.Draft,
            isAvailableToReOpen = false
        )
        assertThat(controller.reOpenFinalizedEcPaymentApplication(paymentApplicationsToEcId)).isEqualTo(
            paymentEcStatusUpdate(PaymentEcStatusDTO.Draft, false)
        )
    }

    @Test
    fun getAvailableAccountingYearsForPaymentFund() {
        every { getAvailableAccountingYearsForPaymentFund.getAvailableAccountingYearsForPaymentFund(1L) } returns
                listOf(AccountingYearAvailability(id = 1L, year = 2021, startDate = startDate, endDate = endDate, true))
        assertThat(controller.getAvailableAccountingYearsForPaymentFund(1L)).containsExactly(
            AccountingYearAvailabilityDTO(
                id = 1L,
                year = 2021,
                startDate = startDate,
                endDate = endDate,
                available = true,
            )
        )
    }

}
