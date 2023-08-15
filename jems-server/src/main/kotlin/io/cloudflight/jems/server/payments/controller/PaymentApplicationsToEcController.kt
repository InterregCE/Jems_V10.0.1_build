package io.cloudflight.jems.server.payments.controller

import io.cloudflight.jems.api.payments.PaymentApplicationsToEcApi
import io.cloudflight.jems.api.payments.dto.PaymentApplicationsToEcDTO
import io.cloudflight.jems.api.payments.dto.PaymentApplicationsToEcDetailDTO
import io.cloudflight.jems.api.payments.dto.PaymentApplicationsToEcUpdateDTO
import io.cloudflight.jems.server.payments.service.toDto
import io.cloudflight.jems.server.payments.service.toModel
import io.cloudflight.jems.server.payments.service.paymentApplicationsToEc.deletePaymentApplicationsToEc.DeletePaymentApplicationsToEcInteractor
import io.cloudflight.jems.server.payments.service.paymentApplicationsToEc.getPaymentApplicationsToEc.GetPaymentApplicationsToEcInteractor
import io.cloudflight.jems.server.payments.service.paymentApplicationsToEc.getPaymentApplicationsToEcDetail.GetPaymentApplicationsToEcDetailInteractor
import io.cloudflight.jems.server.payments.service.paymentApplicationsToEc.updatePaymentApplicationsToEcDetail.UpdatePaymentApplicationsToEcDetailInteractor
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.web.bind.annotation.RestController

@RestController
class PaymentApplicationsToEcController(
    private val updatePaymentApplicationsToEc: UpdatePaymentApplicationsToEcDetailInteractor,
    private val getPaymentApplicationsToEc: GetPaymentApplicationsToEcInteractor,
    private val deletePaymentApplicationsToEc: DeletePaymentApplicationsToEcInteractor,
    private val getPaymentApplicationsToEcDetail: GetPaymentApplicationsToEcDetailInteractor
) : PaymentApplicationsToEcApi {

    override fun updatePaymentApplicationsToEc(paymentApplicationsToEcUpdate: PaymentApplicationsToEcUpdateDTO): PaymentApplicationsToEcDetailDTO {
        return updatePaymentApplicationsToEc.updatePaymentApplicationsToEc(paymentApplicationsToEcUpdate.toModel())
            .toDto()
    }

    override fun getPaymentApplicationsToEcDetail(id: Long): PaymentApplicationsToEcDetailDTO {
        return getPaymentApplicationsToEcDetail.getPaymentApplicationsToEcDetail(id).toDto()
    }

    override fun getPaymentApplicationsToEc(pageable: Pageable): Page<PaymentApplicationsToEcDTO> =
        getPaymentApplicationsToEc.getPaymentApplicationsToEc(pageable).toDto()

    override fun deletePaymentApplicationToEc(id: Long) {
        deletePaymentApplicationsToEc.deleteById(id)
    }

}
