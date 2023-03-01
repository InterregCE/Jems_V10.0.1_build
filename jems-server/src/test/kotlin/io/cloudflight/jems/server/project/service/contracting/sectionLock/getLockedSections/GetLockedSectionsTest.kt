package io.cloudflight.jems.server.project.service.contracting.sectionLock.getLockedSections

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.project.service.contracting.model.ProjectContractingSection
import io.cloudflight.jems.server.project.service.contracting.sectionLock.ProjectContractingSectionLockPersistence
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class GetLockedSectionsTest : UnitTest() {

    @MockK
    lateinit var persistence: ProjectContractingSectionLockPersistence

    @InjectMockKs
    lateinit var interactor: GetLockedSections

    @Test
    fun getLockedSections() {
        val projectId = 1L
        every { persistence.getLockedSections(projectId) } returns listOf(ProjectContractingSection.ProjectManagers)

        assertThat(interactor.getLockedSections(projectId)).isEqualTo(listOf(ProjectContractingSection.ProjectManagers))
    }

    @Test
    fun getLockedSectionsForNonExistentProject() {
        val invalidProjectId = -1L
        val exception = ProjectContractingGetLockedSectionsException(Exception())
        every { persistence.getLockedSections(invalidProjectId) } throws exception

        assertThrows<ProjectContractingGetLockedSectionsException> { interactor.getLockedSections(invalidProjectId) }
    }
}
