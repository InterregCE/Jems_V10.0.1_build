package io.cloudflight.jems.server.project.controller.report.partner.identification

import io.cloudflight.jems.api.programme.dto.language.SystemLanguage.EN
import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.api.project.dto.description.ProjectTargetGroupDTO
import io.cloudflight.jems.api.project.dto.report.partner.identification.ProjectPartnerReportSpendingProfileDTO
import io.cloudflight.jems.api.project.dto.report.partner.identification.UpdateProjectPartnerReportIdentificationDTO
import io.cloudflight.jems.api.project.dto.report.partner.identification.ProjectPartnerReportIdentificationDTO
import io.cloudflight.jems.api.project.dto.report.partner.identification.ProjectPartnerReportPeriodDTO
import io.cloudflight.jems.api.project.dto.report.partner.identification.ProjectPartnerReportIdentificationTargetGroupDTO
import io.cloudflight.jems.api.project.dto.report.partner.identification.control.ProjectPartnerControlReportDTO
import io.cloudflight.jems.api.project.dto.report.partner.identification.control.ReportDesignatedControllerDTO
import io.cloudflight.jems.api.project.dto.report.partner.identification.control.ReportFileFormatDTO
import io.cloudflight.jems.api.project.dto.report.partner.identification.control.ReportTypeDTO
import io.cloudflight.jems.api.project.dto.report.partner.identification.control.ReportOnTheSpotVerificationDTO
import io.cloudflight.jems.api.project.dto.report.partner.identification.control.ReportLocationOnTheSpotVerificationDTO
import io.cloudflight.jems.api.project.dto.report.partner.identification.control.ReportVerificationDTO
import io.cloudflight.jems.api.project.dto.report.partner.identification.control.ReportMethodologyDTO
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.project.service.model.ProjectTargetGroup
import io.cloudflight.jems.server.project.service.report.model.partner.identification.ProjectPartnerReportIdentification
import io.cloudflight.jems.server.project.service.report.model.partner.identification.ProjectPartnerReportIdentificationTargetGroup
import io.cloudflight.jems.server.project.service.report.model.partner.identification.ProjectPartnerReportPeriod
import io.cloudflight.jems.server.project.service.report.model.partner.identification.ProjectPartnerReportSpendingProfile
import io.cloudflight.jems.server.project.service.report.model.partner.identification.UpdateProjectPartnerReportIdentification
import io.cloudflight.jems.server.project.service.report.model.partner.identification.control.ProjectPartnerControlReport
import io.cloudflight.jems.server.project.service.report.model.partner.identification.control.ReportFileFormat
import io.cloudflight.jems.server.project.service.report.model.partner.identification.control.ReportType
import io.cloudflight.jems.server.project.service.report.model.partner.identification.control.ReportDesignatedController
import io.cloudflight.jems.server.project.service.report.model.partner.identification.control.ReportOnTheSpotVerification
import io.cloudflight.jems.server.project.service.report.model.partner.identification.control.ReportLocationOnTheSpotVerification
import io.cloudflight.jems.server.project.service.report.model.partner.identification.control.ReportVerification
import io.cloudflight.jems.server.project.service.report.model.partner.identification.control.ReportMethodology
import io.cloudflight.jems.server.project.service.report.partner.identification.control.getProjectPartnerControlReportIdentification.GetProjectPartnerControlReportIdentificationInteractor
import io.cloudflight.jems.server.project.service.report.partner.identification.control.updateProjectPartnerControlReportIdentification.UpdateProjectPartnerControlReportIdentificationInteractor
import io.cloudflight.jems.server.project.service.report.partner.identification.getProjectPartnerReportAvailablePeriods.GetProjectPartnerReportAvailablePeriodsInteractor
import io.cloudflight.jems.server.project.service.report.partner.identification.getProjectPartnerReportIdentification.GetProjectPartnerReportIdentificationInteractor
import io.cloudflight.jems.server.project.service.report.partner.identification.updateProjectPartnerReportIdentification.UpdateProjectPartnerReportIdentificationInteractor
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.slot
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.LocalDate
import java.time.ZonedDateTime

class ProjectPartnerReportIdentificationControllerTest : UnitTest() {

