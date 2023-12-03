package io.cloudflight.jems.server.payments.repository.applicationToEc.linkToCorrection

import com.querydsl.core.Tuple
import com.querydsl.core.types.EntityPath
import com.querydsl.core.types.Expression
import com.querydsl.core.types.Predicate
import com.querydsl.core.types.dsl.BooleanOperation
import com.querydsl.jpa.impl.JPAQuery
import com.querydsl.jpa.impl.JPAQueryFactory
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.call.createTestCallEntity
import io.cloudflight.jems.server.payments.entity.AccountingYearEntity
import io.cloudflight.jems.server.payments.entity.PaymentApplicationToEcEntity
import io.cloudflight.jems.server.payments.entity.PaymentToEcCorrectionExtensionEntity
import io.cloudflight.jems.server.payments.model.ec.CorrectionInEcPaymentMetadata
import io.cloudflight.jems.server.payments.model.ec.EcPaymentCorrectionExtension
import io.cloudflight.jems.server.payments.model.ec.PaymentToEcCorrectionLinkingUpdate
import io.cloudflight.jems.server.payments.model.regular.PaymentEcStatus
import io.cloudflight.jems.server.payments.model.regular.PaymentSearchRequestScoBasis
import io.cloudflight.jems.server.payments.repository.applicationToEc.PaymentApplicationsToEcRepository
import io.cloudflight.jems.server.programme.entity.fund.ProgrammeFundEntity
import io.cloudflight.jems.server.project.entity.ProjectEntity
import io.cloudflight.jems.server.project.entity.ProjectStatusHistoryEntity
import io.cloudflight.jems.server.project.entity.auditAndControl.AuditControlCorrectionEntity
import io.cloudflight.jems.server.project.entity.auditAndControl.AuditControlEntity
import io.cloudflight.jems.server.project.repository.auditAndControl.correction.AuditControlCorrectionRepository
import io.cloudflight.jems.server.project.service.application.ApplicationStatus
import io.cloudflight.jems.server.project.service.auditAndControl.model.AuditControlStatus
import io.cloudflight.jems.server.project.service.auditAndControl.model.AuditControlType
import io.cloudflight.jems.server.project.service.auditAndControl.model.ControllingBody
import io.cloudflight.jems.server.project.service.auditAndControl.model.CorrectionType
import io.cloudflight.jems.server.project.service.auditAndControl.model.ProjectCorrectionFinancialDescription
import io.cloudflight.jems.server.project.service.auditAndControl.model.correction.AuditControlCorrectionType
import io.cloudflight.jems.server.project.service.auditAndControl.model.correction.CorrectionFollowUpType
import io.cloudflight.jems.server.project.service.auditAndControl.model.correction.impact.CorrectionImpactAction
import io.cloudflight.jems.server.project.service.contracting.model.ContractingMonitoringExtendedOption
import io.cloudflight.jems.server.user.entity.UserEntity
import io.cloudflight.jems.server.user.entity.UserRoleEntity
import io.cloudflight.jems.server.user.service.model.UserStatus
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.slot
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.LocalDate
import java.time.ZonedDateTime

class EcPaymentCorrectionLinkPersistenceProviderTest : UnitTest() {

    @MockK
    private lateinit var ecPaymentRepository: PaymentApplicationsToEcRepository

    @MockK
    private lateinit var ecPaymentCorrectionExtensionRepository: EcPaymentCorrectionExtensionRepository

    @MockK
    private lateinit var auditControlCorrectionRepository: AuditControlCorrectionRepository

    @MockK
    private lateinit var jpaQueryFactory: JPAQueryFactory

    @InjectMockKs
    private lateinit var persistenceProvider: EcPaymentCorrectionLinkPersistenceProvider

