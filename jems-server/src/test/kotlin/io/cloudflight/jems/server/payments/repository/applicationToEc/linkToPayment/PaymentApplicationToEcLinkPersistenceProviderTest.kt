package io.cloudflight.jems.server.payments.repository.applicationToEc.linkToPayment

import com.querydsl.core.Tuple
import com.querydsl.core.types.EntityPath
import com.querydsl.core.types.Predicate
import com.querydsl.core.types.dsl.BooleanOperation
import com.querydsl.jpa.impl.JPAQuery
import com.querydsl.jpa.impl.JPAQueryFactory
import io.cloudflight.jems.api.programme.dto.priority.ProgrammeObjectivePolicy
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.payments.entity.AccountingYearEntity
import io.cloudflight.jems.server.payments.entity.PaymentApplicationToEcEntity
import io.cloudflight.jems.server.payments.entity.PaymentEntity
import io.cloudflight.jems.server.payments.entity.PaymentToEcExtensionEntity
import io.cloudflight.jems.server.payments.entity.PaymentToEcPriorityAxisCumulativeOverviewEntity
import io.cloudflight.jems.server.payments.entity.PaymentToEcPriorityAxisOverviewEntity
import io.cloudflight.jems.server.payments.entity.QPaymentEntity
import io.cloudflight.jems.server.payments.model.ec.PaymentInEcPaymentMetadata
import io.cloudflight.jems.server.payments.model.ec.PaymentToEcAmountSummaryLine
import io.cloudflight.jems.server.payments.model.ec.PaymentToEcAmountSummaryLineTmp
import io.cloudflight.jems.server.payments.model.ec.PaymentToEcExtension
import io.cloudflight.jems.server.payments.model.ec.PaymentToEcLinkingUpdate
import io.cloudflight.jems.server.payments.model.ec.PaymentToEcOverviewType
import io.cloudflight.jems.server.payments.model.ec.overview.EcPaymentSummaryLine
import io.cloudflight.jems.server.payments.model.regular.PaymentEcStatus
import io.cloudflight.jems.server.payments.model.regular.PaymentSearchRequestScoBasis
import io.cloudflight.jems.server.payments.model.regular.PaymentType
import io.cloudflight.jems.server.payments.repository.applicationToEc.PaymentApplicationsToEcRepository
import io.cloudflight.jems.server.payments.repository.applicationToEc.PaymentToEcExtensionRepository
import io.cloudflight.jems.server.payments.repository.applicationToEc.PaymentToEcPriorityAxisCumulativeOverviewRepository
import io.cloudflight.jems.server.payments.repository.applicationToEc.PaymentToEcPriorityAxisOverviewRepository
import io.cloudflight.jems.server.programme.entity.ProgrammePriorityEntity
import io.cloudflight.jems.server.programme.entity.QProgrammePriorityEntity
import io.cloudflight.jems.server.programme.entity.QProgrammeSpecificObjectiveEntity
import io.cloudflight.jems.server.programme.entity.fund.ProgrammeFundEntity
import io.cloudflight.jems.server.programme.repository.priority.ProgrammePriorityRepository
import io.cloudflight.jems.server.project.entity.QProjectEntity
import io.cloudflight.jems.server.project.entity.auditAndControl.QAuditControlCorrectionEntity
import io.cloudflight.jems.server.project.entity.auditAndControl.QAuditControlEntity
import io.cloudflight.jems.server.project.service.contracting.model.ContractingMonitoringExtendedOption
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.LocalDate

class PaymentApplicationToEcLinkPersistenceProviderTest : UnitTest() {

    @MockK
    private lateinit var ecPaymentRepository: PaymentApplicationsToEcRepository

    @MockK
    private lateinit var ecPaymentExtensionRepository: PaymentToEcExtensionRepository

    @MockK
    private lateinit var ecPaymentPriorityAxisOverviewRepository: PaymentToEcPriorityAxisOverviewRepository

    @MockK
    private lateinit var ecPaymentPriorityAxisCumulativeOverviewRepository: PaymentToEcPriorityAxisCumulativeOverviewRepository

    @MockK
    private lateinit var programmePriorityRepository: ProgrammePriorityRepository

    @MockK
    private lateinit var jpaQueryFactory: JPAQueryFactory

    @InjectMockKs
    private lateinit var persistenceProvider: PaymentApplicationToEcLinkPersistenceProvider


