package io.cloudflight.jems.server.payments.controller

import io.cloudflight.jems.api.payments.dto.AdvancePaymentDTO
import io.cloudflight.jems.api.payments.dto.AdvancePaymentDetailDTO
import io.cloudflight.jems.api.payments.dto.AdvancePaymentUpdateDTO
import io.cloudflight.jems.api.payments.PaymentAdvanceApi
import io.cloudflight.jems.server.payments.service.advance.deleteAdvancePayment.DeleteAdvancePaymentInteractor
import io.cloudflight.jems.server.payments.service.advance.getAdvancePaymentDetail.GetAdvancePaymentDetailInteractor
import io.cloudflight.jems.server.payments.service.advance.getAdvancePayments.GetAdvancePaymentsInteractor
import io.cloudflight.jems.server.payments.service.toDTO
import io.cloudflight.jems.server.payments.service.toModel
import io.cloudflight.jems.server.payments.service.advance.updateAdvancePaymentDetail.UpdateAdvancePaymentDetailInteractor
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.web.bind.annotation.RestController

@RestController
class PaymentAdvanceController(
    private val getAdvancePayments: GetAdvancePaymentsInteractor,
    private val getAdvancePaymentDetail: GetAdvancePaymentDetailInteractor,
    private val updateAdvancePaymentDetail: UpdateAdvancePaymentDetailInteractor,
    private val deleteAdvancePayment: DeleteAdvancePaymentInteractor
): PaymentAdvanceApi {

    override fun getAdvancePayments(pageable: Pageable): Page<AdvancePaymentDTO> {
        return getAdvancePayments.list(pageable).map { it.toDTO() }
    }

    override fun getAdvancePaymentDetail(paymentId: Long): AdvancePaymentDetailDTO {
        return getAdvancePaymentDetail.getPaymentDetail(paymentId).toDTO()
    }

    override fun updateAdvancePayment(
        advancePayment: AdvancePaymentUpdateDTO
    ): AdvancePaymentDetailDTO {
        return updateAdvancePaymentDetail.updateDetail(advancePayment.toModel()).toDTO()
    }

    override fun deleteAdvancePayment(paymentId: Long) {
        return deleteAdvancePayment.delete(paymentId)
    }
}
