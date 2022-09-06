package io.cloudflight.jems.server.payments.entity

import io.cloudflight.jems.api.payments.PaymentToProjectDTO
import io.cloudflight.jems.api.payments.PaymentType
import io.cloudflight.jems.server.payments.service.model.ComputedPaymentToProject
import io.cloudflight.jems.server.payments.service.model.PaymentRow
import io.cloudflight.jems.server.payments.service.model.PaymentToProject
import io.cloudflight.jems.server.programme.entity.fund.ProgrammeFundEntity
import io.cloudflight.jems.server.project.entity.ProjectEntity
import io.cloudflight.jems.server.project.entity.lumpsum.ProjectLumpSumEntity
import org.springframework.data.domain.Page
import java.math.BigDecimal

fun PaymentToProject.toDTO() = PaymentToProjectDTO(
    paymentId = paymentId,
    paymentType = paymentType,
    projectId = projectId,
    projectAcronym = projectAcronym,
    paymentClaimNo = paymentClaimNo,
    paymentClaimSubmissionDate = paymentClaimSubmissionDate,
    paymentApprovalDate = paymentApprovalDate,
    totalEligibleAmount = totalEligibleAmount,
    fundName = fundName,
    amountApprovedPerFound = amountApprovedPerFound,
    amountPaidPerFund = amountPaidPerFund,
    dateOfLastPayment = dateOfLastPayment
)

fun Page<PaymentToProjectEntity>.toListModel(
    getLumpSum: (Long, Int) -> ProjectLumpSumEntity,
) = map {
    it.toModel(
        getLumpSum.invoke(it.project.id, it.orderNr)
    )
}

fun PaymentToProjectEntity.toModel(lumpSum: ProjectLumpSumEntity) = PaymentToProject(
    paymentId = id,
    paymentType = PaymentType.FTLS,
    projectId = project.customIdentifier,
    projectAcronym = project.acronym,
    paymentClaimNo = 0,
    paymentClaimSubmissionDate = project.contractedDecision?.updated,
    paymentApprovalDate = lumpSum.paymentEnabledDate,
    totalEligibleAmount = lumpSum.programmeLumpSum.cost,
    fundName = fund.type.name,
    amountApprovedPerFound = amountApprovedPerFund!!,
    amountPaidPerFund = BigDecimal.ZERO,
    dateOfLastPayment = null
)

fun List<PaymentRow>.toListModel() = map { it.toModel() }

fun PaymentRow.toModel() = ComputedPaymentToProject(
    projectId, partnerId, orderNr, programmeLumpSumId, programmeFundId, amountApprovedPerFund
)

fun List<ComputedPaymentToProject>.toEntity(
    project: ProjectEntity,
    getProgrammeFund: (Long) -> ProgrammeFundEntity,
) = this.associateBy { PaymentGrouppingId(it.orderNr, it.partnerId, it.programmeFundId)}.map { (_, row) ->
    PaymentToProjectEntity(
        project = project,
        orderNr = row.orderNr,
        programmeLumpSumId = row.programmeLumpSumId,
        fund = getProgrammeFund.invoke(row.programmeFundId),
        partnerId = row.partnerId,
        amountApprovedPerFund = row.amountApprovedPerFund
    )
}
