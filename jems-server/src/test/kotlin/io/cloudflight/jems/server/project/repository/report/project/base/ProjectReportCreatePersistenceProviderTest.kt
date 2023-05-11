package io.cloudflight.jems.server.project.repository.report.project.base

import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.api.project.dto.description.ProjectHorizontalPrinciplesEffect
import io.cloudflight.jems.api.project.dto.description.ProjectTargetGroupDTO
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.programme.entity.costoption.ProgrammeLumpSumEntity
import io.cloudflight.jems.server.programme.entity.costoption.ProgrammeUnitCostEntity
import io.cloudflight.jems.server.programme.entity.fund.ProgrammeFundEntity
import io.cloudflight.jems.server.programme.entity.indicator.OutputIndicatorEntity
import io.cloudflight.jems.server.programme.entity.indicator.ResultIndicatorEntity
import io.cloudflight.jems.server.programme.repository.costoption.ProgrammeLumpSumRepository
import io.cloudflight.jems.server.programme.repository.costoption.ProgrammeUnitCostRepository
import io.cloudflight.jems.server.programme.repository.fund.ProgrammeFundRepository
import io.cloudflight.jems.server.programme.repository.indicator.OutputIndicatorRepository
import io.cloudflight.jems.server.programme.repository.indicator.ResultIndicatorRepository
import io.cloudflight.jems.server.project.entity.contracting.reporting.ProjectContractingReportingEntity
import io.cloudflight.jems.server.project.entity.partner.ProjectPartnerEntity
import io.cloudflight.jems.server.project.entity.report.partner.ProjectPartnerReportEntity
import io.cloudflight.jems.server.project.entity.report.project.ProjectReportCoFinancingEntity
import io.cloudflight.jems.server.project.entity.report.project.ProjectReportEntity
import io.cloudflight.jems.server.project.entity.report.project.financialOverview.*
import io.cloudflight.jems.server.project.entity.report.project.identification.ProjectReportIdentificationTargetGroupEntity
import io.cloudflight.jems.server.project.entity.report.project.identification.ProjectReportSpendingProfileEntity
import io.cloudflight.jems.server.project.entity.report.project.resultPrinciple.ProjectReportHorizontalPrincipleEntity
import io.cloudflight.jems.server.project.entity.report.project.resultPrinciple.ProjectReportProjectResultEntity
import io.cloudflight.jems.server.project.entity.report.project.workPlan.*
import io.cloudflight.jems.server.project.repository.contracting.reporting.ProjectContractingReportingRepository
import io.cloudflight.jems.server.project.repository.partner.ProjectPartnerRepository
import io.cloudflight.jems.server.project.repository.report.partner.ProjectPartnerReportRepository
import io.cloudflight.jems.server.project.repository.report.project.ProjectReportCoFinancingRepository
import io.cloudflight.jems.server.project.repository.report.project.financialOverview.coFinancing.ReportProjectCertificateCoFinancingRepository
import io.cloudflight.jems.server.project.repository.report.project.financialOverview.costCategory.ReportProjectCertificateCostCategoryRepository
import io.cloudflight.jems.server.project.repository.report.project.financialOverview.investment.ReportProjectCertificateInvestmentRepository
import io.cloudflight.jems.server.project.repository.report.project.financialOverview.lumpSums.ReportProjectCertificateLumpSumRepository
import io.cloudflight.jems.server.project.repository.report.project.financialOverview.unitCosts.ReportProjectCertificateUnitCostRepository
import io.cloudflight.jems.server.project.repository.report.project.identification.ProjectReportIdentificationTargetGroupRepository
import io.cloudflight.jems.server.project.repository.report.project.identification.ProjectReportSpendingProfileRepository
import io.cloudflight.jems.server.project.repository.report.project.resultPrinciple.ProjectReportHorizontalPrincipleRepository
import io.cloudflight.jems.server.project.repository.report.project.resultPrinciple.ProjectReportProjectResultRepository
import io.cloudflight.jems.server.project.repository.report.project.workPlan.*
import io.cloudflight.jems.server.project.service.budget.model.BudgetCostsCalculationResultFull
import io.cloudflight.jems.server.project.service.contracting.model.reporting.ContractingDeadlineType
import io.cloudflight.jems.server.project.service.model.ProjectHorizontalPrinciples
import io.cloudflight.jems.server.project.service.model.ProjectRelevanceBenefit
import io.cloudflight.jems.server.project.service.model.ProjectTargetGroup
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerRole
import io.cloudflight.jems.server.project.service.report.model.partner.ReportStatus
import io.cloudflight.jems.server.project.service.report.model.partner.workPlan.create.CreateProjectPartnerReportWorkPackageActivityDeliverable
import io.cloudflight.jems.server.project.service.report.model.partner.workPlan.create.CreateProjectPartnerReportWorkPackageOutput
import io.cloudflight.jems.server.project.service.report.model.project.ProjectReportStatus
import io.cloudflight.jems.server.project.service.report.model.project.base.ProjectReportModel
import io.cloudflight.jems.server.project.service.report.model.project.base.create.*
import io.cloudflight.jems.server.project.service.report.model.project.financialOverview.costCategory.ReportCertificateCostCategory
import io.cloudflight.jems.server.project.service.report.model.project.workPlan.ProjectReportWorkPlanStatus
import io.cloudflight.jems.server.project.service.report.model.project.workPlan.create.ProjectReportWorkPackageActivityCreate
import io.cloudflight.jems.server.project.service.report.model.project.workPlan.create.ProjectReportWorkPackageActivityDeliverableCreate
import io.cloudflight.jems.server.project.service.report.model.project.workPlan.create.ProjectReportWorkPackageCreate
import io.cloudflight.jems.server.project.service.report.model.project.workPlan.create.ProjectReportWorkPackageOutputCreate
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.slot
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.LocalDate
import java.time.ZonedDateTime

