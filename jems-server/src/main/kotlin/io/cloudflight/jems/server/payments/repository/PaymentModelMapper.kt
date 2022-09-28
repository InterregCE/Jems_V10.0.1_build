package io.cloudflight.jems.server.payments.repository

import io.cloudflight.jems.server.payments.entity.PaymentEntity
import io.cloudflight.jems.server.payments.entity.PaymentPartnerEntity
import io.cloudflight.jems.server.payments.service.model.PaymentPartnerToCreate
import io.cloudflight.jems.server.payments.service.model.PaymentPerPartner
import io.cloudflight.jems.server.payments.service.model.PaymentRow
import io.cloudflight.jems.server.payments.service.model.PaymentToCreate
import io.cloudflight.jems.server.payments.service.model.PaymentToProject
import io.cloudflight.jems.server.programme.entity.fund.ProgrammeFundEntity
import io.cloudflight.jems.server.project.entity.ProjectEntity
import io.cloudflight.jems.server.project.entity.lumpsum.ProjectLumpSumEntity
import io.cloudflight.jems.server.project.service.model.ProjectFull
import org.springframework.data.domain.Page
import java.math.BigDecimal

fun Page<PaymentEntity>.toListModel(
    getLumpSum: (Long, Int) -> ProjectLumpSumEntity,
    getProject: (Long, String?) -> ProjectFull
) = map {
    val lumpSum = getLumpSum.invoke(it.project.id, it.orderNr)
    it.toModel(
        lumpSum,
        getProject.invoke(it.project.id, lumpSum.lastApprovedVersionBeforeReadyForPayment)
    )
}

fun PaymentEntity.toModel(lumpSum: ProjectLumpSumEntity, projectFull: ProjectFull) = PaymentToProject(
    id = id,
    paymentType = type,
    projectId = projectFull.customIdentifier,
    projectAcronym = projectFull.acronym,
    paymentClaimNo = 0,
    paymentClaimSubmissionDate = projectFull.contractedDecision?.updated,
    paymentApprovalDate = lumpSum.paymentEnabledDate,
    totalEligibleAmount = lumpSum.programmeLumpSum.cost,
    fundName = fund.type.name,
    amountApprovedPerFund = amountApprovedPerFund!!,
    amountPaidPerFund = BigDecimal.ZERO,
    dateOfLastPayment = null,
    lastApprovedVersionBeforeReadyForPayment = lumpSum.lastApprovedVersionBeforeReadyForPayment
)

fun List<PaymentRow>.toListModel() = map { it.toModel() }
fun PaymentRow.toModel() = PaymentPerPartner(
    projectId, partnerId, orderNr, programmeLumpSumId, programmeFundId, amountApprovedPerPartner
)

fun PaymentToCreate.toEntity(
    projectEntity: ProjectEntity,
    orderNr: Int,
    fundEntity: ProgrammeFundEntity
) = PaymentEntity(
    type = io.cloudflight.jems.server.payments.service.model.PaymentType.FTLS,
    project = projectEntity,
    orderNr = orderNr,
    programmeLumpSumId = programmeLumpSumId,
    fund = fundEntity,
    amountApprovedPerFund = amountApprovedPerFund
)

fun PaymentPartnerToCreate.toEntity(paymentEntity: PaymentEntity) = PaymentPartnerEntity (
    payment = paymentEntity,
    partnerId = partnerId,
    amountApprovedPerPartner = amountApprovedPerPartner
)
