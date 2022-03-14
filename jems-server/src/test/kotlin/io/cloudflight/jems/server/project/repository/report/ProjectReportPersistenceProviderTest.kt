package io.cloudflight.jems.server.project.repository.report

import io.cloudflight.jems.api.programme.dto.language.SystemLanguage.EN
import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.api.project.dto.description.ProjectTargetGroupDTO
import io.cloudflight.jems.api.project.dto.partner.cofinancing.ProjectPartnerCoFinancingFundTypeDTO
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.programme.entity.fund.ProgrammeFundEntity
import io.cloudflight.jems.server.programme.entity.legalstatus.ProgrammeLegalStatusEntity
import io.cloudflight.jems.server.programme.repository.fund.ProgrammeFundRepository
import io.cloudflight.jems.server.programme.repository.legalstatus.ProgrammeLegalStatusRepository
import io.cloudflight.jems.server.programme.service.fund.model.ProgrammeFund
import io.cloudflight.jems.server.programme.service.fund.model.ProgrammeFundType
import io.cloudflight.jems.server.programme.service.legalstatus.model.ProgrammeLegalStatus
import io.cloudflight.jems.server.programme.service.legalstatus.model.ProgrammeLegalStatusType
import io.cloudflight.jems.server.project.entity.report.PartnerReportIdentificationEntity
import io.cloudflight.jems.server.project.entity.report.ProjectPartnerReportCoFinancingEntity
import io.cloudflight.jems.server.project.entity.report.ProjectPartnerReportCoFinancingIdEntity
import io.cloudflight.jems.server.project.entity.report.ProjectPartnerReportEntity
import io.cloudflight.jems.server.project.entity.report.expenditureCosts.PartnerReportExpenditureCostEntity
import io.cloudflight.jems.server.project.entity.report.identification.ProjectPartnerReportIdentificationEntity
import io.cloudflight.jems.server.project.entity.report.identification.ProjectPartnerReportIdentificationTargetGroupEntity
import io.cloudflight.jems.server.project.entity.report.workPlan.ProjectPartnerReportWorkPackageActivityDeliverableEntity
import io.cloudflight.jems.server.project.entity.report.workPlan.ProjectPartnerReportWorkPackageActivityEntity
import io.cloudflight.jems.server.project.entity.report.workPlan.ProjectPartnerReportWorkPackageEntity
import io.cloudflight.jems.server.project.entity.report.workPlan.ProjectPartnerReportWorkPackageOutputEntity
import io.cloudflight.jems.server.project.repository.report.expenditureCosts.PartnerReportExpenditureCostsRepository
import io.cloudflight.jems.server.project.repository.report.identification.ProjectPartnerReportIdentificationRepository
import io.cloudflight.jems.server.project.repository.report.identification.ProjectPartnerReportIdentificationTargetGroupRepository
import io.cloudflight.jems.server.project.repository.report.workPlan.ProjectPartnerReportWorkPackageActivityDeliverableRepository
import io.cloudflight.jems.server.project.repository.report.workPlan.ProjectPartnerReportWorkPackageActivityRepository
import io.cloudflight.jems.server.project.repository.report.workPlan.ProjectPartnerReportWorkPackageOutputRepository
import io.cloudflight.jems.server.project.repository.report.workPlan.ProjectPartnerReportWorkPackageRepository
import io.cloudflight.jems.server.project.service.model.ProjectRelevanceBenefit
import io.cloudflight.jems.server.project.service.model.ProjectTargetGroup
import io.cloudflight.jems.server.project.service.partner.cofinancing.model.ProjectPartnerCoFinancing
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerRole
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerVatRecovery
import io.cloudflight.jems.server.project.service.report.model.PartnerReportExpenditureCost
import io.cloudflight.jems.server.project.service.report.model.PartnerReportIdentification
import io.cloudflight.jems.server.project.service.report.model.PartnerReportIdentificationCreate
import io.cloudflight.jems.server.project.service.report.model.ProjectPartnerReport
import io.cloudflight.jems.server.project.service.report.model.ProjectPartnerReportCreate
import io.cloudflight.jems.server.project.service.report.model.ProjectPartnerReportStatusAndVersion
import io.cloudflight.jems.server.project.service.report.model.ProjectPartnerReportSubmissionSummary
import io.cloudflight.jems.server.project.service.report.model.ProjectPartnerReportSummary
import io.cloudflight.jems.server.project.service.report.model.ReportStatus
import io.cloudflight.jems.server.project.service.report.model.workPlan.create.CreateProjectPartnerReportWorkPackage
import io.cloudflight.jems.server.project.service.report.model.workPlan.create.CreateProjectPartnerReportWorkPackageActivity
import io.cloudflight.jems.server.project.service.report.model.workPlan.create.CreateProjectPartnerReportWorkPackageActivityDeliverable
import io.cloudflight.jems.server.project.service.report.model.workPlan.create.CreateProjectPartnerReportWorkPackageOutput
import io.mockk.CapturingSlot
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.slot
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import java.math.BigDecimal
import java.math.BigDecimal.ONE
import java.math.BigDecimal.TEN
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.*

