package io.cloudflight.jems.server.project.repository.report.project.identification

import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.common.entity.TranslationId
import io.cloudflight.jems.server.project.entity.report.project.ProjectReportEntity
import io.cloudflight.jems.server.project.entity.report.project.identification.ProjectReportIdentificationTargetGroupEntity
import io.cloudflight.jems.server.project.entity.report.project.identification.ProjectReportIdentificationTargetGroupTranslEntity
import io.cloudflight.jems.server.project.repository.report.partner.ProjectPartnerReportRepository
import io.cloudflight.jems.server.project.repository.report.project.base.ProjectReportRepository
import io.cloudflight.jems.server.project.service.contracting.model.reporting.ContractingDeadlineType
import io.cloudflight.jems.server.project.service.model.ProjectTargetGroup
import io.cloudflight.jems.server.project.service.report.model.project.ProjectReportStatus
import io.cloudflight.jems.server.project.service.report.model.project.identification.ProjectReportIdentification
import io.cloudflight.jems.server.project.service.report.model.project.identification.ProjectReportIdentificationTargetGroup
import io.cloudflight.jems.server.project.service.report.model.project.identification.ProjectReportIdentificationUpdate
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.time.ZonedDateTime

internal class ProjectReportIdentificationPersistenceProviderTest: UnitTest() {

    companion object {

        private val identification = ProjectReportIdentification(
            targetGroups = listOf(
                ProjectReportIdentificationTargetGroup(
                    type = ProjectTargetGroup.BusinessSupportOrganisation,
                    sortNumber = 1,
                    description = setOf(InputTranslation(SystemLanguage.EN, "description"))
                )
            ),
            highlights = setOf(),
            partnerProblems = setOf(),
            deviations = setOf(),
            spendingProfiles = listOf()
        )

        private val projectReportEntity = ProjectReportEntity(
            id = 1L,
            projectId = 99L,
            number = 1,
            status = ProjectReportStatus.Draft,
            applicationFormVersion = "3.0",
            startDate = LocalDate.now().minusDays(1),
            endDate = null,

            type = ContractingDeadlineType.Both,
            deadline = mockk(),
            reportingDate = mockk(),
            periodNumber = 4,
            projectIdentifier = "projectIdentifier",
            projectAcronym = "projectAcronym",
            leadPartnerNameInOriginalLanguage = "nameInOriginalLanguage",
            leadPartnerNameInEnglish = "nameInEnglish",

            createdAt = ZonedDateTime.now().minusWeeks(1),
            firstSubmission = ZonedDateTime.now().minusYears(1),
            verificationDate = null,
        )

        private val identificationUpdate = ProjectReportIdentificationUpdate(
            targetGroups = listOf(
                setOf(
                    InputTranslation(SystemLanguage.EN, "description"),
                )),
            highlights = setOf(),
            partnerProblems = setOf(),
            deviations = setOf()
        )

        private val translationId =  TranslationId<ProjectReportIdentificationTargetGroupEntity>(
            sourceEntity = mockk(),
            language = SystemLanguage.EN
        )

        private val projectReportIdentificationTargetGroupTranslEntity = ProjectReportIdentificationTargetGroupTranslEntity(
            translationId = translationId,
            description = "description"
        )

        private val projectReportIdentificationTargetGroupEntity = ProjectReportIdentificationTargetGroupEntity(
            id = 1L,
            projectReportEntity = projectReportEntity,
            type = ProjectTargetGroup.BusinessSupportOrganisation,
            sortNumber = 1,
            translatedValues = mutableSetOf(projectReportIdentificationTargetGroupTranslEntity)
        )

    }

    @MockK
    private lateinit var projectReportRepository: ProjectReportRepository
    @MockK
    private lateinit var targetGroupRepository: ProjectReportIdentificationTargetGroupRepository
    @MockK
    private lateinit var reportSpendingProfileRepository: ProjectReportSpendingProfileRepository
    @MockK
    private lateinit var partnerReportRepository: ProjectPartnerReportRepository


    @InjectMockKs
    private lateinit var persistence: ProjectReportIdentificationPersistenceProvider

    @BeforeEach
    fun reset() {
        clearMocks(projectReportRepository, targetGroupRepository, reportSpendingProfileRepository, partnerReportRepository)
    }

    @Test
    fun getReportIdentification() {
        every {projectReportRepository.getByIdAndProjectId(1L, 99L)} returns projectReportEntity
        every {targetGroupRepository.findAllByProjectReportEntityOrderBySortNumber(projectReportEntity)} returns
            listOf(projectReportIdentificationTargetGroupEntity)

        Assertions.assertThat(persistence.getReportIdentification(99L, reportId = 1L)).isEqualTo(identification)
    }

    @Test
    fun createReportIdentification() {
        every {projectReportRepository.getByIdAndProjectId(1L, 99L)} returns projectReportEntity
        every {targetGroupRepository.findAllByProjectReportEntityOrderBySortNumber(projectReportEntity)} returns
            listOf(projectReportIdentificationTargetGroupEntity)

        Assertions.assertThat(persistence.updateReportIdentification(99L, reportId = 1L, identificationUpdate)).isEqualTo(identification)
    }
}
