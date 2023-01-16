package io.cloudflight.jems.server.project.service.report.partner.identification.control.updateProjectPartnerControlReportIdentification

import io.cloudflight.jems.api.programme.dto.language.SystemLanguage.EN
import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.programme.entity.ProgrammeDataEntity
import io.cloudflight.jems.server.programme.repository.ProgrammeDataRepository
import io.cloudflight.jems.server.project.service.ProjectPersistence
import io.cloudflight.jems.server.project.service.contracting.monitoring.getProjectContractingMonitoring.GetContractingMonitoringService
import io.cloudflight.jems.server.project.service.model.ProjectFull
import io.cloudflight.jems.server.project.service.partner.PartnerPersistence
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerRole
import io.cloudflight.jems.server.project.service.report.partner.ProjectPartnerReportPersistence
import io.cloudflight.jems.server.project.service.report.model.partner.PartnerReportIdentification
import io.cloudflight.jems.server.project.service.report.model.partner.ProjectPartnerReport
import io.cloudflight.jems.server.project.service.report.model.partner.ReportStatus
import io.cloudflight.jems.server.project.service.report.model.partner.identification.ProjectPartnerReportIdentification
import io.cloudflight.jems.server.project.service.report.model.partner.identification.ProjectPartnerReportPeriod
import io.cloudflight.jems.server.project.service.report.model.partner.identification.ProjectPartnerReportSpendingProfile
import io.cloudflight.jems.server.project.service.report.model.partner.identification.control.ProjectPartnerControlReport
import io.cloudflight.jems.server.project.service.report.model.partner.identification.control.ProjectPartnerControlReportChange
import io.cloudflight.jems.server.project.service.report.model.partner.identification.control.ReportDesignatedController
import io.cloudflight.jems.server.project.service.report.model.partner.identification.control.ReportFileFormat
import io.cloudflight.jems.server.project.service.report.model.partner.identification.control.ReportLocationOnTheSpotVerification
import io.cloudflight.jems.server.project.service.report.model.partner.identification.control.ReportMethodology
import io.cloudflight.jems.server.project.service.report.model.partner.identification.control.ReportOnTheSpotVerification
import io.cloudflight.jems.server.project.service.report.model.partner.identification.control.ReportType
import io.cloudflight.jems.server.project.service.report.model.partner.identification.control.ReportVerification
import io.cloudflight.jems.server.project.service.report.partner.identification.ProjectPartnerReportIdentificationPersistence
import io.cloudflight.jems.server.project.service.report.partner.identification.ProjectPartnerReportDesignatedControllerPersistence
import io.cloudflight.jems.server.project.service.report.partner.identification.ProjectPartnerReportVerificationPersistence
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.slot
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import java.math.BigDecimal
import java.time.LocalDate
import java.time.ZonedDateTime
import java.util.Optional

internal class UpdateProjectPartnerControlReportIdentificationTest : UnitTest() {

    companion object {
        private const val PARTNER_ID = 2248L
        private const val PROJECT_ID = 490L

        private val YESTERDAY = ZonedDateTime.now().minusDays(1)
        private val YESTERDAY_LOCALDATE = LocalDate.now().plusDays(1)
        private val TOMORROW = LocalDate.now().plusDays(1)
        private val YEARS_AGO_3 = LocalDate.now().minusYears(3)
        private val YEARS_AGO_5 = LocalDate.now().minusYears(5)

        private fun report(id: Long, status: ReportStatus) = ProjectPartnerReport(
            id = id,
            reportNumber = 5,
            status = status,
            version = "8.1",
            firstSubmission = YESTERDAY,
            identification = PartnerReportIdentification(
                projectIdentifier = "projectIdentifier",
                projectAcronym = "projectAcronym",
                partnerNumber = 2,
                partnerAbbreviation = "",
                partnerRole = ProjectPartnerRole.PARTNER,
                nameInOriginalLanguage = null,
                nameInEnglish = null,
                legalStatus = null,
                partnerType = null,
                vatRecovery = null,
                country = null,
                currency = null,
                coFinancing = emptyList(),
            ),
        )

        val identification = ProjectPartnerReportIdentification(
            startDate = YEARS_AGO_5,
            endDate = YEARS_AGO_3,
            summary = emptySet(),
            problemsAndDeviations = emptySet(),
            spendingDeviations = emptySet(),
            targetGroups = emptyList(),
            spendingProfile = ProjectPartnerReportSpendingProfile(
                periodDetail = ProjectPartnerReportPeriod(
                    number = 10,
                    periodBudget = BigDecimal.ZERO,
                    periodBudgetCumulative = BigDecimal.ZERO,
                    start = 21,
                    end = 22,
                ),
                currentReport = BigDecimal.ZERO,
                previouslyReported = BigDecimal.ZERO,
                differenceFromPlan = BigDecimal.ZERO,
                differenceFromPlanPercentage = BigDecimal.ZERO,
                nextReportForecast = BigDecimal.ZERO,
            ),
            controllerFormats = setOf(ReportFileFormat.Originals),
            type = ReportType.FinalReport,
        )

        private val designatedController = ReportDesignatedController(
            controlInstitution = "Test",
            controlInstitutionId = 1,
            controllingUserId = 2,
            jobTitle = "JobTitle",
            divisionUnit = "divisionUnit",
            address = "address",
            countryCode = "RO",
            country = "Romania (RO)",
            telephone = "0000123456",
            controllerReviewerId = 3
        )

        private val verificationInstances = listOf(
            ReportOnTheSpotVerification(
                id = 1,
                verificationFrom = YESTERDAY_LOCALDATE,
                verificationTo = TOMORROW,
                verificationLocations = setOf(ReportLocationOnTheSpotVerification.PlaceOfPhysicalProjectOutput),
                verificationFocus = "some Focus"
            )
        )

        private val reportVerification = ReportVerification(
            generalMethodologies = setOf(ReportMethodology.AdministrativeVerification),
            verificationInstances = verificationInstances,
            riskBasedVerificationApplied = true,
            riskBasedVerificationDescription = "some description"
        )

        private fun expectedControlReport(id: Long) = ProjectPartnerControlReport(
            id = id,
            programmeTitle = "programme title",
            projectTitle = setOf(InputTranslation(EN, "title EN")),
            projectAcronym = "projectAcronym",
            projectIdentifier = "projectIdentifier",
            linkedFormVersion = "8.1",
            reportNumber = 5,
            projectStart = LocalDate.of(2020, 9, 30),
            projectEnd = LocalDate.of(2025, 8, 14),
            reportPeriodNumber = 10,
            reportPeriodStart = YEARS_AGO_5,
            reportPeriodEnd = YEARS_AGO_3,
            reportFirstSubmission = YESTERDAY,
            controllerFormats = setOf(ReportFileFormat.Originals),
            type = ReportType.FinalReport,
            designatedController = designatedController,
            reportVerification = reportVerification
        )

    }