    companion object {
        private const val EC_PAYMENT_ID = 1L
        private const val ACCOUNTING_YEAR_ID = 3L
        private const val PROGRAMME_FUND_ID = 10L
        private const val PROJECT_ID = 15L
        private const val CORRECTION_ID = 15L
        private const val AUDIT_CONTROL_ID = 20L
        private const val PROCUREMENT_ID = 21L
        private val submissionDate = LocalDate.now()
        private val date = ZonedDateTime.now()

        private val programmeFundEntity = ProgrammeFundEntity(PROGRAMME_FUND_ID, true)

        private val accountingYearEntity =
            AccountingYearEntity(ACCOUNTING_YEAR_ID, 2021, LocalDate.of(2021, 1, 1), LocalDate.of(2022, 6, 30))

        private val account = UserEntity(
            id = 1,
            email = "admin@admin.dev",
            sendNotificationsToEmail = false,
            name = "Name",
            surname = "Surname",
            userRole = UserRoleEntity(id = 1, name = "ADMIN"),
            password = "hash_pass",
            userStatus = UserStatus.ACTIVE
        )

        private val dummyProject = ProjectEntity(
            id = 1,
            call = createTestCallEntity(0, name = "Test Call"),
            acronym = "Test Project",
            applicant = account,
            currentStatus = ProjectStatusHistoryEntity(id = 1, status = ApplicationStatus.DRAFT, user = account),
        )

        private val paymentApplicationToEcEntity = PaymentApplicationToEcEntity(
            id = EC_PAYMENT_ID,
            programmeFund = programmeFundEntity,
            accountingYear = accountingYearEntity,
            status = PaymentEcStatus.Draft,
            nationalReference = "National Reference",
            technicalAssistanceEur = BigDecimal.valueOf(105.32),
            submissionToSfcDate = submissionDate,
            sfcNumber = "SFC number",
            comment = "Comment"
        )
        private val projectAuditControlEntity = AuditControlEntity(
            id = AUDIT_CONTROL_ID,
            number = 20,
            project = dummyProject,
            status = AuditControlStatus.Ongoing,
            controllingBody = ControllingBody.OLAF,
            controlType = AuditControlType.Administrative,
            startDate = date.minusDays(1),
            endDate = date.plusDays(1),
            finalReportDate = date.minusDays(5),
            totalControlledAmount = BigDecimal.valueOf(10000),
            comment = null
        )

        private val correctionEntity = AuditControlCorrectionEntity(
            id = CORRECTION_ID,
            auditControl = projectAuditControlEntity,
            orderNr = 10,
            status = AuditControlStatus.Ongoing,
            correctionType = AuditControlCorrectionType.LinkedToInvoice,
            followUpOfCorrection = null,
            followUpOfCorrectionType = CorrectionFollowUpType.No,
            repaymentDate = submissionDate,
            lateRepayment = submissionDate.plusDays(1),
            partnerReport = null,
            programmeFund = null,
            impact = CorrectionImpactAction.AdjustmentInNextPayment,
            impactComment = "Impact comment",
            expenditure = null,
            costCategory = null,
            procurementId = PROCUREMENT_ID,
            projectModificationId = null
        )

        private fun paymentToEcExtensionEntity(paymentApplicationToEcEntity: PaymentApplicationToEcEntity?) =
            PaymentToEcCorrectionExtensionEntity(
                correctionId = CORRECTION_ID,
                correction = correctionEntity,
                fundAmount = BigDecimal.valueOf(25.80),
                publicContribution = BigDecimal.valueOf(35.00),
                correctedPublicContribution = BigDecimal.valueOf(36.20),
                autoPublicContribution = BigDecimal.valueOf(15.00),
                correctedAutoPublicContribution = BigDecimal.valueOf(16.00),
                privateContribution = BigDecimal.valueOf(45.00),
                correctedPrivateContribution = BigDecimal.valueOf(46.20),
                paymentApplicationToEc = paymentApplicationToEcEntity,
                finalScoBasis = null,
                comment = "Comment"
            )

        private val paymentToEcExtensionModel = EcPaymentCorrectionExtension(
            correctionId = CORRECTION_ID,
            ecPaymentId = EC_PAYMENT_ID,
            ecPaymentStatus = PaymentEcStatus.Draft,
            auditControlStatus = AuditControlStatus.Ongoing,
            comment = "Comment",
            fundAmount = BigDecimal.valueOf(25.80),
            publicContribution = BigDecimal.valueOf(35.00),
            correctedPublicContribution = BigDecimal.valueOf(36.20),
            autoPublicContribution = BigDecimal.valueOf(15.00),
            correctedAutoPublicContribution = BigDecimal.valueOf(16.00),
            privateContribution = BigDecimal.valueOf(45.00),
            correctedPrivateContribution = BigDecimal.valueOf(46.20),
        )

        private val correctionUpdate = PaymentToEcCorrectionLinkingUpdate(
            correctedPrivateContribution = BigDecimal(205),
            correctedPublicContribution = BigDecimal(206),
            correctedAutoPublicContribution = BigDecimal(207),
            comment = "Updated comment"
        )

        private fun financialDescription(isDeduction: Boolean) = ProjectCorrectionFinancialDescription(
            correctionId = CORRECTION_ID,
            deduction = isDeduction,
            fundAmount = if (isDeduction) BigDecimal.valueOf(-10L) else BigDecimal.valueOf(10L),
            publicContribution = if (isDeduction) BigDecimal.valueOf(-11L) else BigDecimal.valueOf(11L),
            autoPublicContribution = if (isDeduction) BigDecimal.valueOf(-12L) else BigDecimal.valueOf(12L),
            privateContribution = if (isDeduction) BigDecimal.valueOf(-13L) else BigDecimal.valueOf(13L),
            infoSentBeneficiaryDate = null,
            infoSentBeneficiaryComment = "test",
            correctionType = CorrectionType.Ref7Dot5,
            clericalTechnicalMistake = false,
            goldPlating = false,
            suspectedFraud = true,
            correctionComment = "comment",
        )

    }

