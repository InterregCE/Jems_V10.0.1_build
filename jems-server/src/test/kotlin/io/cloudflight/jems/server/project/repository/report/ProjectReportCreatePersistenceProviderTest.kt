package io.cloudflight.jems.server.project.repository.report

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
import io.cloudflight.jems.server.project.entity.report.ProjectPartnerReportCoFinancingEntity
import io.cloudflight.jems.server.project.entity.report.ProjectPartnerReportEntity
import io.cloudflight.jems.server.project.entity.report.contribution.ProjectPartnerReportContributionEntity
import io.cloudflight.jems.server.project.entity.report.expenditure.PartnerReportLumpSumEntity
import io.cloudflight.jems.server.project.entity.report.expenditure.PartnerReportUnitCostEntity
import io.cloudflight.jems.server.project.entity.report.financialOverview.ReportProjectPartnerExpenditureCoFinancingEntity
import io.cloudflight.jems.server.project.entity.report.financialOverview.ReportProjectPartnerExpenditureCostCategoryEntity
import io.cloudflight.jems.server.project.entity.report.identification.ProjectPartnerReportBudgetPerPeriodEntity
import io.cloudflight.jems.server.project.entity.report.identification.ProjectPartnerReportIdentificationEntity
import io.cloudflight.jems.server.project.entity.report.identification.ProjectPartnerReportIdentificationTargetGroupEntity
import io.cloudflight.jems.server.project.entity.report.workPlan.ProjectPartnerReportWorkPackageActivityDeliverableEntity
import io.cloudflight.jems.server.project.entity.report.workPlan.ProjectPartnerReportWorkPackageActivityEntity
import io.cloudflight.jems.server.project.entity.report.workPlan.ProjectPartnerReportWorkPackageEntity
import io.cloudflight.jems.server.project.entity.report.workPlan.ProjectPartnerReportWorkPackageOutputEntity
import io.cloudflight.jems.server.project.repository.report.contribution.ProjectPartnerReportContributionRepository
import io.cloudflight.jems.server.project.repository.report.expenditure.ProjectPartnerReportLumpSumRepository
import io.cloudflight.jems.server.project.repository.report.expenditure.ProjectPartnerReportUnitCostRepository
import io.cloudflight.jems.server.project.repository.report.financialOverview.coFinancing.ReportProjectPartnerExpenditureCoFinancingRepository
import io.cloudflight.jems.server.project.repository.report.financialOverview.costCategory.ReportProjectPartnerExpenditureCostCategoryRepository
import io.cloudflight.jems.server.project.repository.report.identification.ProjectPartnerReportBudgetPerPeriodRepository
import io.cloudflight.jems.server.project.repository.report.identification.ProjectPartnerReportIdentificationRepository
import io.cloudflight.jems.server.project.repository.report.identification.ProjectPartnerReportIdentificationTargetGroupRepository
import io.cloudflight.jems.server.project.repository.report.workPlan.ProjectPartnerReportWorkPackageActivityDeliverableRepository
import io.cloudflight.jems.server.project.repository.report.workPlan.ProjectPartnerReportWorkPackageActivityRepository
import io.cloudflight.jems.server.project.repository.report.workPlan.ProjectPartnerReportWorkPackageOutputRepository
import io.cloudflight.jems.server.project.repository.report.workPlan.ProjectPartnerReportWorkPackageRepository
import io.cloudflight.jems.server.project.service.budget.model.BudgetCostsCalculationResultFull
import io.cloudflight.jems.server.project.service.model.ProjectRelevanceBenefit
import io.cloudflight.jems.server.project.service.model.ProjectTargetGroup
import io.cloudflight.jems.server.project.service.partner.cofinancing.model.ProjectPartnerContributionStatus
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerBudgetOptions
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerRole
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerVatRecovery
import io.cloudflight.jems.server.project.service.report.model.ReportStatus
import io.cloudflight.jems.server.project.service.report.model.contribution.create.CreateProjectPartnerReportContribution
import io.cloudflight.jems.server.project.service.report.model.create.*
import io.cloudflight.jems.server.project.service.report.model.financialOverview.costCategory.ReportExpenditureCostCategory
import io.cloudflight.jems.server.project.service.report.model.identification.ProjectPartnerReportPeriod
import io.cloudflight.jems.server.project.service.report.model.identification.control.ReportType
import io.cloudflight.jems.server.project.service.report.model.workPlan.create.CreateProjectPartnerReportWorkPackage
import io.cloudflight.jems.server.project.service.report.model.workPlan.create.CreateProjectPartnerReportWorkPackageActivity
import io.cloudflight.jems.server.project.service.report.model.workPlan.create.CreateProjectPartnerReportWorkPackageActivityDeliverable
import io.cloudflight.jems.server.project.service.report.model.workPlan.create.CreateProjectPartnerReportWorkPackageOutput
import io.mockk.CapturingSlot
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.slot
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import java.math.BigDecimal
import java.math.BigDecimal.ONE
import java.math.BigDecimal.TEN
import java.math.BigDecimal.ZERO
import java.util.UUID

