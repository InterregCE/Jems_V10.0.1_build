package io.cloudflight.jems.server.payments.controller.applicationToEc

import io.cloudflight.jems.api.payments.applicationToEc.CorrectionToEcPaymentLinkingApi
import io.cloudflight.jems.api.payments.dto.PaymentToEcCorrectionExtensionDTO
import io.cloudflight.jems.api.payments.dto.PaymentToEcCorrectionLinkingDTO
import io.cloudflight.jems.api.payments.dto.PaymentToEcCorrectionLinkingUpdateDTO
import io.cloudflight.jems.server.payments.service.ecPayment.linkToCorrection.deselectCorrection.DeselectCorrectionFromEcInteractor
import io.cloudflight.jems.server.payments.service.ecPayment.linkToCorrection.getAvailableClosedCorrections.GetAvailableClosedCorrectionsForEcPaymentInteractor
import io.cloudflight.jems.server.payments.service.ecPayment.linkToCorrection.selectCorrection.SelectCorrectionToEcPaymentInteractor
import io.cloudflight.jems.server.payments.service.ecPayment.linkToCorrection.updateCorrection.UpdateLinkedCorrectionToEcPaymentInteractor
import io.cloudflight.jems.server.payments.service.toDto
import io.cloudflight.jems.server.payments.service.toModel
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.web.bind.annotation.RestController

@RestController
class CorrectionToEcPaymentLinkingController(
    private val getAvailableCorrections: GetAvailableClosedCorrectionsForEcPaymentInteractor,
    private val selectCorrectionToEcPayment: SelectCorrectionToEcPaymentInteractor,
    private val deselectCorrectionToEcPayment: DeselectCorrectionFromEcInteractor,
    private val updateLinkedCorrection: UpdateLinkedCorrectionToEcPaymentInteractor,
) : CorrectionToEcPaymentLinkingApi {

    override fun getAvailableCorrections(
        pageable: Pageable,
        ecApplicationId: Long
    ): Page<PaymentToEcCorrectionLinkingDTO> =
        getAvailableCorrections.getClosedCorrectionList(pageable, ecApplicationId).map { it.toDto() }

    override fun selectCorrectionToEcPayment(ecApplicationId: Long, correctionId: Long) =
        selectCorrectionToEcPayment.selectCorrectionToEcPayment(
            correctionId = correctionId,
            ecPaymentId = ecApplicationId
        )

    override fun deselectCorrectionFromEcPayment(correctionId: Long) =
        deselectCorrectionToEcPayment.deselectCorrectionFromEcPayment(correctionId = correctionId)

    override fun updateLinkedCorrection(
        correctionId: Long,
        correctionLinkingUpdate: PaymentToEcCorrectionLinkingUpdateDTO
    ): PaymentToEcCorrectionExtensionDTO =
        updateLinkedCorrection.updateLinkedCorrection(
            correctionId = correctionId,
            updateLinkedCorrection = correctionLinkingUpdate.toModel()
        ).toDto()

}