    companion object {
        private const val paymentApplicationsToEcId = 1L
        private const val accountingYearId = 3L
        private const val programmeFundId = 10L
        private val submissionDate = LocalDate.now()

        private val programmeFundEntity = ProgrammeFundEntity(programmeFundId, true)

        private val accountingYearEntity =
            AccountingYearEntity(accountingYearId, 2021, LocalDate.of(2021, 1, 1), LocalDate.of(2022, 6, 30))

        private fun paymentApplicationToEcEntity(status: PaymentEcStatus = PaymentEcStatus.Draft) = PaymentApplicationToEcEntity(
            id = paymentApplicationsToEcId,
            programmeFund = programmeFundEntity,
            accountingYear = accountingYearEntity,
            status = status,
            nationalReference = "National Reference",
            technicalAssistanceEur = BigDecimal.valueOf(105.32),
            submissionToSfcDate = submissionDate,
            sfcNumber = "SFC number",
            comment = "Comment"
        )

        private val ftlsPayment = PaymentEntity(
            id = 99L,
            type = PaymentType.FTLS,
            project = mockk(),
            projectCustomIdentifier = "sample project",
            amountApprovedPerFund = BigDecimal.valueOf(255.88),
            fund = mockk(),
            projectAcronym = "sample",
            projectLumpSum = mockk(),
            projectReport = mockk()
        )

        private fun paymentToEcExtensionEntity(paymentApplicationToEcEntity: PaymentApplicationToEcEntity?) =
            PaymentToEcExtensionEntity(
                paymentId = 99L,
                payment = ftlsPayment,
                autoPublicContribution = BigDecimal.ZERO,
                correctedAutoPublicContribution = BigDecimal.ZERO,
                partnerContribution = BigDecimal.valueOf(45.80),
                publicContribution = BigDecimal.valueOf(25.00),
                correctedPublicContribution = BigDecimal.valueOf(55.00),
                privateContribution = BigDecimal.ZERO,
                correctedPrivateContribution = BigDecimal.ZERO,
                paymentApplicationToEc = paymentApplicationToEcEntity,
                finalScoBasis = null,
            )

        private val paymentToEcExtensionModel = PaymentToEcExtension(
            paymentId = 99L,
            ecPaymentId = paymentApplicationsToEcId,
            ecPaymentStatus = PaymentEcStatus.Draft
        )

        private val programmePriority1 = ProgrammePriorityEntity(
            id = 18L,
            code = "PO1",
            objective = ProgrammeObjectivePolicy.AdvancedTechnologies.objective
        )
        private val programmePriority2 = ProgrammePriorityEntity(
            id = 19L,
            code = "PO2",
            objective = ProgrammeObjectivePolicy.Digitisation.objective
        )
        private val programmePriority3 = ProgrammePriorityEntity(
            id = 3L,
            code = "PO3",
            objective = ProgrammeObjectivePolicy.Growth.objective
        )
        private val programmePriority4 = ProgrammePriorityEntity(
            id = 4L,
            code = "PO4",
            objective = ProgrammeObjectivePolicy.IndustrialTransition.objective
        )
        private val programmePriority5 = ProgrammePriorityEntity(
            id = 5L,
            code = "PO5",
            objective = ProgrammeObjectivePolicy.CultureAndTourism.objective
        )
        private val programmePriority6 = ProgrammePriorityEntity(
            id = 6L,
            code = "PO6",
            objective = ProgrammeObjectivePolicy.EnergyEfficiency.objective
        )

        private val selectedPaymentsToEcEntitiesNotArticle = listOf(
            PaymentToEcPriorityAxisOverviewEntity(
                id = 1L,
                paymentApplicationToEc = paymentApplicationToEcEntity(),
                priorityAxis = programmePriority1,
                type = PaymentToEcOverviewType.DoesNotFallUnderArticle94Nor95,
                totalEligibleExpenditure = BigDecimal(101),
                totalUnionContribution = BigDecimal.ZERO,
                totalPublicContribution = BigDecimal(102)
            ),
            PaymentToEcPriorityAxisOverviewEntity(
                id = 2L,
                paymentApplicationToEc = paymentApplicationToEcEntity(),
                priorityAxis = programmePriority2,
                type = PaymentToEcOverviewType.DoesNotFallUnderArticle94Nor95,
                totalEligibleExpenditure = BigDecimal(201),
                totalUnionContribution = BigDecimal.ZERO,
                totalPublicContribution = BigDecimal(202)
            ),
        )

        private val selectedPaymentsToEcEntitiesArticle = listOf(
            PaymentToEcPriorityAxisOverviewEntity(
                id = 3L,
                paymentApplicationToEc = paymentApplicationToEcEntity(),
                priorityAxis = programmePriority3,
                type = PaymentToEcOverviewType.FallsUnderArticle94Or95,
                totalEligibleExpenditure = BigDecimal(301),
                totalUnionContribution = BigDecimal.ZERO,
                totalPublicContribution = BigDecimal(302),
            ),
            PaymentToEcPriorityAxisOverviewEntity(
                id = 4L,
                paymentApplicationToEc = paymentApplicationToEcEntity(),
                priorityAxis = programmePriority4,
                type = PaymentToEcOverviewType.FallsUnderArticle94Or95,
                totalEligibleExpenditure = BigDecimal(401),
                totalUnionContribution = BigDecimal.ZERO,
                totalPublicContribution = BigDecimal(402),
            ),
        )

        private val selectedPaymentsToEcEntitiesCorrection = listOf(
            PaymentToEcPriorityAxisOverviewEntity(
                id =5L,
                paymentApplicationToEc = paymentApplicationToEcEntity(),
                priorityAxis = programmePriority5,
                type = PaymentToEcOverviewType.Correction,
                totalEligibleExpenditure = BigDecimal(501),
                totalUnionContribution = BigDecimal.ZERO,
                totalPublicContribution = BigDecimal(502),
            ),
            PaymentToEcPriorityAxisOverviewEntity(
                id = 6L,
                paymentApplicationToEc = paymentApplicationToEcEntity(),
                priorityAxis = programmePriority6,
                type = PaymentToEcOverviewType.Correction,
                totalEligibleExpenditure = BigDecimal(601),
                totalUnionContribution = BigDecimal.ZERO,
                totalPublicContribution = BigDecimal(602),
            ),
        )

        private val expectedPaymentsIncludedInPaymentsToEcMapped = mapOf(
            PaymentToEcOverviewType.DoesNotFallUnderArticle94Nor95 to mapOf(
                    18L to PaymentToEcAmountSummaryLine(
                        priorityAxis = "PO1",
                        totalEligibleExpenditure = BigDecimal(101),
                        totalUnionContribution = BigDecimal.ZERO,
                        totalPublicContribution = BigDecimal(102)
                    ),
                    19L to PaymentToEcAmountSummaryLine(
                        priorityAxis = "PO2",
                        totalEligibleExpenditure = BigDecimal(201),
                        totalUnionContribution = BigDecimal.ZERO,
                        totalPublicContribution = BigDecimal(202)
                    ),
            ),
            PaymentToEcOverviewType.FallsUnderArticle94Or95 to mapOf(
                3L to PaymentToEcAmountSummaryLine(
                    priorityAxis = "PO3",
                    totalEligibleExpenditure = BigDecimal(301),
                    totalUnionContribution = BigDecimal.ZERO,
                    totalPublicContribution = BigDecimal(302)
                ),
                4L to PaymentToEcAmountSummaryLine(
                    priorityAxis = "PO4",
                    totalEligibleExpenditure = BigDecimal(401),
                    totalUnionContribution = BigDecimal.ZERO,
                    totalPublicContribution = BigDecimal(402)
                ),
            ),
            PaymentToEcOverviewType.Correction to mapOf(
                5L to PaymentToEcAmountSummaryLine(
                    priorityAxis = "PO5",
                    totalEligibleExpenditure = BigDecimal(501),
                    totalUnionContribution = BigDecimal.ZERO,
                    totalPublicContribution = BigDecimal(502)
                ),
                6L to PaymentToEcAmountSummaryLine(
                    priorityAxis = "PO6",
                    totalEligibleExpenditure = BigDecimal(601),
                    totalUnionContribution = BigDecimal.ZERO,
                    totalPublicContribution = BigDecimal(602)
                ),
            )
        )
    }

