package io.cloudflight.jems.server.payments.service.account.finance.reconciliation.getReconciliationOverview

import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import io.cloudflight.jems.api.programme.dto.priority.ProgrammeObjective
import io.cloudflight.jems.api.programme.dto.priority.ProgrammeObjectivePolicy
import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.payments.model.account.PaymentAccount
import io.cloudflight.jems.server.payments.model.account.PaymentAccountStatus
import io.cloudflight.jems.server.payments.model.account.finance.reconciliation.PaymentAccountReconciliation
import io.cloudflight.jems.server.payments.model.account.finance.reconciliation.ReconciledAmountByType
import io.cloudflight.jems.server.payments.model.account.finance.reconciliation.ReconciledAmountPerPriority
import io.cloudflight.jems.server.payments.model.account.finance.reconciliation.ReconciledPriority
import io.cloudflight.jems.server.payments.model.account.finance.reconciliation.ReconciledScenario
import io.cloudflight.jems.server.payments.repository.account.correction.PAYMENT_ACCOUNT_ID
import io.cloudflight.jems.server.payments.service.account.accountingYear
import io.cloudflight.jems.server.payments.service.account.finance.PaymentAccountFinancePersistence
import io.cloudflight.jems.server.payments.service.account.programmeFund
import io.cloudflight.jems.server.payments.service.account.reconciliation.PaymentAccountReconciliationPersistence
import io.cloudflight.jems.server.payments.service.account.submissionToSfcDateUpdated
import io.cloudflight.jems.server.programme.service.priority.ProgrammePriorityPersistence
import io.cloudflight.jems.server.programme.service.priority.model.ProgrammeObjectiveDimension
import io.cloudflight.jems.server.programme.service.priority.model.ProgrammePriority
import io.cloudflight.jems.server.programme.service.priority.model.ProgrammeSpecificObjective
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.math.BigDecimal

class GetReconciliationOverviewTest : UnitTest() {

