package io.cloudflight.jems.server.payments.entity

import io.cloudflight.jems.api.payments.PaymentToProjectDTO
import io.cloudflight.jems.api.payments.PaymentType
import io.cloudflight.jems.server.payments.service.model.*
import io.cloudflight.jems.server.programme.entity.fund.ProgrammeFundEntity
import io.cloudflight.jems.server.project.entity.ProjectEntity
import io.cloudflight.jems.server.project.entity.lumpsum.ProjectLumpSumEntity
import org.springframework.data.domain.Page
import java.math.BigDecimal
import java.time.ZonedDateTime


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

fun Page<PaymentToProjectEntity>.toListModel() = map { it.toModel() }

fun PaymentToProjectEntity.toModel() = PaymentToProject(
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

fun List<ComputedPaymentToProject>.toEntity(project: ProjectEntity,
                              projectLumpSums: List<ProjectLumpSumEntity>,
                              getProgrammeFund: (Long) -> ProgrammeFundEntity,
) = this.groupBy { PaymentGrouppingId(it.orderNr, it.partnerId, it.programmeFundId)}.map { groupedRows ->
    PaymentToProjectEntity(
        project = project,
        lumpSum =  projectLumpSums.first {
            groupedRows.value.first().programmeLumpSumId == it.programmeLumpSum.id && groupedRows.value.first().orderNr == it.id.orderNr
        },
        orderNr = projectLumpSums.first {
            groupedRows.value.first().programmeLumpSumId == it.programmeLumpSum.id && groupedRows.value.first().orderNr == it.id.orderNr
        }.id.orderNr,
        programmeLumpSumId = projectLumpSums.first {
            groupedRows.value.first().programmeLumpSumId == it.programmeLumpSum.id && groupedRows.value.first().orderNr == it.id.orderNr
        }.programmeLumpSum.id,
        fund = getProgrammeFund.invoke(groupedRows.value.first().programmeFundId),
        programmeFundId = groupedRows.value.first().programmeFundId,
        partnerId = groupedRows.value.first().partnerId,
        amountApprovedPerFund = groupedRows.value.first().amountApprovedPerFund
    )
}