class ProjectReportPersistenceProviderTest : UnitTest() {

    companion object {
        private const val PARTNER_ID = 10L
        private const val REPORT_ID = 10L

        private const val WORK_PACKAGE_ID = 9658L
        private const val ACTIVITY_ID = 9942L
        private const val DELIVERABLE_ID = 9225L

        private fun draftReportEntity(id: Long, createdAt: ZonedDateTime = ZonedDateTime.now()) = ProjectPartnerReportEntity(
            id = id,
            partnerId = PARTNER_ID,
            number = 1,
            status = ReportStatus.Draft,
            applicationFormVersion = "3.0",
            firstSubmission = null,
            identification = PartnerReportIdentificationEntity(
                projectIdentifier = "projectIdentifier",
                projectAcronym = "projectAcronym",
                partnerNumber = 4,
                partnerAbbreviation = "partnerAbbreviation",
                partnerRole = ProjectPartnerRole.PARTNER,
                nameInOriginalLanguage = "nameInOriginalLanguage",
                nameInEnglish = "nameInEnglish",
                legalStatus = legalStatusEntity,
                partnerType = ProjectTargetGroup.SectoralAgency,
                vatRecovery = ProjectPartnerVatRecovery.Yes,
            ),
            createdAt = createdAt,
            expenditureCosts = mutableSetOf(reportExpenditureCostEntity)
        )

        private fun draftReportSubmissionEntity(id: Long, createdAt: ZonedDateTime = ZonedDateTime.now()) = ProjectPartnerReportSubmissionSummary(
            id = id,
            reportNumber = 1,
            status = ReportStatus.Draft,
            version = "3.0",
            firstSubmission = null,
            createdAt = createdAt,
            projectIdentifier = "projectIdentifier",
            projectAcronym = "projectAcronym",
            partnerNumber = 4,
            partnerRole = ProjectPartnerRole.PARTNER,
        )

        private val reportExpenditureCost = PartnerReportExpenditureCost(
            id = 754,
            costCategory = "costCategory",
            investmentNumber = "number-1",
            contractId = "",
            internalReferenceNumber = "internal-1",
            invoiceNumber = "invoice-1",
            invoiceDate = LocalDate.of(2022, 1, 1),
            dateOfPayment = LocalDate.of(2022, 2, 1),
            description = emptySet(),
            comment = emptySet(),
            totalValueInvoice = BigDecimal.valueOf(22),
            vat = BigDecimal.valueOf(18.0),
            declaredAmount = BigDecimal.valueOf(1.3)
        )

        private val updatedReportExpenditureCost = PartnerReportExpenditureCost(
            id = 754,
            costCategory = "costCategory-updated",
            investmentNumber = "number-1-updated",
            contractId = "",
            internalReferenceNumber = "internal-1-updated",
            invoiceNumber = "invoice-1-updated",
            invoiceDate = LocalDate.of(2021, 1, 1),
            dateOfPayment = LocalDate.of(2021, 2, 1),
            description = emptySet(),
            comment = emptySet(),
            totalValueInvoice = BigDecimal.valueOf(21),
            vat = BigDecimal.valueOf(18.2),
            declaredAmount = BigDecimal.valueOf(1.2)
        )

        private val newReportExpenditureCost = PartnerReportExpenditureCost(
            id = null,
            costCategory = "costCategory-2",
            investmentNumber = "number-2",
            contractId = "",
            internalReferenceNumber = "internal-2",
            invoiceNumber = "invoice-2",
            invoiceDate = LocalDate.of(2020, 1, 1),
            dateOfPayment = LocalDate.of(2020, 2, 1),
            description = emptySet(),
            comment = emptySet(),
            totalValueInvoice = BigDecimal.valueOf(21),
            vat = BigDecimal.valueOf(10.0),
            declaredAmount = BigDecimal.valueOf(1.5)
        )

        private val reportExpenditureCostEntity = PartnerReportExpenditureCostEntity(
            id = 754,
            costCategory = "costCategory",
            investmentNumber = "number-1",
            contractId = "",
            internalReferenceNumber = "internal-1",
            invoiceNumber = "invoice-1",
            invoiceDate = LocalDate.of(2022, 1, 1).atStartOfDay(ZoneId.systemDefault()).toInstant(),
            dateOfPayment = LocalDate.of(2022, 2, 1).atStartOfDay(ZoneId.systemDefault()).toInstant(),
            translatedValues = mutableSetOf(),
            totalValueInvoice = BigDecimal.valueOf(22),
            vat = BigDecimal.valueOf(18.0),
            declaredAmount = BigDecimal.valueOf(1.3),
            partnerReport = null
        )

        private val updatedReportExpenditureCostEntity = PartnerReportExpenditureCostEntity(
            id = 754,
            costCategory = "costCategory-updated",
            investmentNumber = "number-1-updated",
            contractId = "",
            internalReferenceNumber = "internal-1-updated",
            invoiceNumber = "invoice-1-updated",
            invoiceDate = LocalDate.of(2021, 1, 1).atStartOfDay(ZoneId.systemDefault()).toInstant(),
            dateOfPayment = LocalDate.of(2021, 2, 1).atStartOfDay(ZoneId.systemDefault()).toInstant(),
            translatedValues = mutableSetOf(),
            totalValueInvoice = BigDecimal.valueOf(21),
            vat = BigDecimal.valueOf(18.2),
            declaredAmount = BigDecimal.valueOf(1.2),
            partnerReport = null
        )

        private val newReportExpenditureCostEntity = PartnerReportExpenditureCostEntity(
            id = 754,
            costCategory = "costCategory-2",
            investmentNumber = "number-2",
            contractId = "",
            internalReferenceNumber = "internal-2",
            invoiceNumber = "invoice-2",
            invoiceDate = LocalDate.of(2020, 1, 1).atStartOfDay(ZoneId.systemDefault()).toInstant(),
            dateOfPayment = LocalDate.of(2020, 2, 1).atStartOfDay(ZoneId.systemDefault()).toInstant(),
            translatedValues = mutableSetOf(),
            totalValueInvoice = BigDecimal.valueOf(21),
            vat = BigDecimal.valueOf(10.0),
            declaredAmount = BigDecimal.valueOf(1.5),
            partnerReport = null
        )

        private val programmeFundEntity = ProgrammeFundEntity(
            id = 1L,
            selected = true,
            type = ProgrammeFundType.ERDF,
        )

        private val programmeFund = ProgrammeFund(
            id = programmeFundEntity.id,
            selected = programmeFundEntity.selected,
            type = programmeFundEntity.type,
        )

        private val legalStatusEntity = ProgrammeLegalStatusEntity(
            id = 650L,
            type = ProgrammeLegalStatusType.PRIVATE,
        )

        private val legalStatus = ProgrammeLegalStatus(
            id = legalStatusEntity.id,
            type = legalStatusEntity.type,
        )

        private fun draftReport(id: Long, coFinancing: List<ProjectPartnerCoFinancing>) = ProjectPartnerReport(
            id = id,
            reportNumber = 1,
            status = ReportStatus.Draft,
            version = "3.0",
            identification = PartnerReportIdentification(
                projectIdentifier = "projectIdentifier",
                projectAcronym = "projectAcronym",
                partnerNumber = 4,
                partnerAbbreviation = "partnerAbbreviation",
                partnerRole = ProjectPartnerRole.PARTNER,
                nameInOriginalLanguage = "nameInOriginalLanguage",
                nameInEnglish = "nameInEnglish",
                legalStatus = legalStatus,
                partnerType = ProjectTargetGroup.SectoralAgency,
                vatRecovery = ProjectPartnerVatRecovery.Yes,
                coFinancing = coFinancing,
            )
        )

        private fun draftReportSummary(id: Long, createdAt: ZonedDateTime) = ProjectPartnerReportSummary(
            id = id,
            reportNumber = 1,
            status = ReportStatus.Draft,
            version = "3.0",
            firstSubmission = null,
            createdAt = createdAt,
        )

        private fun coFinancingEntities(report: ProjectPartnerReportEntity) = listOf(
            ProjectPartnerReportCoFinancingEntity(
                ProjectPartnerReportCoFinancingIdEntity(report = report, fundSortNumber = 1),
                programmeFund = programmeFundEntity,
                percentage = ONE,
            ),
            ProjectPartnerReportCoFinancingEntity(
                ProjectPartnerReportCoFinancingIdEntity(report = report, fundSortNumber = 1),
                programmeFund = null,
                percentage = TEN,
            ),
        )

        private val coFinancing = listOf(
            ProjectPartnerCoFinancing(
                fundType = ProjectPartnerCoFinancingFundTypeDTO.MainFund,
                fund = programmeFund,
                percentage = ONE,
            ),
            ProjectPartnerCoFinancing(
                fundType = ProjectPartnerCoFinancingFundTypeDTO.PartnerContribution,
                fund = null,
                percentage = TEN,
            ),
        )

        private val reportToBeCreated = ProjectPartnerReportCreate(
            partnerId = PARTNER_ID,
            reportNumber = 1,
            status = ReportStatus.Draft,
            version = "6.5",
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
                coFinancing = coFinancing,
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
        )
    }

