package io.cloudflight.jems.server.project.repository.auditAndControl.correction.measure

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.payments.accountingYears.repository.toModel
import io.cloudflight.jems.server.payments.entity.AccountingYearEntity
import io.cloudflight.jems.server.payments.entity.PaymentApplicationToEcEntity
import io.cloudflight.jems.server.payments.entity.PaymentToEcCorrectionExtensionEntity
import io.cloudflight.jems.server.payments.model.regular.PaymentEcStatus
import io.cloudflight.jems.server.payments.model.regular.PaymentSearchRequestScoBasis
import io.cloudflight.jems.server.payments.repository.account.correction.PaymentAccountCorrectionExtensionRepository
import io.cloudflight.jems.server.payments.repository.applicationToEc.linkToCorrection.EcPaymentCorrectionExtensionRepository
import io.cloudflight.jems.server.programme.entity.fund.ProgrammeFundEntity
import io.cloudflight.jems.server.project.entity.auditAndControl.AuditControlCorrectionEntity
import io.cloudflight.jems.server.project.entity.auditAndControl.AuditControlCorrectionMeasureEntity
import io.cloudflight.jems.server.project.service.auditAndControl.correction.model.ProjectCorrectionProgrammeMeasure
import io.cloudflight.jems.server.project.service.auditAndControl.correction.model.ProjectCorrectionProgrammeMeasureScenario
import io.cloudflight.jems.server.project.service.auditAndControl.correction.model.ProjectCorrectionProgrammeMeasureUpdate
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.LocalDate

class AuditControlCorrectionMeasurePersistenceProviderTest : UnitTest() {

    companion object {
        private const val CORRECTION_ID = 176L
        private const val paymentApplicationsToEcId = 1L
        private const val accountingYearId = 3L
        private const val programmeFundId = 10L
        private val submissionDate = LocalDate.now()

        private val programmeFundEntity = ProgrammeFundEntity(programmeFundId, true)
        private val accountingYearEntity =
            AccountingYearEntity(accountingYearId, 2021, LocalDate.of(2021, 1, 1), LocalDate.of(2022, 6, 30))

        private val programmeMeasureEntity = AuditControlCorrectionMeasureEntity(
            correctionId = CORRECTION_ID,
            correction = mockk(),
            scenario = ProjectCorrectionProgrammeMeasureScenario.SCENARIO_5,
            comment = "comment"
        )

        private val programmeMeasureModel = ProjectCorrectionProgrammeMeasure(
            correctionId = CORRECTION_ID,
            scenario = ProjectCorrectionProgrammeMeasureScenario.SCENARIO_5,
            comment = "comment",
            includedInAccountingYear = accountingYearEntity.toModel(),
        )

        private val paymentApplicationToEcEntity = PaymentApplicationToEcEntity(
            id = paymentApplicationsToEcId,
            programmeFund = programmeFundEntity,
            accountingYear = accountingYearEntity,
            status = PaymentEcStatus.Draft,
            nationalReference = "National Reference",
            technicalAssistanceEur = BigDecimal.valueOf(105.32),
            submissionToSfcDate = submissionDate,
            sfcNumber = "SFC number",
            comment = "Comment"
        )

        private fun correctionEntity(): AuditControlCorrectionEntity {
            val entity = mockk<AuditControlCorrectionEntity>()
            every { entity.id } returns CORRECTION_ID
            return entity
        }

        private val correctionExtensionEntity = PaymentToEcCorrectionExtensionEntity(
            correctionId = CORRECTION_ID,
            correction = correctionEntity(),
            paymentApplicationToEc = paymentApplicationToEcEntity,
            fundAmount = BigDecimal(100),
            publicContribution = BigDecimal(200),
            correctedPublicContribution = BigDecimal(300),
            autoPublicContribution = BigDecimal(400),
            correctedAutoPublicContribution = BigDecimal(500),
            privateContribution = BigDecimal(600),
            correctedPrivateContribution = BigDecimal(700),
            comment = "Comment",
            finalScoBasis = PaymentSearchRequestScoBasis.FallsUnderArticle94Or95,
            correctedFundAmount = BigDecimal(107),
            unionContribution = BigDecimal(0),
            correctedTotalEligibleWithoutArt94or95 = BigDecimal(108),
            correctedUnionContribution = BigDecimal(55),
            totalEligibleWithoutArt94or95 = BigDecimal(800),
        )

    }

    @MockK
    lateinit var programmeMeasureRepository: CorrectionProgrammeMeasureRepository
    @MockK
    lateinit var ecPaymentCorrectionExtensionRepository: EcPaymentCorrectionExtensionRepository
    @MockK
    lateinit var paymentAccountCorrectionExtensionRepository: PaymentAccountCorrectionExtensionRepository

    @InjectMockKs
    lateinit var persistenceProvider: AuditControlCorrectionMeasurePersistenceProvider

    @BeforeEach
    fun setup() {
        clearMocks(programmeMeasureRepository)
    }

    @Test
    fun getProgrammeMeasure() {
        every { programmeMeasureRepository.getByCorrectionId(CORRECTION_ID) } returns programmeMeasureEntity
        every { ecPaymentCorrectionExtensionRepository.getAccountingYearByCorrectionId(CORRECTION_ID) } returns accountingYearEntity
        every { paymentAccountCorrectionExtensionRepository.getAccountingYearByCorrectionId(CORRECTION_ID) } returns null

        assertThat(persistenceProvider.getProgrammeMeasure(CORRECTION_ID)).isEqualTo(programmeMeasureModel)
    }

    @Test
    fun updateProgrammeMeasure() {
        val programmeMeasureUpdate = ProjectCorrectionProgrammeMeasureUpdate(
            ProjectCorrectionProgrammeMeasureScenario.SCENARIO_3,
            "newComment"
        )

        every { programmeMeasureRepository.getByCorrectionId(CORRECTION_ID) } returns programmeMeasureEntity
        every { ecPaymentCorrectionExtensionRepository.getAccountingYearByCorrectionId(CORRECTION_ID) } returns null
        every { paymentAccountCorrectionExtensionRepository.getAccountingYearByCorrectionId(CORRECTION_ID) } returns accountingYearEntity

        assertThat(persistenceProvider.updateProgrammeMeasure(CORRECTION_ID, programmeMeasureUpdate)).isEqualTo(
            programmeMeasureModel.copy(scenario = ProjectCorrectionProgrammeMeasureScenario.SCENARIO_3, comment = "newComment")
        )
    }
}
