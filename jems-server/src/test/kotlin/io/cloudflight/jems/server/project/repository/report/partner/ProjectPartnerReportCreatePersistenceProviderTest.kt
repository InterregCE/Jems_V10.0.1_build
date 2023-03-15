package io.cloudflight.jems.server.project.repository.report.partner

import io.cloudflight.jems.api.programme.dto.language.SystemLanguage.EN
import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.api.project.dto.description.ProjectTargetGroupDTO
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.programme.entity.costoption.ProgrammeLumpSumEntity
import io.cloudflight.jems.server.programme.entity.costoption.ProgrammeUnitCostEntity
import io.cloudflight.jems.server.programme.entity.fund.ProgrammeFundEntity
import io.cloudflight.jems.server.programme.entity.legalstatus.ProgrammeLegalStatusEntity
import io.cloudflight.jems.server.programme.repository.costoption.ProgrammeLumpSumRepository
import io.cloudflight.jems.server.programme.repository.costoption.ProgrammeUnitCostRepository
import io.cloudflight.jems.server.programme.repository.fund.ProgrammeFundRepository
import io.cloudflight.jems.server.programme.repository.legalstatus.ProgrammeLegalStatusRepository
import io.cloudflight.jems.server.programme.service.fund.model.ProgrammeFundType
import io.cloudflight.jems.server.programme.service.legalstatus.model.ProgrammeLegalStatus
import io.cloudflight.jems.server.programme.service.legalstatus.model.ProgrammeLegalStatusType
import io.cloudflight.jems.server.project.entity.report.partner.ProjectPartnerReportCoFinancingEntity
import io.cloudflight.jems.server.project.entity.report.partner.ProjectPartnerReportEntity
import io.cloudflight.jems.server.project.entity.report.partner.contribution.ProjectPartnerReportContributionEntity
import io.cloudflight.jems.server.project.entity.report.partner.expenditure.PartnerReportInvestmentEntity
import io.cloudflight.jems.server.project.entity.report.partner.expenditure.PartnerReportLumpSumEntity
import io.cloudflight.jems.server.project.entity.report.partner.expenditure.PartnerReportUnitCostEntity
import io.cloudflight.jems.server.project.entity.report.partner.financialOverview.ReportProjectPartnerExpenditureCoFinancingEntity
import io.cloudflight.jems.server.project.entity.report.partner.financialOverview.ReportProjectPartnerExpenditureCostCategoryEntity
import io.cloudflight.jems.server.project.entity.report.partner.identification.ProjectPartnerReportBudgetPerPeriodEntity
import io.cloudflight.jems.server.project.entity.report.partner.identification.ProjectPartnerReportIdentificationEntity
import io.cloudflight.jems.server.project.entity.report.partner.identification.ProjectPartnerReportIdentificationTargetGroupEntity
import io.cloudflight.jems.server.project.entity.report.partner.workPlan.ProjectPartnerReportWorkPackageActivityDeliverableEntity
import io.cloudflight.jems.server.project.entity.report.partner.workPlan.ProjectPartnerReportWorkPackageActivityEntity
import io.cloudflight.jems.server.project.entity.report.partner.workPlan.ProjectPartnerReportWorkPackageEntity
import io.cloudflight.jems.server.project.entity.report.partner.workPlan.ProjectPartnerReportWorkPackageOutputEntity
import io.cloudflight.jems.server.project.repository.report.partner.contribution.ProjectPartnerReportContributionRepository
import io.cloudflight.jems.server.project.repository.report.partner.expenditure.ProjectPartnerReportInvestmentRepository
import io.cloudflight.jems.server.project.repository.report.partner.expenditure.ProjectPartnerReportLumpSumRepository
import io.cloudflight.jems.server.project.repository.report.partner.expenditure.ProjectPartnerReportUnitCostRepository
import io.cloudflight.jems.server.project.repository.report.partner.financialOverview.coFinancing.ReportProjectPartnerExpenditureCoFinancingRepository
import io.cloudflight.jems.server.project.repository.report.partner.financialOverview.costCategory.ReportProjectPartnerExpenditureCostCategoryRepository
import io.cloudflight.jems.server.project.repository.report.partner.identification.ProjectPartnerReportBudgetPerPeriodRepository
import io.cloudflight.jems.server.project.repository.report.partner.identification.ProjectPartnerReportIdentificationRepository
import io.cloudflight.jems.server.project.repository.report.partner.identification.ProjectPartnerReportIdentificationTargetGroupRepository
import io.cloudflight.jems.server.project.repository.report.partner.workPlan.ProjectPartnerReportWorkPackageActivityDeliverableRepository
import io.cloudflight.jems.server.project.repository.report.partner.workPlan.ProjectPartnerReportWorkPackageActivityRepository
import io.cloudflight.jems.server.project.repository.report.partner.workPlan.ProjectPartnerReportWorkPackageOutputRepository
import io.cloudflight.jems.server.project.repository.report.partner.workPlan.ProjectPartnerReportWorkPackageRepository
import io.cloudflight.jems.server.project.service.budget.model.BudgetCostsCalculationResultFull
import io.cloudflight.jems.server.project.service.model.ProjectRelevanceBenefit
import io.cloudflight.jems.server.project.service.model.ProjectTargetGroup
import io.cloudflight.jems.server.project.service.partner.cofinancing.model.ProjectPartnerContributionStatus
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerBudgetOptions
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerRole
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerVatRecovery
import io.cloudflight.jems.server.project.service.report.model.partner.ReportStatus
import io.cloudflight.jems.server.project.service.report.model.partner.base.create.PartnerReportBaseData
import io.cloudflight.jems.server.project.service.report.model.partner.base.create.PartnerReportBudget
import io.cloudflight.jems.server.project.service.report.model.partner.base.create.PartnerReportIdentificationCreate
import io.cloudflight.jems.server.project.service.report.model.partner.base.create.PartnerReportInvestment
import io.cloudflight.jems.server.project.service.report.model.partner.base.create.PartnerReportLumpSum
import io.cloudflight.jems.server.project.service.report.model.partner.base.create.PartnerReportUnitCostBase
import io.cloudflight.jems.server.project.service.report.model.partner.base.create.PreviouslyReportedCoFinancing
import io.cloudflight.jems.server.project.service.report.model.partner.base.create.PreviouslyReportedFund
import io.cloudflight.jems.server.project.service.report.model.partner.base.create.ProjectPartnerReportCreate
import io.cloudflight.jems.server.project.service.report.model.partner.contribution.create.CreateProjectPartnerReportContribution
import io.cloudflight.jems.server.project.service.report.model.partner.financialOverview.costCategory.ReportExpenditureCostCategory
import io.cloudflight.jems.server.project.service.report.model.partner.identification.ProjectPartnerReportPeriod
import io.cloudflight.jems.server.project.service.report.model.partner.identification.control.ReportType
import io.cloudflight.jems.server.project.service.report.model.partner.workPlan.create.CreateProjectPartnerReportWorkPackage
import io.cloudflight.jems.server.project.service.report.model.partner.workPlan.create.CreateProjectPartnerReportWorkPackageActivity
import io.cloudflight.jems.server.project.service.report.model.partner.workPlan.create.CreateProjectPartnerReportWorkPackageActivityDeliverable
import io.cloudflight.jems.server.project.service.report.model.partner.workPlan.create.CreateProjectPartnerReportWorkPackageOutput
import io.mockk.CapturingSlot
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.slot
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import java.math.BigDecimal.ONE
import java.math.BigDecimal.TEN
import java.math.BigDecimal.ZERO
import java.math.BigDecimal.valueOf
import java.util.UUID

