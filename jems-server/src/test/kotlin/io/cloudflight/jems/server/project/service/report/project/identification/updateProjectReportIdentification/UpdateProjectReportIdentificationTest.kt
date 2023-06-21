package io.cloudflight.jems.server.project.service.report.project.identification.updateProjectReportIdentification

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.common.validator.AppInputValidationException
import io.cloudflight.jems.server.common.validator.GeneralValidatorDefaultImpl
import io.cloudflight.jems.server.common.validator.GeneralValidatorService
import io.cloudflight.jems.server.programme.service.priority.getStringOfLength
import io.cloudflight.jems.server.project.service.model.ProjectTargetGroup
import io.cloudflight.jems.server.project.service.report.model.project.identification.ProjectReportIdentification
import io.cloudflight.jems.server.project.service.report.model.project.identification.ProjectReportIdentificationTargetGroup
import io.cloudflight.jems.server.project.service.report.model.project.identification.ProjectReportIdentificationUpdate
import io.cloudflight.jems.server.project.service.report.project.identification.ProjectReportIdentificationPersistence
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class UpdateProjectReportIdentificationTest: UnitTest() {

    companion object {
        private const val PROJECT_ID = 1L
        private const val REPORT_ID = 2L

        private val identification = ProjectReportIdentification(
            targetGroups = listOf(
                ProjectReportIdentificationTargetGroup(
                    type = ProjectTargetGroup.CrossBorderLegalBody,
                    sortNumber = 1,
                    description = setOf(InputTranslation(SystemLanguage.EN, "description"))
                )
            ),
            highlights = setOf(
                InputTranslation(SystemLanguage.EN, "highlights EN"),
                InputTranslation(SystemLanguage.DE, "highlights DE")
            ),
            partnerProblems = setOf(),
            deviations = setOf(),
            spendingProfiles = listOf(),
        )

        private val identificationUpdate = ProjectReportIdentificationUpdate(
            targetGroups = listOf(
                setOf(
                    InputTranslation(SystemLanguage.EN, "highlights EN"),
                    InputTranslation(SystemLanguage.DE, "highlights DE")
                )),
            highlights = setOf(
                InputTranslation(SystemLanguage.EN, "highlights EN"),
                InputTranslation(SystemLanguage.DE, "highlights DE")
            ),
            partnerProblems = setOf(),
            deviations = setOf()
        )

        private val identificationUpdateInvalid = ProjectReportIdentificationUpdate(
            highlights = setOf(InputTranslation(SystemLanguage.EN, getStringOfLength(5001))),
            partnerProblems = setOf(InputTranslation(SystemLanguage.EN, getStringOfLength(5001))),
            targetGroups = listOf(
                emptySet(),
                setOf(InputTranslation(SystemLanguage.EN, getStringOfLength(2001))),
            ),
            deviations = setOf(),
        )
    }

    @MockK
    private lateinit var projectReportIdentification: ProjectReportIdentificationPersistence

    lateinit var generalValidator: GeneralValidatorService

    lateinit var interactor: UpdateProjectReportIdentification

    @BeforeEach
    fun setup() {
        MockKAnnotations.init(this)
        generalValidator = GeneralValidatorDefaultImpl()
        interactor = UpdateProjectReportIdentification(projectReportIdentification, generalValidator)
    }

    @Test
    fun updateIdentification() {
        every { projectReportIdentification.updateReportIdentification(PROJECT_ID, REPORT_ID, identificationUpdate) } returns identification
        assertThat(interactor.updateIdentification(PROJECT_ID, REPORT_ID, identificationUpdate)).isEqualTo(identification)
    }

    @Test
    fun updateIdentificationWithWrongInputs() {
        val ex = assertThrows<AppInputValidationException> {
            interactor.updateIdentification(PROJECT_ID, REPORT_ID, identificationUpdateInvalid)
        }

        assertThat(ex.formErrors).hasSize(3)
        assertThat(ex.formErrors["highlights.language.en"]).isEqualTo(
            I18nMessage("common.error.field.max.length", mapOf("actualLength" to "5001", "requiredLength" to "5000"))
        )
        assertThat(ex.formErrors["partnerProblems.language.en"]).isEqualTo(
            I18nMessage("common.error.field.max.length", mapOf("actualLength" to "5001", "requiredLength" to "5000"))
        )
        assertThat(ex.formErrors["descriptionOfTheTargetGroup[1].language.en"]).isEqualTo(
            I18nMessage("common.error.field.max.length", mapOf("actualLength" to "2001", "requiredLength" to "2000"))
        )
    }
}
