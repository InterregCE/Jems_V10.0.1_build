package io.cloudflight.jems.server.payments.controller.applicationToEc

import io.cloudflight.jems.api.payments.applicationToEc.PaymentToEcPaymentLinkingApi
import io.cloudflight.jems.api.payments.dto.PaymentToEcLinkingDTO
import io.cloudflight.jems.api.payments.dto.PaymentToEcLinkingUpdateDTO
import io.cloudflight.jems.server.payments.service.paymentApplicationsToEc.linkedPaymentsToEc.deselectPayment.DeselectPaymentFromEcInteractor
import io.cloudflight.jems.server.payments.service.paymentApplicationsToEc.linkedPaymentsToEc.getPayments.artNot94Not95.GetFtlsPaymentsAvailableForArtNot94Not95Interactor
import io.cloudflight.jems.server.payments.service.paymentApplicationsToEc.linkedPaymentsToEc.selectPayment.SelectPaymentToEcInteractor
import io.cloudflight.jems.server.payments.service.paymentApplicationsToEc.linkedPaymentsToEc.updatePayment.UpdateLinkedPaymentInteractor
import io.cloudflight.jems.server.payments.service.toModel
import io.cloudflight.jems.server.payments.service.toPaymentToEcLinkingDTO
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.web.bind.annotation.RestController

@RestController
class PaymentToEcPaymentLinkingController(
    private val getPaymentsAvailableForArtNot94Not95: GetFtlsPaymentsAvailableForArtNot94Not95Interactor,
    private val deselectPaymentFromEc: DeselectPaymentFromEcInteractor,
    private val selectPaymentToEc: SelectPaymentToEcInteractor,
    private val updateLinkedPayment: UpdateLinkedPaymentInteractor
) : PaymentToEcPaymentLinkingApi {

    override fun getFTLSPaymentsLinkedWithEcForArtNot94Not95(pageable: Pageable, ecApplicationId: Long): Page<PaymentToEcLinkingDTO> =
        getPaymentsAvailableForArtNot94Not95.getPaymentList(pageable, ecApplicationId).toPaymentToEcLinkingDTO()

    override fun selectPaymentToEcPayment(ecApplicationId: Long, paymentId: Long) =
        selectPaymentToEc.selectPaymentToEcPayment(paymentId = paymentId, ecApplicationId)

    override fun deselectPaymentFromEcPayment(paymentId: Long) =
        deselectPaymentFromEc.deselectPaymentFromEcPayment(paymentId)

    override fun updateLinkedPayment(
        paymentId: Long,
        paymentToEcLinkingUpdate: PaymentToEcLinkingUpdateDTO,
    ) =  updateLinkedPayment.updateLinkedPayment(paymentId, paymentToEcLinkingUpdate.toModel())
}