class ProjectPartnerReportCreatePersistenceProviderTest : UnitTest() {

    companion object {
        private const val PARTNER_ID = 10L

        private const val WORK_PACKAGE_ID = 9658L
        private const val ACTIVITY_ID = 9942L
        private const val DELIVERABLE_ID = 9225L

        private val HISTORY_CONTRIBUTION_UUID = UUID.randomUUID()

        private val programmeFundEntity = ProgrammeFundEntity(
            id = 1L,
            selected = true,
            type = ProgrammeFundType.ERDF,
        )

        private val legalStatusEntity = ProgrammeLegalStatusEntity(
            id = 650L,
            type = ProgrammeLegalStatusType.PRIVATE,
        )

        private val legalStatus = ProgrammeLegalStatus(
            id = legalStatusEntity.id,
            type = legalStatusEntity.type,
        )

        private val reportToBeCreated = ProjectPartnerReportCreate(
            baseData = PartnerReportBaseData(
                partnerId = PARTNER_ID,
                reportNumber = 1,
                status = ReportStatus.Draft,
                version = "6.5",
            ),
            identification = PartnerReportIdentificationCreate(
                projectIdentifier = "projectIdentifier",
                projectAcronym = "projectAcronym",
                partnerNumber = 4,
                partnerAbbreviation = "partnerAbbreviation",
                partnerRole = ProjectPartnerRole.PARTNER,
                nameInOriginalLanguage = "nameInOriginalLanguage",
                nameInEnglish = "nameInEnglish",
                legalStatusId = legalStatus.id,
                partnerType = ProjectTargetGroup.SectoralAgency,
                vatRecovery = ProjectPartnerVatRecovery.Yes,
                country = "Österreich (AT)",
                countryCode = "AT",
                currency = "EUR",
            ),
            workPackages = listOf(
                CreateProjectPartnerReportWorkPackage(
                    workPackageId = WORK_PACKAGE_ID,
                    number = 4,
                    deactivated = false,
                    specificObjective = emptySet(),
                    communicationObjective = emptySet(),
                    activities = listOf(
                        CreateProjectPartnerReportWorkPackageActivity(
                            activityId = ACTIVITY_ID,
                            number = 1,
                            title = setOf(InputTranslation(EN, "4.1 activity title")),
                            deactivated = false,
                            startPeriodNumber = 6,
                            endPeriodNumber = 8,
                            deliverables = listOf(
                                CreateProjectPartnerReportWorkPackageActivityDeliverable(
                                    deliverableId = DELIVERABLE_ID,
                                    number = 1,
                                    title = setOf(InputTranslation(EN, "4.1.1 title")),
                                    deactivated = false,
                                    periodNumber = 7,
                                    previouslyReported = null,
                                )
                            ),
                        )
                    ),
                    outputs = listOf(
                        CreateProjectPartnerReportWorkPackageOutput(
                            number = 7,
                            title = setOf(InputTranslation(EN, "7 output title")),
                            deactivated = false,
                            programmeOutputIndicatorId = 75L,
                            periodNumber = 9,
                            targetValue = TEN,
                            previouslyReported = null,
                        )
                    ),
                )
            ),
            targetGroups = listOf(
                ProjectRelevanceBenefit(
                    group = ProjectTargetGroupDTO.BusinessSupportOrganisation,
                    specification = setOf(InputTranslation(EN, "first target group")),
                ),
                ProjectRelevanceBenefit(
                    group = ProjectTargetGroupDTO.EducationTrainingCentreAndSchool,
                    specification = emptySet(),
                ),
                ProjectRelevanceBenefit(
                    group = ProjectTargetGroupDTO.CrossBorderLegalBody,
                    specification = setOf(InputTranslation(EN, "third target group")),
                ),
            ),
            budget = PartnerReportBudget(
                contributions = listOf(
                    CreateProjectPartnerReportContribution(
                        sourceOfContribution = "source text",
                        legalStatus = ProjectPartnerContributionStatus.AutomaticPublic,
                        idFromApplicationForm = 4L,
                        historyIdentifier = HISTORY_CONTRIBUTION_UUID,
                        createdInThisReport = false,
                        amount = ONE,
                        previouslyReported = ONE,
                        currentlyReported = ZERO,
                    ),
                ),
                availableLumpSums = listOf(
                    PartnerReportLumpSum(
                        lumpSumId = 85L,
                        orderNr = 7,
                        period = 0,
                        total = ONE,
                        previouslyReported = valueOf(7, 1),
                        previouslyPaid = valueOf(7, 1),
                        previouslyReportedParked = valueOf(1000),
                    ),
                ),
                unitCosts = setOf(
                    PartnerReportUnitCostBase(
                        unitCostId = 5L,
                        numberOfUnits = ONE,
                        totalCost = ONE,
                        previouslyReported = valueOf(5, 1),
                        previouslyReportedParked = ZERO
                    )
                ),
                budgetPerPeriod = listOf(
                    ProjectPartnerReportPeriod(1, ONE, ONE, 1, 3),
                    ProjectPartnerReportPeriod(2, TEN, valueOf(11L), 4, 6),
                ),
                expenditureSetup = ReportExpenditureCostCategory(
                    options = ProjectPartnerBudgetOptions(
                        partnerId = PARTNER_ID,
                        officeAndAdministrationOnStaffCostsFlatRate = null,
                        officeAndAdministrationOnDirectCostsFlatRate = null,
                        travelAndAccommodationOnStaffCostsFlatRate = null,
                        staffCostsFlatRate = null,
                        otherCostsOnStaffCostsFlatRate = 40,
                    ),
                    totalsFromAF = BudgetCostsCalculationResultFull(
                        staff = valueOf(10),
                        office = valueOf(11),
                        travel = valueOf(12),
                        external = valueOf(13),
                        equipment = valueOf(14),
                        infrastructure = valueOf(15),
                        other = valueOf(16),
                        lumpSum = valueOf(17),
                        unitCost = valueOf(18),
                        sum = valueOf(19),
                    ),
                    currentlyReported = BudgetCostsCalculationResultFull(
                        staff = valueOf(20),
                        office = valueOf(21),
                        travel = valueOf(22),
                        external = valueOf(23),
                        equipment = valueOf(24),
                        infrastructure = valueOf(25),
                        other = valueOf(26),
                        lumpSum = valueOf(27),
                        unitCost = valueOf(28),
                        sum = valueOf(29),
                    ),
                    currentlyReportedParked = BudgetCostsCalculationResultFull(
                        staff = valueOf(70),
                        office = valueOf(71),
                        travel = valueOf(77),
                        external = valueOf(77),
                        equipment = valueOf(74),
                        infrastructure = valueOf(77),
                        other = valueOf(76),
                        lumpSum = valueOf(77),
                        unitCost = valueOf(78),
                        sum = valueOf(79),
                    ),
                    currentlyReportedReIncluded = BudgetCostsCalculationResultFull(
                        staff = valueOf(50),
                        office = valueOf(51),
                        travel = valueOf(55),
                        external = valueOf(55),
                        equipment = valueOf(54),
                        infrastructure = valueOf(55),
                        other = valueOf(56),
                        lumpSum = valueOf(57),
                        unitCost = valueOf(58),
                        sum = valueOf(59),
                    ),
                    totalEligibleAfterControl = BudgetCostsCalculationResultFull(
                        staff = valueOf(40),
                        office = valueOf(41),
                        travel = valueOf(42),
                        external = valueOf(43),
                        equipment = valueOf(44),
                        infrastructure = valueOf(45),
                        other = valueOf(46),
                        lumpSum = valueOf(47),
                        unitCost = valueOf(48),
                        sum = valueOf(49),
                    ),
                    previouslyReported = BudgetCostsCalculationResultFull(
                        staff = valueOf(30),
                        office = valueOf(31),
                        travel = valueOf(32),
                        external = valueOf(33),
                        equipment = valueOf(34),
                        infrastructure = valueOf(35),
                        other = valueOf(36),
                        lumpSum = valueOf(37),
                        unitCost = valueOf(38),
                        sum = valueOf(39),
                    ),
                    previouslyReportedParked = BudgetCostsCalculationResultFull(
                        staff = valueOf(60),
                        office = valueOf(61),
                        travel = valueOf(62),
                        external = valueOf(66),
                        equipment = valueOf(64),
                        infrastructure = valueOf(65),
                        other = valueOf(66),
                        lumpSum = valueOf(67),
                        unitCost = valueOf(68),
                        sum = valueOf(69),
                    ),
                ),
                previouslyReportedCoFinancing = PreviouslyReportedCoFinancing(
                    fundsSorted = listOf(
                        PreviouslyReportedFund(
                            fundId = programmeFundEntity.id, percentage = TEN,
                            total = valueOf(100L), previouslyReported = valueOf(25),
                            previouslyPaid = valueOf(35),
                            previouslyReportedParked = valueOf(100)
                        ),
                        PreviouslyReportedFund(
                            fundId = null, percentage = valueOf(90),
                            total = valueOf(900L), previouslyReported = valueOf(400),
                            previouslyPaid = valueOf(410),
                            previouslyReportedParked = valueOf(100)
                        ),
                    ),
                    totalPartner = valueOf(900L),
                    totalPublic = valueOf(200L),
                    totalAutoPublic = valueOf(300L),
                    totalPrivate = valueOf(400L),
                    totalSum = valueOf(5000L),

                    previouslyReportedPartner = valueOf(400L),
                    previouslyReportedPublic = valueOf(100L),
                    previouslyReportedAutoPublic = valueOf(130L),
                    previouslyReportedPrivate = valueOf(170L),
                    previouslyReportedSum = valueOf(7500L),

                    previouslyReportedParkedPartner = valueOf(100),
                    previouslyReportedParkedAutoPublic = valueOf(100),
                    previouslyReportedParkedPrivate = valueOf(100),
                    previouslyReportedParkedPublic = valueOf(100),
                    previouslyReportedParkedSum = valueOf(100),
                ),
                investments = listOf(
                    PartnerReportInvestment(
                        investmentId = 245,
                        investmentNumber = 4,
                        workPackageNumber = 7,
                        title = setOf(InputTranslation(EN, "investment title EN")),
                        total = valueOf(100L),
                        previouslyReported = valueOf(50L),
                        previouslyReportedParked = valueOf(130),
                        deactivated = false,
                    )
                )
            ),
        )
    }