class ProjectReportCreatePersistenceProviderTest : UnitTest() {

    companion object {
        private val LAST_WEEK = ZonedDateTime.now().minusWeeks(1)
        private val LAST_YEAR = ZonedDateTime.now().minusYears(1)
        private val YESTERDAY = LocalDate.now().minusDays(1)
        private val MONTH_AGO = LocalDate.now().minusMonths(1)

        private fun report(id: Long, projectId: Long) = ProjectReportModel(
            id = id,
            reportNumber = 1,
            status = ProjectReportStatus.Draft,
            linkedFormVersion = "3.0",
            startDate = YESTERDAY,
            endDate = MONTH_AGO,

            type = ContractingDeadlineType.Both,
            deadlineId = 54L,
            periodNumber = 4,
            reportingDate = YESTERDAY.minusDays(1),
            projectId = projectId,
            projectIdentifier = "projectIdentifier",
            projectAcronym = "projectAcronym",
            leadPartnerNameInOriginalLanguage = "nameInOriginalLanguage",
            leadPartnerNameInEnglish = "nameInEnglish",

            createdAt = LAST_WEEK,
            firstSubmission = LAST_YEAR,
            verificationDate = null,
        )

        private fun workPackages() = listOf(
            ProjectReportWorkPackageCreate(
                workPackageId = 200L,
                number = 2,
                deactivated = false,
                specificObjective = setOf(InputTranslation(SystemLanguage.EN, "spec-obj")),
                specificStatus = null,
                communicationObjective = setOf(InputTranslation(SystemLanguage.EN, "comm-obj")),
                communicationStatus = ProjectReportWorkPlanStatus.Not,
                completed = true,
                activities = listOf(
                    ProjectReportWorkPackageActivityCreate(
                        activityId = 300L,
                        number = 3,
                        title = setOf(InputTranslation(SystemLanguage.EN, "act-title-EN")),
                        deactivated = false,
                        startPeriodNumber = 3,
                        endPeriodNumber = 5,
                        status = ProjectReportWorkPlanStatus.Fully,
                        deliverables = listOf(
                            ProjectReportWorkPackageActivityDeliverableCreate(
                                deliverableId = 400L,
                                number = 4,
                                title = setOf(InputTranslation(SystemLanguage.EN, "del-title-EN")),
                                deactivated = true,
                                periodNumber = 4,
                                previouslyReported = BigDecimal.valueOf(478L, 2),
                            ),
                        ),
                    ),
                ),
                outputs = listOf(
                    ProjectReportWorkPackageOutputCreate(
                        number = 7,
                        title = setOf(InputTranslation(SystemLanguage.EN, "wp-out")),
                        deactivated = false,
                        programmeOutputIndicatorId = 701L,
                        periodNumber = 7,
                        targetValue = BigDecimal.valueOf(700),
                        previouslyReported = BigDecimal.valueOf(954L, 2),
                    ),
                ),
                investments = emptyList()
            ),
        )

        private fun projectRelevanceBenefits() = listOf(
            ProjectRelevanceBenefit(
                group = ProjectTargetGroupDTO.Hospitals,
                specification = setOf(
                    InputTranslation(SystemLanguage.EN, "en"),
                    InputTranslation(SystemLanguage.DE, "de")
                )
            ),
            ProjectRelevanceBenefit(
                group = ProjectTargetGroupDTO.CrossBorderLegalBody,
                specification = setOf(
                    InputTranslation(SystemLanguage.EN, "en 2"),
                    InputTranslation(SystemLanguage.DE, "de 2")
                )
            )
        )

        private fun partnerReport() = ProjectPartnerReportEntity(
            id = 9L,
            partnerId = 9L,
            number = 9,
            status = ReportStatus.Certified,
            applicationFormVersion = "",
            firstSubmission = null,
            lastReSubmission = null,
            controlEnd = null,
            identification = mockk(),
            createdAt = ZonedDateTime.now(),
            projectReport = null,
        )

        val budget = ProjectReportBudget(
            coFinancing = PreviouslyProjectReportedCoFinancing(
                fundsSorted = listOf(
                    PreviouslyProjectReportedFund(
                        fundId = 410L,
                        percentage = BigDecimal.valueOf(40),
                        total = BigDecimal.valueOf(1500),
                        previouslyReported = BigDecimal.valueOf(256),
                        previouslyPaid = BigDecimal.valueOf(512),
                    ),
                ),
                totalPartner = BigDecimal.valueOf(13),
                totalPublic = BigDecimal.valueOf(14),
                totalAutoPublic = BigDecimal.valueOf(15),
                totalPrivate = BigDecimal.valueOf(16),
                totalSum = BigDecimal.valueOf(17),
                previouslyReportedPartner = BigDecimal.valueOf(33),
                previouslyReportedPublic = BigDecimal.valueOf(34),
                previouslyReportedAutoPublic = BigDecimal.valueOf(35),
                previouslyReportedPrivate = BigDecimal.valueOf(36),
                previouslyReportedSum = BigDecimal.valueOf(37),
            ),
            costCategorySetup = ReportCertificateCostCategory(
                totalsFromAF = BudgetCostsCalculationResultFull(
                    staff = BigDecimal.valueOf(105),
                    office = BigDecimal.valueOf(115),
                    travel = BigDecimal.valueOf(125),
                    external = BigDecimal.valueOf(135),
                    equipment = BigDecimal.valueOf(145),
                    infrastructure = BigDecimal.valueOf(155),
                    other = BigDecimal.valueOf(165),
                    lumpSum = BigDecimal.valueOf(175),
                    unitCost = BigDecimal.valueOf(185),
                    sum = BigDecimal.valueOf(195),
                ),
                currentlyReported = BudgetCostsCalculationResultFull(
                    staff = BigDecimal.valueOf(106),
                    office = BigDecimal.valueOf(116),
                    travel = BigDecimal.valueOf(126),
                    external = BigDecimal.valueOf(136),
                    equipment = BigDecimal.valueOf(146),
                    infrastructure = BigDecimal.valueOf(156),
                    other = BigDecimal.valueOf(166),
                    lumpSum = BigDecimal.valueOf(176),
                    unitCost = BigDecimal.valueOf(186),
                    sum = BigDecimal.valueOf(196),
                ),
                previouslyReported = BudgetCostsCalculationResultFull(
                    staff = BigDecimal.valueOf(107),
                    office = BigDecimal.valueOf(117),
                    travel = BigDecimal.valueOf(127),
                    external = BigDecimal.valueOf(137),
                    equipment = BigDecimal.valueOf(147),
                    infrastructure = BigDecimal.valueOf(157),
                    other = BigDecimal.valueOf(167),
                    lumpSum = BigDecimal.valueOf(177),
                    unitCost = BigDecimal.valueOf(187),
                    sum = BigDecimal.valueOf(197),
                )
            ),
            availableLumpSums = listOf(
                ProjectReportLumpSum(
                    lumpSumId = 1L,
                    orderNr = 1,
                    period = 1,
                    total = BigDecimal.TEN,
                    previouslyReported = BigDecimal.ONE,
                    previouslyPaid = BigDecimal.ZERO
                )
            ),
            unitCosts = setOf(
                ProjectReportUnitCostBase(
                    unitCostId = 1L,
                    numberOfUnits = BigDecimal.ONE,
                    totalCost = BigDecimal.TEN,
                    previouslyReported = BigDecimal.ONE
                )
            ),
            investments = listOf(
                ProjectReportInvestment(
                    investmentId = 1L,
                    investmentNumber = 1,
                    workPackageNumber = 1,
                    title = emptySet(),
                    deactivated = false,
                    total = BigDecimal.TEN,
                    previouslyReported = BigDecimal.ONE
                )
            )
        )

        fun projectResult(): List<ProjectReportResultCreate> = listOf(
            ProjectReportResultCreate(
                resultNumber = 57,
                deactivated = false,
                periodNumber = 1,
                programmeResultIndicatorId = 2L,
                baseline = BigDecimal.valueOf(3),
                targetValue = BigDecimal.valueOf(4),
                previouslyReported = BigDecimal.valueOf(5),
            ),
        )

        val projectManagement = ProjectHorizontalPrinciples(
            sustainableDevelopmentCriteriaEffect = ProjectHorizontalPrinciplesEffect.PositiveEffects,
            equalOpportunitiesEffect = ProjectHorizontalPrinciplesEffect.NegativeEffects,
            sexualEqualityEffect = ProjectHorizontalPrinciplesEffect.Neutral,
        )

        fun deadline(): ProjectContractingReportingEntity {
            val deadline = mockk<ProjectContractingReportingEntity>()
            every { deadline.id } returns 54L
            every { deadline.type } returns ContractingDeadlineType.Finance
            every { deadline.periodNumber } returns 17
            every { deadline.deadline } returns MONTH_AGO
            return deadline
        }
    }