    @Test
    fun getPaymentExtension() {
        every { ecPaymentCorrectionExtensionRepository.getById(99L) } returns paymentToEcExtensionEntity(
            paymentApplicationToEcEntity
        )
        assertThat(persistenceProvider.getCorrectionExtension(99L)).isEqualTo(paymentToEcExtensionModel)
    }

    @Test
    fun getCorrectionLinkedToEcPayment() {
        val query = mockk<JPAQuery<Tuple>>()
        every { jpaQueryFactory.select(any(), any(), any(), any(), any(), any(), any()) } returns query
        every { query.from(any()) } returns query
        every { query.leftJoin(any<EntityPath<Any>>()) } returns query
        every { query.on(any()) } returns query
        val slotWhere = slot<Predicate>()
        every { query.where(capture(slotWhere)) } returns query

        val tuple = mockk<Tuple>()
        every { tuple.get(0, Long::class.java) } returns CORRECTION_ID
        every { tuple.get(1, Int::class.java) } returns 1
        every { tuple.get(2, Int::class.java) } returns 1
        every { tuple.get(3, Long::class.java) } returns PROJECT_ID
        every {
            tuple.get(
                4,
                PaymentSearchRequestScoBasis::class.java
            )
        } returns PaymentSearchRequestScoBasis.DoesNotFallUnderArticle94Nor95
        every {
            tuple.get(
                5,
                ContractingMonitoringExtendedOption::class.java
            )
        } returns ContractingMonitoringExtendedOption.No
        every {
            tuple.get(
                6,
                ContractingMonitoringExtendedOption::class.java
            )
        } returns ContractingMonitoringExtendedOption.No

        val result = mockk<List<Tuple>>()
        every { result.size } returns 1
        every { query.fetch() } returns listOf(tuple)

        assertThat(persistenceProvider.getCorrectionsLinkedToEcPayment(EC_PAYMENT_ID)).isEqualTo(
            mapOf(
                CORRECTION_ID to CorrectionInEcPaymentMetadata(
                    correctionId = CORRECTION_ID,
                    auditControlNr = 1,
                    correctionNr = 1,
                    projectId = PROJECT_ID,
                    finalScoBasis = PaymentSearchRequestScoBasis.DoesNotFallUnderArticle94Nor95,
                    typologyProv94 = ContractingMonitoringExtendedOption.No,
                    typologyProv95 = ContractingMonitoringExtendedOption.No
                )
            )
        )
    }

    @Test
    fun selectCorrectionToEcPayment() {
        val entity = paymentToEcExtensionEntity(null)
        every { ecPaymentCorrectionExtensionRepository.findAllById(setOf(CORRECTION_ID)) } returns listOf(entity)
        every { ecPaymentRepository.getById(EC_PAYMENT_ID) } returns paymentApplicationToEcEntity

        persistenceProvider.selectCorrectionToEcPayment(
            correctionIds = setOf(CORRECTION_ID),
            ecPaymentId = EC_PAYMENT_ID
        )
        assertThat(entity.paymentApplicationToEc).isEqualTo(paymentApplicationToEcEntity)
    }

    @Test
    fun deselectCorrectionFromEcPaymentAndResetFields() {
        val entity = paymentToEcExtensionEntity(paymentApplicationToEcEntity)
        every { ecPaymentCorrectionExtensionRepository.findAllById(setOf(CORRECTION_ID)) } returns listOf(entity)

        persistenceProvider.deselectCorrectionFromEcPaymentAndResetFields(setOf(CORRECTION_ID))
        assertThat(entity.paymentApplicationToEc).isEqualTo(null)
        assertThat(entity.correctedPublicContribution).isEqualTo(BigDecimal.valueOf(35.00))
        assertThat(entity.correctedAutoPublicContribution).isEqualTo(BigDecimal.valueOf(15.00))
        assertThat(entity.correctedPrivateContribution).isEqualTo(BigDecimal.valueOf(45.00))
    }

    @Test
    fun updateCorrectionLinkedToEcPaymentCorrectedAmounts() {
        val entity = paymentToEcExtensionEntity(paymentApplicationToEcEntity)
        every { ecPaymentCorrectionExtensionRepository.getById(CORRECTION_ID) } returns entity

        persistenceProvider.updateCorrectionLinkedToEcPaymentCorrectedAmounts(CORRECTION_ID, correctionUpdate)
        assertThat(entity.correctedPublicContribution).isEqualTo(BigDecimal.valueOf(206))
        assertThat(entity.correctedAutoPublicContribution).isEqualTo(BigDecimal.valueOf(207))
        assertThat(entity.correctedPrivateContribution).isEqualTo(BigDecimal.valueOf(205))
    }

