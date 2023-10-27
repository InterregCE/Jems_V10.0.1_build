package io.cloudflight.jems.server.payments.controller.applicationToEc

import io.cloudflight.jems.api.payments.applicationToEc.PaymentToEcPaymentLinkingApi
import io.cloudflight.jems.api.payments.dto.PaymentSearchRequestScoBasisDTO
import io.cloudflight.jems.api.payments.dto.PaymentToEcAmountSummaryDTO
import io.cloudflight.jems.api.payments.dto.PaymentToEcLinkingDTO
import io.cloudflight.jems.api.payments.dto.PaymentToEcLinkingUpdateDTO
import io.cloudflight.jems.server.payments.service.ecPayment.linkToPayment.deselectPayment.DeselectPaymentFromEcInteractor
import io.cloudflight.jems.server.payments.service.ecPayment.linkToPayment.getCumulativeAmountsForArtNot94Not95.GetOverviewByTypeInteractor
import io.cloudflight.jems.server.payments.service.ecPayment.linkToPayment.getCumulativeOverview.GetCumulativeOverviewInteractor
import io.cloudflight.jems.server.payments.service.ecPayment.linkToPayment.getPayments.artNot94Not95.ftls.GetFtlsPaymentsAvailableForArtNot94Not95Interactor
import io.cloudflight.jems.server.payments.service.ecPayment.linkToPayment.getPayments.artNot94Not95.regular.GetRegularPaymentsAvailableForArtNot94Not95Interactor
import io.cloudflight.jems.server.payments.service.ecPayment.linkToPayment.selectPayment.SelectPaymentToEcInteractor
import io.cloudflight.jems.server.payments.service.ecPayment.linkToPayment.updatePayment.UpdateLinkedPaymentInteractor
import io.cloudflight.jems.server.payments.service.toDto
import io.cloudflight.jems.server.payments.service.toModel
import io.cloudflight.jems.server.payments.service.toPaymentToEcLinkingDTO
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.web.bind.annotation.RestController

@RestController
class PaymentToEcPaymentLinkingController(
    private val getFtlsPaymentsAvailableForArtNot94Not95: GetFtlsPaymentsAvailableForArtNot94Not95Interactor,
    private val getRegularPaymentsAvailableForArtNot94Not95: GetRegularPaymentsAvailableForArtNot94Not95Interactor,
    private val deselectPaymentFromEc: DeselectPaymentFromEcInteractor,
    private val selectPaymentToEc: SelectPaymentToEcInteractor,
    private val updateLinkedPayment: UpdateLinkedPaymentInteractor,
    private val getCumulativeAmountsSummaryInteractor: GetOverviewByTypeInteractor,
    private val getCumulativeOverview: GetCumulativeOverviewInteractor
) : PaymentToEcPaymentLinkingApi {

    override fun getFTLSPaymentsLinkedWithEcForArtNot94Not95(pageable: Pageable, ecApplicationId: Long): Page<PaymentToEcLinkingDTO> =
        getFtlsPaymentsAvailableForArtNot94Not95.getPaymentList(pageable, ecApplicationId).toPaymentToEcLinkingDTO()

    override fun getRegularPaymentsLinkedWithEcForArtNot94Not95(pageable: Pageable, ecApplicationId: Long): Page<PaymentToEcLinkingDTO> =
        getRegularPaymentsAvailableForArtNot94Not95.getPaymentList(pageable, ecApplicationId).toPaymentToEcLinkingDTO()

    override fun selectPaymentToEcPayment(ecApplicationId: Long, paymentId: Long) =
        selectPaymentToEc.selectPaymentToEcPayment(paymentId = paymentId, ecApplicationId)

    override fun deselectPaymentFromEcPayment(paymentId: Long) =
        deselectPaymentFromEc.deselectPaymentFromEcPayment(paymentId)

    override fun updateLinkedPayment(
        paymentId: Long,
        paymentToEcLinkingUpdate: PaymentToEcLinkingUpdateDTO,
    ) =  updateLinkedPayment.updateLinkedPayment(paymentId, paymentToEcLinkingUpdate.toModel())

    override fun getPaymentApplicationToEcOverviewAmountsByType(paymentId: Long, type: PaymentSearchRequestScoBasisDTO?): PaymentToEcAmountSummaryDTO =
        getCumulativeAmountsSummaryInteractor.getOverviewAmountsByType(paymentId, type?.toModel()).toDto()

    override fun getPaymentApplicationToEcCumulativeOverview(paymentId: Long): PaymentToEcAmountSummaryDTO =
        getCumulativeOverview.getCumulativeOverview(paymentId).toDto()
}
