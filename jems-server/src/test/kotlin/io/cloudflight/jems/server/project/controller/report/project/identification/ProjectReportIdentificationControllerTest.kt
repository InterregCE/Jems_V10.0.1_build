package io.cloudflight.jems.server.project.controller.report.project.identification

import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.api.project.dto.description.ProjectTargetGroupDTO
import io.cloudflight.jems.api.project.dto.report.project.identification.ProjectReportIdentificationDTO
import io.cloudflight.jems.api.project.dto.report.project.identification.ProjectReportIdentificationTargetGroupDTO
import io.cloudflight.jems.api.project.dto.report.project.identification.UpdateProjectReportIdentificationDTO
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.project.service.model.ProjectTargetGroup
import io.cloudflight.jems.server.project.service.report.model.project.identification.ProjectReportIdentification
import io.cloudflight.jems.server.project.service.report.model.project.identification.ProjectReportIdentificationTargetGroup
import io.cloudflight.jems.server.project.service.report.model.project.identification.ProjectReportIdentificationUpdate
import io.cloudflight.jems.server.project.service.report.project.identification.getProjectReportIdentification.GetProjectReportIdentificationInteractor
import io.cloudflight.jems.server.project.service.report.project.identification.updateProjectReportIdentification.UpdateProjectReportIdentificationInteractor
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class ProjectReportIdentificationControllerTest: UnitTest() {

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
            spendingProfiles = listOf()
        )

        private val identificationDTO = ProjectReportIdentificationDTO(
            targetGroups = listOf(
                ProjectReportIdentificationTargetGroupDTO(
                    type = ProjectTargetGroupDTO.CrossBorderLegalBody,
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
            spendingProfiles = listOf()
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

        private val identificationUpdateDTO = UpdateProjectReportIdentificationDTO(
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
    }

    @MockK
    private lateinit var getProjectReportIdentification: GetProjectReportIdentificationInteractor
    @MockK
    private lateinit var updateProjectReportIdentification: UpdateProjectReportIdentificationInteractor

    @InjectMockKs
    private lateinit var controller: ProjectReportIdentificationController

    @BeforeEach
    fun resetMocks() {
        clearMocks(getProjectReportIdentification, updateProjectReportIdentification)
    }

    @Test
    fun getProjectReportIdentification() {
        every { getProjectReportIdentification.getIdentification(PROJECT_ID, REPORT_ID) } returns identification
        assertThat(controller.getProjectReportIdentification(PROJECT_ID, REPORT_ID)).isEqualTo(identificationDTO)
    }

    @Test
    fun updateProjectReportIdentification() {
        every { updateProjectReportIdentification.updateIdentification(PROJECT_ID, REPORT_ID, identificationUpdate) } returns identification
        assertThat(controller.updateProjectReportIdentification(PROJECT_ID, REPORT_ID, identificationUpdateDTO)).isEqualTo(identificationDTO)
    }
}