    @Test
    fun updatePaymentToEcFinalScoBasis() {
        val entity = paymentToEcExtensionEntity(paymentApplicationToEcEntity)
        every { ecPaymentCorrectionExtensionRepository.findAllById(setOf(CORRECTION_ID)) } returns listOf(entity)
        persistenceProvider.updatePaymentToEcFinalScoBasis(mapOf(CORRECTION_ID to PaymentSearchRequestScoBasis.DoesNotFallUnderArticle94Nor95))
        assertThat(entity.finalScoBasis).isEqualTo(PaymentSearchRequestScoBasis.DoesNotFallUnderArticle94Nor95)
    }

    @Test
    fun `createCorrectionExtension - negative sign`() {
        every { auditControlCorrectionRepository.getById(CORRECTION_ID) } returns correctionEntity

        val extensionSlot = slot<PaymentToEcCorrectionExtensionEntity>()
        every { ecPaymentCorrectionExtensionRepository.save(capture(extensionSlot)) } returnsArgument 0

        persistenceProvider.createCorrectionExtension(financialDescription(true))

        assertThat(extensionSlot.captured.publicContribution).isEqualTo(BigDecimal.valueOf(-11))
        assertThat(extensionSlot.captured.correctedPublicContribution).isEqualTo(BigDecimal.valueOf(-11))
        assertThat(extensionSlot.captured.autoPublicContribution).isEqualTo(BigDecimal.valueOf(-12))
        assertThat(extensionSlot.captured.correctedAutoPublicContribution).isEqualTo(BigDecimal.valueOf(-12))
        assertThat(extensionSlot.captured.privateContribution).isEqualTo(BigDecimal.valueOf(-13))
        assertThat(extensionSlot.captured.correctedPrivateContribution).isEqualTo(BigDecimal.valueOf(-13))
    }

    @Test
    fun `createCorrectionExtension - positive sign`() {
        every { auditControlCorrectionRepository.getById(CORRECTION_ID) } returns correctionEntity

        val extensionSlot = slot<PaymentToEcCorrectionExtensionEntity>()
        every { ecPaymentCorrectionExtensionRepository.save(capture(extensionSlot)) } returnsArgument 0

        persistenceProvider.createCorrectionExtension(financialDescription(false))

        assertThat(extensionSlot.captured.publicContribution).isEqualTo(BigDecimal.valueOf(11))
        assertThat(extensionSlot.captured.correctedPublicContribution).isEqualTo(BigDecimal.valueOf(11))
        assertThat(extensionSlot.captured.autoPublicContribution).isEqualTo(BigDecimal.valueOf(12))
        assertThat(extensionSlot.captured.correctedAutoPublicContribution).isEqualTo(BigDecimal.valueOf(12))
        assertThat(extensionSlot.captured.privateContribution).isEqualTo(BigDecimal.valueOf(13))
        assertThat(extensionSlot.captured.correctedPrivateContribution).isEqualTo(BigDecimal.valueOf(13))
    }

    @Test
    fun getCorrectionIdsAvailableForEcPayments() {
        val entity = paymentToEcExtensionEntity(paymentApplicationToEcEntity)
        every { ecPaymentCorrectionExtensionRepository.getAllByPaymentApplicationToEcIdNull() } returns listOf(entity)

        val correctionQuery = mockk<JPAQuery<Long?>>()
        every { jpaQueryFactory.select(any() as Expression<Long>) } returns correctionQuery
        every { correctionQuery.from(any()) } returns correctionQuery
        val slotCorrectionLeftJoin = mutableListOf<EntityPath<Any>>()
        every { correctionQuery.leftJoin(capture(slotCorrectionLeftJoin)) } returns correctionQuery
        val slotCorrectionLeftJoinOn = mutableListOf<BooleanOperation>()
        every { correctionQuery.on(capture(slotCorrectionLeftJoinOn)) } returns correctionQuery
        val slotCorrectionWhere = slot<BooleanOperation>()
        every { correctionQuery.where(capture(slotCorrectionWhere)) } returns correctionQuery


        val correctionResult = mockk<List<Long?>>()
        every { correctionResult.size } returns 1
        every { correctionQuery.fetch() } returns listOf(15L)


        assertThat(persistenceProvider.getCorrectionIdsAvailableForEcPayments(fundId = PROGRAMME_FUND_ID)).isEqualTo(
            setOf(CORRECTION_ID)
        )
    }

}
