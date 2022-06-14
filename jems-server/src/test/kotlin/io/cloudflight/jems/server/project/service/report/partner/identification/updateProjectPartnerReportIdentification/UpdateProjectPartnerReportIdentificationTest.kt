package io.cloudflight.jems.server.project.service.report.partner.identification.updateProjectPartnerReportIdentification

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.api.programme.dto.language.SystemLanguage.EN
import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.common.validator.AppInputValidationException
import io.cloudflight.jems.server.common.validator.GeneralValidatorDefaultImpl
import io.cloudflight.jems.server.common.validator.GeneralValidatorService
import io.cloudflight.jems.server.programme.service.priority.getStringOfLength
import io.cloudflight.jems.server.project.service.ProjectPersistence
import io.cloudflight.jems.server.project.service.model.ProjectPeriod
import io.cloudflight.jems.server.project.service.model.ProjectTargetGroup
import io.cloudflight.jems.server.project.service.partner.PartnerPersistence
import io.cloudflight.jems.server.project.service.report.ProjectReportPersistence
import io.cloudflight.jems.server.project.service.report.model.ProjectPartnerReportStatusAndVersion
import io.cloudflight.jems.server.project.service.report.model.ReportStatus
import io.cloudflight.jems.server.project.service.report.model.identification.ProjectPartnerReportIdentification
import io.cloudflight.jems.server.project.service.report.model.identification.ProjectPartnerReportIdentificationTargetGroup
import io.cloudflight.jems.server.project.service.report.model.identification.ProjectPartnerReportPeriod
import io.cloudflight.jems.server.project.service.report.model.identification.ProjectPartnerReportSpendingProfile
import io.cloudflight.jems.server.project.service.report.partner.identification.ProjectReportIdentificationPersistence
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.math.BigDecimal
import java.time.LocalDate

internal class UpdateProjectPartnerReportIdentificationTest : UnitTest() {

    companion object {
        private const val PARTNER_ID = 490L

        private val YESTERDAY = LocalDate.now().minusDays(1)
        private val TOMORROW = LocalDate.now().plusDays(1)

        private val periods = listOf(
            ProjectPartnerReportPeriod(number = 1, periodBudget = BigDecimal.valueOf(15), BigDecimal.valueOf(15), 1, 3),
            ProjectPartnerReportPeriod(number = 2, periodBudget = BigDecimal.valueOf(12), BigDecimal.valueOf(27), 4, 6),
            ProjectPartnerReportPeriod(number = 3, periodBudget = BigDecimal.valueOf(18), BigDecimal.valueOf(35), 7, 9),
        )

        private val updateData = io.cloudflight.jems.server.project.service.report.model.identification.UpdateProjectPartnerReportIdentification(
            startDate = YESTERDAY,
            endDate = TOMORROW,
            period = 2,
            summary = setOf(InputTranslation(EN, "summary EN")),
            problemsAndDeviations = setOf(InputTranslation(EN, "problem EN")),
            targetGroups = listOf(
                emptySet(),
                setOf(InputTranslation(EN, "problem EN")),
            ),
            nextReportForecast = BigDecimal.TEN,
            spendingDeviations = setOf(InputTranslation(EN, "spendingDeviations EN")),
        )

        private val updateDataInvalid = io.cloudflight.jems.server.project.service.report.model.identification.UpdateProjectPartnerReportIdentification(
            startDate = TOMORROW,
            endDate = YESTERDAY,
            period = 2,
            summary = setOf(InputTranslation(EN, getStringOfLength(2001))),
            problemsAndDeviations = setOf(InputTranslation(EN, getStringOfLength(2001))),
            targetGroups = listOf(
                emptySet(),
                setOf(InputTranslation(EN, getStringOfLength(2001))),
            ),
            nextReportForecast = BigDecimal.valueOf(999_999_999_9901, 4),
            spendingDeviations = setOf(InputTranslation(EN, getStringOfLength(2001))),
        )

        private fun saveResult(
            differenceFromPlan: BigDecimal = BigDecimal.ZERO,
            differenceFromPlanPercentage: BigDecimal = BigDecimal.ZERO,
            currentReport: BigDecimal = BigDecimal.ZERO,
        ) = ProjectPartnerReportIdentification(
            startDate = TOMORROW,
            endDate = YESTERDAY,
            summary = setOf(InputTranslation(EN, getStringOfLength(2001))),
            problemsAndDeviations = setOf(InputTranslation(EN, getStringOfLength(2001))),
            spendingDeviations = setOf(InputTranslation(EN, "spendingDeviations EN")),
            targetGroups = listOf(
                ProjectPartnerReportIdentificationTargetGroup(ProjectTargetGroup.CrossBorderLegalBody, 1, emptySet(), emptySet()),
                ProjectPartnerReportIdentificationTargetGroup(ProjectTargetGroup.GeneralPublic, 2, emptySet(), setOf(InputTranslation(EN, "problem EN"))),
            ),
            spendingProfile = ProjectPartnerReportSpendingProfile(
                periodDetail = ProjectPartnerReportPeriod(number = 2, periodBudget = BigDecimal.valueOf(12), BigDecimal.valueOf(27), 4, 6),
                currentReport = currentReport /* should be zero when Draft but not important for this test */,
                previouslyReported = BigDecimal.valueOf(30),
                differenceFromPlan = differenceFromPlan /* should be ZERO always from persistence */,
                differenceFromPlanPercentage = differenceFromPlanPercentage /* should be ZERO always from persistence */,
                nextReportForecast = BigDecimal.TEN,
            )
        )

    }