    @MockK
    lateinit var partnerReportRepository: ProjectPartnerReportRepository

    @MockK
    lateinit var partnerReportCoFinancingRepository: ProjectPartnerReportCoFinancingRepository

    @MockK
    lateinit var reportProjectPartnerExpenditureCoFinancingRepository: ReportProjectPartnerExpenditureCoFinancingRepository

    @MockK
    lateinit var legalStatusRepository: ProgrammeLegalStatusRepository

    @MockK
    lateinit var programmeFundRepository: ProgrammeFundRepository

    @MockK
    lateinit var programmeLumpSumRepository: ProgrammeLumpSumRepository

    @MockK
    lateinit var programmeUnitCostRepository: ProgrammeUnitCostRepository

    @MockK
    lateinit var workPlanRepository: ProjectPartnerReportWorkPackageRepository

    @MockK
    lateinit var workPlanActivityRepository: ProjectPartnerReportWorkPackageActivityRepository

    @MockK
    lateinit var workPlanActivityDeliverableRepository: ProjectPartnerReportWorkPackageActivityDeliverableRepository

    @MockK
    lateinit var workPlanOutputRepository: ProjectPartnerReportWorkPackageOutputRepository

    @MockK
    lateinit var projectPartnerReportIdentificationRepository: ProjectPartnerReportIdentificationRepository

