package io.cloudflight.jems.server.payments.controller

import io.cloudflight.jems.api.payments.PaymentApplicationToEcApi
import io.cloudflight.jems.api.payments.dto.PaymentApplicationToEcDTO
import io.cloudflight.jems.api.payments.dto.PaymentApplicationToEcDetailDTO
import io.cloudflight.jems.api.payments.dto.PaymentApplicationToEcUpdateDTO
import io.cloudflight.jems.server.payments.service.paymentApplicationsToEc.createPaymentApplicationToEc.CreatePaymentApplicationToEcInteractor
import io.cloudflight.jems.server.payments.service.toDto
import io.cloudflight.jems.server.payments.service.toModel
import io.cloudflight.jems.server.payments.service.paymentApplicationsToEc.deletePaymentApplicationToEc.DeletePaymentApplicationToEcInteractor
import io.cloudflight.jems.server.payments.service.paymentApplicationsToEc.getPaymentApplicationsToEc.GetPaymentApplicationsToEcInteractor
import io.cloudflight.jems.server.payments.service.paymentApplicationsToEc.getPaymentApplicationToEcDetail.GetPaymentApplicationToEcDetailInteractor
import io.cloudflight.jems.server.payments.service.paymentApplicationsToEc.updatePaymentApplicationToEcDetail.UpdatePaymentApplicationToEcDetailInteractor
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.web.bind.annotation.RestController

@RestController
class PaymentApplicationToEcController(
    private val createPaymentApplicationsToEc: CreatePaymentApplicationToEcInteractor,
    private val updatePaymentApplicationToEc: UpdatePaymentApplicationToEcDetailInteractor,
    private val getPaymentApplicationsToEc: GetPaymentApplicationsToEcInteractor,
    private val deletePaymentApplicationToEc: DeletePaymentApplicationToEcInteractor,
    private val getPaymentApplicationToEcDetail: GetPaymentApplicationToEcDetailInteractor
) : PaymentApplicationToEcApi {

    override fun createPaymentApplicationToEc(paymentApplicationToEcUpdate: PaymentApplicationToEcUpdateDTO): PaymentApplicationToEcDetailDTO =
        createPaymentApplicationsToEc.createPaymentApplicationToEc(paymentApplicationToEcUpdate.toModel())
            .toDto()

    override fun updatePaymentApplicationToEc(paymentApplicationToEcUpdate: PaymentApplicationToEcUpdateDTO): PaymentApplicationToEcDetailDTO =
        updatePaymentApplicationToEc.updatePaymentApplicationToEc(paymentApplicationToEcUpdate.toModel())
            .toDto()


    override fun getPaymentApplicationToEcDetail(id: Long): PaymentApplicationToEcDetailDTO {
        return getPaymentApplicationToEcDetail.getPaymentApplicationToEcDetail(id).toDto()
    }

    override fun getPaymentApplicationsToEc(pageable: Pageable): Page<PaymentApplicationToEcDTO> =
        getPaymentApplicationsToEc.getPaymentApplicationsToEc(pageable).toDto()

    override fun deletePaymentApplicationToEc(id: Long) {
        deletePaymentApplicationToEc.deleteById(id)
    }

}