    @BeforeEach
    fun resetMocks() {
        clearMocks(ecPaymentRepository, ecPaymentExtensionRepository,
            ecPaymentPriorityAxisOverviewRepository, ecPaymentPriorityAxisCumulativeOverviewRepository,
            programmePriorityRepository, jpaQueryFactory
        )
    }

    @Test
    fun getPaymentExtension() {
        every { ecPaymentExtensionRepository.getById(99L) } returns paymentToEcExtensionEntity(paymentApplicationToEcEntity())
        assertThat(persistenceProvider.getPaymentExtension(99L)).isEqualTo(paymentToEcExtensionModel)
    }

    @Test
    fun getPaymentsLinkedToEcPayment() {
        every { ecPaymentExtensionRepository.findAllByPaymentApplicationToEcId(paymentApplicationsToEcId) } returns
            listOf(paymentToEcExtensionEntity(paymentApplicationToEcEntity()))
        val query = mockk<JPAQuery<Tuple>>()
        every { jpaQueryFactory.select(any(), any(), any(), any(), any()) } returns query
        every { query.from(any()) } returns query
        every { query.leftJoin(any<EntityPath<Any>>()) } returns query
        every { query.on(any()) } returns query
        val slotWhere = slot<Predicate>()
        every { query.where(capture(slotWhere)) } returns query

        val tuple = mockk<Tuple>()
        every { tuple.get(0, Long::class.java) } returns 1L
        every { tuple.get(1, PaymentType::class.java) } returns PaymentType.REGULAR
        every { tuple.get(2, PaymentSearchRequestScoBasis::class.java) } returns PaymentSearchRequestScoBasis.DoesNotFallUnderArticle94Nor95
        every { tuple.get(3, ContractingMonitoringExtendedOption::class.java) } returns ContractingMonitoringExtendedOption.No
        every { tuple.get(4, ContractingMonitoringExtendedOption::class.java) } returns ContractingMonitoringExtendedOption.No

        val result = mockk<List<Tuple>>()
        every { result.size } returns 1
        every { query.fetch() } returns listOf(tuple)

        assertThat(persistenceProvider.getPaymentsLinkedToEcPayment(paymentApplicationsToEcId)).isEqualTo(
            mapOf(
                1L to PaymentInEcPaymentMetadata(
                    paymentId = 1,
                    type = PaymentType.REGULAR,
                    finalScoBasis = PaymentSearchRequestScoBasis.DoesNotFallUnderArticle94Nor95,
                    typologyProv94 = ContractingMonitoringExtendedOption.No,
                    typologyProv95 = ContractingMonitoringExtendedOption.No
                )
            )
        )
    }

