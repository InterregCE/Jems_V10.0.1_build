package io.cloudflight.jems.server.payments.entity

import io.cloudflight.jems.server.payments.service.PaymentType
import io.cloudflight.jems.server.payments.service.model.ComputedPaymentToProject
import io.cloudflight.jems.server.payments.service.model.PaymentRow
import io.cloudflight.jems.server.payments.service.model.PaymentToProject
import io.cloudflight.jems.server.programme.entity.fund.ProgrammeFundEntity
import io.cloudflight.jems.server.project.entity.ProjectEntity
import io.cloudflight.jems.server.project.entity.lumpsum.ProjectLumpSumEntity
import org.springframework.data.domain.Page
import java.math.BigDecimal
import java.time.ZonedDateTime

fun Page<PaymentToProjectEntity>.toListModel() = map { it.toModel() }

fun PaymentToProjectEntity.toModel() = PaymentToProject(
    paymentId = id,
    paymentType = PaymentType.FTLS,
    projectId = project.id,
    projectAcronym = project.acronym,
    paymentClaimNo = 0,
    paymentClaimSubmissionDate = project.contractedDecision?.updated,
    paymentApprovalDate = ZonedDateTime.now(),
    totalEligibleAmount = BigDecimal.ZERO,
    fundName = fund.type.name,
    amountApprovedPerFound = amountApprovedPerFund,
    amountPaidPerFund = BigDecimal.ZERO,
    dateOfLastPayment = null
)

fun List<PaymentRow>.toModel() = map { it.toModel() }
fun PaymentRow.toModel() = ComputedPaymentToProject(
    projectId, partnerId, orderNr, programmeLumpSumId, programmeFundId, amountApprovedPerFund
)

fun List<PaymentRow>.toEntity(project: ProjectEntity,
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
