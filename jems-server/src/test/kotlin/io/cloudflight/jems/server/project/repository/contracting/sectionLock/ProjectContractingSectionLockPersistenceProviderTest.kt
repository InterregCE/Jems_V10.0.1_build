package io.cloudflight.jems.server.project.repository.contracting.sectionLock

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.project.entity.contracting.ProjectContractingSectionLockEntity
import io.cloudflight.jems.server.project.entity.contracting.ProjectContractingSectionLockId
import io.cloudflight.jems.server.project.service.contracting.model.ProjectContractingSection
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.util.Optional

internal class ProjectContractingSectionLockPersistenceProviderTest : UnitTest() {


    companion object {
        const val PROJECT_ID = 21L

        private val lockedSectionsEntities = listOf(
            ProjectContractingSectionLockEntity(
                contractingSectionLockId = ProjectContractingSectionLockId(
                    PROJECT_ID,
                    ProjectContractingSection.ContractsAgreements
                )
            ),
            ProjectContractingSectionLockEntity(
                contractingSectionLockId = ProjectContractingSectionLockId(
                    PROJECT_ID,
                    ProjectContractingSection.ProjectReportingSchedule
                )
            )
        )

        private val lockedSections = setOf<ProjectContractingSection>(
            ProjectContractingSection.ContractsAgreements,
            ProjectContractingSection.ProjectReportingSchedule,
        )
    }

    @MockK
    lateinit var projectContractingSectionLockRepository: ProjectContractingSectionLockRepository

    @InjectMockKs
    lateinit var projectContractingSectionLockPersistenceProvider: ProjectContractingSectionLockPersistenceProvider

    @Test
    fun `locked sections are fetched and mapped`() {
        every { projectContractingSectionLockRepository.findAllByContractingSectionLockIdProjectId(PROJECT_ID) } returns lockedSectionsEntities
        assertThat(projectContractingSectionLockPersistenceProvider.getLockedSections(PROJECT_ID)).containsAll(
            lockedSections
        )
    }

    @Test
    fun `check if section is locked`() {
        every {
            projectContractingSectionLockRepository.findById(
                ProjectContractingSectionLockId(
                    PROJECT_ID,
                    ProjectContractingSection.ContractsAgreements
                )
            )
        } returns Optional.of(mockk())
        assertThat(
            projectContractingSectionLockPersistenceProvider.isLocked(
                PROJECT_ID,
                ProjectContractingSection.ContractsAgreements
            )
        ).isTrue

    }
}