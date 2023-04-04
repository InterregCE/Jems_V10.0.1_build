package io.cloudflight.jems.server.controllerInstitution.repository

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.common.file.service.model.UserSimple
import io.cloudflight.jems.server.controllerInstitution.entity.ControllerInstitutionPartnerEntity
import io.cloudflight.jems.server.controllerInstitution.entity.ControllerInstitutionUserEntity
import io.cloudflight.jems.server.controllerInstitution.entity.ControllerInstitutionUserId
import io.cloudflight.jems.server.controllerInstitution.nutsAustria
import io.cloudflight.jems.server.controllerInstitution.service.model.ControllerInstitutionList
import io.cloudflight.jems.server.controllerInstitution.service.model.InstitutionPartnerAssignment
import io.cloudflight.jems.server.controllerInstitution.service.model.InstitutionPartnerAssignmentRow
import io.cloudflight.jems.server.controllerInstitution.service.model.UserInstitutionAccessLevel
import io.cloudflight.jems.server.nuts.repository.NutsRegion3Repository
import io.cloudflight.jems.server.project.entity.partner.ControllerInstitutionEntity
import io.cloudflight.jems.server.user.entity.UserEntity
import io.cloudflight.jems.server.user.entity.UserRoleEntity
import io.cloudflight.jems.server.user.repository.user.UserRepository
import io.cloudflight.jems.server.user.service.model.UserStatus
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
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

        private fun dummyInstitutionAssignmentEntity(): ControllerInstitutionPartnerEntity {
            val institution = mockk<ControllerInstitutionEntity>()
            every { institution.id } returns 2L

            val entity = mockk<ControllerInstitutionPartnerEntity>()
            every { entity.partnerId } returns 1L
            every { entity.institution } returns institution
            every { entity.partnerProjectId } returns 10L
            return entity
        }

        private class InstitutionAssignmentImpl(
            override val institutionId: Long,
            override val partnerId: Long,
            override val partnerProjectId: Long
        ): InstitutionPartnerAssignmentRow

        private val dummyAssignmentToDelete = InstitutionAssignmentImpl(
            institutionId = 2L,
            partnerId = 1L,
            partnerProjectId = 1L
        )

        private val userEntities = listOf(
            ControllerInstitutionUserEntity(
                id = ControllerInstitutionUserId(
                    controllerInstitutionId = 1L,
                    user = UserEntity(
                        id = 1L,
                        email = "some email",
                        sendNotificationsToEmail = false,
                        name = "some name",
                        surname = "some surname",
                        userRole = UserRoleEntity(
                            id = 1L,
                            name = "some role"
                        ),
                        password = "some password",
                        userStatus = UserStatus.ACTIVE
                    )
                ),
                accessLevel = UserInstitutionAccessLevel.Edit
            )
        )

        private val expectedUser =
            UserSimple(
                id = 1L,
                name = "some name",
                surname = "some surname",
                email = "some email"
            )
    }

    @MockK
    lateinit var nutsRegion3Repository: NutsRegion3Repository

    @MockK
    lateinit var institutionRepository: ControllerInstitutionRepository

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
        clearMocks(institutionRepository)
        clearMocks(nutsRegion3Repository)
        clearMocks(institutionUserRepository)
        clearMocks(institutionPartnerRepository)
        clearMocks(userRepository)
    }

    @Test
    fun getControllerInstitutions() {
        every { institutionRepository.findAll(Pageable.unpaged()) } returns PageImpl(listOf(dummyControllerEntity))
        assertThat(controllerInstitutionPersistenceProvider.getControllerInstitutions(Pageable.unpaged()).content).contains(expectedControllerInstitution)
    }

    @Test
    fun getInstitutionPartnerAssignments() {
        val entitySlot = slot<Iterable<ControllerInstitutionPartnerEntity>>()
        every { institutionPartnerRepository.deleteAllByIdInBatch(any()) } answers { }
        val institution_2 = mockk<ControllerInstitutionEntity>()
        every { institution_2.id } returns 2L
        every { institutionRepository.findAllById(setOf(2L)) } returns listOf(institution_2)
        every { institutionPartnerRepository.saveAll(capture(entitySlot)) } returnsArgument 0

        assertThat(controllerInstitutionPersistenceProvider.assignInstitutionToPartner(
            partnerIdsToRemove = setOf(3L),
            assignmentsToSave = listOf(assignmentToSave)
        )).containsExactly(assignmentToSave)

        verify(exactly = 1) { institutionPartnerRepository.deleteAllByIdInBatch(setOf(3L)) }
    }

    @Test
    fun getRelatedUserIdsForProject() {
        every { institutionPartnerRepository.getRelatedUserIdsForProject(projectId = 188L) } returns setOf(1L, 2L, 3L)
        assertThat(controllerInstitutionPersistenceProvider.getRelatedUserIdsForProject(188L))
            .containsExactly(1L, 2L, 3L)
    }

    @Test
    fun getRelatedProjectAndPartnerIdsForUser() {
        every { institutionPartnerRepository.getRelatedProjectIdsForUser(userId = 444L) } returns listOf(
            Pair(1L, 10L), Pair(1L, 11L),
            Pair(2L, 20L), Pair(2L, 21L),
        )
        assertThat(controllerInstitutionPersistenceProvider.getRelatedProjectAndPartnerIdsForUser(444L))
            .containsExactlyEntriesOf(mapOf(1L to setOf(10L, 11L), 2L to setOf(20L, 21L)))
    }

    @Test
    fun getInstitutionPartnerAssignmentsToDeleteByProjectId() {
        every { institutionPartnerRepository.getInstitutionPartnerAssignmentsToDeleteByProjectId(1L) } returns
            listOf(dummyInstitutionAssignmentEntity())
        assertThat(controllerInstitutionPersistenceProvider.getInstitutionPartnerAssignmentsToDeleteByProjectId(1L))
            .containsExactly(InstitutionPartnerAssignment(institutionId = 2L, partnerId = 1L, partnerProjectId = 10L))
    }

    @Test
    fun getInstitutionPartnerAssignmentsToDeleteByInstitutionId() {
        every { institutionPartnerRepository.getInstitutionPartnerAssignmentsToDeleteByInstitutionId(1L) } returns
            listOf(dummyAssignmentToDelete)
        assertThat(controllerInstitutionPersistenceProvider.getInstitutionPartnerAssignmentsToDeleteByInstitutionId(1L))
            .containsExactly(dummyAssignmentToDelete.toModel())
    }

    @Test
    fun getControllerUsersForReportByInstitutionId() {
        every { institutionUserRepository.findAllByControllerInstitutionId(2L) } returns userEntities
        assertThat(controllerInstitutionPersistenceProvider.getControllerUsersForReportByInstitutionId(2L))
            .containsExactly(expectedUser)
    }
}