    @MockK
    lateinit var partnerReportRepository: ProjectPartnerReportRepository

    @MockK
    lateinit var partnerReportCoFinancingRepository: ProjectPartnerReportCoFinancingRepository

    @MockK
    lateinit var legalStatusRepository: ProgrammeLegalStatusRepository

    @MockK
    lateinit var programmeFundRepository: ProgrammeFundRepository

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
    lateinit var partnerReportExpenditureCostsRepository: PartnerReportExpenditureCostsRepository

    @MockK
    lateinit var projectPartnerReportIdentificationTargetGroupRepository: ProjectPartnerReportIdentificationTargetGroupRepository

    @InjectMockKs
    lateinit var persistence: ProjectReportPersistenceProvider

    @ParameterizedTest(name = "createPartnerReport, without legal status {0}")
    @ValueSource(booleans = [true, false])
    fun createPartnerReport(withoutLegalStatus: Boolean) {
        val reportSlot = slot<ProjectPartnerReportEntity>()
        val reportCoFinancingSlot = slot<Iterable<ProjectPartnerReportCoFinancingEntity>>()
        every { legalStatusRepository.getById(legalStatusEntity.id) } returns legalStatusEntity
        every { partnerReportRepository.save(capture(reportSlot)) } returnsArgument 0
        every { programmeFundRepository.getById(programmeFundEntity.id) } returns programmeFundEntity
        every { partnerReportCoFinancingRepository.saveAll(capture(reportCoFinancingSlot)) } returnsArgument 0

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

        val createdReport = persistence.createPartnerReport(reportToBeCreated.copy(
            identification = reportToBeCreated.identification.removeLegalStatusIf(withoutLegalStatus)
        ))

        assertThat(createdReport.createdAt).isNotNull
        assertThat(createdReport.reportNumber).isEqualTo(reportToBeCreated.reportNumber)
        assertThat(createdReport.status).isEqualTo(ReportStatus.Draft)
        assertThat(createdReport.version).isEqualTo(reportToBeCreated.version)
        assertThat(createdReport.firstSubmission).isNull()

        with(reportSlot.captured) {
            assertThat(partnerId).isEqualTo(PARTNER_ID)
            assertThat(number).isEqualTo(reportToBeCreated.reportNumber)
            assertThat(status).isEqualTo(ReportStatus.Draft)
            assertThat(applicationFormVersion).isEqualTo(reportToBeCreated.version)
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
            assertThat(vatRecovery).isEqualTo(ProjectPartnerVatRecovery.Yes)
        }

        assertThat(reportCoFinancingSlot.captured).hasSize(2)
        with(reportCoFinancingSlot.captured.find { it.id.fundSortNumber == 1 }!!) {
            assertThat(programmeFund!!.equals(programmeFundEntity)).isTrue
            assertThat(percentage).isEqualTo(ONE)
        }
        with(reportCoFinancingSlot.captured.find { it.id.fundSortNumber == 2 }!!) {
            assertThat(programmeFund).isNull()
            assertThat(percentage).isEqualTo(TEN)
        }

        // work plan
        assertWorkPlan(wpSlot, wpActivitySlot, wpActivityDeliverableSlot, wpOutputSlot)
        assertIdentification(idSlot, idTargetGroupsSlot)
    }

