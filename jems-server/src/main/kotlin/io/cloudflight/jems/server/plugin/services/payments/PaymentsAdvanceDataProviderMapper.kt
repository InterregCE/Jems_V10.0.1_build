package io.cloudflight.jems.server.plugin.services.payments

import io.cloudflight.jems.plugin.contract.models.common.IdNamePairData
import io.cloudflight.jems.plugin.contract.models.payments.advance.AdvancePaymentData
import io.cloudflight.jems.plugin.contract.models.payments.advance.AdvancePaymentDetailData
import io.cloudflight.jems.plugin.contract.models.payments.advance.AdvancePaymentSettlementData
import io.cloudflight.jems.plugin.contract.models.project.sectionB.partners.ProjectPartnerRoleData
import io.cloudflight.jems.server.call.service.model.IdNamePair
import io.cloudflight.jems.server.payments.model.advance.AdvancePayment
import io.cloudflight.jems.server.payments.model.advance.AdvancePaymentDetail
import io.cloudflight.jems.server.payments.model.advance.AdvancePaymentSettlement
import io.cloudflight.jems.server.plugin.services.toDataModel


fun List<AdvancePayment>.toDataModelList() = map { it.toDataModel() }
fun AdvancePayment.toDataModel() = AdvancePaymentData(
    id = id,
    projectCustomIdentifier = projectCustomIdentifier,
    projectAcronym = projectAcronym,
    partnerType = ProjectPartnerRoleData.valueOf(partnerType.name),
    partnerNumber = partnerNumber,
    partnerAbbreviation = partnerAbbreviation,
    programmeFund = programmeFund?.toDataModel(),
    partnerContribution = partnerContribution?.toDataModel(),
    partnerContributionSpf = partnerContributionSpf?.toDataModel(),
    paymentAuthorized = paymentAuthorized,
    amountPaid = amountPaid,
    paymentDate = paymentDate,
    amountSettled = amountSettled
)

fun AdvancePaymentDetail.toDataModel() = AdvancePaymentDetailData(
    id = id,
    projectId = projectId,
    projectCustomIdentifier = projectCustomIdentifier,
    projectAcronym = projectAcronym,
    projectVersion = projectVersion,
    partnerId = partnerId,
    partnerType =ProjectPartnerRoleData.valueOf(partnerType.name),
    partnerNumber = partnerNumber,
    partnerAbbreviation = partnerAbbreviation,
    programmeFund = programmeFund?.toDataModel(),
    partnerContribution = partnerContribution?.toDataModel(),
    partnerContributionSpf = partnerContributionSpf?.toDataModel(),
    amountPaid = amountPaid,
    paymentDate = paymentDate,
    comment = comment,
    paymentAuthorized = paymentAuthorized,
    paymentAuthorizedUser = paymentAuthorizedUser?.toUserSummaryDataModel(),
    paymentAuthorizedDate = paymentAuthorizedDate,
    paymentConfirmed = paymentConfirmed,
    paymentConfirmedUser = paymentConfirmedUser?.toUserSummaryDataModel(),
    paymentConfirmedDate = paymentConfirmedDate,
    paymentSettlements = paymentSettlements.map { it.toDataModel() }
)

fun IdNamePair.toDataModel() = IdNamePairData(
    id = id,
    name = name
)

fun AdvancePaymentSettlement.toDataModel() = AdvancePaymentSettlementData(
    id = id,
    number = number,
    settlementDate = settlementDate,
    amountSettled = amountSettled,
    comment = comment
)