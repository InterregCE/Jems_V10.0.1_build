package io.cloudflight.jems.server.project.service.report.partner.identification.control.getProjectPartnerControlReportIdentification

import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.programme.entity.ProgrammeDataEntity
import io.cloudflight.jems.server.programme.repository.ProgrammeDataRepository
import io.cloudflight.jems.server.project.service.ProjectPersistence
import io.cloudflight.jems.server.project.service.contracting.monitoring.getProjectContractingMonitoring.GetContractingMonitoringService
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
import io.cloudflight.jems.server.project.service.report.model.partner.identification.control.ReportFileFormat
import io.cloudflight.jems.server.project.service.report.model.partner.identification.control.ReportLocationOnTheSpotVerification
import io.cloudflight.jems.server.project.service.report.model.partner.identification.control.ReportMethodology
import io.cloudflight.jems.server.project.service.report.model.partner.identification.control.ReportOnTheSpotVerification
import io.cloudflight.jems.server.project.service.report.model.partner.identification.control.ReportType
import io.cloudflight.jems.server.project.service.report.model.partner.identification.control.ReportVerification
import io.cloudflight.jems.server.project.service.report.model.partner.identification.control.ReportDesignatedController
import io.cloudflight.jems.server.project.service.report.partner.identification.ProjectPartnerReportIdentificationPersistence
import io.cloudflight.jems.server.project.service.report.partner.identification.ProjectPartnerReportDesignatedControllerPersistence
import io.cloudflight.jems.server.project.service.report.partner.identification.ProjectPartnerReportVerificationPersistence
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import java.math.BigDecimal
import java.time.LocalDate
import java.time.ZonedDateTime
import java.util.Optional.of

internal class GetProjectPartnerControlReportIdentificationTest : UnitTest() {

    companion object {
        private const val PROJECT_ID = 586L
        private const val PARTNER_ID = 581L
        private val YESTERDAY = LocalDate.now().minusDays(1)
        private val TOMORROW = LocalDate.now().plusDays(1)
        private val YEARS_AGO_10 = ZonedDateTime.now().minusYears(10)

        private fun report(status: ReportStatus) = ProjectPartnerReport(
            id = 10L,
            reportNumber = 1,
            status = status,
            version = "1.0",
            firstSubmission = YEARS_AGO_10,
            identification = PartnerReportIdentification(
                projectIdentifier = "projectIdentifier",
                projectAcronym = "projectAcronym",
                partnerNumber = 0,
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
            controlEnd = null
        )

        private val identification = ProjectPartnerReportIdentification(
            startDate = YESTERDAY.minusYears(1),
            endDate = TOMORROW.plusYears(1),
            summary = emptySet(),
            problemsAndDeviations = emptySet(),
            spendingDeviations = emptySet(),
            targetGroups = emptyList(),
            spendingProfile = ProjectPartnerReportSpendingProfile(
                periodDetail = ProjectPartnerReportPeriod(number = 2, BigDecimal.ZERO, BigDecimal.ZERO, 3, 4),
                currentReport = BigDecimal.ZERO,
                previouslyReported = BigDecimal.ZERO,
                differenceFromPlan = BigDecimal.ZERO,
                differenceFromPlanPercentage = BigDecimal.ZERO,
                nextReportForecast = BigDecimal.ZERO,
            ),
            controllerFormats = setOf(ReportFileFormat.Electronic),
            type = ReportType.PartnerReport,
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

        private val verificationInstances = setOf(
            ReportOnTheSpotVerification(
                id = 1,
                verificationFrom = YESTERDAY,
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

        private val expectedIdentification = ProjectPartnerControlReport(
            id = 10L,
            programmeTitle = "programmeTitle",
            projectTitle = setOf(InputTranslation(SystemLanguage.EN, "projectTitle")),
            projectAcronym = "projectAcronym",
            projectIdentifier = "projectIdentifier",
            linkedFormVersion = "1.0",
            reportNumber = 1,
            projectStart = YESTERDAY,
            projectEnd = TOMORROW,
            reportPeriodNumber = 2,
            reportPeriodStart = YESTERDAY.minusYears(1),
            reportPeriodEnd = TOMORROW.plusYears(1),
            reportFirstSubmission = YEARS_AGO_10,
            controllerFormats = setOf(
                ReportFileFormat.Electronic,
            ),
            type = ReportType.PartnerReport,
            designatedController = designatedController,
            reportVerification = reportVerification,
            reportControlEnd = null
        )
    }

    @MockK
    lateinit var reportPersistence: ProjectPartnerReportPersistence
    @MockK
    lateinit var reportIdentificationPersistence: ProjectPartnerReportIdentificationPersistence
    @MockK
    lateinit var partnerPersistence: PartnerPersistence
    @MockK
    lateinit var projectPersistence: ProjectPersistence
    @MockK
    lateinit var designatedControllerPersistence: ProjectPartnerReportDesignatedControllerPersistence
    @MockK
    lateinit var reportVerificationPersistence: ProjectPartnerReportVerificationPersistence
    @MockK
    lateinit var programmeDataRepository: ProgrammeDataRepository
    @MockK
    lateinit var getContractingMonitoringService: GetContractingMonitoringService

    @InjectMockKs
    lateinit var interactor: GetProjectPartnerControlReportIdentification

    @ParameterizedTest(name = "getControlIdentification (status {0})")
    @EnumSource(value = ReportStatus::class, names = ["InControl", "Certified"])
    fun getControlIdentification(status: ReportStatus) {
        every { reportPersistence.getPartnerReportById(PARTNER_ID, reportId = 12L) } returns report(status)

        every { reportIdentificationPersistence.getPartnerReportIdentification(PARTNER_ID, reportId = 12L) } returns of(identification)
        every { partnerPersistence.getProjectIdForPartnerId(PARTNER_ID, "1.0") } returns PROJECT_ID
        every { projectPersistence.getProject(PROJECT_ID, "1.0").title } returns
            setOf(InputTranslation(SystemLanguage.EN, "projectTitle"))
        every { getContractingMonitoringService.getContractMonitoringDates(PROJECT_ID) } returns Pair(YESTERDAY, TOMORROW)
        val programme = mockk<ProgrammeDataEntity>()
        every { programme.title } returns "programmeTitle"
        every { programmeDataRepository.findById(1L) } returns of(programme)
        every { designatedControllerPersistence.getControlReportDesignatedController(PARTNER_ID, 12L)} returns designatedController
        every { reportVerificationPersistence.getControlReportVerification(PARTNER_ID, 12L)} returns of(reportVerification)

        assertThat(interactor.getControlIdentification(PARTNER_ID, reportId = 12L)).isEqualTo(expectedIdentification)
    }

    @ParameterizedTest(name = "getControlIdentification - wrong status (status {0})")
    @EnumSource(value = ReportStatus::class, names = ["InControl", "Certified"], mode = EnumSource.Mode.EXCLUDE)
    fun `getControlIdentification - wrong status`(status: ReportStatus) {
        val reportId = 15L + status.ordinal
        every { reportPersistence.getPartnerReportById(PARTNER_ID, reportId = reportId) } returns report(status)
        assertThrows<ReportNotInControl> { interactor.getControlIdentification(PARTNER_ID, reportId = reportId) }
    }

}
