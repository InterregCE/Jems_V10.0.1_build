package io.cloudflight.jems.server.project.repository.lumpsum

import io.cloudflight.jems.api.programme.dto.costoption.ProgrammeLumpSumPhase
import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.call.createTestCallEntity
import io.cloudflight.jems.server.common.exception.ResourceNotFoundException
import io.cloudflight.jems.server.programme.entity.costoption.ProgrammeLumpSumEntity
import io.cloudflight.jems.server.programme.entity.legalstatus.ProgrammeLegalStatusEntity
import io.cloudflight.jems.server.programme.repository.costoption.ProgrammeLumpSumRepository
import io.cloudflight.jems.server.programme.repository.costoption.combineLumpSumTranslatedValues
import io.cloudflight.jems.server.project.entity.ProjectEntity
import io.cloudflight.jems.server.project.entity.ProjectStatusHistoryEntity
import io.cloudflight.jems.server.project.entity.lumpsum.ProjectLumpSumEntity
import io.cloudflight.jems.server.project.entity.lumpsum.ProjectLumpSumId
import io.cloudflight.jems.server.project.entity.lumpsum.ProjectPartnerLumpSumEntity
import io.cloudflight.jems.server.project.entity.lumpsum.ProjectPartnerLumpSumId
import io.cloudflight.jems.server.project.entity.partner.ProjectPartnerEntity
import io.cloudflight.jems.server.project.repository.ProjectRepository
import io.cloudflight.jems.server.project.repository.ProjectVersionRepository
import io.cloudflight.jems.server.project.repository.ProjectVersionUtils
import io.cloudflight.jems.server.project.repository.partner.ProjectPartnerRepository
import io.cloudflight.jems.server.project.service.application.ApplicationStatus
import io.cloudflight.jems.server.project.service.lumpsum.model.ProjectLumpSum
import io.cloudflight.jems.server.project.service.lumpsum.model.ProjectPartnerLumpSum
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerRole
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.slot
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.math.BigDecimal
import java.util.*

internal class ProjectLumpSumPersistenceTest : UnitTest() {

    companion object {
        private val dummyCall = createTestCallEntity(10)

        private val dummyProject = ProjectEntity(
            id = 1,
            call = dummyCall,
            acronym = "Test Project",
            applicant = dummyCall.creator,
            currentStatus = ProjectStatusHistoryEntity(
                id = 1,
                status = ApplicationStatus.DRAFT,
                user = dummyCall.creator
            ),
        )

        private fun partner(sortNumber: Int, id: Long) = ProjectPartnerEntity(
            id = id,
            project = dummyProject,
            abbreviation = "",
            role = ProjectPartnerRole.LEAD_PARTNER,
            sortNumber = sortNumber,
            legalStatus = ProgrammeLegalStatusEntity(id = 1),
        )

        private fun programmeLumpSum(id: Long) = ProgrammeLumpSumEntity(
            id = id,
            translatedValues = combineLumpSumTranslatedValues(
                programmeLumpSumId = id,
                name = setOf(InputTranslation(SystemLanguage.EN, "")),
                description = emptySet()
            ),
            cost = BigDecimal.TEN,
            splittingAllowed = true,
            phase = ProgrammeLumpSumPhase.Preparation,
        )

    }

    @MockK
    lateinit var projectRepository: ProjectRepository

    @MockK
    lateinit var projectPartnerRepository: ProjectPartnerRepository

    @MockK
    lateinit var programmeLumpSumRepository: ProgrammeLumpSumRepository

    @MockK
    lateinit var projectLumpSumRepository: ProjectLumpSumRepository

    @MockK
    lateinit var projectVersionRepo: ProjectVersionRepository

    private lateinit var persistence: ProjectLumpSumPersistenceProvider

    private lateinit var projectVersionUtils: ProjectVersionUtils

    @BeforeEach
    fun setup() {
        MockKAnnotations.init(this)
        projectVersionUtils = ProjectVersionUtils(projectVersionRepo)
        persistence = ProjectLumpSumPersistenceProvider(projectVersionUtils, projectRepository, projectLumpSumRepository, programmeLumpSumRepository, projectPartnerRepository)
    }

    @Test
    fun `get lump sums - not-existing project`() {
        every { projectRepository.findById(eq(-1)) } returns Optional.empty()
        val ex = assertThrows<ResourceNotFoundException> { persistence.getLumpSums(-1) }
        assertThat(ex.entity).isEqualTo("project")
    }

    @Test
    fun `lump sums are correctly mapped and sorted`() {
        val programmeLumpSum = programmeLumpSum(id = 50)
        val projectLumpSumId = ProjectLumpSumId(projectId = dummyProject.id, orderNr = 1)
        val contribution1 = ProjectPartnerLumpSumEntity(
            id = ProjectPartnerLumpSumId(projectLumpSumId, partner(sortNumber = 1, id = 1)),
            amount = BigDecimal.TEN,
        )
        val contribution2 = ProjectPartnerLumpSumEntity(
            id = ProjectPartnerLumpSumId(projectLumpSumId, partner(sortNumber = 2, id = 2)),
            amount = BigDecimal.ONE,
        )
        val lumpSum1 = ProjectLumpSumEntity(
            id = projectLumpSumId,
            programmeLumpSum = programmeLumpSum,
            endPeriod = 7,
            lumpSumContributions = setOf(contribution2, contribution1)
        )
        val lumpSum2 = ProjectLumpSumEntity(
            id = ProjectLumpSumId(projectId = dummyProject.id, orderNr = 2),
            programmeLumpSum = programmeLumpSum,
            endPeriod = 8,
        )

        every { projectRepository.findById(eq(1)) } returns Optional.of(
            dummyProject.copy(
                lumpSums = listOf(
                    lumpSum2,
                    lumpSum1
                )
            )
        )

        assertThat(persistence.getLumpSums(1L)).containsExactly(
            ProjectLumpSum(
                programmeLumpSumId = 50,
                period = 7,
                lumpSumContributions = listOf(
                    ProjectPartnerLumpSum(partnerId = 1, amount = BigDecimal.TEN),
                    ProjectPartnerLumpSum(partnerId = 2, amount = BigDecimal.ONE),
                )
            ),
            ProjectLumpSum(
                programmeLumpSumId = 50,
                period = 8,
            ),
        )
    }