    @MockK
    lateinit var projectPartnerReportIdentificationTargetGroupRepository: ProjectPartnerReportIdentificationTargetGroupRepository

    @MockK
    lateinit var contributionRepository: ProjectPartnerReportContributionRepository

    @MockK
    lateinit var reportLumpSumRepository: ProjectPartnerReportLumpSumRepository

    @MockK
    lateinit var reportUnitCostRepository: ProjectPartnerReportUnitCostRepository

    @MockK
    lateinit var reportInvestmentRepository: ProjectPartnerReportInvestmentRepository

    @MockK
    lateinit var reportBudgetPerPeriodRepository: ProjectPartnerReportBudgetPerPeriodRepository

    @MockK
    lateinit var reportBudgetExpenditureRepository: ReportProjectPartnerExpenditureCostCategoryRepository

    @InjectMockKs
    lateinit var persistence: ProjectPartnerReportCreatePersistenceProvider

    @ParameterizedTest(name = "createPartnerReport, without legal status {0}")
    @ValueSource(booleans = [true, false])
    fun createPartnerReport(withoutLegalStatus: Boolean) {
        val reportSlot = slot<ProjectPartnerReportEntity>()
        val reportCoFinancingSlot = slot<Iterable<ProjectPartnerReportCoFinancingEntity>>()
        val reportExpenditureCoFinancingSlot = slot<ReportProjectPartnerExpenditureCoFinancingEntity>()
        every { legalStatusRepository.getById(legalStatusEntity.id) } returns legalStatusEntity
        every { partnerReportRepository.save(capture(reportSlot)) } returnsArgument 0
        every { programmeFundRepository.getById(programmeFundEntity.id) } returns programmeFundEntity
        every { partnerReportCoFinancingRepository.saveAll(capture(reportCoFinancingSlot)) } returnsArgument 0
        every { reportProjectPartnerExpenditureCoFinancingRepository.save(capture(reportExpenditureCoFinancingSlot)) } returnsArgument 0

        // work plan
        val wpSlot = slot<ProjectPartnerReportWorkPackageEntity>()
        val wpActivitySlot = slot<ProjectPartnerReportWorkPackageActivityEntity>()
        val wpActivityDeliverableSlot = slot<Iterable<ProjectPartnerReportWorkPackageActivityDeliverableEntity>>()
        val wpOutputSlot = slot<Iterable<ProjectPartnerReportWorkPackageOutputEntity>>()
        every { workPlanRepository.save(capture(wpSlot)) } returnsArgument 0
        every { workPlanActivityRepository.save(capture(wpActivitySlot)) } returnsArgument 0
        every { workPlanActivityDeliverableRepository.saveAll(capture(wpActivityDeliverableSlot)) } returnsArgument 0
        every { workPlanOutputRepository.saveAll(capture(wpOutputSlot)) } returnsArgument 0

        // identification
        val idSlot = slot<ProjectPartnerReportIdentificationEntity>()
        val idTargetGroupsSlot = slot<Iterable<ProjectPartnerReportIdentificationTargetGroupEntity>>()
        every { projectPartnerReportIdentificationRepository.save(capture(idSlot)) } returnsArgument 0
        every { projectPartnerReportIdentificationTargetGroupRepository.saveAll(capture(idTargetGroupsSlot)) } returnsArgument 0

        val contribSlot = slot<Iterable<ProjectPartnerReportContributionEntity>>()
        every { contributionRepository.saveAll(capture(contribSlot)) } returnsArgument 0

        // available lumpSums
        val lumpSumEntity = mockk<ProgrammeLumpSumEntity>()
        every { programmeLumpSumRepository.getById(85L) } returns lumpSumEntity
        val lumpSumSlot = slot<Iterable<PartnerReportLumpSumEntity>>()
        every { reportLumpSumRepository.saveAll(capture(lumpSumSlot)) } returnsArgument 0

        // available unitCosts
        val unitCostEntity = ProgrammeUnitCostEntity(
            id = 5L,
            projectId = null,
            isOneCostCategory = false,
            costPerUnit = ONE,
            costPerUnitForeignCurrency = ONE,
            foreignCurrencyCode = "RON",
            translatedValues = mutableSetOf(),
            categories = mutableSetOf()
        )
        every { programmeUnitCostRepository.getById(5L) } returns unitCostEntity
        val unitCostSlot = slot<Iterable<PartnerReportUnitCostEntity>>()
        every { reportUnitCostRepository.saveAll(capture(unitCostSlot)) } returnsArgument 0

        // available investments
        val investmentSlot = slot<Iterable<PartnerReportInvestmentEntity>>()
        every { reportInvestmentRepository.saveAll(capture(investmentSlot)) } returnsArgument 0

        // budget per period
        val budgetPerPeriodSlot = slot<Iterable<ProjectPartnerReportBudgetPerPeriodEntity>>()
        every { reportBudgetPerPeriodRepository.saveAll(capture(budgetPerPeriodSlot)) } returnsArgument 0

        // expenditureSetup
        val expenditureSlot = slot<ReportProjectPartnerExpenditureCostCategoryEntity>()
        every { reportBudgetExpenditureRepository.save(capture(expenditureSlot)) } returnsArgument 0

        val createdReport = persistence.createPartnerReport(
            reportToBeCreated.copy(
                identification = reportToBeCreated.identification.removeLegalStatusIf(withoutLegalStatus)
            )
        )

        assertThat(createdReport.createdAt).isNotNull
        assertThat(createdReport.reportNumber).isEqualTo(reportToBeCreated.baseData.reportNumber)
        assertThat(createdReport.status).isEqualTo(ReportStatus.Draft)
        assertThat(createdReport.version).isEqualTo(reportToBeCreated.baseData.version)
        assertThat(createdReport.firstSubmission).isNull()

        with(reportSlot.captured) {
            assertThat(partnerId).isEqualTo(PARTNER_ID)
            assertThat(number).isEqualTo(reportToBeCreated.baseData.reportNumber)
            assertThat(status).isEqualTo(ReportStatus.Draft)
            assertThat(applicationFormVersion).isEqualTo(reportToBeCreated.baseData.version)
            assertThat(firstSubmission).isNull()
        }
        with(reportSlot.captured.identification) {
            assertThat(projectIdentifier).isEqualTo("projectIdentifier")
            assertThat(projectAcronym).isEqualTo("projectAcronym")
            assertThat(partnerNumber).isEqualTo(4)
            assertThat(partnerAbbreviation).isEqualTo("partnerAbbreviation")
            assertThat(partnerRole).isEqualTo(ProjectPartnerRole.PARTNER)
            assertThat(nameInOriginalLanguage).isEqualTo("nameInOriginalLanguage")
            assertThat(nameInEnglish).isEqualTo("nameInEnglish")
            if (withoutLegalStatus)
                assertThat(legalStatus).isNull()
            else
                assertThat(legalStatus!!.equals(legalStatusEntity)).isTrue
            assertThat(partnerType).isEqualTo(ProjectTargetGroup.SectoralAgency)
            assertThat(country).isEqualTo("Österreich (AT)")
            assertThat(currency).isEqualTo("EUR")
            assertThat(vatRecovery).isEqualTo(ProjectPartnerVatRecovery.Yes)
        }

        assertThat(reportCoFinancingSlot.captured).hasSize(2)
        with(reportCoFinancingSlot.captured.find { it.id.fundSortNumber == 1 }!!) {
            assertThat(programmeFund!!.equals(programmeFundEntity)).isTrue
            assertThat(percentage).isEqualByComparingTo(valueOf(10L))
            assertThat(previouslyPaid).isEqualByComparingTo(valueOf(35L))
        }
        with(reportCoFinancingSlot.captured.find { it.id.fundSortNumber == 2 }!!) {
            assertThat(programmeFund).isNull()
            assertThat(percentage).isEqualTo(valueOf(90L))
            assertThat(previouslyPaid).isEqualByComparingTo(valueOf(410L))
        }

        assertThat(investmentSlot.captured).hasSize(1)
        with(investmentSlot.captured.find { it.investmentId == 245L }!!) {
            assertThat(investmentNumber).isEqualTo(4)
            assertThat(workPackageNumber).isEqualTo(7)
            assertThat(total).isEqualTo(valueOf(100L))
            assertThat(previouslyReported).isEqualByComparingTo(valueOf(50L))
        }

        assertExpenditureCoFinancing(reportExpenditureCoFinancingSlot)
        assertWorkPlan(wpSlot, wpActivitySlot, wpActivityDeliverableSlot, wpOutputSlot)
        assertIdentification(idSlot, idTargetGroupsSlot)
        assertContribution(contribSlot)
        assertLumpSums(lumpSumSlot)
        assertUnitCosts(unitCostSlot)
        assertBudgetPerPeriod(budgetPerPeriodSlot)
        assertExpenditure(expenditureSlot)
    }