    @Test
    fun selectPaymentToEcPayment() {
        val paymentToEcExtension = paymentToEcExtensionEntity(null)
        val paymentApplicationToEc = paymentApplicationToEcEntity()
        every { ecPaymentExtensionRepository.findAllById(setOf(99L)) } returns listOf(paymentToEcExtension)
        every { ecPaymentRepository.getById(paymentApplicationsToEcId) } returns paymentApplicationToEc

        persistenceProvider.selectPaymentToEcPayment(paymentIds = setOf(99L), ecPaymentId = paymentApplicationsToEcId)
        assertThat(paymentToEcExtension.paymentApplicationToEc).isEqualTo(paymentApplicationToEc)
    }

    @Test
    fun deselectPaymentFromEcPaymentAndResetFields() {
        val entity = paymentToEcExtensionEntity(paymentApplicationToEcEntity())
        every { ecPaymentExtensionRepository.findAllById(setOf(99L)) } returns listOf(entity)

        persistenceProvider.deselectPaymentFromEcPaymentAndResetFields(setOf(99L))
        assertThat(entity.paymentApplicationToEc).isEqualTo(null)
        assertThat(entity.correctedPublicContribution).isEqualTo(BigDecimal.valueOf(25.00))
        assertThat(entity.correctedAutoPublicContribution).isEqualTo(BigDecimal.ZERO)
        assertThat(entity.correctedPrivateContribution).isEqualTo(BigDecimal.ZERO)
    }

    @Test
    fun updatePaymentToEcCorrectedAmounts() {
        val entity = paymentToEcExtensionEntity(paymentApplicationToEcEntity())
        every { ecPaymentExtensionRepository.getById(99L) } returns entity
        every { ecPaymentRepository.getById(paymentApplicationsToEcId) } returns paymentApplicationToEcEntity()
        val update = PaymentToEcLinkingUpdate(
            correctedPrivateContribution = BigDecimal.TEN,
            correctedPublicContribution = BigDecimal.valueOf(100.00),
            correctedAutoPublicContribution = BigDecimal.ZERO
        )
        persistenceProvider.updatePaymentToEcCorrectedAmounts(99L, update)
        assertThat(entity.correctedPublicContribution).isEqualTo(BigDecimal.valueOf(100.00))
        assertThat(entity.correctedAutoPublicContribution).isEqualTo(BigDecimal.ZERO)
        assertThat(entity.correctedPrivateContribution).isEqualTo(BigDecimal.TEN)
    }

    @Test
    fun updatePaymentToEcFinalScoBasis() {
        val entity = paymentToEcExtensionEntity(paymentApplicationToEcEntity())
        every { ecPaymentExtensionRepository.findAllById(setOf(99L)) } returns listOf(entity)
        persistenceProvider.updatePaymentToEcFinalScoBasis(mapOf(99L to PaymentSearchRequestScoBasis.DoesNotFallUnderArticle94Nor95))
        assertThat(entity.finalScoBasis).isEqualTo(PaymentSearchRequestScoBasis.DoesNotFallUnderArticle94Nor95)
    }