    @MockK
    private lateinit var projectReportRepository: ProjectReportRepository

    @MockK
    private lateinit var contractingDeadlineRepository: ProjectContractingReportingRepository

    @MockK
    private lateinit var reportIdentificationTargetGroupRepository: ProjectReportIdentificationTargetGroupRepository

    @MockK
    private lateinit var partnerRepository: ProjectPartnerRepository

    @MockK
    private lateinit var partnerReportRepository: ProjectPartnerReportRepository

    @MockK
    private lateinit var projectReportSpendingProfileRepository: ProjectReportSpendingProfileRepository

    @MockK
    private lateinit var projectReportCertificateCoFinancingRepository: ReportProjectCertificateCoFinancingRepository

    @MockK
    private lateinit var programmeFundRepository: ProgrammeFundRepository

    @MockK
    private lateinit var workPlanRepository: ProjectReportWorkPackageRepository

    @MockK
    private lateinit var workPlanInvestmentRepository: ProjectReportWorkPackageInvestmentRepository

    @MockK
    private lateinit var workPlanActivityRepository: ProjectReportWorkPackageActivityRepository

    @MockK
    private lateinit var workPlanActivityDeliverableRepository: ProjectReportWorkPackageActivityDeliverableRepository

    @MockK
    private lateinit var workPlanOutputRepository: ProjectReportWorkPackageOutputRepository

