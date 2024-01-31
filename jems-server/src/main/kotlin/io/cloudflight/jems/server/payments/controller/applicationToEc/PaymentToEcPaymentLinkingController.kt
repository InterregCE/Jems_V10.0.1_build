package io.cloudflight.jems.server.payments.controller.applicationToEc

import io.cloudflight.jems.api.payments.applicationToEc.PaymentToEcPaymentLinkingApi
import io.cloudflight.jems.api.payments.dto.PaymentToEcAmountSummaryDTO
import io.cloudflight.jems.api.payments.dto.PaymentToEcLinkingDTO
import io.cloudflight.jems.api.payments.dto.PaymentToEcLinkingUpdateDTO
import io.cloudflight.jems.api.payments.dto.PaymentToEcOverviewTypeDTO
import io.cloudflight.jems.api.payments.dto.PaymentTypeDTO
import io.cloudflight.jems.server.payments.service.ecPayment.linkToPayment.deselectPayment.DeselectPaymentFromEcInteractor
import io.cloudflight.jems.server.payments.service.ecPayment.linkToPayment.getCumulativeAmountsForArtNot94Not95.GetOverviewByTypeInteractor
import io.cloudflight.jems.server.payments.service.ecPayment.linkToPayment.getCumulativeOverview.GetCumulativeOverviewInteractor
import io.cloudflight.jems.server.payments.service.ecPayment.linkToPayment.getPayments.art94Art95.GetPaymentsAvailableForArt94Art95Interactor
import io.cloudflight.jems.server.payments.service.ecPayment.linkToPayment.getPayments.artNot94Not95.GetPaymentsAvailableArtNot94Not95Interactor
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
    private val getPaymentsAvailableForArt94Art95Interactor: GetPaymentsAvailableForArt94Art95Interactor,
    private val getPaymentsAvailableArtNot94Not95Interactor: GetPaymentsAvailableArtNot94Not95Interactor,
    private val deselectPaymentFromEc: DeselectPaymentFromEcInteractor,
    private val selectPaymentToEc: SelectPaymentToEcInteractor,
    private val updateLinkedPayment: UpdateLinkedPaymentInteractor,
    private val getCumulativeAmountsSummaryInteractor: GetOverviewByTypeInteractor,
    private val getCumulativeOverview: GetCumulativeOverviewInteractor
) : PaymentToEcPaymentLinkingApi {

    override fun getPaymentsLinkedWithEcForArt94OrArt95(
        pageable: Pageable,
        ecPaymentId: Long,
        projectPaymentType: PaymentTypeDTO
    ): Page<PaymentToEcLinkingDTO> =
        getPaymentsAvailableForArt94Art95Interactor.getPaymentList(pageable, ecPaymentId, projectPaymentType.toModel())
            .toPaymentToEcLinkingDTO()


    override fun getPaymentsLinkedWithEcNotArt94NotArt95(
        pageable: Pageable,
        ecPaymentId: Long,
        projectPaymentType: PaymentTypeDTO
    ): Page<PaymentToEcLinkingDTO> =
        getPaymentsAvailableArtNot94Not95Interactor.getPaymentList(pageable, ecPaymentId, projectPaymentType.toModel())
            .toPaymentToEcLinkingDTO()


    override fun selectPaymentToEcPayment(ecPaymentId: Long, paymentId: Long) =
        selectPaymentToEc.selectPaymentToEcPayment(paymentId = paymentId, ecPaymentId)

    override fun deselectPaymentFromEcPayment(ecPaymentId: Long, paymentId: Long) =
        deselectPaymentFromEc.deselectPaymentFromEcPayment(paymentId)

    override fun updateLinkedPayment(
        ecPaymentId: Long,
        paymentId: Long,
        paymentToEcLinkingUpdate: PaymentToEcLinkingUpdateDTO,
    ) =  updateLinkedPayment.updateLinkedPayment(paymentId, paymentToEcLinkingUpdate.toModel())

    override fun getPaymentApplicationToEcOverviewAmountsByType(ecPaymentId: Long, type: PaymentToEcOverviewTypeDTO?): PaymentToEcAmountSummaryDTO =
        getCumulativeAmountsSummaryInteractor.getOverviewAmountsByType(ecPaymentId, type?.toModel()).toDto()

    override fun getPaymentApplicationToEcCumulativeOverview(ecPaymentId: Long): PaymentToEcAmountSummaryDTO =
        getCumulativeOverview.getCumulativeOverview(ecPaymentId).toDto()
}
