package io.cloudflight.jems.server.payments.controller.account.finance

import io.cloudflight.jems.api.payments.account.finance.PaymentAccountCorrectionLinkingApi
import io.cloudflight.jems.api.payments.dto.account.PaymentAccountAmountSummaryDTO
import io.cloudflight.jems.api.payments.dto.account.PaymentAccountCorrectionExtensionDTO
import io.cloudflight.jems.api.payments.dto.account.PaymentAccountCorrectionLinkingDTO
import io.cloudflight.jems.api.payments.dto.account.PaymentAccountCorrectionLinkingUpdateDTO
import io.cloudflight.jems.server.payments.service.account.finance.correction.deselectCorrection.DeselectCorrectionFromPaymentAccountInteractor
import io.cloudflight.jems.server.payments.service.account.finance.correction.getAvailableClosedCorrections.GetAvailableClosedCorrectionsForPaymentAccountInteractor
import io.cloudflight.jems.server.payments.service.account.finance.correction.getOverview.GetPaymentAccountCurrentOverviewInteractor
import io.cloudflight.jems.server.payments.service.account.finance.correction.selectCorrection.SelectCorrectionToPaymentAccountInteractor
import io.cloudflight.jems.server.payments.service.account.finance.correction.updateCorrection.UpdateLinkedCorrectionToPaymentAccountInteractor
import io.cloudflight.jems.server.payments.service.toDto
import io.cloudflight.jems.server.payments.service.toModel
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.web.bind.annotation.RestController

@RestController
class PaymentAccountCorrectionLinkingController(
    private val getAvailableCorrections: GetAvailableClosedCorrectionsForPaymentAccountInteractor,
    private val selectCorrection: SelectCorrectionToPaymentAccountInteractor,
    private val deselectCorrection: DeselectCorrectionFromPaymentAccountInteractor,
    private val updateLinkedCorrection: UpdateLinkedCorrectionToPaymentAccountInteractor,
    private val getCurrentOverview: GetPaymentAccountCurrentOverviewInteractor
) : PaymentAccountCorrectionLinkingApi {

    override fun getAvailableCorrections(
        pageable: Pageable,
        paymentAccountId: Long
    ): Page<PaymentAccountCorrectionLinkingDTO> =
        getAvailableCorrections.getClosedCorrections(pageable, paymentAccountId).map { it.toDto() }

    override fun selectCorrectionToPaymentAccount(paymentAccountId: Long, correctionId: Long) =
        selectCorrection.selectCorrection(correctionId = correctionId, paymentAccountId = paymentAccountId)

    override fun deselectCorrectionFromPaymentAccount(correctionId: Long) =
        deselectCorrection.deselectCorrection(correctionId = correctionId)

    override fun updateLinkedCorrection(
        correctionId: Long,
        correctionLinkingUpdate: PaymentAccountCorrectionLinkingUpdateDTO
    ): PaymentAccountCorrectionExtensionDTO =
        updateLinkedCorrection.updateCorrection(
            correctionId = correctionId,
            correctionLinkingUpdate = correctionLinkingUpdate.toModel()
        ).toDto()

    override fun getCurrentOverview(paymentAccountId: Long): PaymentAccountAmountSummaryDTO =
        getCurrentOverview.getCurrentOverview(paymentAccountId).toDto()

}