    @MockK
    lateinit var reportPersistence: ProjectPartnerReportPersistence
    @MockK
    lateinit var reportIdentificationPersistence: ProjectPartnerReportIdentificationPersistence
    @MockK
    lateinit var reportDesignatedControllerPersistence: ProjectPartnerReportDesignatedControllerPersistence
    @MockK
    lateinit var reportReportVerificationPersistence: ProjectPartnerReportVerificationPersistence
    @MockK
    lateinit var partnerPersistence: PartnerPersistence
    @MockK
    lateinit var projectPersistence: ProjectPersistence
    @MockK
    lateinit var programmeDataRepository: ProgrammeDataRepository
    @MockK
    lateinit var getContractingMonitoringService: GetContractingMonitoringService

    @InjectMockKs
    lateinit var interactor: UpdateProjectPartnerControlReportIdentification

    @BeforeEach
    fun setup() {
        clearMocks(reportPersistence, reportIdentificationPersistence, partnerPersistence,
            projectPersistence, programmeDataRepository, getContractingMonitoringService)
    }

    @ParameterizedTest(name = "updateControlIdentification (status {0})")
    @EnumSource(value = ReportStatus::class, names = ["InControl"])
    fun `updateControlIdentification - success`(status: ReportStatus) {
        val reportId = 66L + status.ordinal

        every { reportPersistence.getPartnerReportById(PARTNER_ID, reportId = reportId) } returns report(reportId, status)
        val changeSlot = slot<ProjectPartnerControlReportChange>()
        every { reportIdentificationPersistence.updatePartnerControlReportIdentification(
            PARTNER_ID, reportId = reportId, capture(changeSlot)
        ) } returns identification
        every { partnerPersistence.getProjectIdForPartnerId(PARTNER_ID, "8.1") } returns PROJECT_ID

        val project = mockk<ProjectFull>()
        every { project.title } returns setOf(InputTranslation(EN, "title EN"))
        every { projectPersistence.getProject(PROJECT_ID, "8.1") } returns project

        val programme = mockk<ProgrammeDataEntity>()
        every { programme.title } returns "programme title"
        every { programmeDataRepository.findById(1L) } returns Optional.of(programme)
        every { getContractingMonitoringService.getContractMonitoringDates(PROJECT_ID) } returns Pair(
            LocalDate.of(2020, 9, 30),
            LocalDate.of(2025, 8, 14),
        )

        every { reportDesignatedControllerPersistence.updateDesignatedController(PARTNER_ID, reportId, designatedController)} returns designatedController
        every { reportReportVerificationPersistence.updateReportVerification(PARTNER_ID, reportId, reportVerification)} returns reportVerification

        val change = ProjectPartnerControlReportChange(
            controllerFormats = setOf(ReportFileFormat.Electronic),
            type = ReportType.FinalReport,
            designatedController = designatedController,
            reportVerification = reportVerification
        )
        assertThat(interactor.updateControlIdentification(PARTNER_ID, reportId = reportId, change))
            .isEqualTo(expectedControlReport(reportId))
        assertThat(changeSlot.captured).isEqualTo(change)
    }

    @ParameterizedTest(name = "updateControlIdentification - wrong status (status {0})")
    @EnumSource(value = ReportStatus::class, names = ["InControl"], mode = EnumSource.Mode.EXCLUDE)
    fun `updateControlIdentification - wrong status`(status: ReportStatus) {
        val reportId = 250L + status.ordinal

        every { reportPersistence.getPartnerReportById(PARTNER_ID, reportId = reportId) } returns report(reportId, status)
        assertThrows<ReportNotInControl> { interactor.updateControlIdentification(PARTNER_ID, reportId = reportId, mockk()) }
    }

}
