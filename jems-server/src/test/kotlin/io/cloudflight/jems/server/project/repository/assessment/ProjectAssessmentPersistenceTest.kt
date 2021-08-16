package io.cloudflight.jems.server.project.repository.assessment

import io.cloudflight.jems.api.project.dto.assessment.ProjectAssessmentEligibilityResult
import io.cloudflight.jems.api.project.dto.assessment.ProjectAssessmentQualityResult
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.project.entity.ProjectEntity
import io.cloudflight.jems.server.project.entity.ProjectStatusHistoryEntity
import io.cloudflight.jems.server.project.entity.assessment.ProjectAssessmentEligibilityEntity
import io.cloudflight.jems.server.project.entity.assessment.ProjectAssessmentId
import io.cloudflight.jems.server.project.entity.assessment.ProjectAssessmentQualityEntity
import io.cloudflight.jems.server.project.repository.ProjectRepository
import io.cloudflight.jems.server.project.service.application.ApplicationStatus
import io.cloudflight.jems.server.project.service.model.assessment.ProjectAssessmentEligibility
import io.cloudflight.jems.server.project.service.model.assessment.ProjectAssessmentQuality
import io.cloudflight.jems.server.user.repository.user.UserRepository
import io.cloudflight.jems.server.utils.partner.ProjectPartnerTestUtil.Companion.call
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.slot
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class ProjectAssessmentPersistenceTest : UnitTest() {

    companion object {
        private fun projectWithId(id: Long) = ProjectEntity(
            id = id,
            call = call,
            acronym = "Test Project",
            applicant = call.creator,
            currentStatus = ProjectStatusHistoryEntity(
                id = 1,
                status = ApplicationStatus.SUBMITTED,
                user = call.creator
            ),
        )
    }

    @MockK
    lateinit var eligibilityRepository: ProjectAssessmentEligibilityRepository

    @MockK
    lateinit var qualityRepository: ProjectAssessmentQualityRepository

    @MockK
    lateinit var projectRepository: ProjectRepository

    @MockK
    lateinit var userRepository: UserRepository

    @InjectMockKs
    private lateinit var persistence: ProjectAssessmentPersistenceProvider

    @Test
    fun eligibilityForStepExists() {
        every { eligibilityRepository.existsByIdProjectIdAndIdStep(285L, 1598) } returns true
        assertThat(persistence.eligibilityForStepExists(285L, 1598)).isTrue
    }

    @Test
    fun setEligibility() {
        val ID = 87L
        val project = projectWithId(ID)
        every { projectRepository.getOne(ID) } returns project
        every { userRepository.getOne(call.creator.id) } returns call.creator

        val eligibilitySlot = slot<ProjectAssessmentEligibilityEntity>()
        every { eligibilityRepository.save(capture(eligibilitySlot)) } returnsArgument 0

        val eligibility = ProjectAssessmentEligibility(
            projectId = ID,
            step = 2,
            result = ProjectAssessmentEligibilityResult.PASSED,
            note = "test note",
        )

        persistence.setEligibility(call.creator.id, eligibility)

        assertThat(eligibilitySlot.captured.id).isEqualTo(ProjectAssessmentId(project, 2))
        assertThat(eligibilitySlot.captured.result).isEqualTo(ProjectAssessmentEligibilityResult.PASSED)
        assertThat(eligibilitySlot.captured.user).isEqualTo(call.creator)
        assertThat(eligibilitySlot.captured.note).isEqualTo("test note")
    }

    @Test
    fun qualityForStepExists() {
        every { qualityRepository.existsByIdProjectIdAndIdStep(811L, 2366) } returns true
        assertThat(persistence.qualityForStepExists(811L, 2366)).isTrue
    }

    @Test
    fun setQuality() {
        val ID = 92L
        val project = projectWithId(ID)
        every { projectRepository.getOne(ID) } returns project
        every { userRepository.getOne(call.creator.id) } returns call.creator

        val qualitySlot = slot<ProjectAssessmentQualityEntity>()
        every { qualityRepository.save(capture(qualitySlot)) } returnsArgument 0

        val quality = ProjectAssessmentQuality(
            projectId = ID,
            step = 2,
            result = ProjectAssessmentQualityResult.NOT_RECOMMENDED,
            note = "test note",
        )

        persistence.setQuality(call.creator.id, quality)

        assertThat(qualitySlot.captured.id).isEqualTo(ProjectAssessmentId(project, 2))
        assertThat(qualitySlot.captured.result).isEqualTo(ProjectAssessmentQualityResult.NOT_RECOMMENDED)
        assertThat(qualitySlot.captured.user).isEqualTo(call.creator)
        assertThat(qualitySlot.captured.note).isEqualTo("test note")
    }

}