    @MockK
    private lateinit var projectReportCoFinancingRepository: ProjectReportCoFinancingRepository

    @MockK
    private lateinit var projectReportCertificateCostCategoryRepository: ReportProjectCertificateCostCategoryRepository

    @MockK
    private lateinit var resultIndicatorRepository: ResultIndicatorRepository

    @MockK
    private lateinit var outputIndicatorRepository: OutputIndicatorRepository

    @MockK
    private lateinit var projectResultRepository: ProjectReportProjectResultRepository

    @MockK
    private lateinit var horizontalPrincipleRepository: ProjectReportHorizontalPrincipleRepository

    @MockK
    private lateinit var reportProjectCertificateLumpSumRepository: ReportProjectCertificateLumpSumRepository

    @MockK
    private lateinit var programmeLumpSumRepository: ProgrammeLumpSumRepository

    @MockK
    private lateinit var programmeUnitCostRepository: ProgrammeUnitCostRepository

    @MockK
    private lateinit var reportProjectCertificateUnitCostRepository: ReportProjectCertificateUnitCostRepository

    @MockK
    private lateinit var reportInvestmentRepository: ReportProjectCertificateInvestmentRepository

    @InjectMockKs
    private lateinit var persistence: ProjectReportCreatePersistenceProvider

    @BeforeEach
    fun reset() {
        clearMocks(
            projectReportRepository,
            contractingDeadlineRepository,
            reportIdentificationTargetGroupRepository,
            partnerRepository,
            partnerReportRepository,
            projectReportSpendingProfileRepository,
            projectReportCertificateCoFinancingRepository,
            programmeFundRepository,
            workPlanRepository,
            workPlanInvestmentRepository,
            workPlanActivityRepository,
            workPlanActivityDeliverableRepository,
            workPlanOutputRepository,
            projectReportCoFinancingRepository,
            projectReportCertificateCostCategoryRepository,
            resultIndicatorRepository,
            outputIndicatorRepository,
            projectResultRepository,
            horizontalPrincipleRepository,
            reportProjectCertificateLumpSumRepository,
            programmeLumpSumRepository,
            programmeUnitCostRepository,
            reportProjectCertificateUnitCostRepository,
            reportInvestmentRepository
        )
    }

