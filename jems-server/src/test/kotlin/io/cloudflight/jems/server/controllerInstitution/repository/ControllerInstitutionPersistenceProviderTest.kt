package io.cloudflight.jems.server.controllerInstitution.repository

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.controllerInstitution.entity.ControllerInstitutionPartnerEntity
import io.cloudflight.jems.server.controllerInstitution.nutsAustria
import io.cloudflight.jems.server.controllerInstitution.service.model.ControllerInstitutionList
import io.cloudflight.jems.server.controllerInstitution.service.model.InstitutionPartnerAssignment
import io.cloudflight.jems.server.nuts.repository.NutsRegion3Repository
import io.cloudflight.jems.server.project.entity.partner.ControllerInstitutionEntity
import io.cloudflight.jems.server.user.repository.user.UserRepository
import io.mockk.*
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import java.time.ZonedDateTime

class ControllerInstitutionPersistenceProviderTest: UnitTest() {

    companion object {
        private val createdAt = ZonedDateTime.now().minusDays(1)
        val dummyControllerEntity = ControllerInstitutionEntity(
            id = 1L,
            name = "dummy controller",
            description = "dummy controller description",
            institutionNuts = mutableSetOf(nutsAustria),
            createdAt = createdAt
        )

        val expectedControllerInstitution = ControllerInstitutionList(
            id = 1L,
            name = "dummy controller",
            description = "dummy controller description",
            institutionNuts = setOf(nutsAustria).toDto(),
            createdAt = createdAt
        )

        private val assignmentToSave = InstitutionPartnerAssignment(
            institutionId = 2L,
            partnerId = 1L,
            partnerProjectId = 1L
        )
        private val assignmentToDelete = InstitutionPartnerAssignment(
            institutionId = 1L,
            partnerId = 3L,
            partnerProjectId = 1L
        )
        private val dummyInstitutionAssignmentEntity = ControllerInstitutionPartnerEntity (
            institutionId = 2L,
            partnerId = 1L,
            partnerProjectId = 1L
        )

    }

    @MockK
    lateinit var controllerRepo: ControllerInstitutionRepository

    @MockK
    lateinit var nutsRegion3Repository: NutsRegion3Repository

    @MockK
    lateinit var institutionUserRepository: ControllerInstitutionUserRepository

    @RelaxedMockK
    lateinit var institutionPartnerRepository: ControllerInstitutionPartnerRepository

    @MockK
    lateinit var userRepository: UserRepository

    @InjectMockKs
    lateinit var controllerInstitutionPersistenceProvider: ControllerInstitutionPersistenceProvider

    @BeforeEach
    fun reset() {
        clearMocks(controllerRepo)
        clearMocks(nutsRegion3Repository)
        clearMocks(institutionUserRepository)
        clearMocks(institutionPartnerRepository)
        clearMocks(userRepository)
    }

    @Test
    fun getControllerInstitutions() {
        every { controllerRepo.findAll(Pageable.unpaged()) } returns PageImpl(listOf(dummyControllerEntity))
        assertThat(controllerInstitutionPersistenceProvider.getControllerInstitutions(Pageable.unpaged()).content).contains(expectedControllerInstitution)
    }

    @Test
    fun getInstitutionPartnerAssignments() {
        val entitySlot = slot<Iterable<ControllerInstitutionPartnerEntity>>()
        every { institutionPartnerRepository.deleteAllByIdInBatch(listOf(assignmentToDelete.partnerId)) } just Runs
        every { institutionPartnerRepository.saveAll(capture(entitySlot)) } returnsArgument 0

        assertThat(controllerInstitutionPersistenceProvider.assignInstitutionToPartner(
            assignmentsToRemove = listOf(assignmentToDelete),
            assignmentsToSave = listOf(assignmentToSave)
        )).containsExactly(assignmentToSave)

        verify(exactly = 1) { institutionPartnerRepository.deleteAllByIdInBatch(listOf(assignmentToDelete.partnerId)) }
    }


    @Test
    fun getInstitutionPartnerAssignmentsToDeleteByProjectId() {
        every { institutionPartnerRepository.getInstitutionPartnerAssignmentsToDeleteByProjectId(1L) } returns
            listOf(dummyInstitutionAssignmentEntity)
        assertThat(controllerInstitutionPersistenceProvider.getInstitutionPartnerAssignmentsToDeleteByProjectId(1L))
            .containsExactly(dummyInstitutionAssignmentEntity.toModel())
    }

    @Test
    fun getInstitutionPartnerAssignmentsToDeleteByInstitutionId() {
        every { institutionPartnerRepository.getInstitutionPartnerAssignmentsToDeleteByInstitutionId(1L) } returns
            listOf(dummyInstitutionAssignmentEntity)
        assertThat(controllerInstitutionPersistenceProvider.getInstitutionPartnerAssignmentsToDeleteByInstitutionId(1L))
            .containsExactly(dummyInstitutionAssignmentEntity.toModel())
    }
}