    @Test
    fun calculateAndGetOverview() {

        val expectedPaymentsToEcTmp = mapOf<Long?, PaymentToEcAmountSummaryLineTmp>(
            17L to PaymentToEcAmountSummaryLineTmp(
                priorityId = 17L,
                priorityAxis = "PO1",
                fundAmount = BigDecimal.valueOf(100),
                partnerContribution = BigDecimal.valueOf(200),
                ofWhichPublic = BigDecimal.valueOf(300),
                ofWhichAutoPublic = BigDecimal.valueOf(400),
                correctedFundAmount = BigDecimal.valueOf(0),
                unionContribution = BigDecimal.valueOf(0)
            ),
        )

        val expectedPaymentsToEcCorrectionTmp = mapOf<Long?, PaymentToEcAmountSummaryLineTmp>(
            18L to PaymentToEcAmountSummaryLineTmp(
                priorityId = 18L,
                priorityAxis = "PO2",
                fundAmount = BigDecimal.valueOf(101),
                partnerContribution = BigDecimal.valueOf(1800),
                ofWhichPublic = BigDecimal.valueOf(200),
                ofWhichAutoPublic = BigDecimal.valueOf(300),
                correctedFundAmount = BigDecimal.valueOf(900),
                unionContribution = BigDecimal.valueOf(800)
            ),
        )

        val query = mockk<JPAQuery<Tuple>>()
        every { jpaQueryFactory.select(any(), any(), any(), any(), any(), any(), any()) } returns query
        every { query.from(any()) } returns query
        val slotLeftJoin = mutableListOf<EntityPath<Any>>()
        every { query.leftJoin(capture(slotLeftJoin)) } returns query
        val slotLeftJoinOn = mutableListOf<BooleanOperation>()
        every { query.on(capture(slotLeftJoinOn)) } returns query
        every { query.groupBy(any()) } returns query
        val slotWhere = slot<BooleanOperation>()
        every { query.where(capture(slotWhere)) } returns query

        val tuple = mockk<Tuple>()
        every { tuple.get(0, Long::class.java) } returns 17L
        every { tuple.get(1, String::class.java) } returns "PO1"
        every { tuple.get(2, BigDecimal::class.java) } returns BigDecimal.valueOf(100)
        every { tuple.get(3, BigDecimal::class.java) } returns BigDecimal.valueOf(200)
        every { tuple.get(4, BigDecimal::class.java) } returns BigDecimal.valueOf(300)
        every { tuple.get(5, BigDecimal::class.java) } returns BigDecimal.valueOf(400)

        val result = mockk<List<Tuple>>()
        every { result.size } returns 1
        every { query.fetch() } returns listOf(tuple)


//        Get corrections

        val correctionQuery = mockk<JPAQuery<Tuple>>()
        every { jpaQueryFactory.select(any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any()) } returns correctionQuery
        every { correctionQuery.from(any()) } returns correctionQuery
        val slotCorrectionLeftJoin = mutableListOf<EntityPath<Any>>()
        every { correctionQuery.leftJoin(capture(slotCorrectionLeftJoin)) } returns correctionQuery
        val slotCorrectionLeftJoinOn = mutableListOf<BooleanOperation>()
        every { correctionQuery.on(capture(slotCorrectionLeftJoinOn)) } returns correctionQuery
        every { correctionQuery.groupBy(any()) } returns correctionQuery
        val slotCorrectionWhere = slot<BooleanOperation>()
        every { correctionQuery.where(capture(slotCorrectionWhere)) } returns correctionQuery

        val correctionTuple = mockk<Tuple>()
        every { correctionTuple.get(0, Long::class.java) } returns 18L
        every { correctionTuple.get(1, String::class.java) } returns "PO2"
        every { correctionTuple.get(2, BigDecimal::class.java) } returns BigDecimal.valueOf(101)
        every { correctionTuple.get(3, BigDecimal::class.java) } returns BigDecimal.valueOf(200)
        every { correctionTuple.get(4, BigDecimal::class.java) } returns BigDecimal.valueOf(300)
        every { correctionTuple.get(5, BigDecimal::class.java) } returns BigDecimal.valueOf(400)
        every { correctionTuple.get(6, BigDecimal::class.java) } returns BigDecimal.valueOf(500)
        every { correctionTuple.get(7, BigDecimal::class.java) } returns BigDecimal.valueOf(600)
        every { correctionTuple.get(8, BigDecimal::class.java) } returns BigDecimal.valueOf(700)
        every { correctionTuple.get(9, BigDecimal::class.java) } returns BigDecimal.valueOf(800)
        every { correctionTuple.get(10, BigDecimal::class.java) } returns BigDecimal.valueOf(900)


        val correctionResult = mockk<List<Tuple>>()
        every { correctionResult.size } returns 1
        every { correctionQuery.fetch() } returns listOf(correctionTuple)

        assertThat(
            persistenceProvider.calculateAndGetOverviewForDraftEcPayment(15L)
        ).containsExactlyInAnyOrderEntriesOf(mapOf(
            PaymentToEcOverviewType.DoesNotFallUnderArticle94Nor95 to expectedPaymentsToEcTmp,
            PaymentToEcOverviewType.FallsUnderArticle94Or95 to emptyMap(),
            PaymentToEcOverviewType.Correction to expectedPaymentsToEcCorrectionTmp,
        ))
        assertThat(slotLeftJoin).hasSize(4)
        assertThat(slotLeftJoin[0]).isInstanceOf(QPaymentEntity::class.java)
        assertThat(slotLeftJoinOn[0].toString())
            .isEqualTo("paymentEntity.id = paymentToEcExtensionEntity.payment.id")
        assertThat(slotLeftJoin[1]).isInstanceOf(QProgrammeSpecificObjectiveEntity::class.java)
        assertThat(slotLeftJoinOn[1].toString())
            .isEqualTo("programmeSpecificObjectiveEntity.programmeObjectivePolicy = paymentEntity.project.priorityPolicy.programmeObjectivePolicy")
        assertThat(slotLeftJoin[2]).isInstanceOf(QProgrammePriorityEntity::class.java)
        assertThat(slotLeftJoinOn[2].toString())
            .isEqualTo("programmePriorityEntity.id = programmeSpecificObjectiveEntity.programmePriority.id")
        assertThat(slotWhere.captured.toString())
            .isEqualTo("paymentToEcExtensionEntity.paymentApplicationToEc.id = 15 && " +
                "(projectContractingMonitoringEntity.typologyProv94 = No || projectContractingMonitoringEntity.typologyProv94 is null) " +
                "&& (projectContractingMonitoringEntity.typologyProv95 = No || projectContractingMonitoringEntity.typologyProv95 is null)")

        assertThat(slotCorrectionLeftJoin).hasSize(6)
        assertThat(slotCorrectionLeftJoin[0]).isInstanceOf(QAuditControlCorrectionEntity::class.java)
        assertThat(slotCorrectionLeftJoinOn[0].toString())
            .isEqualTo("auditControlCorrectionEntity.id = paymentToEcCorrectionExtensionEntity.correctionId")
        assertThat(slotCorrectionLeftJoin[1]).isInstanceOf(QAuditControlEntity::class.java)
        assertThat(slotCorrectionLeftJoinOn[1].toString())
            .isEqualTo("auditControlEntity.id = auditControlCorrectionEntity.auditControl.id")
        assertThat(slotCorrectionLeftJoin[2]).isInstanceOf(QProjectEntity::class.java)
        assertThat(slotCorrectionLeftJoinOn[2].toString())
            .isEqualTo("projectEntity.id = auditControlEntity.project.id")
        assertThat(slotCorrectionLeftJoin[3]).isInstanceOf(QProgrammeSpecificObjectiveEntity::class.java)
        assertThat(slotCorrectionLeftJoinOn[3].toString())
            .isEqualTo("programmeSpecificObjectiveEntity.programmeObjectivePolicy = projectEntity.priorityPolicy.programmeObjectivePolicy")
        assertThat(slotCorrectionLeftJoin[4]).isInstanceOf(QProgrammePriorityEntity::class.java)
        assertThat(slotCorrectionLeftJoinOn[4].toString())
            .isEqualTo("programmePriorityEntity.id = programmeSpecificObjectiveEntity.programmePriority.id")
        assertThat(slotCorrectionWhere.captured.toString())
            .isEqualTo("paymentToEcCorrectionExtensionEntity.paymentApplicationToEc.id = 15")
    }

