package io.cloudflight.jems.server.payments.service.ecPayment.linkToCorrection.getAvailableClosedCorrections

import io.cloudflight.jems.server.payments.model.ec.PaymentToEcCorrectionLinking
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface GetAvailableClosedCorrectionsForEcPaymentInteractor {

    fun getClosedCorrectionList(pageable: Pageable, ecPaymentId: Long): Page<PaymentToEcCorrectionLinking>

}
