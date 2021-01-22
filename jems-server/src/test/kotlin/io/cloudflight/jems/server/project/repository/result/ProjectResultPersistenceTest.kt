package io.cloudflight.jems.server.project.repository.result

import io.cloudflight.jems.api.call.dto.CallStatus
import io.cloudflight.jems.api.project.dto.status.ProjectApplicationStatus
import io.cloudflight.jems.server.call.entity.CallEntity
import io.cloudflight.jems.server.programme.entity.indicator.IndicatorResult
import io.cloudflight.jems.server.programme.repository.indicator.IndicatorResultRepository
import io.cloudflight.jems.server.project.entity.ProjectEntity
import io.cloudflight.jems.server.project.entity.ProjectPeriodEntity
import io.cloudflight.jems.server.project.entity.ProjectPeriodId
import io.cloudflight.jems.server.project.entity.ProjectStatus
import io.cloudflight.jems.server.project.entity.result.ProjectResultEntity
import io.cloudflight.jems.server.project.repository.ProjectRepository
import io.cloudflight.jems.server.project.repository.description.ProjectPeriodRepository
import io.cloudflight.jems.server.user.entity.User
import io.cloudflight.jems.server.user.entity.UserRole
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Sort
import java.time.ZonedDateTime
import java.util.Optional
import java.util.UUID

@ExtendWith(MockKExtension::class)
class ProjectResultPersistenceTest {

    companion object {
        val projectPeriodEntity = ProjectPeriodEntity(ProjectPeriodId(1, 3), 1, 12)
        val user = User(id = 1, name = "applicant", userRole = UserRole(id = 1, name = "APPLICANT"), email = "", surname = "test", password = "")
        val callEntity = CallEntity(
            id = 1,
            name = "test",
            lengthOfPeriod = 12,
            status = CallStatus.DRAFT,
            priorityPolicies = emptySet(),
            creator = user,
            startDate = ZonedDateTime.now(),
            endDate = ZonedDateTime.now(),
            funds = emptySet(),
            strategies = emptySet())
        val projectStatus = ProjectStatus(status = ProjectApplicationStatus.DRAFT, user = user)

        val projectEntity = ProjectEntity(id = 1, acronym = "test", applicant = user, call = callEntity, projectStatus = projectStatus)
        val indicatorResult = IndicatorResult(
            id = 1,
            identifier = "id",
            code = "code",
            name = "indicator title",
            programmePriorityPolicy = null,
            measurementUnit = "unit"
        )
        val projectResult = ProjectResultEntity(
            id = UUID.randomUUID(),
            project = projectEntity,
            resultNumber = 1,
            period = projectPeriodEntity,
            programmeResultIndicator = indicatorResult
        )
    }

    @RelaxedMockK
    lateinit var projectResultRepository: ProjectResultRepository

    @RelaxedMockK
    lateinit var projectRepository: ProjectRepository

    @RelaxedMockK
    lateinit var indicatorResultRepository: IndicatorResultRepository

    @RelaxedMockK
    lateinit var projectPeriodRepository: ProjectPeriodRepository

    @InjectMockKs
    private lateinit var persistence: ProjectResultPersistenceProvider

    @Test
    fun `get project results for project`() {
        every { projectResultRepository.findAllByProjectId(1, Sort.by("resultNumber")) } returns PageImpl(listOf(projectResult))

        val result = persistence.getProjectResultsForProject(1)

        assertThat(result).isEqualTo(setOf(projectResult.ProjectResultDTO()))
    }

    @Test
    fun `project results are updated`() {
        every { projectRepository.findById(1) } returns Optional.of(projectEntity)
        val dummyProjectEntity = ProjectEntity(id = 1, acronym = "test", applicant = user, call = callEntity, projectStatus = projectStatus)
        every { projectResultRepository.save(any<ProjectResultEntity>()) } returns projectResult.copy(project = dummyProjectEntity)
        every { indicatorResultRepository.findById(indicatorResult.id) } returns Optional.of(indicatorResult)
        every { projectPeriodRepository.findByIdProjectIdAndIdNumber(projectEntity.id, projectPeriodEntity.id.number) } returns projectPeriodEntity

        val result = persistence.updateProjectResults(1, setOf(projectResult.ProjectResultDTO()))

        assertThat(result).isEqualTo(setOf(projectResult.ProjectResultDTO()))
        verify(exactly = 2) { projectResultRepository.save(projectResult) }
        verify { projectPeriodRepository.findByIdProjectIdAndIdNumber(projectEntity.id, projectPeriodEntity.id.number) }
    }

}