    @Test
    fun saveTotalsWhenFinishingEcPayment() {
        val prioP01 = PaymentToEcAmountSummaryLine(
            priorityAxis = "PO1",
            totalEligibleExpenditure = BigDecimal(101),
            totalUnionContribution = BigDecimal.valueOf(102),
            totalPublicContribution = BigDecimal(103),
        )
        val prioP02 = PaymentToEcAmountSummaryLine(
            priorityAxis = "PO2",
            totalEligibleExpenditure = BigDecimal(201),
            totalUnionContribution = BigDecimal.valueOf(202),
            totalPublicContribution = BigDecimal(203),
        )
        val entitySlot = slot<List<PaymentToEcPriorityAxisOverviewEntity>>()
        every { programmePriorityRepository.findAllById(setOf(18L, 19L)) } returns listOf(
            programmePriority1,
            programmePriority2
        )
        every { ecPaymentRepository.getById(paymentApplicationsToEcId) } returns paymentApplicationToEcEntity()
        every { ecPaymentPriorityAxisOverviewRepository.deleteAllByPaymentApplicationToEcId(paymentApplicationsToEcId) } answers { }
        every { ecPaymentPriorityAxisOverviewRepository.flush() } answers { }
        every { ecPaymentPriorityAxisOverviewRepository.saveAll(capture(entitySlot)) } returnsArgument 0

        persistenceProvider.saveTotalsWhenFinishingEcPayment(
            ecPaymentId = paymentApplicationsToEcId,
            totals = mapOf(PaymentToEcOverviewType.DoesNotFallUnderArticle94Nor95 to mapOf(
                18L to prioP01,
                19L to prioP02,
            ))
        )
        verify(exactly = 1) { ecPaymentPriorityAxisOverviewRepository.deleteAllByPaymentApplicationToEcId(paymentApplicationsToEcId) }
        verify(exactly = 1) { ecPaymentPriorityAxisOverviewRepository.saveAll(entitySlot.captured) }

        assertThat(entitySlot.captured).hasSize(2)
        assertThat(entitySlot.captured[0].type).isEqualTo(PaymentToEcOverviewType.DoesNotFallUnderArticle94Nor95)
        assertThat(entitySlot.captured[0].priorityAxis).isEqualTo(programmePriority1)
        assertThat(entitySlot.captured[0].totalEligibleExpenditure).isEqualTo(BigDecimal.valueOf(101L))
        assertThat(entitySlot.captured[0].totalUnionContribution).isEqualTo(BigDecimal.valueOf(102L))
        assertThat(entitySlot.captured[0].totalPublicContribution).isEqualTo(BigDecimal.valueOf(103L))

        assertThat(entitySlot.captured[1].type).isEqualTo(PaymentToEcOverviewType.DoesNotFallUnderArticle94Nor95)
        assertThat(entitySlot.captured[1].priorityAxis).isEqualTo(programmePriority2)
        assertThat(entitySlot.captured[1].totalEligibleExpenditure).isEqualTo(BigDecimal.valueOf(201L))
        assertThat(entitySlot.captured[1].totalUnionContribution).isEqualTo(BigDecimal.valueOf(202L))
        assertThat(entitySlot.captured[1].totalPublicContribution).isEqualTo(BigDecimal.valueOf(203L))
    }

