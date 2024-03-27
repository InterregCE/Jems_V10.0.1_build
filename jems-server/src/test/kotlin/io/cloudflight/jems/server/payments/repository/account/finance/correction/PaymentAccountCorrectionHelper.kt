package io.cloudflight.jems.server.payments.repository.account.correction

import io.cloudflight.jems.api.programme.dto.priority.ProgrammeObjectivePolicy
import io.cloudflight.jems.server.payments.accountingYears.repository.toEntity
import io.cloudflight.jems.server.payments.entity.account.PaymentAccountCorrectionExtensionEntity
import io.cloudflight.jems.server.payments.entity.account.PaymentAccountEntity
import io.cloudflight.jems.server.payments.entity.account.PaymentAccountReconciliationEntity
import io.cloudflight.jems.server.payments.model.account.PaymentAccount
import io.cloudflight.jems.server.payments.model.account.PaymentAccountStatus
import io.cloudflight.jems.server.payments.model.account.finance.reconciliation.PaymentAccountReconciliation
import io.cloudflight.jems.server.payments.model.account.finance.reconciliation.PaymentAccountReconciliationType
import io.cloudflight.jems.server.payments.model.account.finance.reconciliation.ReconciledAmountUpdate
import io.cloudflight.jems.server.payments.service.account.accountingYear
import io.cloudflight.jems.server.payments.service.account.paymentAccountEntity
import io.cloudflight.jems.server.payments.service.account.programmeFund
import io.cloudflight.jems.server.payments.service.account.submissionToSfcDate
import io.cloudflight.jems.server.programme.entity.ProgrammePriorityEntity
import io.cloudflight.jems.server.programme.entity.fund.ProgrammeFundEntity
import io.cloudflight.jems.server.project.service.auditAndControl.model.AuditControlStatus
import io.mockk.every
import io.mockk.mockk
import java.math.BigDecimal


const val PAYMENT_ACCOUNT_ID = 13L
const val CORRECTION_ID = 105L
const val PRIORITY_AXIS_ID = 2L
const val PRIORITY_AXIS_ID_2 = 3L

val paymentAccountModel = PaymentAccount(
    id = PAYMENT_ACCOUNT_ID,
    fund = programmeFund,
    accountingYear = accountingYear,
    status = PaymentAccountStatus.DRAFT,
    nationalReference = "national reference",
    technicalAssistance = BigDecimal.ONE,
    submissionToSfcDate = submissionToSfcDate,
    sfcNumber = "sfc number",
    comment = "comment"
)

val programmeFundEntity = ProgrammeFundEntity(id = 1, selected = true)
val accountingYearEntity = accountingYear.toEntity()

val paymentAccount = mockk<PaymentAccountEntity> {
    every { id } returns PAYMENT_ACCOUNT_ID
    every { status } returns PaymentAccountStatus.DRAFT
    every { programmeFund } returns programmeFundEntity
    every { accountingYear } returns accountingYearEntity
}

val programmePriority1 = ProgrammePriorityEntity(
    id = PRIORITY_AXIS_ID,
    code = "P01",
    objective = ProgrammeObjectivePolicy.EnergyEfficiency.objective
)
val programmePriority2 = ProgrammePriorityEntity(
    id = PRIORITY_AXIS_ID_2,
    code = "P02",
    objective = ProgrammeObjectivePolicy.EnergyEfficiency.objective
)

fun correctionExtensionEntity(paymentAccount: PaymentAccountEntity?) = PaymentAccountCorrectionExtensionEntity(
    correctionId = CORRECTION_ID,
    correction = mockk {
        every { id } returns CORRECTION_ID
        every { auditControl.status } returns AuditControlStatus.Closed
        every { auditControl.project.priorityPolicy?.programmePriority } returns programmePriority1
    },
    paymentAccount = paymentAccount,
    comment = "comm",
    fundAmount = BigDecimal.valueOf(100),
    publicContribution = BigDecimal.valueOf(50),
    correctedPublicContribution = BigDecimal.valueOf(50),
    autoPublicContribution = BigDecimal.valueOf(20),
    correctedAutoPublicContribution = BigDecimal.valueOf(20),
    privateContribution = BigDecimal.valueOf(20),
    correctedPrivateContribution = BigDecimal.valueOf(20),
)

fun paymentAccountReconciliation() = PaymentAccountReconciliationEntity(
    id = 1L,
    paymentAccount = paymentAccountEntity(),
    priorityAxis = programmePriority1,
    totalComment = "Comment Total",
    aaComment = "Comment ofAa",
    ecComment = "Comment ofEc",
)

val newAccountReconciliation = PaymentAccountReconciliationEntity(
    id = 1L,
    paymentAccount = paymentAccountEntity(),
    priorityAxis = programmePriority1,
    totalComment = "Updated comment",
    aaComment = "",
    ecComment = "",
)

fun reconciliationEntityList() = listOf(
    PaymentAccountReconciliationEntity(
        id = 1L,
        paymentAccount = paymentAccountEntity(),
        priorityAxis = programmePriority1,
        totalComment = "Comment Total",
        aaComment = "Comment ofAa",
        ecComment = "Comment ofEc",
    ),
    PaymentAccountReconciliationEntity(
        id = 2L,
        paymentAccount = paymentAccountEntity(),
        priorityAxis = programmePriority2,
        totalComment = "Comment Total2",
        aaComment = "Comment ofAa2",
        ecComment = "Comment ofEc2",
    )
)

val paymentReconciliationList = listOf(
    PaymentAccountReconciliation(
        id = 1L,
        paymentAccount = paymentAccountModel,
        priorityAxisId = PRIORITY_AXIS_ID,
        totalComment = "Comment Total",
        aaComment = "Comment ofAa",
        ecComment = "Comment ofEc",
    ),
    PaymentAccountReconciliation(
        id = 2L,
        paymentAccount = paymentAccountModel,
        priorityAxisId = PRIORITY_AXIS_ID_2,
        totalComment = "Comment Total2",
        aaComment = "Comment ofAa2",
        ecComment = "Comment ofEc2",
    )
)

val reconciliationUpdate = ReconciledAmountUpdate(
    priorityAxisId = PRIORITY_AXIS_ID,
    type = PaymentAccountReconciliationType.Total,
    comment = "Updated comment"
)

fun expectedAccountReconciliationUpdate(id: Long) = PaymentAccountReconciliation(
    id = id,
    paymentAccount = paymentAccountModel,
    priorityAxisId = PRIORITY_AXIS_ID_2,
    totalComment = "Updated comment",
    aaComment = "",
    ecComment = "",
)