    private fun PartnerReportIdentificationCreate.removeLegalStatusIf(needed: Boolean) =
        this.copy(legalStatusId = if (needed) null else this.legalStatusId)

    private fun assertExpenditureCoFinancing(
        expenditureCoFinancingSlot: CapturingSlot<ReportProjectPartnerExpenditureCoFinancingEntity>,
    ) {
        with(expenditureCoFinancingSlot.captured) {
            assertThat(partnerContributionTotal).isEqualByComparingTo(valueOf(900L))
            assertThat(publicContributionTotal).isEqualByComparingTo(valueOf(200L))
            assertThat(automaticPublicContributionTotal).isEqualByComparingTo(valueOf(300L))
            assertThat(privateContributionTotal).isEqualByComparingTo(valueOf(400L))
            assertThat(sumTotal).isEqualByComparingTo(valueOf(5000L))

            assertThat(partnerContributionCurrent).isEqualByComparingTo(ZERO)
            assertThat(publicContributionCurrent).isEqualByComparingTo(ZERO)
            assertThat(automaticPublicContributionCurrent).isEqualByComparingTo(ZERO)
            assertThat(privateContributionCurrent).isEqualByComparingTo(ZERO)
            assertThat(sumCurrent).isEqualByComparingTo(ZERO)

            assertThat(partnerContributionPreviouslyReported).isEqualByComparingTo(valueOf(400L))
            assertThat(publicContributionPreviouslyReported).isEqualByComparingTo(valueOf(100L))
            assertThat(automaticPublicContributionPreviouslyReported).isEqualByComparingTo(valueOf(130L))
            assertThat(privateContributionPreviouslyReported).isEqualByComparingTo(valueOf(170L))
            assertThat(sumPreviouslyReported).isEqualByComparingTo(valueOf(7500L))
        }
    }