    @Test
    fun getTotalsForFinishedEcPayment() {
        every {
            ecPaymentPriorityAxisOverviewRepository.getAllByPaymentApplicationToEcIdAndType(
                paymentApplicationsToEcId,
                PaymentToEcOverviewType.DoesNotFallUnderArticle94Nor95
            )
        } returns selectedPaymentsToEcEntitiesNotArticle
        every {
            ecPaymentPriorityAxisOverviewRepository.getAllByPaymentApplicationToEcIdAndType(
                paymentApplicationsToEcId,
                PaymentToEcOverviewType.FallsUnderArticle94Or95
            )
        } returns selectedPaymentsToEcEntitiesArticle
        every {
            ecPaymentPriorityAxisOverviewRepository.getAllByPaymentApplicationToEcIdAndType(
                paymentApplicationsToEcId,
                PaymentToEcOverviewType.Correction
            )
        } returns selectedPaymentsToEcEntitiesCorrection

        val result = persistenceProvider.getTotalsForFinishedEcPayment(ecPaymentId = paymentApplicationsToEcId)
        assertThat(result.keys).containsExactlyInAnyOrderElementsOf(PaymentToEcOverviewType.values().toList())
        assertThat(result).isEqualTo(expectedPaymentsIncludedInPaymentsToEcMapped)
    }

    @Test
    fun getCumulativeAmounts() {

        val query = mockk<JPAQuery<Tuple>>()
        every { jpaQueryFactory.select(any(), any(), any(), any()) } returns query
        every { query.from(any()) } returns query
        val slotWhere = slot<BooleanOperation>()
        every { query.where(capture(slotWhere)) } returns query
        every { query.groupBy(any()) } returns query

        val tuple = mockk<Tuple>()
        every { tuple.get(0, Long::class.java) } returns 17L
        every { tuple.get(1, BigDecimal::class.java) } returns BigDecimal.valueOf(100)
        every { tuple.get(2, BigDecimal::class.java) } returns BigDecimal.valueOf(200)
        every { tuple.get(3, BigDecimal::class.java) } returns BigDecimal.valueOf(300)

        val result = mockk<List<Tuple>>()
        every { result.size } returns 1
        every { query.fetch() } returns listOf(tuple)

        assertThat(
            persistenceProvider.getCumulativeAmounts(setOf(24L, 25L))
        ).containsExactlyInAnyOrderEntriesOf(
            mapOf(17L to EcPaymentSummaryLine(
                totalEligibleExpenditure = BigDecimal.valueOf(100),
                totalUnionContribution = BigDecimal.valueOf(200),
                totalPublicContribution = BigDecimal.valueOf(300),
            ))
        )
        assertThat(slotWhere.captured.toString())
            .isEqualTo("paymentToEcPriorityAxisOverviewEntity.paymentApplicationToEc.id in [24, 25]")
    }