    private fun PartnerReportIdentificationCreate.removeLegalStatusIf(needed: Boolean) =
        this.copy(legalStatusId = if (needed) null else this.legalStatusId)

    private fun assertWorkPlan(
        wpSlot: CapturingSlot<ProjectPartnerReportWorkPackageEntity>,
        wpActivitySlot: CapturingSlot<ProjectPartnerReportWorkPackageActivityEntity>,
        wpActivityDeliverableSlot: CapturingSlot<Iterable<ProjectPartnerReportWorkPackageActivityDeliverableEntity>>,
        wpOutputSlot: CapturingSlot<Iterable<ProjectPartnerReportWorkPackageOutputEntity>>,
    ) {
        with(wpSlot.captured) {
            assertThat(number).isEqualTo(4)
            assertThat(workPackageId).isEqualTo(io.cloudflight.jems.server.project.repository.report.ProjectReportPersistenceProviderTest.WORK_PACKAGE_ID)
            assertThat(translatedValues).isEmpty()
        }
        with(wpActivitySlot.captured) {
            assertThat(number).isEqualTo(1)
            assertThat(activityId).isEqualTo(io.cloudflight.jems.server.project.repository.report.ProjectReportPersistenceProviderTest.ACTIVITY_ID)
            assertThat(translatedValues).hasSize(1)
            assertThat(translatedValues.first().translationId.language).isEqualTo(io.cloudflight.jems.api.programme.dto.language.SystemLanguage.EN)
            assertThat(translatedValues.first().title).isEqualTo("4.1 activity title")
            assertThat(translatedValues.first().description).isNull()
        }
        assertThat(wpActivityDeliverableSlot.captured).hasSize(1)
        with(wpActivityDeliverableSlot.captured.first()) {
            assertThat(number).isEqualTo(1)
            assertThat(deliverableId).isEqualTo(io.cloudflight.jems.server.project.repository.report.ProjectReportPersistenceProviderTest.DELIVERABLE_ID)
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

    @Test
    fun submitReportById() {
        val NOW = ZonedDateTime.now()
        val YESTERDAY = ZonedDateTime.now().minusDays(1)

        val report = draftReportEntity(id = 45L, YESTERDAY)
        every { partnerReportRepository.findByIdAndPartnerId(45L, 10L) } returns report

        val submittedReport = persistence.submitReportById(10L, 45L, NOW)

        assertThat(submittedReport).isEqualTo(
            draftReportSubmissionEntity(id = 45L, YESTERDAY).copy(
                status = ReportStatus.Submitted,
                firstSubmission = NOW,
            )
        )
    }

    @Test
    fun getPartnerReportStatusById() {
        val report = draftReportEntity(id = 75L)
        every { partnerReportRepository.findByIdAndPartnerId(75L, 20L) } returns report
        assertThat(persistence.getPartnerReportStatusAndVersion(partnerId = 20L, reportId = 75L))
            .isEqualTo(ProjectPartnerReportStatusAndVersion(ReportStatus.Draft, "3.0"))
    }

    @Test
    fun getPartnerReportById() {
        val report = draftReportEntity(id = 35L)
        every { partnerReportRepository.findByIdAndPartnerId(35L, 10L) } returns report
        every { partnerReportCoFinancingRepository.findAllByIdReportIdOrderByIdFundSortNumber(35L) } returns
            coFinancingEntities(report)

        assertThat(persistence.getPartnerReportById(partnerId = PARTNER_ID, reportId = 35L))
            .isEqualTo(draftReport(id = 35L, coFinancing = coFinancing))
    }

    @Test
    fun listPartnerReports() {
        val twoWeeksAgo = ZonedDateTime.now().minusDays(14)

        every { partnerReportRepository.findAllByPartnerId(PARTNER_ID, Pageable.unpaged()) } returns
            PageImpl(listOf(draftReportEntity(id = 18L, createdAt = twoWeeksAgo)))

        assertThat(persistence.listPartnerReports(PARTNER_ID, Pageable.unpaged()).content)
            .containsExactly(draftReportSummary(id = 18L, createdAt = twoWeeksAgo))
    }

    @Test
    fun getCurrentLatestReportNumberForPartner() {
        every { partnerReportRepository.getMaxNumberForPartner(PARTNER_ID) } returns 7
        assertThat(persistence.getCurrentLatestReportNumberForPartner(PARTNER_ID)).isEqualTo(7)
    }

    @Test
    fun updatePartnerReportExpenditureCosts() {
        val report = Optional.of(draftReportEntity(id = 1L))
        val updatedReport = report.get()
        updatedReport.expenditureCosts = mutableSetOf(updatedReportExpenditureCostEntity)
        every { partnerReportRepository.findById(REPORT_ID) } returns report
        assertThat(persistence.updatePartnerReportExpenditureCosts(REPORT_ID, listOf(updatedReportExpenditureCost)))
            .isEqualTo(updatedReport)
    }

    @Test
    fun getPartnerReportExpenditureCosts() {
        val expenditureCosts = mutableListOf(updatedReportExpenditureCostEntity)
        every { partnerReportExpenditureCostsRepository.findAllByPartnerReportIdOrderById(PARTNER_ID) } returns expenditureCosts
        assertThat(persistence.getPartnerReportExpenditureCosts(REPORT_ID))
            .containsExactly(updatedReportExpenditureCostEntity)
    }

    @Test
    fun updatePartnerReportExpenditureCostsWithNew() {
        val report = Optional.of(draftReportEntity(id = 1L))
        val updatedReport = report.get()
        updatedReport.expenditureCosts =
            mutableSetOf(updatedReportExpenditureCostEntity, newReportExpenditureCostEntity)
        every { partnerReportRepository.findById(PARTNER_ID) } returns report
        assertThat(
            persistence.updatePartnerReportExpenditureCosts(
                PARTNER_ID,
                listOf(reportExpenditureCost, newReportExpenditureCost)
            )
        )
            .isEqualTo(updatedReport)
    }

    @Test
    fun countForPartner() {
        every { partnerReportRepository.countAllByPartnerId(PARTNER_ID) } returns 24
        assertThat(persistence.countForPartner(PARTNER_ID)).isEqualTo(24)
    }
}
