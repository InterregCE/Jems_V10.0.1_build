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
import io.cloudflight.jems.server.project.service.partner.PartnerPersistence
import io.cloudflight.jems.server.project.service.report.ProjectReportPersistence
import io.cloudflight.jems.server.project.service.report.model.ProjectPartnerReportStatusAndVersion
import io.cloudflight.jems.server.project.service.report.model.ReportStatus
import io.cloudflight.jems.server.project.service.report.model.identification.ProjectPartnerReportIdentification
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
import java.time.LocalDate

internal class UpdateProjectPartnerReportIdentificationTest : UnitTest() {

    companion object {
        private const val PARTNER_ID = 490L

        private val YESTERDAY = LocalDate.now().minusDays(1)
        private val TOMORROW = LocalDate.now().plusDays(1)

        private val periods = listOf(
            ProjectPeriod(number = 1, start = 1, end = 6),
            ProjectPeriod(number = 2, start = 7, end = 12),
            ProjectPeriod(number = 3, start = 13, end = 15),
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
            )
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
            )
        )
    }

    @MockK
    lateinit var reportPersistence: ProjectReportPersistence

    @MockK
    lateinit var reportIdentificationPersistence: ProjectReportIdentificationPersistence

    @MockK
    lateinit var partnerPersistence: PartnerPersistence

    @MockK
    lateinit var projectPersistence: ProjectPersistence

    lateinit var generalValidator: GeneralValidatorService

    lateinit var updateIdentification: UpdateProjectPartnerReportIdentification

    @BeforeEach
    fun setup() {
        MockKAnnotations.init(this)
        generalValidator = GeneralValidatorDefaultImpl()
        updateIdentification = UpdateProjectPartnerReportIdentification(
            reportPersistence,
            reportIdentificationPersistence,
            partnerPersistence,
            projectPersistence,
            generalValidator,
        )
    }

    @Test
    fun updateIdentification() {
        val reportId = 66L
        val projectId = 112L
        every { reportPersistence.getPartnerReportStatusAndVersion(PARTNER_ID, reportId = reportId) } returns
            ProjectPartnerReportStatusAndVersion(ReportStatus.Draft, "17.0.1")
        every { partnerPersistence.getProjectIdForPartnerId(id = PARTNER_ID, version = "17.0.1") } returns projectId
        every { projectPersistence.getProjectPeriods(projectId, "17.0.1") } returns periods
        val slotData = slot<io.cloudflight.jems.server.project.service.report.model.identification.UpdateProjectPartnerReportIdentification>()
        val resultMock = mockk<ProjectPartnerReportIdentification>()
        every { reportIdentificationPersistence.updatePartnerReportIdentification(PARTNER_ID, reportId = reportId, capture(slotData)) } returns resultMock

        assertThat(updateIdentification.updateIdentification(PARTNER_ID, reportId = reportId, updateData)).isEqualTo(resultMock)

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

        assertThat(ex.formErrors).hasSize(5)
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
    }

    @Test
    fun `updateIdentification - wrong period`() {
        val reportId = 75L
        val projectId = 125L
        every { reportPersistence.getPartnerReportStatusAndVersion(PARTNER_ID, reportId = reportId) } returns
            ProjectPartnerReportStatusAndVersion(ReportStatus.Draft, "8.0")
        every { partnerPersistence.getProjectIdForPartnerId(id = PARTNER_ID, version = "8.0") } returns projectId
        every { projectPersistence.getProjectPeriods(projectId, "8.0") } returns periods

        val ex = assertThrows<InvalidPeriodNumber> {
            updateIdentification.updateIdentification(PARTNER_ID, reportId = reportId, updateData.copy(period = 4))
        }
        assertThat(ex.message).isEqualTo("Period number 4 is not valid.")
    }

}