    @Test
    fun saveCumulativeAmounts() {
        val ecPayment = mockk<PaymentApplicationToEcEntity>()
        every { ecPaymentRepository.getById(14L) } returns ecPayment

        val priority63 = mockk<ProgrammePriorityEntity>()
        every { priority63.id } returns 63L
        every { programmePriorityRepository.findAllById(setOf(63L)) } returns listOf(priority63)

        val slotSaved = slot<List<PaymentToEcPriorityAxisCumulativeOverviewEntity>>()
        every { ecPaymentPriorityAxisCumulativeOverviewRepository.saveAll(capture(slotSaved)) } returnsArgument 0

        val totals = mapOf(
            63L to EcPaymentSummaryLine(BigDecimal.valueOf(7), BigDecimal.valueOf(8), BigDecimal.valueOf(9)),
            null to EcPaymentSummaryLine(BigDecimal.valueOf(14), BigDecimal.valueOf(15), BigDecimal.valueOf(16)),
        )
        persistenceProvider.saveCumulativeAmounts(14L, totals)

        assertThat(slotSaved.captured).hasSize(2)

        assertThat(slotSaved.captured[0].paymentApplicationToEc).isEqualTo(ecPayment)
        assertThat(slotSaved.captured[0].priorityAxis).isEqualTo(priority63)
        assertThat(slotSaved.captured[0].totalEligibleExpenditure).isEqualTo(BigDecimal.valueOf(7))
        assertThat(slotSaved.captured[0].totalUnionContribution).isEqualTo(BigDecimal.valueOf(8))
        assertThat(slotSaved.captured[0].totalPublicContribution).isEqualTo(BigDecimal.valueOf(9))

        assertThat(slotSaved.captured[1].paymentApplicationToEc).isEqualTo(ecPayment)
        assertThat(slotSaved.captured[1].priorityAxis).isNull()
        assertThat(slotSaved.captured[1].totalEligibleExpenditure).isEqualTo(BigDecimal.valueOf(14))
        assertThat(slotSaved.captured[1].totalUnionContribution).isEqualTo(BigDecimal.valueOf(15))
        assertThat(slotSaved.captured[1].totalPublicContribution).isEqualTo(BigDecimal.valueOf(16))
    }

    @Test
    fun getCumulativeTotalForEcPayment() {
        val po = mockk<ProgrammePriorityEntity>()
        every { po.id } returns 315L
        every { po.code } returns "PO45"
        val entity = PaymentToEcPriorityAxisCumulativeOverviewEntity(
            id = 54L,
            paymentApplicationToEc = mockk(),
            priorityAxis = po,
            totalEligibleExpenditure = BigDecimal.valueOf(4),
            totalUnionContribution = BigDecimal.valueOf(5),
            totalPublicContribution = BigDecimal.valueOf(6),
        )
        every { ecPaymentPriorityAxisCumulativeOverviewRepository
            .getAllByPaymentApplicationToEcId(12L) } returns listOf(entity)

        assertThat(persistenceProvider.getCumulativeTotalForEcPayment(12L))
            .containsExactlyInAnyOrderEntriesOf(mapOf(
                315L to PaymentToEcAmountSummaryLine(
                    priorityAxis = "PO45",
                    totalEligibleExpenditure = BigDecimal.valueOf(4),
                    totalUnionContribution = BigDecimal.valueOf(5),
                    totalPublicContribution = BigDecimal.valueOf(6),
                ),
            ))
    }

    @Test
    fun getPaymentToEcIdsProjectReportIncluded() {
        val entity = paymentToEcExtensionEntity(paymentApplicationToEcEntity())
        every { ecPaymentExtensionRepository.findAllByPaymentApplicationToEcNotNullAndPaymentProjectReportId(99L) } returns
            listOf(entity)
        assertThat(persistenceProvider.getPaymentToEcIdsProjectReportIncluded(99L)).isEqualTo(setOf(1L))
    }

}