    private fun assertWorkPlan(
        wpSlot: CapturingSlot<ProjectPartnerReportWorkPackageEntity>,
        wpActivitySlot: CapturingSlot<ProjectPartnerReportWorkPackageActivityEntity>,
        wpActivityDeliverableSlot: CapturingSlot<Iterable<ProjectPartnerReportWorkPackageActivityDeliverableEntity>>,
        wpOutputSlot: CapturingSlot<Iterable<ProjectPartnerReportWorkPackageOutputEntity>>,
    ) {
        with(wpSlot.captured) {
            assertThat(number).isEqualTo(4)
            assertThat(workPackageId).isEqualTo(WORK_PACKAGE_ID)
            assertThat(translatedValues).isEmpty()
        }
        with(wpActivitySlot.captured) {
            assertThat(number).isEqualTo(1)
            assertThat(activityId).isEqualTo(ACTIVITY_ID)
            assertThat(translatedValues).hasSize(1)
            assertThat(translatedValues.first().translationId.language).isEqualTo(io.cloudflight.jems.api.programme.dto.language.SystemLanguage.EN)
            assertThat(translatedValues.first().title).isEqualTo("4.1 activity title")
            assertThat(translatedValues.first().description).isNull()
        }
        assertThat(wpActivityDeliverableSlot.captured).hasSize(1)
        with(wpActivityDeliverableSlot.captured.first()) {
            assertThat(number).isEqualTo(1)
            assertThat(deliverableId).isEqualTo(DELIVERABLE_ID)
            assertThat(contribution).isNull()
            assertThat(evidence).isNull()
            assertThat(translatedValues).hasSize(1)
            assertThat(translatedValues.first().translationId.language).isEqualTo(io.cloudflight.jems.api.programme.dto.language.SystemLanguage.EN)
            assertThat(translatedValues.first().title).isEqualTo("4.1.1 title")
        }
        assertThat(wpOutputSlot.captured).hasSize(1)
        with(wpOutputSlot.captured.first()) {
            assertThat(number).isEqualTo(7)
            assertThat(contribution).isNull()
            assertThat(evidence).isNull()
            assertThat(translatedValues).hasSize(1)
            assertThat(translatedValues.first().translationId.language).isEqualTo(io.cloudflight.jems.api.programme.dto.language.SystemLanguage.EN)
            assertThat(translatedValues.first().title).isEqualTo("7 output title")
        }
    }

