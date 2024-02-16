package io.cloudflight.jems.server.project.controller.report.project.identification

import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.api.project.dto.description.ProjectTargetGroupDTO
import io.cloudflight.jems.api.project.dto.report.project.identification.ProjectReportIdentificationDTO
import io.cloudflight.jems.api.project.dto.report.project.identification.ProjectReportIdentificationTargetGroupDTO
import io.cloudflight.jems.api.project.dto.report.project.identification.ProjectReportSpendingProfileDTO
import io.cloudflight.jems.api.project.dto.report.project.identification.ProjectReportSpendingProfileTotalDto
import io.cloudflight.jems.api.project.dto.report.project.identification.UpdateProjectReportIdentificationDTO
import io.cloudflight.jems.api.project.dto.report.project.identification.resultIndicator.ProjectReportOutputIndicatorOverviewDTO
import io.cloudflight.jems.api.project.dto.report.project.identification.resultIndicator.ProjectReportOutputLineOverviewDTO
import io.cloudflight.jems.api.project.dto.report.project.identification.resultIndicator.ProjectReportResultIndicatorOverviewDTO
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.project.service.model.ProjectTargetGroup
import io.cloudflight.jems.server.project.service.report.model.project.identification.ProjectReportIdentification
import io.cloudflight.jems.server.project.service.report.model.project.identification.ProjectReportIdentificationTargetGroup
import io.cloudflight.jems.server.project.service.report.model.project.identification.ProjectReportIdentificationUpdate
import io.cloudflight.jems.server.project.service.report.model.project.identification.ProjectReportSpendingProfile
import io.cloudflight.jems.server.project.service.report.model.project.identification.SpendingProfileTotal
import io.cloudflight.jems.server.project.service.report.model.project.identification.overview.ProjectReportOutputIndicatorOverview
import io.cloudflight.jems.server.project.service.report.model.project.identification.overview.ProjectReportOutputLineOverview
import io.cloudflight.jems.server.project.service.report.model.project.identification.overview.ProjectReportResultIndicatorOverview
import io.cloudflight.jems.server.project.service.report.project.identification.getProjectReportIdentification.GetProjectReportIdentificationInteractor
import io.cloudflight.jems.server.project.service.report.project.identification.getProjectReportResultIndicatorOverview.GetProjectReportResultIndicatorOverviewInteractor
import io.cloudflight.jems.server.project.service.report.project.identification.updateProjectReportIdentification.UpdateProjectReportIdentificationInteractor
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.math.BigDecimal