    companion object {
        private const val PRIORITY_AXIS = "P01"
        private const val PRIORITY_AXIS_2 = "P02"
        private const val PRIORITY_ID = 1L
        private const val PRIORITY_ID_2 = 2L

        val g1 = listOf(
            ReconciledPriority(
                priorityId = PRIORITY_ID,
                priorityCode = PRIORITY_AXIS,
                total = BigDecimal(408),
                ofWhichAa = BigDecimal(422),
                ofWhichEc = BigDecimal(4),
            ),
            ReconciledPriority(
                priorityId = PRIORITY_ID_2,
                priorityCode = PRIORITY_AXIS_2,
                total = BigDecimal(10),
                ofWhichAa = BigDecimal(3),
                ofWhichEc = BigDecimal(4),
            )
        )

        val g2 = listOf(
            ReconciledPriority(
                priorityId = PRIORITY_ID,
                priorityCode = PRIORITY_AXIS,
                total = BigDecimal(10),
                ofWhichAa = BigDecimal(3),
                ofWhichEc = BigDecimal(4),
            ),
            ReconciledPriority(
                priorityId = PRIORITY_ID_2,
                priorityCode = PRIORITY_AXIS_2,
                total = BigDecimal(10),
                ofWhichAa = BigDecimal(3),
                ofWhichEc = BigDecimal(4),
            )
        )

        val g3 = listOf(
            ReconciledPriority(
                priorityId = PRIORITY_ID,
                priorityCode = PRIORITY_AXIS,
                total = BigDecimal(10),
                ofWhichAa = BigDecimal(3),
                ofWhichEc = BigDecimal(4),
            ),
            ReconciledPriority(
                priorityId = PRIORITY_ID_2,
                priorityCode = PRIORITY_AXIS_2,
                total = BigDecimal(10),
                ofWhichAa = BigDecimal(3),
                ofWhichEc = BigDecimal(4),
            )
        )

        val paymentAccount = PaymentAccount(
            id = PAYMENT_ACCOUNT_ID,
            fund = programmeFund,
            accountingYear = accountingYear,
            status = PaymentAccountStatus.DRAFT,
            nationalReference = "national reference",
            technicalAssistance = BigDecimal.TEN,
            submissionToSfcDate = submissionToSfcDateUpdated,
            sfcNumber = "sfc number",
            comment = "comment"
        )

        val paymentReconciliationList = listOf(
            PaymentAccountReconciliation(
                id = 1L,
                paymentAccount = paymentAccount,
                priorityAxisId = PRIORITY_ID,
                totalComment = "Comment Total",
                aaComment = "Comment ofAa",
                ecComment = "Comment ofEc"
            ),
            PaymentAccountReconciliation(
                id = 2L,
                paymentAccount = paymentAccount,
                priorityAxisId = PRIORITY_ID_2,
                totalComment = "Comment Total",
                aaComment = "Comment ofAa",
                ecComment = "Comment ofEc"
            )
        )
        private val totalReconciliation1 = ReconciledAmountByType(
            scenario3Sum = BigDecimal(10),
            scenario4Sum = BigDecimal(408),
            clericalMistakesSum = BigDecimal(10),
            comment = "Comment Total"
        )

        private val reconciliationOfAa1 = ReconciledAmountByType(
            scenario3Sum = BigDecimal(3),
            scenario4Sum = BigDecimal(422),
            clericalMistakesSum = BigDecimal(3),
            comment = "Comment ofAa"
        )

        private val reconciliationOfEc1 = ReconciledAmountByType(
            scenario3Sum = BigDecimal(4),
            scenario4Sum = BigDecimal(4),
            clericalMistakesSum = BigDecimal(4),
            comment = "Comment ofEc"
        )

        private val totalReconciliation2 = ReconciledAmountByType(
            scenario3Sum = BigDecimal(10),
            scenario4Sum = BigDecimal(10),
            clericalMistakesSum = BigDecimal(10),
            comment = "Comment Total"
        )

        private val reconciliationOfAa2 = ReconciledAmountByType(
            scenario3Sum = BigDecimal(3),
            scenario4Sum = BigDecimal(3),
            clericalMistakesSum = BigDecimal(3),
            comment = "Comment ofAa"
        )

        private val reconciliationOfEc2 = ReconciledAmountByType(
            scenario3Sum = BigDecimal(4),
            scenario4Sum = BigDecimal(4),
            clericalMistakesSum = BigDecimal(4),
            comment = "Comment ofEc"
        )

        val expectedReconciledAmounts = listOf(
            ReconciledAmountPerPriority(
                priorityAxis = PRIORITY_AXIS,

                reconciledAmountTotal = totalReconciliation1,
                reconciledAmountOfAa = reconciliationOfAa1,
                reconciledAmountOfEc = reconciliationOfEc1,
            ),
            ReconciledAmountPerPriority(
                priorityAxis = PRIORITY_AXIS_2,

                reconciledAmountTotal = totalReconciliation2,
                reconciledAmountOfAa = reconciliationOfAa2,
                reconciledAmountOfEc = reconciliationOfEc2,
            ),
        )

        val priorityList = listOf(
            ProgrammePriority(
                id = 1L,
                code = "P01",
                title = setOf(InputTranslation(SystemLanguage.EN, "PO-02 title")),
                objective = ProgrammeObjective.PO2,
                specificObjectives = listOf(
                    ProgrammeSpecificObjective(
                        programmeObjectivePolicy = ProgrammeObjectivePolicy.GreenInfrastructure,
                        code = "GU",
                        dimensionCodes = mapOf(ProgrammeObjectiveDimension.EconomicActivity to listOf("001", "002"))
                    ),
                    ProgrammeSpecificObjective(
                        programmeObjectivePolicy = ProgrammeObjectivePolicy.RenewableEnergy,
                        code = "RE",
                        dimensionCodes = mapOf(ProgrammeObjectiveDimension.FormOfSupport to listOf("003", "004"))
                    ),
                ),
            ),
            ProgrammePriority(
                id = 2L,
                code = "P02",
                title = setOf(InputTranslation(SystemLanguage.EN, "PO-02 title")),
                objective = ProgrammeObjective.PO2,
                specificObjectives = listOf(
                    ProgrammeSpecificObjective(
                        programmeObjectivePolicy = ProgrammeObjectivePolicy.GreenInfrastructure,
                        code = "GU",
                        dimensionCodes = mapOf(ProgrammeObjectiveDimension.EconomicActivity to listOf("001", "002"))
                    ),
                    ProgrammeSpecificObjective(
                        programmeObjectivePolicy = ProgrammeObjectivePolicy.RenewableEnergy,
                        code = "RE",
                        dimensionCodes = mapOf(ProgrammeObjectiveDimension.FormOfSupport to listOf("003", "004"))
                    ),
                ),
            ),
        )
    }

    @MockK
    lateinit var reconciliationPersistence: PaymentAccountReconciliationPersistence

    @MockK
    lateinit var paymentAccountFinancePersistence: PaymentAccountFinancePersistence

    @MockK
    lateinit var programmePriorityPersistence: ProgrammePriorityPersistence

    @InjectMockKs
    lateinit var service: GetReconciliationOverview

    @Test
    fun getReconciliationOverview() {
        every {
            paymentAccountFinancePersistence.getReconciliationOverview(
                PAYMENT_ACCOUNT_ID,
                ReconciledScenario.Scenario4
            )
        } returns g1
        every {
            paymentAccountFinancePersistence.getReconciliationOverview(
                PAYMENT_ACCOUNT_ID,
                ReconciledScenario.Scenario3
            )
        } returns g2
        every {
            paymentAccountFinancePersistence.getReconciliationOverview(
                PAYMENT_ACCOUNT_ID,
                ReconciledScenario.ClericalMistakes
            )
        } returns g3
        every { programmePriorityPersistence.getAllMax56Priorities() } returns priorityList
        every { reconciliationPersistence.getByPaymentAccountId(PAYMENT_ACCOUNT_ID) } returns paymentReconciliationList

        assertThat(service.getReconciliationOverview(PAYMENT_ACCOUNT_ID)).isEqualTo(expectedReconciledAmounts)
    }
}