    private fun assertIdentification(
        idSlot: CapturingSlot<ProjectPartnerReportIdentificationEntity>,
        idTargetGroupsSlot: CapturingSlot<Iterable<ProjectPartnerReportIdentificationTargetGroupEntity>>,
    ) {
        with(idSlot.captured) {
            assertThat(startDate).isNull()
            assertThat(endDate).isNull()
            assertThat(periodNumber).isNull()
            assertThat(nextReportForecast).isEqualByComparingTo(ZERO)
            assertThat(formatOriginals).isFalse()
            assertThat(formatCopy).isFalse()
            assertThat(formatElectronic).isFalse()
            assertThat(type).isEqualTo(ReportType.PartnerReport)
            assertThat(translatedValues).isEmpty()
        }
        assertThat(idTargetGroupsSlot.captured).hasSize(3)
        with(idTargetGroupsSlot.captured.first { it.sortNumber == 1 }) {
            assertThat(translatedValues).hasSize(1)
            with(translatedValues.first()) {
                assertThat(translationId.language).isEqualTo(io.cloudflight.jems.api.programme.dto.language.SystemLanguage.EN)
                assertThat(specification).isEqualTo("first target group")
                assertThat(description).isNull()
            }
        }
        with(idTargetGroupsSlot.captured.first { it.sortNumber == 2 }) {
            assertThat(translatedValues).isEmpty()
        }
        with(idTargetGroupsSlot.captured.first { it.sortNumber == 3 }) {
            assertThat(translatedValues).hasSize(1)
            with(translatedValues.first()) {
                assertThat(translationId.language).isEqualTo(io.cloudflight.jems.api.programme.dto.language.SystemLanguage.EN)
                assertThat(specification).isEqualTo("third target group")
                assertThat(description).isNull()
            }
        }
    }

