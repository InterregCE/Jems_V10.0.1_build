package io.cloudflight.jems.server.payments.controller.applicationToEc

import io.cloudflight.jems.api.payments.applicationToEc.PaymentApplicationToEcApi
import io.cloudflight.jems.api.payments.dto.PaymentApplicationToEcCreateDTO
import io.cloudflight.jems.api.payments.dto.PaymentApplicationToEcDTO
import io.cloudflight.jems.api.payments.dto.PaymentApplicationToEcDetailDTO
import io.cloudflight.jems.api.payments.dto.PaymentApplicationToEcSummaryUpdateDTO
import io.cloudflight.jems.api.payments.dto.PaymentEcStatusUpdateDTO
import io.cloudflight.jems.api.payments.dto.applicationToEc.AccountingYearAvailabilityDTO
import io.cloudflight.jems.server.payments.accountingYears.service.toAvailabilityDto
import io.cloudflight.jems.server.payments.service.ecPayment.createPaymentApplicationToEc.CreatePaymentApplicationToEcInteractor
import io.cloudflight.jems.server.payments.service.ecPayment.deletePaymentApplicationToEc.DeletePaymentApplicationToEcInteractor
import io.cloudflight.jems.server.payments.service.ecPayment.finalizePaymentApplicationToEc.FinalizePaymentApplicationToEcInteractor
import io.cloudflight.jems.server.payments.service.ecPayment.getAvailableAccountingYearsForPaymentFund.GetAvailableAccountingYearsForPaymentFundInteractor
import io.cloudflight.jems.server.payments.service.ecPayment.getPaymentApplicationToEcDetail.GetPaymentApplicationToEcDetailInteractor
import io.cloudflight.jems.server.payments.service.ecPayment.getPaymentApplicationToEcList.GetPaymentApplicationToEcListInteractor
import io.cloudflight.jems.server.payments.service.ecPayment.reOpenFinalizedEcPaymentApplication.ReOpenFinalizedEcPaymentApplicationInteractor
import io.cloudflight.jems.server.payments.service.ecPayment.updatePaymentApplicationToEcDetail.UpdatePaymentApplicationToEcDetailInteractor
import io.cloudflight.jems.server.payments.service.toDto
import io.cloudflight.jems.server.payments.service.toModel
import io.cloudflight.jems.server.payments.service.toStatusUpdateDto
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.web.bind.annotation.RestController

@RestController
class PaymentApplicationToEcController(
    private val createPaymentApplicationsToEc: CreatePaymentApplicationToEcInteractor,
    private val updatePaymentApplicationToEc: UpdatePaymentApplicationToEcDetailInteractor,
    private val getPaymentApplicationsToEc: GetPaymentApplicationToEcListInteractor,
    private val deletePaymentApplicationToEc: DeletePaymentApplicationToEcInteractor,
    private val getPaymentApplicationToEcDetail: GetPaymentApplicationToEcDetailInteractor,
    private val finalizePaymentApplicationToEc: FinalizePaymentApplicationToEcInteractor,
    private val reOpenFinalizedEcPaymentApplication: ReOpenFinalizedEcPaymentApplicationInteractor,
    private val getAvailableAccountingYearsForPaymentFund: GetAvailableAccountingYearsForPaymentFundInteractor,
) : PaymentApplicationToEcApi {

    override fun createPaymentApplicationToEc(paymentApplication: PaymentApplicationToEcCreateDTO): PaymentApplicationToEcDetailDTO =
        createPaymentApplicationsToEc.createPaymentApplicationToEc(paymentApplication.toModel()).toDto()

    override fun updatePaymentApplicationToEc(id: Long, paymentApplicationToEcUpdate: PaymentApplicationToEcSummaryUpdateDTO): PaymentApplicationToEcDetailDTO =
        updatePaymentApplicationToEc.updatePaymentApplicationToEc(id, paymentApplicationToEcUpdate.toModel())
            .toDto()

    override fun getPaymentApplicationToEcDetail(id: Long): PaymentApplicationToEcDetailDTO {
        return getPaymentApplicationToEcDetail.getPaymentApplicationToEcDetail(id).toDto()
    }

    override fun getPaymentApplicationsToEc(pageable: Pageable): Page<PaymentApplicationToEcDTO> =
        getPaymentApplicationsToEc.getPaymentApplicationsToEc(pageable).toDto()


    override fun deletePaymentApplicationToEc(id: Long) {
        deletePaymentApplicationToEc.deleteById(id)
    }

    override fun finalizePaymentApplicationToEc(paymentId: Long): PaymentEcStatusUpdateDTO =
        finalizePaymentApplicationToEc.finalizePaymentApplicationToEc(paymentId).toStatusUpdateDto()

    override fun reOpenFinalizedEcPaymentApplication(paymentId: Long): PaymentEcStatusUpdateDTO =
        reOpenFinalizedEcPaymentApplication.reOpen(paymentId).toStatusUpdateDto()

    override fun getAvailableAccountingYearsForPaymentFund(programmeFundId: Long): List<AccountingYearAvailabilityDTO> =
        getAvailableAccountingYearsForPaymentFund.getAvailableAccountingYearsForPaymentFund(programmeFundId).toAvailabilityDto()

}