    @Test
    fun createReport() {
        val projectId = 93L

        // base
        val deadline54 = deadline()
        every { contractingDeadlineRepository.findByProjectIdAndId(projectId, 54L) } returns deadline54
        val saveSlot = slot<ProjectReportEntity>()
        every { projectReportRepository.save(capture(saveSlot)) } returnsArgument 0

        // workPlan
        val slotWp = slot<ProjectReportWorkPackageEntity>()
        every { workPlanRepository.save(capture(slotWp)) } returnsArgument 0
        val slotInvestment = slot<Iterable<ProjectReportWorkPackageInvestmentEntity>>()
        every { workPlanInvestmentRepository.saveAll(capture(slotInvestment)) } returnsArgument 0
        val slotActivity = slot<ProjectReportWorkPackageActivityEntity>()
        every { workPlanActivityRepository.save(capture(slotActivity)) } returnsArgument 0
        val slotDeliverables = slot<Iterable<ProjectReportWorkPackageActivityDeliverableEntity>>()
        every { workPlanActivityDeliverableRepository.saveAll(capture(slotDeliverables)) } returnsArgument 0
        val slotOutputs = slot<Iterable<ProjectReportWorkPackageOutputEntity>>()
        every { workPlanOutputRepository.saveAll(capture(slotOutputs)) } returnsArgument 0
        val outputIndicator = mockk<OutputIndicatorEntity>()
        every { outputIndicatorRepository.getById(701L) } returns outputIndicator

        // target groups
        val targetGroupsSlot = slot<Iterable<ProjectReportIdentificationTargetGroupEntity>>()
        every { reportIdentificationTargetGroupRepository.saveAll(capture(targetGroupsSlot)) } returnsArgument 0

        // spending profile
        val slotSpending = slot<Iterable<ProjectReportSpendingProfileEntity>>()
        every { projectReportSpendingProfileRepository.saveAll(capture(slotSpending)) } returnsArgument 0

        // co financing
        val fund410 = mockk<ProgrammeFundEntity>()
        every { programmeFundRepository.getById(410L) } returns fund410
        val slotCoFinancing = slot<Iterable<ProjectReportCoFinancingEntity>>()
        val slotCertificateCoFinancing = slot<ReportProjectCertificateCoFinancingEntity>()
        every { projectReportCoFinancingRepository.saveAll(capture(slotCoFinancing)) } returnsArgument 0
        every { projectReportCertificateCoFinancingRepository.save(capture(slotCertificateCoFinancing)) } returnsArgument 0

        // cost category
        val slotCostCategory = slot<ReportProjectCertificateCostCategoryEntity>()
        every { projectReportCertificateCostCategoryRepository.save(capture(slotCostCategory)) } returnsArgument 0

        // result and horizontal principle
        val resultIndicator = mockk<ResultIndicatorEntity>()
        every { resultIndicatorRepository.getById(2L) } returns resultIndicator
        val slotResult = slot<Iterable<ProjectReportProjectResultEntity>>()
        val slotPrinciple = slot<ProjectReportHorizontalPrincipleEntity>()
        every { projectResultRepository.saveAll(capture(slotResult)) } returnsArgument 0
        every { horizontalPrincipleRepository.save(capture(slotPrinciple)) } returnsArgument 0

        // lump sums
        val lumpSumEntity = mockk<ProgrammeLumpSumEntity>()
        every { programmeLumpSumRepository.getById(1L) } returns lumpSumEntity
        val lumpSumSlot = slot<Iterable<ReportProjectCertificateLumpSumEntity>>()
        every { reportProjectCertificateLumpSumRepository.saveAll(capture(lumpSumSlot)) } returnsArgument 0

        // unit costs
        val unitCostEntity = mockk<ProgrammeUnitCostEntity>()
        every { programmeUnitCostRepository.getById(1L) } returns unitCostEntity
        val unitCostSlot = slot<Iterable<ReportProjectCertificateUnitCostEntity>>()
        every { reportProjectCertificateUnitCostRepository.saveAll(capture(unitCostSlot)) } returnsArgument 0

        // investments
        val investmentSlot = slot<Iterable<ReportProjectCertificateInvestmentEntity>>()
        every { reportInvestmentRepository.saveAll(capture(investmentSlot)) } returnsArgument 0

        // fill certificates
        val partner = mockk<ProjectPartnerEntity>()
        every { partner.id } returns 8789L
        every { partnerRepository.findTop30ByProjectId(projectId) } returns listOf(partner)
        val partnerReport = partnerReport()
        every { partnerReportRepository.findAllByPartnerIdInAndProjectReportNullAndStatus(setOf(8789L), ReportStatus.Certified) } returns
            listOf(partnerReport)

        val partners = listOf(
            ProjectReportPartnerCreateModel(
                partnerId = 8789L,
                partnerNumber = 8,
                partnerAbbreviation = "P8-part",
                partnerRole = ProjectPartnerRole.PARTNER,
                country = "country-8",
                previouslyReported = BigDecimal.valueOf(421L, 2),
            )
        )

        assertThat(
            persistence.createReportAndFillItToEmptyCertificates(
                ProjectReportCreateModel(
                    reportBase = report(0L, projectId),
                    reportBudget = budget,
                    workPackages = workPackages(),
                    targetGroups = projectRelevanceBenefits(),
                    partners = partners,
                    results = projectResult(),
                    horizontalPrinciples = projectManagement,
                )
            )
        ).isEqualTo(report(0L /* is changed by DB */, projectId).copy(
            type = ContractingDeadlineType.Finance,
            periodNumber = 17,
            reportingDate = MONTH_AGO,
        ))

        assertThat(saveSlot.captured.projectId).isEqualTo(projectId)
        assertThat(saveSlot.captured.number).isEqualTo(1)
        assertThat(saveSlot.captured.status).isEqualTo(ProjectReportStatus.Draft)
        assertThat(saveSlot.captured.applicationFormVersion).isEqualTo("3.0")
        assertThat(saveSlot.captured.startDate).isEqualTo(YESTERDAY)
        assertThat(saveSlot.captured.endDate).isEqualTo(MONTH_AGO)
        assertThat(saveSlot.captured.deadline).isEqualTo(deadline54)
        assertThat(saveSlot.captured.type).isEqualTo(ContractingDeadlineType.Both)
        assertThat(saveSlot.captured.periodNumber).isEqualTo(4)
        assertThat(saveSlot.captured.reportingDate).isEqualTo(YESTERDAY.minusDays(1))
        assertThat(saveSlot.captured.projectIdentifier).isEqualTo("projectIdentifier")
        assertThat(saveSlot.captured.projectAcronym).isEqualTo("projectAcronym")
        assertThat(saveSlot.captured.leadPartnerNameInOriginalLanguage).isEqualTo("nameInOriginalLanguage")
        assertThat(saveSlot.captured.leadPartnerNameInEnglish).isEqualTo("nameInEnglish")
        assertThat(saveSlot.captured.createdAt).isEqualTo(LAST_WEEK)
        assertThat(saveSlot.captured.firstSubmission).isEqualTo(LAST_YEAR)
        assertThat(saveSlot.captured.verificationDate).isNull()
        assertThat(saveSlot.captured.translatedValues).isEmpty()

        assertThat(slotWp.captured.reportEntity).isEqualTo(saveSlot.captured)
        assertThat(slotWp.captured.number).isEqualTo(2)
        assertThat(slotWp.captured.deactivated).isFalse()
        assertThat(slotWp.captured.workPackageId).isEqualTo(200L)
        assertThat(slotWp.captured.specificStatus).isNull()
        assertThat(slotWp.captured.communicationStatus).isEqualTo(ProjectReportWorkPlanStatus.Not)
        assertThat(slotWp.captured.completed).isTrue()
        assertThat(slotWp.captured.translatedValues.first().specificObjective).isEqualTo("spec-obj")
        assertThat(slotWp.captured.translatedValues.first().communicationObjective).isEqualTo("comm-obj")

        assertThat(slotActivity.captured.workPackageEntity).isEqualTo(slotWp.captured)
        assertThat(slotActivity.captured.number).isEqualTo(3)
        assertThat(slotActivity.captured.deactivated).isFalse()
        assertThat(slotActivity.captured.activityId).isEqualTo(300L)
        assertThat(slotActivity.captured.startPeriodNumber).isEqualTo(3)
        assertThat(slotActivity.captured.endPeriodNumber).isEqualTo(5)
        assertThat(slotActivity.captured.status).isEqualTo(ProjectReportWorkPlanStatus.Fully)
        assertThat(slotActivity.captured.translatedValues.first().title).isEqualTo("act-title-EN")

        assertThat(slotDeliverables.captured).hasSize(1)
        assertThat(slotDeliverables.captured.first().activityEntity).isEqualTo(slotActivity.captured)
        assertThat(slotDeliverables.captured.first().number).isEqualTo(4)
        assertThat(slotDeliverables.captured.first().deactivated).isTrue()
        assertThat(slotDeliverables.captured.first().deliverableId).isEqualTo(400L)
        assertThat(slotDeliverables.captured.first().periodNumber).isEqualTo(4)
        assertThat(slotDeliverables.captured.first().previouslyReported).isEqualTo(BigDecimal.valueOf(478L, 2))
        assertThat(slotDeliverables.captured.first().currentReport).isZero()
        assertThat(slotDeliverables.captured.first().translatedValues.first().title).isEqualTo("del-title-EN")

        assertThat(slotOutputs.captured).hasSize(1)
        assertThat(slotOutputs.captured.first().workPackageEntity).isEqualTo(slotWp.captured)
        assertThat(slotOutputs.captured.first().number).isEqualTo(7)
        assertThat(slotOutputs.captured.first().deactivated).isFalse()
        assertThat(slotOutputs.captured.first().programmeOutputIndicator).isEqualTo(outputIndicator)
        assertThat(slotOutputs.captured.first().periodNumber).isEqualTo(7)
        assertThat(slotOutputs.captured.first().targetValue).isEqualTo(BigDecimal.valueOf(700))
        assertThat(slotOutputs.captured.first().previouslyReported).isEqualTo(BigDecimal.valueOf(954L, 2))
        assertThat(slotOutputs.captured.first().currentReport).isZero()
        assertThat(slotOutputs.captured.first().translatedValues.first().title).isEqualTo("wp-out")

        assertThat(targetGroupsSlot.captured).hasSize(2)
        with(targetGroupsSlot.captured.first { it.sortNumber == 1 }) {
            assertThat(type).isEqualTo(ProjectTargetGroup.Hospitals)
            assertThat(translatedValues).isEmpty()
        }
        with(targetGroupsSlot.captured.first { it.sortNumber == 2 }) {
            assertThat(type).isEqualTo(ProjectTargetGroup.CrossBorderLegalBody)
            assertThat(translatedValues).isEmpty()
        }

        assertThat(slotSpending.captured).hasSize(1)
        assertThat(slotSpending.captured.first().id.partnerId).isEqualTo(8789L)
        assertThat(slotSpending.captured.first().previouslyReported).isEqualTo(BigDecimal.valueOf(421L, 2))
        assertThat(slotSpending.captured.first().partnerNumber).isEqualTo(8)
        assertThat(slotSpending.captured.first().partnerAbbreviation).isEqualTo("P8-part")
        assertThat(slotSpending.captured.first().partnerRole).isEqualTo(ProjectPartnerRole.PARTNER)
        assertThat(slotSpending.captured.first().country).isEqualTo("country-8")
        assertThat(slotSpending.captured.first().currentlyReported).isZero()

        assertThat(slotCoFinancing.captured).hasSize(1)
        assertThat(slotCoFinancing.captured.first().programmeFund).isEqualTo(fund410)
        assertThat(slotCoFinancing.captured.first().percentage).isEqualTo(BigDecimal.valueOf(40))
        assertThat(slotCoFinancing.captured.first().total).isEqualTo(BigDecimal.valueOf(1500))
        assertThat(slotCoFinancing.captured.first().previouslyReported).isEqualTo(BigDecimal.valueOf(256))
        assertThat(slotCoFinancing.captured.first().previouslyPaid).isEqualTo(BigDecimal.valueOf(512))

        assertThat(slotCertificateCoFinancing.captured.reportEntity).isEqualTo(saveSlot.captured)
        assertThat(slotCertificateCoFinancing.captured.partnerContributionTotal).isEqualTo(BigDecimal.valueOf(13))
        assertThat(slotCertificateCoFinancing.captured.publicContributionTotal).isEqualTo(BigDecimal.valueOf(14))
        assertThat(slotCertificateCoFinancing.captured.automaticPublicContributionTotal).isEqualTo(BigDecimal.valueOf(15))
        assertThat(slotCertificateCoFinancing.captured.privateContributionTotal).isEqualTo(BigDecimal.valueOf(16))
        assertThat(slotCertificateCoFinancing.captured.sumTotal).isEqualTo(BigDecimal.valueOf(17))

        assertThat(slotCertificateCoFinancing.captured.partnerContributionCurrent).isZero()
        assertThat(slotCertificateCoFinancing.captured.publicContributionCurrent).isZero()
        assertThat(slotCertificateCoFinancing.captured.automaticPublicContributionCurrent).isZero()
        assertThat(slotCertificateCoFinancing.captured.privateContributionCurrent).isZero()
        assertThat(slotCertificateCoFinancing.captured.sumCurrent).isZero()

        assertThat(slotCertificateCoFinancing.captured.partnerContributionPreviouslyReported).isEqualTo(BigDecimal.valueOf(33))
        assertThat(slotCertificateCoFinancing.captured.publicContributionPreviouslyReported).isEqualTo(BigDecimal.valueOf(34))
        assertThat(slotCertificateCoFinancing.captured.automaticPublicContributionPreviouslyReported).isEqualTo(BigDecimal.valueOf(35))
        assertThat(slotCertificateCoFinancing.captured.privateContributionPreviouslyReported).isEqualTo(BigDecimal.valueOf(36))
        assertThat(slotCertificateCoFinancing.captured.sumPreviouslyReported).isEqualTo(BigDecimal.valueOf(37))

        assertThat(slotCostCategory.captured.reportEntity).isEqualTo(saveSlot.captured)
        assertThat(slotCostCategory.captured.staffTotal).isEqualTo(BigDecimal.valueOf(105))
        assertThat(slotCostCategory.captured.officeTotal).isEqualTo(BigDecimal.valueOf(115))
        assertThat(slotCostCategory.captured.travelTotal).isEqualTo(BigDecimal.valueOf(125))
        assertThat(slotCostCategory.captured.externalTotal).isEqualTo(BigDecimal.valueOf(135))
        assertThat(slotCostCategory.captured.equipmentTotal).isEqualTo(BigDecimal.valueOf(145))
        assertThat(slotCostCategory.captured.infrastructureTotal).isEqualTo(BigDecimal.valueOf(155))
        assertThat(slotCostCategory.captured.otherTotal).isEqualTo(BigDecimal.valueOf(165))
        assertThat(slotCostCategory.captured.lumpSumTotal).isEqualTo(BigDecimal.valueOf(175))
        assertThat(slotCostCategory.captured.unitCostTotal).isEqualTo(BigDecimal.valueOf(185))
        assertThat(slotCostCategory.captured.sumTotal).isEqualTo(BigDecimal.valueOf(195))

        assertThat(slotResult.captured).hasSize(1)
        assertThat(slotResult.captured.first().projectReport).isEqualTo(saveSlot.captured)
        assertThat(slotResult.captured.first().resultNumber).isEqualTo(57)
        assertThat(slotResult.captured.first().translatedValues).isEmpty()
        assertThat(slotResult.captured.first().periodNumber).isEqualTo(1)
        assertThat(slotResult.captured.first().programmeResultIndicatorEntity).isEqualTo(resultIndicator)
        assertThat(slotResult.captured.first().baseline).isEqualTo(BigDecimal.valueOf(3))
        assertThat(slotResult.captured.first().targetValue).isEqualTo(BigDecimal.valueOf(4))
        assertThat(slotResult.captured.first().currentReport).isZero()
        assertThat(slotResult.captured.first().previouslyReported).isEqualTo(BigDecimal.valueOf(5))
        assertThat(slotResult.captured.first().deactivated).isFalse()

        assertThat(slotPrinciple.captured.projectReport).isEqualTo(saveSlot.captured)
        assertThat(slotPrinciple.captured.sustainableDevelopmentCriteriaEffect).isEqualTo(ProjectHorizontalPrinciplesEffect.PositiveEffects)
        assertThat(slotPrinciple.captured.equalOpportunitiesEffect).isEqualTo(ProjectHorizontalPrinciplesEffect.NegativeEffects)
        assertThat(slotPrinciple.captured.sexualEqualityEffect).isEqualTo(ProjectHorizontalPrinciplesEffect.Neutral)
        assertThat(slotPrinciple.captured.translatedValues).isEmpty()

        assertThat(lumpSumSlot.captured).hasSize(1)
        assertThat(lumpSumSlot.captured.first().reportEntity).isEqualTo(saveSlot.captured)
        assertThat(lumpSumSlot.captured.first().total).isEqualTo(BigDecimal.TEN)
        assertThat(lumpSumSlot.captured.first().previouslyReported).isEqualTo(BigDecimal.ONE)
        assertThat(lumpSumSlot.captured.first().previouslyPaid).isEqualTo(BigDecimal.ZERO)

        assertThat(unitCostSlot.captured).hasSize(1)
        assertThat(unitCostSlot.captured.first().reportEntity).isEqualTo(saveSlot.captured)
        assertThat(unitCostSlot.captured.first().total).isEqualTo(BigDecimal.TEN)
        assertThat(unitCostSlot.captured.first().previouslyReported).isEqualTo(BigDecimal.ONE)

        assertThat(investmentSlot.captured).hasSize(1)
        assertThat(investmentSlot.captured.first().reportEntity).isEqualTo(saveSlot.captured)
        assertThat(investmentSlot.captured.first().total).isEqualTo(BigDecimal.TEN)
        assertThat(investmentSlot.captured.first().previouslyReported).isEqualTo(BigDecimal.ONE)

        assertThat(partnerReport.projectReport).isEqualTo(saveSlot.captured)
    }

}