    @Test
    fun `updateLumpSums - everything valid`() {
        val projectSlot = slot<ProjectEntity>()
        every { projectRepository.findById(10L) } returns Optional.of(
            dummyProject.copy(
                id = 10,
                lumpSums = emptyList()
            )
        )

        every { programmeLumpSumRepository.findById(20) } returns Optional.of(programmeLumpSum(20))

        val partner17 = partner(sortNumber = 1, id = 17)
        val partner18 = partner(sortNumber = 2, id = 18)
        every { projectPartnerRepository.findFirstByProjectIdAndId(10, 17) } returns Optional.of(partner17)
        every { projectPartnerRepository.findFirstByProjectIdAndId(10, 18) } returns Optional.of(partner18)

        // we do not need to test mapping back to model as that is covered by getWorkPackageActivitiesForWorkPackage
        every { projectRepository.save(capture(projectSlot)) } returnsArgument 0

        val toBeSaved = listOf(
            ProjectLumpSum(
                programmeLumpSumId = 20,
                period = 7,
                lumpSumContributions = listOf(
                    ProjectPartnerLumpSum(partnerId = partner17.id, amount = BigDecimal.TEN),
                    ProjectPartnerLumpSum(partnerId = partner18.id, amount = BigDecimal.ONE),
                )
            ),
        )

        persistence.updateLumpSums(10L, toBeSaved)

        assertThat(projectSlot.captured.lumpSums).hasSize(1)

        val expectedProjectLumpSumId = ProjectLumpSumId(projectId = 10, orderNr = 1)
        val outcome = projectSlot.captured.lumpSums.first()
        assertThat(outcome.id).isEqualTo(expectedProjectLumpSumId)
        assertThat(outcome.programmeLumpSum).isEqualTo(programmeLumpSum(20))
        assertThat(outcome.endPeriod).isEqualTo(7)
        assertThat(outcome.lumpSumContributions).containsExactlyInAnyOrder(
            ProjectPartnerLumpSumEntity(
                id = ProjectPartnerLumpSumId(projectLumpSumId = expectedProjectLumpSumId, projectPartner = partner17),
                amount = BigDecimal.TEN
            ),
            ProjectPartnerLumpSumEntity(
                id = ProjectPartnerLumpSumId(projectLumpSumId = expectedProjectLumpSumId, projectPartner = partner18),
                amount = BigDecimal.ONE
            ),
        )
    }

    @Test
    fun `updateLumpSums - when not existing project`() {
        every { projectRepository.findById(-1) } returns Optional.empty()
        val ex = assertThrows<ResourceNotFoundException> { persistence.updateLumpSums(-1, emptyList()) }
        assertThat(ex.entity).isEqualTo("project")
    }

    @Test
    fun `updateLumpSums - when not existing programme lump sum`() {
        every { projectRepository.findById(11L) } returns Optional.of(
            dummyProject.copy(
                id = 11,
                lumpSums = emptyList()
            )
        )
        every { programmeLumpSumRepository.findById(-1) } returns Optional.empty()

        val toBeSaved = listOf(
            ProjectLumpSum(
                programmeLumpSumId = -1,
                period = 7,
            ),
        )

        val ex = assertThrows<ResourceNotFoundException> { persistence.updateLumpSums(11, toBeSaved) }
        assertThat(ex.entity).isEqualTo("programmeLumpSum")
    }

    @Test
    fun `updateLumpSums - when not existing or not visible partner`() {
        val projectSlot = slot<ProjectEntity>()
        every { projectRepository.findById(12L) } returns Optional.of(
            dummyProject.copy(
                id = 12,
                lumpSums = emptyList()
            )
        )

        every { programmeLumpSumRepository.findById(20) } returns Optional.of(programmeLumpSum(20))

        every { projectPartnerRepository.findFirstByProjectIdAndId(12, -1) } returns Optional.empty()

        // we do not need to test mapping back to model as that is covered by getWorkPackageActivitiesForWorkPackage
        every { projectRepository.save(capture(projectSlot)) } returnsArgument 0

        val toBeSaved = listOf(
            ProjectLumpSum(
                programmeLumpSumId = 20,
                period = 7,
                lumpSumContributions = listOf(
                    // not existing partner ID
                    ProjectPartnerLumpSum(partnerId = -1, amount = BigDecimal.TEN),
                )
            ),
        )

        val ex = assertThrows<ResourceNotFoundException> { persistence.updateLumpSums(12, toBeSaved) }
        assertThat(ex.entity).isEqualTo("projectPartner")
    }

}