    companion object {
        private const val PARTNER_ID = 525L
        private const val REPORT_ID = 605L

        private val YESTERDAY = LocalDate.now().minusDays(1)
        private val TOMORROW = LocalDate.now().plusDays(1)
        private val LAST_WEEK = ZonedDateTime.now().minusWeeks(1)
        private val TODAY = ZonedDateTime.now()

        private val dummyPeriod = ProjectPartnerReportPeriod(number = 3, periodBudget = BigDecimal.ONE, BigDecimal.TEN, 7, 9)

        private val dummyIdentification = ProjectPartnerReportIdentification(
            startDate = YESTERDAY,
            endDate = TOMORROW,
            summary = setOf(InputTranslation(EN, "summary EN")),
            problemsAndDeviations = setOf(InputTranslation(EN, "problem EN")),
            spendingDeviations = setOf(InputTranslation(EN, "spending EN")),
            targetGroups = listOf(
                ProjectPartnerReportIdentificationTargetGroup(
                    type = ProjectTargetGroup.BusinessSupportOrganisation,
                    sortNumber = 1,
                    specification = setOf(InputTranslation(EN, "spec EN")),
                    description = setOf(InputTranslation(EN, "desc EN")),
                ),
            ),
            spendingProfile = ProjectPartnerReportSpendingProfile(
                periodDetail = dummyPeriod,
                currentReport = BigDecimal.valueOf(1),
                previouslyReported = BigDecimal.valueOf(2),
                differenceFromPlan = BigDecimal.valueOf(3),
                differenceFromPlanPercentage = BigDecimal.valueOf(4),
                nextReportForecast = BigDecimal.valueOf(5),
            ),
            controllerFormats = setOf(ReportFileFormat.Originals),
            type = ReportType.PartnerReport,
        )

        private val expectedDummyPeriod = ProjectPartnerReportPeriodDTO(number = 3, periodBudget = BigDecimal.ONE, BigDecimal.TEN, 7, 9, null, null)

        private val expectedDummyIdentification = ProjectPartnerReportIdentificationDTO(
            startDate = YESTERDAY,
            endDate = TOMORROW,
            summary = setOf(InputTranslation(EN, "summary EN")),
            problemsAndDeviations = setOf(InputTranslation(EN, "problem EN")),
            spendingDeviations = setOf(InputTranslation(EN, "spending EN")),
            targetGroups = listOf(
                ProjectPartnerReportIdentificationTargetGroupDTO(
                    type = ProjectTargetGroupDTO.BusinessSupportOrganisation,
                    sortNumber = 1,
                    specification = setOf(InputTranslation(EN, "spec EN")),
                    description = setOf(InputTranslation(EN, "desc EN")),
                ),
            ),
            spendingProfile = ProjectPartnerReportSpendingProfileDTO(
                periodDetail = expectedDummyPeriod,
                currentReport = BigDecimal.valueOf(1),
                previouslyReported = BigDecimal.valueOf(2),
                differenceFromPlan = BigDecimal.valueOf(3),
                differenceFromPlanPercentage = BigDecimal.valueOf(4),
                nextReportForecast = BigDecimal.valueOf(5),
            ),
        )

        private val dummyIdentificationUpdateDto = UpdateProjectPartnerReportIdentificationDTO(
            startDate = YESTERDAY,
            endDate = TOMORROW,
            period = null,
            summary = setOf(InputTranslation(EN, "summary EN")),
            problemsAndDeviations = setOf(InputTranslation(EN, "problem EN")),
            targetGroups = listOf(
                setOf(InputTranslation(EN, "spec EN")),
            ),
            nextReportForecast = BigDecimal.valueOf(8),
            spendingDeviations = setOf(InputTranslation(EN, "spending EN")),
        )

        private val expectedDummyUpdateIdentification = UpdateProjectPartnerReportIdentification(
            startDate = YESTERDAY,
            endDate = TOMORROW,
            period = null,
            summary = setOf(InputTranslation(EN, "summary EN")),
            problemsAndDeviations = setOf(InputTranslation(EN, "problem EN")),
            targetGroups = listOf(
                setOf(InputTranslation(EN, "spec EN")),
            ),
            spendingDeviations = setOf(InputTranslation(EN, "spending EN")),
            nextReportForecast = BigDecimal.valueOf(8)
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

        private val dummyControlIdentification = ProjectPartnerControlReport(
            id = 4L,
            programmeTitle = "programmeTitle",
            projectTitle = setOf(InputTranslation(EN, "summary EN")),
            projectAcronym = "projectAcronym",
            projectIdentifier = "projectIdentifier",
            linkedFormVersion = "V4.7.8",
            reportNumber = 1,
            projectStart = LocalDate.of(2022, 1, 30),
            projectEnd = LocalDate.of(2022, 2, 28),
            reportPeriodNumber = 2,
            reportPeriodStart = LocalDate.of(2022, 2, 15),
            reportPeriodEnd = LocalDate.of(2022, 2, 21),
            reportFirstSubmission = LAST_WEEK,
            controllerFormats = setOf(
                ReportFileFormat.Electronic,
            ),
            type = ReportType.FinalReport,
            designatedController = designatedController,
            reportVerification = reportVerification,
            reportControlEnd = TODAY,
            reportLastResubmission = LAST_WEEK
        )

        private val expectedDesignatedController = ReportDesignatedControllerDTO(
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

        private val expectedVerificationInstances = setOf(
            ReportOnTheSpotVerificationDTO(
                id = 1,
                verificationFrom = YESTERDAY,
                verificationTo = TOMORROW,
                verificationLocations = setOf(ReportLocationOnTheSpotVerificationDTO.PlaceOfPhysicalProjectOutput),
                verificationFocus = "some Focus"
            )
        )

        private val expectedReportVerification = ReportVerificationDTO(
            generalMethodologies = setOf(ReportMethodologyDTO.AdministrativeVerification),
            verificationInstances = expectedVerificationInstances,
            riskBasedVerificationApplied = true,
            riskBasedVerificationDescription = "some description"
        )

        private val expectedDummyControlIdentification = ProjectPartnerControlReportDTO(
            id = 4L,
            programmeTitle = "programmeTitle",
            projectTitle = setOf(InputTranslation(EN, "summary EN")),
            projectAcronym = "projectAcronym",
            projectIdentifier = "projectIdentifier",
            linkedFormVersion = "V4.7.8",
            reportNumber = 1,
            projectStart = LocalDate.of(2022, 1, 30),
            projectEnd = LocalDate.of(2022, 2, 28),
            reportPeriodNumber = 2,
            reportPeriodStart = LocalDate.of(2022, 2, 15),
            reportPeriodEnd = LocalDate.of(2022, 2, 21),
            reportFirstSubmission = LAST_WEEK,
            controllerFormats = setOf(
                ReportFileFormatDTO.Electronic,
            ),
            type = ReportTypeDTO.FinalReport,
            designatedController = expectedDesignatedController,
            reportVerification = expectedReportVerification,
            reportControlEnd = TODAY,
            reportLastResubmission = LAST_WEEK
        )
    }

    @MockK
    lateinit var getIdentification: GetProjectPartnerReportIdentificationInteractor

    @MockK
    lateinit var updateIdentification: UpdateProjectPartnerReportIdentificationInteractor

    @MockK
    lateinit var getControlIdentification: GetProjectPartnerControlReportIdentificationInteractor

    @MockK
    lateinit var updateControlIdentification: UpdateProjectPartnerControlReportIdentificationInteractor

    @MockK
    lateinit var getAvailablePeriods: GetProjectPartnerReportAvailablePeriodsInteractor

    @InjectMockKs
    private lateinit var controller: ProjectPartnerReportIdentificationController

    @Test
    fun getIdentification() {
        every { getIdentification.getIdentification(partnerId = PARTNER_ID, reportId = REPORT_ID) } returns
            dummyIdentification
        assertThat(controller.getIdentification(partnerId = PARTNER_ID, reportId = REPORT_ID))
            .isEqualTo(expectedDummyIdentification)
    }

    @Test
    fun updateIdentification() {
        val slotData = slot<UpdateProjectPartnerReportIdentification>()
        every { updateIdentification.updateIdentification(
            partnerId = PARTNER_ID,
            reportId = REPORT_ID,
            data = capture(slotData),
        ) } returns dummyIdentification

        assertThat(controller.updateIdentification(
            partnerId = PARTNER_ID,
            reportId = REPORT_ID,
            identification = dummyIdentificationUpdateDto,
        )).isEqualTo(expectedDummyIdentification)

        assertThat(slotData.captured).isEqualTo(expectedDummyUpdateIdentification)
    }

    @Test
    fun getAvailablePeriods() {
        every { getAvailablePeriods.get(PARTNER_ID, reportId = REPORT_ID) } returns listOf(dummyPeriod)
        assertThat(controller.getAvailablePeriods(PARTNER_ID, reportId = REPORT_ID)).containsExactly(expectedDummyPeriod)
    }


    @Test
    fun getControlIdentification() {
        every { getControlIdentification.getControlIdentification(partnerId = PARTNER_ID, reportId = REPORT_ID) } returns
            dummyControlIdentification
        assertThat(controller.getControlIdentification(partnerId = PARTNER_ID, reportId = REPORT_ID))
            .isEqualTo(expectedDummyControlIdentification)
    }


}