    private fun assertContribution(
        contribSlot: CapturingSlot<Iterable<ProjectPartnerReportContributionEntity>>,
    ) {
        assertThat(contribSlot.captured).hasSize(1)
        with(contribSlot.captured.first()) {
            assertThat(sourceOfContribution).isEqualTo("source text")
            assertThat(legalStatus).isEqualTo(ProjectPartnerContributionStatus.AutomaticPublic)
            assertThat(idFromApplicationForm).isEqualTo(4L)
            assertThat(historyIdentifier).isEqualTo(HISTORY_CONTRIBUTION_UUID)
            assertThat(createdInThisReport).isEqualTo(false)
            assertThat(amount).isEqualTo(ONE)
            assertThat(previouslyReported).isEqualTo(ONE)
            assertThat(currentlyReported).isEqualTo(ZERO)
            assertThat(attachment).isNull()
        }
    }

    private fun assertLumpSums(
        lumpSumSlot: CapturingSlot<Iterable<PartnerReportLumpSumEntity>>,
    ) {
        assertThat(lumpSumSlot.captured).hasSize(1)
        with(lumpSumSlot.captured.first()) {
            assertThat(programmeLumpSum).isNotNull
            assertThat(period).isEqualTo(0)
            assertThat(total).isEqualTo(ONE)
            assertThat(current).isEqualByComparingTo(ZERO)
            assertThat(previouslyReported).isEqualByComparingTo(valueOf(7, 1))
        }
    }

    private fun assertUnitCosts(
        unitCostSlot: CapturingSlot<Iterable<PartnerReportUnitCostEntity>>,
    ) {
        assertThat(unitCostSlot.captured).hasSize(1)
        with(unitCostSlot.captured.first()) {
            assertThat(programmeUnitCost).isNotNull
            assertThat(numberOfUnits).isEqualTo(ONE)
            assertThat(total).isEqualTo(ONE)
            assertThat(current).isEqualTo(ZERO)
            assertThat(previouslyReported).isEqualTo(valueOf(5, 1))
        }
    }

    private fun assertBudgetPerPeriod(
        budgetSlot: CapturingSlot<Iterable<ProjectPartnerReportBudgetPerPeriodEntity>>,
    ) {
        assertThat(budgetSlot.captured).hasSize(2)
        with(budgetSlot.captured.first()) {
            assertThat(id.periodNumber).isEqualTo(1)
            assertThat(periodBudget).isEqualByComparingTo(ONE)
            assertThat(periodBudgetCumulative).isEqualByComparingTo(ONE)
        }
        with(budgetSlot.captured.last()) {
            assertThat(id.periodNumber).isEqualTo(2)
            assertThat(periodBudget).isEqualByComparingTo(TEN)
            assertThat(periodBudgetCumulative).isEqualByComparingTo(valueOf(11))
        }
    }

    private fun assertExpenditure(expenditureSlot: CapturingSlot<ReportProjectPartnerExpenditureCostCategoryEntity>) {
        with(expenditureSlot.captured) {
            assertThat(officeAndAdministrationOnStaffCostsFlatRate).isNull()
            assertThat(officeAndAdministrationOnDirectCostsFlatRate).isNull()
            assertThat(travelAndAccommodationOnStaffCostsFlatRate).isNull()
            assertThat(staffCostsFlatRate).isNull()
            assertThat(otherCostsOnStaffCostsFlatRate).isEqualTo(40)

            assertThat(staffTotal).isEqualTo(valueOf(10))
            assertThat(officeTotal).isEqualTo(valueOf(11))
            assertThat(travelTotal).isEqualTo(valueOf(12))
            assertThat(externalTotal).isEqualTo(valueOf(13))
            assertThat(equipmentTotal).isEqualTo(valueOf(14))
            assertThat(infrastructureTotal).isEqualTo(valueOf(15))
            assertThat(otherTotal).isEqualTo(valueOf(16))
            assertThat(lumpSumTotal).isEqualTo(valueOf(17))
            assertThat(unitCostTotal).isEqualTo(valueOf(18))
            assertThat(sumTotal).isEqualTo(valueOf(19))

            assertThat(staffCurrent).isZero
            assertThat(officeCurrent).isZero
            assertThat(travelCurrent).isZero
            assertThat(externalCurrent).isZero
            assertThat(equipmentCurrent).isZero
            assertThat(infrastructureCurrent).isZero
            assertThat(otherCurrent).isZero
            assertThat(lumpSumCurrent).isZero
            assertThat(unitCostCurrent).isZero
            assertThat(sumCurrent).isZero

            assertThat(staffPreviouslyReported).isEqualTo(valueOf(30))
            assertThat(officePreviouslyReported).isEqualTo(valueOf(31))
            assertThat(travelPreviouslyReported).isEqualTo(valueOf(32))
            assertThat(externalPreviouslyReported).isEqualTo(valueOf(33))
            assertThat(equipmentPreviouslyReported).isEqualTo(valueOf(34))
            assertThat(infrastructurePreviouslyReported).isEqualTo(valueOf(35))
            assertThat(otherPreviouslyReported).isEqualTo(valueOf(36))
            assertThat(lumpSumPreviouslyReported).isEqualTo(valueOf(37))
            assertThat(unitCostPreviouslyReported).isEqualTo(valueOf(38))
            assertThat(sumPreviouslyReported).isEqualTo(valueOf(39))
        }
    }

}
