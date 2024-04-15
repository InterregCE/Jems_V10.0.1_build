package io.cloudflight.jems.server.plugin.services.payments

import io.cloudflight.jems.plugin.contract.models.payments.advance.AdvancePaymentData
import io.cloudflight.jems.plugin.contract.models.payments.advance.AdvancePaymentDetailData
import io.cloudflight.jems.plugin.contract.models.payments.export.AdvancedPaymentExportData
import io.cloudflight.jems.plugin.contract.models.programme.fund.ProgrammeFundTypeData
import io.cloudflight.jems.plugin.contract.services.payments.PaymentsAdvanceDataProvider
import io.cloudflight.jems.server.payments.service.advance.PaymentAdvancePersistence
import io.cloudflight.jems.server.plugin.services.toDataModel
import io.cloudflight.jems.server.programme.service.fund.model.ProgrammeFundType
import io.cloudflight.jems.server.programme.service.priority.ProgrammePriorityPersistence
import io.cloudflight.jems.server.project.service.ProjectPersistence
import io.cloudflight.jems.server.project.service.partner.PartnerPersistence
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerAddressType
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class PaymentsAdvanceDataProviderImpl(
    private val paymentPersistence: PaymentAdvancePersistence,
    private val projectPersistence: ProjectPersistence,
    private val partnerPersistence: PartnerPersistence,
    private val pogrammePriorityPersistence: ProgrammePriorityPersistence
) : PaymentsAdvanceDataProvider {

    @Transactional(readOnly = true)
    override fun getAdvancePaymentsForProject(projectId: Long): List<AdvancePaymentData> =
        paymentPersistence.getPaymentsByProjectId(projectId).toDataModelList()

    @Transactional(readOnly = true)
    override fun getPaymentDetail(paymentId: Long): AdvancePaymentDetailData =
        paymentPersistence.getPaymentDetail(paymentId).toDataModel()

    @Transactional(readOnly = true)
    override fun getAdvancedPaymentExportData(
        paymentId: Long
    ): AdvancedPaymentExportData {
        val payment = paymentPersistence.getAdvancedPaymentDataForExport(paymentId)
        val project = projectPersistence.getProject(payment.projectId, payment.projectVersion)
        val priorityId = if (project.programmePriority != null)
            pogrammePriorityPersistence.getPriorityIdByCode(project.programmePriority.code)
        else
            null
        val specificObjective = if (project.specificObjective != null)
            pogrammePriorityPersistence.getSpecificObjectivesByCodes(listOf(project.specificObjective.code))
                .firstOrNull()
        else
            null
        payment.priorityAxis = if (project.programmePriority != null)
            priorityId.toString() + " - " + project.programmePriority.code
        else
            null
        payment.specificObjective = specificObjective?.toDataModel()
        payment.callId = project.callSettings.callId
        val partnerCountry = partnerPersistence.getById(
            payment.partnerId,
            payment.projectVersion
        ).addresses.firstOrNull { it.type == ProjectPartnerAddressType.Organization }?.country
        payment.partnerCountry = partnerCountry
        val settlements = paymentPersistence.getPaymentDetail(paymentId).paymentSettlements
        payment.paymentSettlements = settlements.map { it.toDataModel() }
        return payment
    }

    override fun getAllAdvancedPaymentIds(programmeFundType: ProgrammeFundTypeData?): List<Long> {
        return paymentPersistence.getAllAdvancedPaymentIds(
            if (programmeFundType == null) null else ProgrammeFundType.valueOf(programmeFundType.name)
        )
    }
}