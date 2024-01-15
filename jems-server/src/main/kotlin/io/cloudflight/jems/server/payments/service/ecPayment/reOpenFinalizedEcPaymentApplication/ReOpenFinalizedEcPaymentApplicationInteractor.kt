package io.cloudflight.jems.server.payments.service.ecPayment.reOpenFinalizedEcPaymentApplication

import io.cloudflight.jems.server.payments.model.ec.PaymentApplicationToEcDetail

interface ReOpenFinalizedEcPaymentApplicationInteractor {

    fun reOpen(ecPaymentApplicationId: Long): PaymentApplicationToEcDetail

}