class ProjectReportCreatePersistenceProviderTest : UnitTest() {

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
                    activities = listOf(
                        CreateProjectPartnerReportWorkPackageActivity(
                            activityId = ACTIVITY_ID,
                            number = 1,
                            title = setOf(InputTranslation(EN, "4.1 activity title")),
                            deliverables = listOf(
                                CreateProjectPartnerReportWorkPackageActivityDeliverable(
                                    deliverableId = DELIVERABLE_ID,
                                    number = 1,
                                    title = setOf(InputTranslation(EN, "4.1.1 title")),
                                )
                            ),
                        )
                    ),
                    outputs = listOf(
                        CreateProjectPartnerReportWorkPackageOutput(
                            number = 7,
                            title = setOf(InputTranslation(EN, "7 output title")),
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
                lumpSums = listOf(
                    PartnerReportLumpSum(
                        lumpSumId = 85L,
                        period = 0,
                        value = ONE
                    ),
                ),
                unitCosts = setOf(PartnerReportUnitCostBase(
                    unitCostId = 5L,
                    totalCost = ONE,
                    numberOfUnits = ONE
                )),
                budgetPerPeriod = listOf(
                    ProjectPartnerReportPeriod(1, ONE, ONE, 1, 3),
                    ProjectPartnerReportPeriod(2, TEN, BigDecimal.valueOf(11L), 4, 6),
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
                        staff = BigDecimal.valueOf(10),
                        office = BigDecimal.valueOf(11),
                        travel = BigDecimal.valueOf(12),
                        external = BigDecimal.valueOf(13),
                        equipment = BigDecimal.valueOf(14),
                        infrastructure = BigDecimal.valueOf(15),
                        other = BigDecimal.valueOf(16),
                        lumpSum = BigDecimal.valueOf(17),
                        unitCost = BigDecimal.valueOf(18),
                        sum = BigDecimal.valueOf(19),
                    ),
                    currentlyReported = BudgetCostsCalculationResultFull(
                        staff = BigDecimal.valueOf(20),
                        office = BigDecimal.valueOf(21),
                        travel = BigDecimal.valueOf(22),
                        external = BigDecimal.valueOf(23),
                        equipment = BigDecimal.valueOf(24),
                        infrastructure = BigDecimal.valueOf(25),
                        other = BigDecimal.valueOf(26),
                        lumpSum = BigDecimal.valueOf(27),
                        unitCost = BigDecimal.valueOf(28),
                        sum = BigDecimal.valueOf(29),
                    ),
                    previouslyReported = BudgetCostsCalculationResultFull(
                        staff = BigDecimal.valueOf(30),
                        office = BigDecimal.valueOf(31),
                        travel = BigDecimal.valueOf(32),
                        external = BigDecimal.valueOf(33),
                        equipment = BigDecimal.valueOf(34),
                        infrastructure = BigDecimal.valueOf(35),
                        other = BigDecimal.valueOf(36),
                        lumpSum = BigDecimal.valueOf(37),
                        unitCost = BigDecimal.valueOf(38),
                        sum = BigDecimal.valueOf(39),
                    ),
                ),
                previouslyReportedCoFinancing = PreviouslyReportedCoFinancing(
                    fundsSorted = listOf(
                        PreviouslyReportedFund(fundId = programmeFundEntity.id, percentage = TEN,
                            total = BigDecimal.valueOf(100L), previouslyReported = BigDecimal.valueOf(25)),
                        PreviouslyReportedFund(fundId = null, percentage = BigDecimal.valueOf(90),
                            total = BigDecimal.valueOf(900L), previouslyReported = BigDecimal.valueOf(400)),
                    ),
                    totalPartner = BigDecimal.valueOf(900L),
                    totalPublic = BigDecimal.valueOf(200L),
                    totalAutoPublic = BigDecimal.valueOf(300L),
                    totalPrivate = BigDecimal.valueOf(400L),
                    totalSum = BigDecimal.valueOf(5000L),

                    previouslyReportedPartner = BigDecimal.valueOf(400L),
                    previouslyReportedPublic = BigDecimal.valueOf(100L),
                    previouslyReportedAutoPublic = BigDecimal.valueOf(130L),
                    previouslyReportedPrivate = BigDecimal.valueOf(170L),
                    previouslyReportedSum = BigDecimal.valueOf(7500L),
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
    lateinit var reportBudgetPerPeriodRepository: ProjectPartnerReportBudgetPerPeriodRepository

    @MockK
    lateinit var reportBudgetExpenditureRepository: ReportProjectPartnerExpenditureCostCategoryRepository

    @InjectMockKs
    lateinit var persistence: ProjectReportCreatePersistenceProvider

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

        // budget per period
        val budgetPerPeriodSlot = slot<Iterable<ProjectPartnerReportBudgetPerPeriodEntity>>()
        every { reportBudgetPerPeriodRepository.saveAll(capture(budgetPerPeriodSlot)) } returnsArgument 0

        // expenditureSetup
        val expenditureSlot = slot<ReportProjectPartnerExpenditureCostCategoryEntity>()
        every { reportBudgetExpenditureRepository.save(capture(expenditureSlot)) } returnsArgument 0

        val createdReport = persistence.createPartnerReport(reportToBeCreated.copy(
            identification = reportToBeCreated.identification.removeLegalStatusIf(withoutLegalStatus)
        ))

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
            assertThat(percentage).isEqualByComparingTo(BigDecimal.valueOf(10L))
        }
        with(reportCoFinancingSlot.captured.find { it.id.fundSortNumber == 2 }!!) {
            assertThat(programmeFund).isNull()
            assertThat(percentage).isEqualTo(BigDecimal.valueOf(90L))
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
            assertThat(partnerContributionTotal).isEqualByComparingTo(BigDecimal.valueOf(900L))
            assertThat(publicContributionTotal).isEqualByComparingTo(BigDecimal.valueOf(200L))
            assertThat(automaticPublicContributionTotal).isEqualByComparingTo(BigDecimal.valueOf(300L))
            assertThat(privateContributionTotal).isEqualByComparingTo(BigDecimal.valueOf(400L))
            assertThat(sumTotal).isEqualByComparingTo(BigDecimal.valueOf(5000L))

            assertThat(partnerContributionCurrent).isEqualByComparingTo(ZERO)
            assertThat(publicContributionCurrent).isEqualByComparingTo(ZERO)
            assertThat(automaticPublicContributionCurrent).isEqualByComparingTo(ZERO)
            assertThat(privateContributionCurrent).isEqualByComparingTo(ZERO)
            assertThat(sumCurrent).isEqualByComparingTo(ZERO)

            assertThat(partnerContributionPreviouslyReported).isEqualByComparingTo(BigDecimal.valueOf(400L))
            assertThat(publicContributionPreviouslyReported).isEqualByComparingTo(BigDecimal.valueOf(100L))
            assertThat(automaticPublicContributionPreviouslyReported).isEqualByComparingTo(BigDecimal.valueOf(130L))
            assertThat(privateContributionPreviouslyReported).isEqualByComparingTo(BigDecimal.valueOf(170L))
            assertThat(sumPreviouslyReported).isEqualByComparingTo(BigDecimal.valueOf(7500L))
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
        with(idTargetGroupsSlot.captured.first { it.sortNumber == 1}) {
            assertThat(translatedValues).hasSize(1)
            with(translatedValues.first()) {
                assertThat(translationId.language).isEqualTo(io.cloudflight.jems.api.programme.dto.language.SystemLanguage.EN)
                assertThat(specification).isEqualTo("first target group")
                assertThat(description).isNull()
            }
        }
        with(idTargetGroupsSlot.captured.first { it.sortNumber == 2}) {
            assertThat(translatedValues).isEmpty()
        }
        with(idTargetGroupsSlot.captured.first { it.sortNumber == 3}) {
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
            assertThat(cost).isEqualTo(ONE)
        }
    }

    private fun assertUnitCosts(
        unitCostSlot: CapturingSlot<Iterable<PartnerReportUnitCostEntity>>,
    ) {
        assertThat(unitCostSlot.captured).hasSize(1)
        with(unitCostSlot.captured.first()) {
            assertThat(programmeUnitCost).isNotNull
            assertThat(totalCost).isEqualTo(ONE)
            assertThat(numberOfUnits).isEqualTo(ONE)
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
            assertThat(periodBudgetCumulative).isEqualByComparingTo(BigDecimal.valueOf(11))
        }
    }

    private fun assertExpenditure(expenditureSlot: CapturingSlot<ReportProjectPartnerExpenditureCostCategoryEntity>) {
        with(expenditureSlot.captured) {
            assertThat(officeAndAdministrationOnStaffCostsFlatRate).isNull()
            assertThat(officeAndAdministrationOnDirectCostsFlatRate).isNull()
            assertThat(travelAndAccommodationOnStaffCostsFlatRate).isNull()
            assertThat(staffCostsFlatRate).isNull()
            assertThat(otherCostsOnStaffCostsFlatRate).isEqualTo(40)

            assertThat(staffTotal).isEqualTo(BigDecimal.valueOf(10))
            assertThat(officeTotal).isEqualTo(BigDecimal.valueOf(11))
            assertThat(travelTotal).isEqualTo(BigDecimal.valueOf(12))
            assertThat(externalTotal).isEqualTo(BigDecimal.valueOf(13))
            assertThat(equipmentTotal).isEqualTo(BigDecimal.valueOf(14))
            assertThat(infrastructureTotal).isEqualTo(BigDecimal.valueOf(15))
            assertThat(otherTotal).isEqualTo(BigDecimal.valueOf(16))
            assertThat(lumpSumTotal).isEqualTo(BigDecimal.valueOf(17))
            assertThat(unitCostTotal).isEqualTo(BigDecimal.valueOf(18))
            assertThat(sumTotal).isEqualTo(BigDecimal.valueOf(19))

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

            assertThat(staffPreviouslyReported).isEqualTo(BigDecimal.valueOf(30))
            assertThat(officePreviouslyReported).isEqualTo(BigDecimal.valueOf(31))
            assertThat(travelPreviouslyReported).isEqualTo(BigDecimal.valueOf(32))
            assertThat(externalPreviouslyReported).isEqualTo(BigDecimal.valueOf(33))
            assertThat(equipmentPreviouslyReported).isEqualTo(BigDecimal.valueOf(34))
            assertThat(infrastructurePreviouslyReported).isEqualTo(BigDecimal.valueOf(35))
            assertThat(otherPreviouslyReported).isEqualTo(BigDecimal.valueOf(36))
            assertThat(lumpSumPreviouslyReported).isEqualTo(BigDecimal.valueOf(37))
            assertThat(unitCostPreviouslyReported).isEqualTo(BigDecimal.valueOf(38))
            assertThat(sumPreviouslyReported).isEqualTo(BigDecimal.valueOf(39))
        }
    }

}
