package io.cloudflight.jems.server.plugin.services.payments

import io.cloudflight.jems.plugin.contract.models.common.SystemLanguageData
import io.cloudflight.jems.plugin.contract.models.payments.export.RegularPaymentsExportData
import io.cloudflight.jems.plugin.contract.models.payments.regular.PaymentDetailData
import io.cloudflight.jems.plugin.contract.models.payments.regular.PaymentToProjectData
import io.cloudflight.jems.plugin.contract.models.programme.fund.ProgrammeFundTypeData
import io.cloudflight.jems.plugin.contract.services.payments.PaymentsDataProvider
import io.cloudflight.jems.server.payments.service.regular.PaymentPersistence
import io.cloudflight.jems.server.programme.service.fund.model.ProgrammeFundType
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class PaymentsDataProviderImpl(
    private val paymentPersistence: PaymentPersistence
) : PaymentsDataProvider {
    @Transactional(readOnly = true)
    override fun getAllRegularPaymentIds(programmeFundType: ProgrammeFundTypeData?): List<Long> {
        return paymentPersistence.getAllRegularPaymentIds(
            if (programmeFundType == null) null else ProgrammeFundType.valueOf(programmeFundType.name)
        )
    }

    @Transactional(readOnly = true)
    override fun getPaymentDetail(paymentId: Long): PaymentDetailData =
        paymentPersistence.getPaymentDetails(paymentId).toDataModel()

    @Transactional(readOnly = true)
    override fun getPaymentsForProject(projectId: Long): List<PaymentToProjectData> =
        paymentPersistence.getPaymentsByProjectId(projectId).toDataModelList()

    @Transactional(readOnly = true)
    override fun getRegularPaymentExportData(
        exportLanguage: SystemLanguageData,
        paymentId: Long
    ): RegularPaymentsExportData {
        val payment = paymentPersistence.getRegularPaymentDataForExport(paymentId)
        val paymentDetail = paymentPersistence.getPaymentDetails(paymentId)
        val installments = paymentDetail.partnerPayments.toExportModel(paymentDetail.projectId).flatten()
        payment.installments = installments
        return payment
    }

}