internal class ProjectReportIdentificationControllerTest : UnitTest() {

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
            spendingProfilePerPartner = ProjectReportSpendingProfile(
                lines = emptyList(),
                total = SpendingProfileTotal(
                    BigDecimal.ZERO,
                    BigDecimal.ZERO,
                    BigDecimal.ZERO,
                    BigDecimal.ZERO,
                    BigDecimal.ZERO,
                    BigDecimal.ZERO,
                    BigDecimal.ZERO,
                    BigDecimal.ZERO,
                    BigDecimal.ZERO,
                    BigDecimal.ZERO,
                    BigDecimal.ZERO
                )
            )
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
            spendingProfilePerPartner = ProjectReportSpendingProfileDTO(
                lines = emptyList(),
                total = ProjectReportSpendingProfileTotalDto(
                    BigDecimal.ZERO,
                    BigDecimal.ZERO,
                    BigDecimal.ZERO,
                    BigDecimal.ZERO,
                    BigDecimal.ZERO,
                    BigDecimal.ZERO,
                    BigDecimal.ZERO,
                    BigDecimal.ZERO,
                    BigDecimal.ZERO,
                    BigDecimal.ZERO,
                    BigDecimal.ZERO
                )
            )
        )

        private val identificationUpdate = ProjectReportIdentificationUpdate(
            targetGroups = listOf(
                setOf(
                    InputTranslation(SystemLanguage.EN, "highlights EN"),
                    InputTranslation(SystemLanguage.DE, "highlights DE")
                )
            ),
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
                )
            ),
            highlights = setOf(
                InputTranslation(SystemLanguage.EN, "highlights EN"),
                InputTranslation(SystemLanguage.DE, "highlights DE")
            ),
            partnerProblems = setOf(),
            deviations = setOf()
        )

        private fun resultIndicatorMap()
                : Map<ProjectReportResultIndicatorOverview, Map<ProjectReportOutputIndicatorOverview, List<ProjectReportOutputLineOverview>>> {
            val resultIndicator = ProjectReportResultIndicatorOverview(
                id = 1L,
                identifier = "2",
                name = setOf(InputTranslation(SystemLanguage.EN, "name")),
                measurementUnit = setOf(InputTranslation(SystemLanguage.EN, "mu")),
                baseline = BigDecimal.valueOf(3),
                targetValue = BigDecimal.valueOf(4),
                previouslyReported = BigDecimal.valueOf(5),
                currentReport = BigDecimal.valueOf(6),
            )
            return mapOf(resultIndicator to outputIndicatorMap(resultIndicator))
        }

        private fun outputIndicatorMap(
            resultIndicator: ProjectReportResultIndicatorOverview
        ): Map<ProjectReportOutputIndicatorOverview, List<ProjectReportOutputLineOverview>> {
            val outputIndicator = ProjectReportOutputIndicatorOverview(
                id = 11L,
                identifier = "12",
                name = setOf(InputTranslation(SystemLanguage.EN, "name2")),
                measurementUnit = setOf(InputTranslation(SystemLanguage.EN, "mu2")),
                resultIndicator = resultIndicator,
                targetValue = BigDecimal.valueOf(14),
                previouslyReported = BigDecimal.valueOf(15),
                currentReport = BigDecimal.valueOf(16),
            )

            return mapOf(outputIndicator to outputList(outputIndicator))
        }

        private fun outputList(outputIndicator: ProjectReportOutputIndicatorOverview): List<ProjectReportOutputLineOverview> {
            return listOf(
                ProjectReportOutputLineOverview(
                    number = 5,
                    workPackageNumber = 7,
                    name = setOf(InputTranslation(SystemLanguage.EN, "name3")),
                    measurementUnit = setOf(InputTranslation(SystemLanguage.EN, "mu3")),
                    deactivated = false,
                    outputIndicator = outputIndicator,
                    targetValue = BigDecimal.valueOf(24),
                    previouslyReported = BigDecimal.valueOf(25),
                    currentReport = BigDecimal.valueOf(26),
                )
            )
        }

        private val resultIndicatorDTO = listOf(
            ProjectReportResultIndicatorOverviewDTO(
                id = 1L,
                identifier = "2",
                name = setOf(InputTranslation(SystemLanguage.EN, "name")),
                measurementUnit = setOf(InputTranslation(SystemLanguage.EN, "mu")),
                baseline = BigDecimal.valueOf(3),
                targetValue = BigDecimal.valueOf(4),
                previouslyReported = BigDecimal.valueOf(5),
                currentReport = BigDecimal.valueOf(6),
                outputOverviews = listOf(
                    ProjectReportOutputIndicatorOverviewDTO(
                        id = 11L,
                        identifier = "12",
                        name = setOf(InputTranslation(SystemLanguage.EN, "name2")),
                        measurementUnit = setOf(InputTranslation(SystemLanguage.EN, "mu2")),
                        targetValue = BigDecimal.valueOf(14),
                        previouslyReported = BigDecimal.valueOf(15),
                        currentReport = BigDecimal.valueOf(16),
                        outputs = listOf(
                            ProjectReportOutputLineOverviewDTO(
                                number = 5,
                                workPackageNumber = 7,
                                name = setOf(InputTranslation(SystemLanguage.EN, "name3")),
                                measurementUnit = setOf(InputTranslation(SystemLanguage.EN, "mu3")),
                                deactivated = false,
                                targetValue = BigDecimal.valueOf(24),
                                previouslyReported = BigDecimal.valueOf(25),
                                currentReport = BigDecimal.valueOf(26),
                            )
                        )
                    )
                )
            )
        )
    }

    @MockK
    private lateinit var getProjectReportIdentification: GetProjectReportIdentificationInteractor

    @MockK
    private lateinit var updateProjectReportIdentification: UpdateProjectReportIdentificationInteractor

    @MockK
    private lateinit var getProjectReportResultIndicatorOverview: GetProjectReportResultIndicatorOverviewInteractor

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
        every {
            updateProjectReportIdentification.updateIdentification(
                PROJECT_ID,
                REPORT_ID,
                identificationUpdate
            )
        } returns identification
        assertThat(
            controller.updateProjectReportIdentification(
                PROJECT_ID,
                REPORT_ID,
                identificationUpdateDTO
            )
        ).isEqualTo(identificationDTO)
    }

    @Test
    fun getResultIndicatorOverview() {
        every {
            getProjectReportResultIndicatorOverview.getResultIndicatorOverview(
                PROJECT_ID,
                REPORT_ID
            )
        } returns resultIndicatorMap()
        assertThat(controller.getResultIndicatorOverview(PROJECT_ID, REPORT_ID)).isEqualTo(resultIndicatorDTO)
    }
}
