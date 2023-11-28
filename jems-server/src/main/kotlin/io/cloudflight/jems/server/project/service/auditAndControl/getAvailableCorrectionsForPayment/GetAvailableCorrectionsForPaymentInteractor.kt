package io.cloudflight.jems.server.project.service.auditAndControl.getAvailableCorrectionsForPayment

import io.cloudflight.jems.server.project.service.auditAndControl.model.correction.impact.AvailableCorrectionsForPayment

interface GetAvailableCorrectionsForPaymentInteractor {

    fun getAvailableCorrections(paymentId: Long): List<AvailableCorrectionsForPayment>
}