    @MockK
    lateinit var reportPersistence: ProjectReportPersistence

    @MockK
    lateinit var reportIdentificationPersistence: ProjectReportIdentificationPersistence

    lateinit var generalValidator: GeneralValidatorService

    lateinit var updateIdentification: UpdateProjectPartnerReportIdentification

    @BeforeEach
    fun setup() {
        MockKAnnotations.init(this)
        generalValidator = GeneralValidatorDefaultImpl()
        updateIdentification = UpdateProjectPartnerReportIdentification(
            reportPersistence,
            reportIdentificationPersistence,
            generalValidator,
        )
    }

    @Test
    fun updateIdentification() {
        val reportId = 66L
        every { reportPersistence.getPartnerReportStatusAndVersion(PARTNER_ID, reportId = reportId) } returns
            ProjectPartnerReportStatusAndVersion(ReportStatus.Draft, "17.0.1")
        every { reportIdentificationPersistence.getAvailablePeriods(PARTNER_ID, reportId = reportId) } returns periods
        val slotData = slot<io.cloudflight.jems.server.project.service.report.model.identification.UpdateProjectPartnerReportIdentification>()
        every { reportIdentificationPersistence.updatePartnerReportIdentification(PARTNER_ID, reportId = reportId, capture(slotData)) } returns saveResult()

        assertThat(updateIdentification.updateIdentification(PARTNER_ID, reportId = reportId, updateData)).isEqualTo(
            saveResult(differenceFromPlan = BigDecimal.valueOf(-4), differenceFromPlanPercentage = BigDecimal.valueOf(11481, 2), currentReport = BigDecimal.ONE)
        )

        assertThat(slotData.captured).isEqualTo(updateData)
        verify(exactly = 1) { reportIdentificationPersistence.updatePartnerReportIdentification(PARTNER_ID, reportId, any()) }
    }

    @Test
    fun `updateIdentification - report closed`() {
        every { reportPersistence.getPartnerReportStatusAndVersion(PARTNER_ID, reportId = 5L) } returns
            ProjectPartnerReportStatusAndVersion(ReportStatus.Submitted, "1")
        assertThrows<ReportAlreadyClosed> { updateIdentification.updateIdentification(PARTNER_ID, reportId = 5L, mockk()) }
    }

    @Test
    fun `updateIdentification - wrong inputs`() {
        every { reportPersistence.getPartnerReportStatusAndVersion(PARTNER_ID, reportId = 8L) } returns
            ProjectPartnerReportStatusAndVersion(ReportStatus.Draft, "4.0.0")

        val ex = assertThrows<AppInputValidationException> {
            updateIdentification.updateIdentification(PARTNER_ID, reportId = 8L, updateDataInvalid)
        }

        assertThat(ex.formErrors).hasSize(7)
        assertThat(ex.formErrors["summary.language.en"]).isEqualTo(I18nMessage(
            "common.error.field.max.length", mapOf("actualLength" to "2001", "requiredLength" to "2000")
        ))
        assertThat(ex.formErrors["problemsAndDeviations.language.en"]).isEqualTo(I18nMessage(
            "common.error.field.max.length", mapOf("actualLength" to "2001", "requiredLength" to "2000")
        ))
        assertThat(ex.formErrors["descriptionOfTheTargetGroup[1].language.en"]).isEqualTo(I18nMessage(
            "common.error.field.max.length", mapOf("actualLength" to "2001", "requiredLength" to "2000")
        ))
        assertThat(ex.formErrors["startDate"]).isNotNull
        assertThat(ex.formErrors["endDate"]).isNotNull
        assertThat(ex.formErrors["nextReportForecast"]).isEqualTo(I18nMessage(
            "common.error.field.number.out.of.range", mapOf("number" to "999999999.9901", "min" to "0", "max" to "999999999.99")
        ))
        assertThat(ex.formErrors["spendingDeviations.language.en"]).isEqualTo(I18nMessage(
            "common.error.field.max.length", mapOf("actualLength" to "2001", "requiredLength" to "2000")
        ))
    }

    @Test
    fun `updateIdentification - wrong period`() {
        val reportId = 75L
        every { reportPersistence.getPartnerReportStatusAndVersion(PARTNER_ID, reportId = reportId) } returns
            ProjectPartnerReportStatusAndVersion(ReportStatus.Draft, "8.0")

        every { reportIdentificationPersistence.getAvailablePeriods(PARTNER_ID, reportId = reportId) } returns periods

        val ex = assertThrows<InvalidPeriodNumber> {
            updateIdentification.updateIdentification(PARTNER_ID, reportId = reportId, updateData.copy(period = 4))
        }
        assertThat(ex.message).isEqualTo("Period number 4 is not valid.")
    }

}
